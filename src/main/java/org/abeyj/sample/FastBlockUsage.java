package org.abeyj.sample;

import org.abeyj.response.fast.FastBlock;
import org.abeyj.response.snail.FastBalanceChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class FastBlockUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(FastBlockUsage.class);

    public void getLockBalance() {
        String address = "0x710146e71a7E9Ce9124c6A8f82CE47dA56596556";
        BigInteger lockBalance = abeyjRequest.getLockBalance(address,null);
        System.out.println(lockBalance);
    }

    public void getFastBlockByNumber() {
        BigInteger fastNumber = new BigInteger("100");
        FastBlock fastBlock = abeyjRequest.getFastBlockByNumber(fastNumber, true);
        logger.info("fast block=[{}]", fastBlock.toString());
    }

    public void getFastBlockByHash() {
        String fastHash = "0x3d85286e8492eb22911ae004de6e29745f8eb68cf8ea740f4301587bc2e131a4";
        FastBlock fastBlock = abeyjRequest.getFastBlockByHash(fastHash, true);
        logger.info("fast block=[{}]", fastBlock);
    }

    public void getCurrentFastNumber() {
        BigInteger currentFastNumber = abeyjRequest.getCurrentFastNumber();
        logger.info("current fast number=[{}]", currentFastNumber);
    }

    /**
     * useage:
     * get balance with address which has changed
     */
    public void getStateChangeByFastNumber() {
        BigInteger fastNumber = new BigInteger("1011");
        FastBalanceChange fastBalanceChange = abeyjRequest.getStateChangeByFastNumber(fastNumber);
        logger.info("balanceChange=[{}]", fastBalanceChange);
    }

}
