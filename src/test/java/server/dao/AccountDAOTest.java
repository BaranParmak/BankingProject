package server.dao;

import common.Account;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountDAOTest {

    private static final String TEST_ACCOUNT_NO = "ACC123";
    private static final int TEST_CUSTOMER_NO = 9999;
    private static final String TEST_FULL_NAME = "Test User";

    private static AccountDAO dao;

    @BeforeAll
    static void setup() {
        dao = new AccountDAO();
    }


    @Test
    @Order(1)
    void testInsert() {
        Account acc = new Account(TEST_ACCOUNT_NO, TEST_FULL_NAME, TEST_CUSTOMER_NO, 100.0);
        boolean result = dao.insert(acc);
        assertTrue(result, "Insert should succeed");
    }

    @Test
    @Order(2)
    void testFindByAccountNo() {
        Account acc = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertNotNull(acc, "Account should exist");
        assertEquals(TEST_FULL_NAME, acc.getFullName());
    }

    @Test
    @Order(3)
    void testUpdate() {
        Account acc = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertNotNull(acc);
        acc.deposit(50);
        boolean result = dao.update(acc);
        assertTrue(result);

        Account updatedAccount = dao.findByAccountNo(TEST_ACCOUNT_NO);
        assertEquals(150.0, updatedAccount.getBalance());
    }

    @Test
    @Order(4)
    void testDelete() {
        boolean result = dao.delete(TEST_ACCOUNT_NO);
        assertTrue(result);
    }
}

