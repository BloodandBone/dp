package com.johnvon.reconsitution;

import com.johnvon.reconsitution.domain.STATUS;
import com.johnvon.reconsitution.domain.Transaction;
import com.johnvon.reconsitution.service.MockWalletRpcServiceOne;
import com.johnvon.reconsitution.util.TransactionLock;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.InvalidTransactionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@SpringBootTest
public class TransationTests {
    @Test
    public void testExecute() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456L";
        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId);
        boolean executedResult = transaction.execute();
        assertTrue(executedResult);
    }

    @Test
    public void testExecuteOne() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456L";
        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId);
        //使用mock对象来替代真正的RPC服务
        transaction.setWalletRpcService(new MockWalletRpcServiceOne());
        boolean executedResult = transaction.execute();
        assertTrue(executedResult);
        assertEquals(STATUS.EXECUTED, transaction.getStatus());
    }

    @Test
    public void testExecuteLock() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456L";

        TransactionLock transactionLock = new TransactionLock() {
            public boolean lock(String id) {
                return true;
            }

            public void unLock() {
            }
        };
        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId);
        //使用mock对象来替代真正的RPC服务
        transaction.setWalletRpcService(new MockWalletRpcServiceOne());
        transaction.setLock(transactionLock);
        boolean executedResult = transaction.execute();
        assertTrue(executedResult);
        assertEquals(STATUS.EXECUTED, transaction.getStatus());
    }

    @Test
    public void testExecute_with_TransactionIsExpired() throws InvalidTransactionException {
        Long buyerId = 123L;
        Long sellerId = 234L;
        Long productId = 345L;
        String orderId = "456L";
        Transaction transaction = new Transaction(null, buyerId, sellerId, productId, orderId){
            protected boolean isExpired(){
                return true;
            }
        };
        boolean executedResult = transaction.execute();
        assertTrue(executedResult);
        assertEquals(STATUS.EXPIRED, transaction.getStatus());
    }
}
