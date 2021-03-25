package org.abeyj.sample.erc20;

import org.abeyj.common.Constant;
import org.abeyj.sample.AbeyjTestNet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.RawTransaction;
import org.abeyj.crypto.TransactionEncoder;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeyEstimateGas;
import org.abeyj.protocol.core.methods.response.AbeyGetBalance;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;
import org.abeyj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

public class TransactionClientUsage extends AbeyjTestNet {
    private static Logger logger = LoggerFactory.getLogger(TransactionClientUsage.class);

    /**
     * query balance
     *
     * @param address
     * @return balance
     */
    public BigInteger getBalance(String address) {
        BigInteger balance = BigInteger.ZERO;
        try {
            AbeyGetBalance abeyGetBalance = abeyj.abeyGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if (abeyGetBalance != null) {
                balance = abeyGetBalance.getBalance();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("address= " + address + ", balance= " + balance + "wei");
        return balance;
    }

    /**
     * query address balance by privatekey
     *
     * @param privatekey
     * @return
     */
    public BigInteger getBalanceWithPrivateKey(String privatekey) {
        BigInteger balance = null;
        String address = "";
        try {
            Credentials credentials = Credentials.create(privatekey);
            address = credentials.getAddress();
            balance = getBalance(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * Used to generate the String after the transaction signed by the sender offline,
     * which contains the signature information of the sender.
     * Often used to send to third parties and used to send transactions directly
     *
     * @return
     */
    public String genRawTransaction() {
        Credentials credentials = Credentials.create(fromPrivatekey);
        BigInteger nonce = getTransactionNonce(fromAddress);
        RawTransaction rawTransaction =
                RawTransaction.createEtherTransaction(nonce, Constant.DEFAULT_GASPRICE,
                        Constant.DEFAULT_CONTRACT_GASLIMIT, toAddress, Constant.DEFAULT_VALUE);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        String hexMessage = Numeric.toHexString(signedMessage);
        logger.info("genRawTransaction hexMessage ={}", hexMessage);
        return hexMessage;
    }


    /**
     * send transaction
     * Used in conjunction with genRawTransaction method
     *
     * @param hexValue The string form after the transaction is signed
     */
    public void sendRawTransaction(String hexValue) {
        try {
            AbeySendTransaction abeySendTransaction = abeyj.abeySendRawTransaction(hexValue).send();
            String txHash = abeySendTransaction.getTransactionHash();
            if (abeySendTransaction.getError() != null) {
                logger.error("sendTransaction error", abeySendTransaction.getError().getMessage());
            }
            System.out.println("txHash------------------->" + txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a  transaction object
     *
     * @param fromAddress
     * @param toAddress
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param value
     * @return
     */
    public Transaction makeTransaction(String fromAddress, String toAddress,
                                       BigInteger nonce, BigInteger gasPrice,
                                       BigInteger gasLimit, BigInteger value) {
        Transaction transaction;
        transaction = Transaction.createEtherTransaction(fromAddress, nonce, gasPrice, gasLimit, toAddress, value);
        return transaction;
    }

    /**
     * Gets the gas ceiling for a normal transaction
     *
     * @param transaction
     * @return gas
     */
    public BigInteger getTransactionGasLimit(Transaction transaction) {
        BigInteger gasLimit = BigInteger.ZERO;
        try {
            AbeyEstimateGas abeyEstimateGas = abeyj.abeyEstimateGas(transaction).send();
            gasLimit = abeyEstimateGas.getAmountUsed();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gasLimit;
    }

    /**
     * @return nonce
     */
    public BigInteger getTransactionNonce(String address) {
        BigInteger nonce = BigInteger.ZERO;
        try {
            AbeyGetTransactionCount abeyGetTransactionCount = abeyj.abeyGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            nonce = abeyGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nonce;
    }

}
