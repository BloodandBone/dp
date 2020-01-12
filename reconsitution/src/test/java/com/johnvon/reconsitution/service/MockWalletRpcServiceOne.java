package com.johnvon.reconsitution.service;

public class MockWalletRpcServiceOne implements WalletRpcService {
    @Override
    public String moveMoney(String id, Long buyerId, Long sellerId, Double amount) {
        return "123bac";
    }
}
