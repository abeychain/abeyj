package org.abeyj.sample;

import org.abeyj.AbeyjRequest;
import org.abeyj.common.Constant;
import org.abeyj.response.AbeySendTransaction;
import org.abeyj.response.transaction.AbeyRawTransaction;
import org.abeyj.response.transaction.AbeyTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;

import java.math.BigInteger;

public class PaymentTransactionUsage extends AbeyjTestNet {
    private static final Logger logger = LoggerFactory.getLogger(FastBlockUsage.class);

    public String signPaymentTxWithFrom() {
        String fromSignedTxStr = null;
        AbeyTransactionManager abeyTransactionManager = new AbeyTransactionManager(abeyjRequest, chainId);
        try {
            //get nonce of from address
            AbeyGetTransactionCount abeyGetTransactionCount = abeyj.abeyGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = abeyGetTransactionCount.getTransactionCount();
            AbeyRawTransaction abeyRawTransaction = AbeyRawTransaction.createAbeyPaymentTransaction(nonce, Constant.DEFAULT_GASPRICE,
                    Constant.DEFAULT_GASLIMIT, toAddress, Constant.DEFAULT_VALUE, null, paymentAddress);

            fromSignedTxStr = abeyTransactionManager.signWithFromPrivateKey(abeyRawTransaction, fromPrivatekey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("fromSignedTxStr=[{}]", fromSignedTxStr);
        return fromSignedTxStr;
    }

    public String signPaymentTxWithPaymentAndSend(String signedTxWithFrom) {
        String txHash = null;
        try {
            AbeyjRequest abeyjRequest = new AbeyjRequest(Constant.RPC_TESTNET_URL);
            AbeyTransactionManager abeyTransactionManager = new AbeyTransactionManager(abeyjRequest,
                    chainId);

            //payment sign and send transaction
            AbeySendTransaction abeySendTransaction = abeyTransactionManager.signWithPaymentAndSend(signedTxWithFrom, paymentPrivateKey);


            if (abeySendTransaction != null && abeySendTransaction.hasError()) {
                logger.error("sendPaymentTransactionWithSigned error=[{}] ", abeySendTransaction.getError().getMessage());
            }
            txHash = abeySendTransaction.getAbeyTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("txHash=" + txHash);
        return txHash;
    }

    public String sendPaymentTx() {
        String txHash = null;
        try {
            //get nonce of from address
            AbeyGetTransactionCount abeyGetTransactionCount = abeyj.abeyGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger nonce = abeyGetTransactionCount.getTransactionCount();

            //create trueRawTransaction
            AbeyRawTransaction abeyRawTransaction = AbeyRawTransaction.createAbeyPaymentTransaction(nonce, Constant.DEFAULT_GASPRICE,
                    Constant.DEFAULT_GASLIMIT, toAddress, Constant.DEFAULT_VALUE, null, paymentAddress);

            AbeyTransactionManager abeyTransactionManager = new AbeyTransactionManager(abeyjRequest, chainId);


            AbeySendTransaction abeySendTransaction = abeyTransactionManager.signWithFromPaymentAndSend(
                    abeyRawTransaction, fromPrivatekey, paymentPrivateKey);
            if (abeySendTransaction != null && abeySendTransaction.hasError()) {
                logger.error("sendPaymentTransactionWithSigned error=[{}] ", abeySendTransaction.getError().getMessage());
            }
            txHash = abeySendTransaction.getAbeyTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txHash;
    }
}
