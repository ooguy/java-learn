package org.markvarabyou.dao.sql;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.markvarabyou.dao.sql.helpers.SqlAgileToolDaoTestHelper;
import org.markvarabyou.entities.User;

import java.util.ArrayList;

/**
 * Tests for Sql Data Access Object for entity User.
 * User: Mark Varabyou
 * Date: 11/2/13
 * Time: 11:18 PM
 */
public class SqlUserDaoTest extends SqlAgileToolDaoTestHelper {
    private SqlUserDao sqlUserDao;

    private User getValidUser(){
        return new User("test", "test", "test@test.com");
    }

    @Before
    public void setUp() throws Exception {
        setUpConnection();
        sqlUserDao = new SqlUserDao(connection);
    }

    @After
    public void tearDown() throws Exception {
        closeAndRollbackConnection();
    }

    @Test
    public void testCreateSuccess() throws Exception {
        //Given
        User user = getValidUser();

        //When
        User createdUser = sqlUserDao.create(user);

        //Then
        Assert.assertNotNull(createdUser);
        User actualUser = sqlUserDao.read(createdUser.getId());
        Assert.assertEquals(createdUser.getId(), actualUser.getId());
        Assert.assertEquals(createdUser.getFirstName(), actualUser.getFirstName());
        Assert.assertEquals(createdUser.getLastName(), actualUser.getLastName());
        Assert.assertEquals(createdUser.getEmail(), actualUser.getEmail());
    }

    @Test
    public void testReadByIdSuccess(){
        //Given
        User expectedUser = sqlUserDao.create(getValidUser());

        //When
        User actualUser = sqlUserDao.read(expectedUser.getId());

        //Then
        Assert.assertNotNull(actualUser);
        Assert.assertEquals(expectedUser.getId(), actualUser.getId());
        Assert.assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        Assert.assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        Assert.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    public void testReadByIdFailed(){
        //Given
        int wrongKey = -1;

        //When
        User user = sqlUserDao.read(wrongKey);

        //Then
        Assert.assertNull(user);
    }

    @Test
    public void testReadAll(){
        //Given
        User user = sqlUserDao.create(getValidUser());

        //When
        ArrayList<User> list = sqlUserDao.read();

        //Then
        Assert.assertTrue(list.size() >= 1);
        boolean flag = false;
        for (User u : list){
            if (u.getId() == user.getId())
                flag = true;
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void testUpdateSuccess(){
        //Given
        User expectedUser = sqlUserDao.create(getValidUser());
        expectedUser.setFirstName("newTestName");
        expectedUser.setLastName("newTestName");
        expectedUser.setEmail("newEmail");

        //When
        User actualUser = sqlUserDao.update(expectedUser);

        //Then
        Assert.assertNotNull(actualUser);
        Assert.assertEquals(expectedUser.getId(), actualUser.getId());
        Assert.assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        Assert.assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        Assert.assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    public void testUpdateFailed(){
        //Given
        User wrongUser = sqlUserDao.create(getValidUser());
        wrongUser.setId(-1); //Wrong id

        //When
        User actualUser = sqlUserDao.update(wrongUser);

        //Then
        Assert.assertNull(actualUser);
    }

    @Test
    public void testDeleteSuccess(){
        //Given
        User user = sqlUserDao.create(getValidUser());

        //When
        boolean isDeleted = sqlUserDao.delete(user.getId());

        //Then
        Assert.assertTrue(isDeleted);
        Assert.assertNull(sqlUserDao.read(user.getId()));
    }

    @Test
    public void testDeleteFailed(){
        //Given
        int wrongId = -1; //Wrong id for user

        //When
        boolean isDeleted = sqlUserDao.delete(wrongId);

        //Then
        Assert.assertFalse(isDeleted);
    }
}
