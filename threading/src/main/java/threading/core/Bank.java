package threading.core;

import threading.core.enums.ThreadStatus;

import java.util.ArrayList;

/**
 * Class representing the Bank.
 * Created by Mark Varabyou on 12/16/13.
 */
public class Bank {
    private ArrayList<Client> clients;
    private ArrayList<Cashier> cashiers;
    private ArrayList<Account> accounts;
    private ThreadStatus status;
    private int cashAmount;
    private Watcher watcher;
    private CashierPool cashierPool;

    public Bank(int watcherInterval) {
        clients = new ArrayList<Client>();
        cashiers = new ArrayList<Cashier>();
        accounts = new ArrayList<Account>();
        cashierPool = new CashierPool(cashiers);
        watcher = new Watcher(this, watcherInterval);
    }

    public int getCashAmount() {
        return cashAmount;
    }

    public void addClient(int id, String name, int amountInPocket) {
        clients.add(new Client(id, name, this, amountInPocket));
        cashAmount += amountInPocket;
    }

    public void addCashier(int id, String name) {
        Cashier cashier = new Cashier(id, name, this);
        cashiers.add(cashier);
        cashierPool.releaseCashier(cashier);
    }

    public void addAccount(int id, int initialAmount) {
        accounts.add(new Account(id, initialAmount));
        cashAmount += initialAmount;
    }

    public int getAccountsCount() {
        return accounts.size();
    }

    public Account getAccountByIndex(int index) {
        return accounts.get(index);
    }

    public Cashier getCashier() throws InterruptedException {
        return cashierPool.getCashier();
    }

    public void releaseCashier(Cashier cashier) {
        cashierPool.releaseCashier(cashier);
    }

    public void start() {
        status = ThreadStatus.Running;
        Thread thread;
        for (Client client : clients) {
            thread = new Thread(client);
            thread.start();
        }
        thread = new Thread(watcher);
        thread.start();
    }

    public void stop() {
        status = ThreadStatus.Interrupting;
        boolean allStopped = false;
        while (!allStopped) {
            allStopped = true;
            for (Client client : clients) {
                allStopped = allStopped && client.isStopped();
            }
        }

        watcher.interrupt();
        while (!watcher.isStopped()) {
        }
        status = ThreadStatus.Stopped;
    }

    protected ArrayList<Account> getAccounts() {
        return accounts;
    }

    protected ArrayList<Client> getClients() {
        return clients;
    }

    public ThreadStatus getStatus() {
        return status;
    }
}
