package com.johnvon.reconsitution.service.impl;

import com.johnvon.reconsitution.service.WalletRpcService;

public class WalletRpcServiceImpl implements WalletRpcService {
    @Override
    public String moveMoney(String id, Long buyerId, Long sellerId, Double amount) {
        return "2";
    }
}
