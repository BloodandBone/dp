package com.johnvon.reconsitution.domain;

import com.johnvon.reconsitution.service.WalletRpcService;
import com.johnvon.reconsitution.util.IdGenerator;
import com.johnvon.reconsitution.util.TransactionLock;
import lombok.Getter;

import javax.transaction.InvalidTransactionException;

@Getter
public class Transaction {
    private String id;
    private Long buyerId;
    private Long sellerId;
    private Long productId;
    private String orderId;
    private Long createTimestamp;
    private Double amount;
    private STATUS status;
    private String walletTransactionId;
    private WalletRpcService walletRpcService;
    private TransactionLock lock;

    public void setWalletRpcService(WalletRpcService walletRpcService) {
        this.walletRpcService = walletRpcService;
    }

    public void setLock(TransactionLock lock) {
        this.lock = lock;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    protected boolean isExpired() {
        long executionInvokedTimestamp = System.currentTimeMillis();
        return executionInvokedTimestamp - createTimestamp > 14L * 24 * 3600 * 1000;
    }

    protected void fillTransactionId(String preAssignedId) {
        if (preAssignedId != null && !preAssignedId.isEmpty()) {
            this.id = preAssignedId;
        } else {
            this.id = IdGenerator.generateTransactionId();
        }
        if (!this.id.startsWith("t_")) {
            this.id = "t_" + preAssignedId;
        }
    }

    public Transaction(String preAssignedId, Long buyerId, Long sellerId, Long productId, String orderId) {
        fillTransactionId(preAssignedId);
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
        this.status = STATUS.TO_BE_EXECUTD;
        this.createTimestamp = System.currentTimeMillis();
    }

    public boolean execute() throws InvalidTransactionException {
        if ((buyerId == null || sellerId == null || amount < 0.0)) {
            throw new InvalidTransactionException("InvalidTrans");
        }
        if (status == STATUS.EXECUTED) {
            return true;
        }
        boolean isLocked = false;
        try {
            isLocked = lock.lock(id);
            if (!isLocked) {
                return false;
            }
            if (status == STATUS.EXECUTED) {
                return true;
            }
            long executionInvokedTimestamp = System.currentTimeMillis();
//            if (executionInvokedTimestamp - createTimestamp > 14L * 24 * 3600 * 1000) {
//                this.status = STATUS.EXPIRED;
//                return false;
//            }
            if (isExpired()) {
                this.status = STATUS.EXPIRED;
                return false;
            }
            String walletTransactionId = walletRpcService.moveMoney(id, buyerId, sellerId, amount);
            if (walletTransactionId != null) {
                this.walletTransactionId = walletTransactionId;
                this.status = STATUS.EXECUTED;
                return true;
            } else {
                this.status = STATUS.FAILED;
                return false;
            }
        } finally {
            if (isLocked) {

            }
        }
    }
}
