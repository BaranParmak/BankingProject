package server.dao;

import common.Account;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDAOTest {

    private static final String TEST_ACCOUNT_NO = "TEST123";
    private static final AccountDAO dao = new AccountDAO();

    @Test
    @Order(1)
    void testInsert() {
        Account acc = new Account(TEST_ACCOUNT_NO, "Test User", 100.0);
        boolean result = dao.insert(acc);
        assertTrue(result, "Insert should succeed");
    }

    @Test
    @Order(2)
    void testFindByAccountNo() {
        Account acc = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertNotNull(acc, "Account should exist");
        assertEquals("Test User", acc.getFullName());
        assertEquals(100.0, acc.getBalance());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Account acc = dao.findByAccountNo(TEST_ACCOUNT_NO);
        acc.deposit(50);
        boolean result = dao.update(acc);
        assertTrue(result, "Update should succeed");

        Account updated = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertEquals(150.0, updated.getBalance());
    }

    @Test
    @Order(4)
    void testDelete() {
        boolean result = dao.delete(TEST_ACCOUNT_NO);
        assertTrue(result, "Delete should succeed");

        Account acc = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertNull(acc, "Account should be deleted");
    }
}

