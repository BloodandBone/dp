package com.johnvon.reconsitution.service;

public interface WalletRpcService {
    String moveMoney(String id, Long buyerId, Long sellerId, Double amount);
}
