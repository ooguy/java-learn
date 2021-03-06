package threading.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import threading.core.enums.ThreadStatus;

import java.util.Random;

/**
 * Represents Client of the Bank, which performs random operations
 * Created by Mark Varabyou on 12/16/13.
 */
public class Client extends Person implements Runnable {
    private static final Logger logger = LogManager.getLogger("Client");
    private ThreadStatus status;// = ThreadStatus.Running;
    private Pocket pocket;

    public Client(int id, String name, Bank bank, int amountInPocket) {
        super(id, name, bank);
        this.pocket = new Pocket(amountInPocket);
    }

    public void watcherStart() {
        pocket.watcherStart();
    }

    public void watcherEnd() {
        pocket.watcherEnd();
    }

    public int checkPocket() {
        return pocket.check();
    }

    private Account getRandomAccount() {
        Random random = new Random();
        int index = random.nextInt(bank.getAccountsCount());
        return bank.getAccountByIndex(index);
    }

    private int getRandomAmountPart(int available) {
        if (available == 0)
            return 0;
        Random random = new Random();
        return random.nextInt(available);
    }

    private void performRandomOperation(Cashier cashier) {
        Account account = getRandomAccount();
        int amountAvailable = account.check();
        logger.debug(String.format("Client #%d discovered %d dollars available on account #%d",
                getId(), amountAvailable, account.getId()));

        Random random = new Random();
        int choice = random.nextInt(3);
        switch (choice) {
            case 0: { // put from Account to Pocket
                int amountToOperate = getRandomAmountPart(amountAvailable);
                if (amountToOperate == 0) {
                    logger.debug(String.format("Client #%d decided to take money to pocket from account #%d, "
                            + "but account balance is zero",
                            getId(), account.getId()));
                    break;
                }
                logger.debug(String.format("Client #%d decided to take %d dollars to pocket from account #%d",
                        getId(), amountToOperate, account.getId()));

                cashier.startWorkWithAccount(account);
                cashier.takeFromAccount(amountToOperate);
                if (cashier.commitWorkWithAccount()) {
                    pocket.put(amountToOperate);
                    logger.debug(String.format(
                            "Client #%d successfully toke %d dollars to pocket from account #%d",
                            getId(), amountToOperate, account.getId()));
                } else
                    logger.debug(String.format(
                            "Client #%d can't take %d dollars, because balance on account #%d was changed",
                            getId(), amountToOperate, account.getId()));
                break;
            }
            case 1: { // put from Pocket to Account
                int amountToOperate = getRandomAmountPart(pocket.check());
                if (amountToOperate == 0) {
                    logger.debug(String.format("Client #%d decided to put money from pocket to account #%d, "
                            + "but there are no money in pocket",
                            getId(), account.getId()));
                    break;
                }
                logger.debug(String.format("Client #%d decided to put %d dollars from pocket to account #%d",
                        getId(), amountToOperate, account.getId()));

                cashier.startWorkWithAccount(account);
                cashier.putToAccount(amountToOperate);

                if (cashier.commitWorkWithAccount()) {
                    pocket.take(amountToOperate);
                    logger.debug(String.format("Client #%d successfully put %d dollars from pocket to account #%d",
                            getId(), amountToOperate, account.getId()));
                }
                break;
            }
            case 2: { // transfer money to another Account
                int amountToOperate = getRandomAmountPart(amountAvailable);
                if (amountToOperate == 0) {
                    logger.debug(String.format("Client #%d decided to transfer money from one account #%d to another, "
                            + "but account balance is zero",
                            getId(), account.getId()));
                    break;
                }
                Account destination = getRandomAccount();
                logger.debug(String.format("Client #%d decided to transfer %d dollars from account #%d to account #%d",
                        getId(), amountToOperate, account.getId(), destination.getId()));

                cashier.startWorkWithAccount(account);
                cashier.takeFromAccount(amountToOperate);
                if (cashier.commitWorkWithAccount()) {
                    pocket.put(amountToOperate);
                    cashier.startWorkWithAccount(destination);
                    cashier.putToAccount(amountToOperate);
                    cashier.commitWorkWithAccount();
                    pocket.take(amountToOperate);
                    logger.debug(String.format(
                            "Client #%d successfully transferred %d dollars from account #%d to account #%d",
                            getId(), amountToOperate, account.getId(), destination.getId()));
                } else {
                    logger.debug(String.format(
                            "Client #%d can't transfer %d dollars, because balance on source account #%d was changed",
                            getId(), amountToOperate, account.getId()));
                }
                break;
            }
        }
    }

    private int getRandomSleepTime() {
        Random random = new Random();
        return random.nextInt(1000); // no more, than one second
    }

    @Override
    public void run() {
        status = ThreadStatus.Running;
        logger.debug(String.format("Client #%d with name = %s is running", getId(), getName()));
        while (bank.getStatus() == ThreadStatus.Running) {
            try {
                logger.debug(String.format("Client #%d is waiting for Cashier", getId()));
                Cashier cashier = bank.getCashier();
                logger.debug(String.format("Client #%d get a Cashier #%d with name %s",
                        getId(), cashier.getId(), cashier.getName()));
                performRandomOperation(cashier);
                bank.releaseCashier(cashier);
                logger.debug(String.format("Client #%d released a Cashier #%d", getId(), cashier.getId()));
                int sleepTime = getRandomSleepTime();
                logger.debug(String.format("Client #%d going to sleep for #%d milliseconds", getId(), sleepTime));
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        interrupt();
        status = ThreadStatus.Stopped;
        logger.debug(String.format("Client #%d stopped.", getId()));
    }

    public void interrupt() {
        status = ThreadStatus.Interrupting;
        logger.debug(String.format("Client #%d is interrupting.", getId()));
    }

    public boolean isStopped() {
        return status == ThreadStatus.Stopped;
    }
}
