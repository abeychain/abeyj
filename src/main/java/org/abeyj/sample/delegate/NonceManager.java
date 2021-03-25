package org.abeyj.sample.delegate;

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NonceManager {
    private String tag = this.getClass().getName();
    private static NonceManager nonceManager;
    private ConcurrentHashMap<String, NonceLocker> nonceMap = new ConcurrentHashMap<>();

    public static NonceManager getInstance() {
        if (null == nonceManager) nonceManager = new NonceManager();
        return nonceManager;
    }

    private BigInteger getNonceForAbeyj(Abeyj abeyj, String address) {
        try {
            AbeyGetTransactionCount abeyGetTransactionCount =
                    abeyj.abeyGetTransactionCount(address, DefaultBlockParameterName.PENDING).sendAsync().get();
            return abeyGetTransactionCount.getTransactionCount();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BigInteger getNonce(Abeyj abeyj, String address) {
        NonceLocker nonceLocker = nonceMap.get(address);
        if (null == nonceLocker) nonceLocker = new NonceLocker();
        nonceLocker.lock.lock();
        if (null == nonceLocker.nonce)
            nonceLocker.nonce = getNonceForAbeyj(abeyj, address);
        else
            nonceLocker.nonce = nonceLocker.nonce.add(BigInteger.ONE);
        nonceMap.put(address, nonceLocker);
        return nonceLocker.nonce;
    }

    public void exhaustNonce(String address, String tx) {
        NonceLocker nonceLocker = nonceMap.get(address);
        if (null == tx || tx.length() == 0) {
            nonceLocker.nonce = null;
            nonceMap.put(address, nonceLocker);
        }
        nonceLocker.lock.unlock();
    }

    public static class NonceLocker {
        private BigInteger nonce = null;
        private Lock lock = new ReentrantLock();
    }
}
