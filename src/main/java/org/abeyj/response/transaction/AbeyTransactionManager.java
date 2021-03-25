package org.abeyj.response.transaction;

import org.abeyj.AbeyjRequest;
import org.abeyj.response.AbeySendTransaction;
import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.Sign;
import org.abeyj.utils.Numeric;

public class AbeyTransactionManager {

    public AbeyjRequest abeyjRequest;
    public int chainId;

    private AbeyTransactionManager() {

    }

    public AbeyTransactionManager(AbeyjRequest abeyjRequest, int chainId) {
        this.abeyjRequest = abeyjRequest;
        this.chainId = chainId;
    }

    public String signWithFromPrivateKey(AbeyRawTransaction abeyRawTransaction, String fromPrivateKey) {
        String signedTxWithFrom = null;
        try {
            Credentials credentials_from = Credentials.create(fromPrivateKey);
            byte[] signedMessage = AbeyTransactionEncoder.signMessageFrom(abeyRawTransaction, chainId, credentials_from);
            signedTxWithFrom = Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signedTxWithFrom;
    }

    /**
     * @param signedTxWithFrom  transaction singed with from privatekey,called signedTxWithFrom
     * @param paymentPrivateKey payment privateKey
     * @return transaction singed with payment privatekey  based on signedTxWithFrom
     */
    public String signWithPaymentPrivateKey(String signedTxWithFrom, String paymentPrivateKey) {
        String signedTxWithPayment = null;
        try {
            Credentials credentials_payment = Credentials.create(paymentPrivateKey);
            System.out.println("sendPaymentTransaction payment address: " + credentials_payment.getAddress());

            //ͨ��rawTransaction�����������Ϣ������������ǩ��rsv
            SignedAbeyRawTransaction signtrueRawTransaction = (SignedAbeyRawTransaction) AbeyTransactionDecoder.decode(signedTxWithFrom);
            Sign.SignatureData decode_signatureData = signtrueRawTransaction.getSignatureData();
            AbeyRawTransaction decode_abeyRawTransaction = new AbeyRawTransaction(signtrueRawTransaction);

            byte[] signedMessage = AbeyTransactionEncoder.
                    signMessage_payment(decode_abeyRawTransaction, decode_signatureData,
                            chainId, credentials_payment);

            signedTxWithPayment = Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signedTxWithPayment;
    }

    /**
     * sign with payment and send transction
     *
     * @param signedTxWithFrom  transaction singed with from privatekey,called signedTxWithFrom
     * @param paymentPrivateKey payment privateKey
     * @return transaction singed with payment privatekey  based on signedTxWithFrom
     */
    public AbeySendTransaction signWithPaymentAndSend(String signedTxWithFrom, String paymentPrivateKey) {
        String signedTxWithPayment = signWithPaymentPrivateKey(signedTxWithFrom, paymentPrivateKey);
        AbeySendTransaction abeySendTransaction = abeyjRequest.abeySendRawTransaction(signedTxWithPayment);
        return abeySendTransaction;
    }


    /**
     * sign with from and payment private
     * @param abeyRawTransaction trueTransaction info
     * @param fromPrivateKey     tx from privatekey
     * @param paymentPrivateKey  tx payment privatekey
     * @return
     */
    public String signWithFromAndPayment(AbeyRawTransaction abeyRawTransaction,
                                         String fromPrivateKey, String paymentPrivateKey) {
        Credentials fromCredentials = Credentials.create(fromPrivateKey);
        Credentials paymentCredentials = Credentials.create(paymentPrivateKey);
        byte[] signedMessage = AbeyTransactionEncoder.signMessage_fromAndPayment(
                abeyRawTransaction, chainId, fromCredentials, paymentCredentials);
        String signedTxWithPayment = Numeric.toHexString(signedMessage);
        return signedTxWithPayment;
    }

    public AbeySendTransaction signWithFromPaymentAndSend(AbeyRawTransaction abeyRawTransaction,
                                                          String fromPrivateKey, String paymentPrivateKey) {
        Credentials fromCredentials = Credentials.create(fromPrivateKey);
        Credentials paymentCredentials = Credentials.create(paymentPrivateKey);
        byte[] signedMessage = AbeyTransactionEncoder.signMessage_fromAndPayment(
                abeyRawTransaction, chainId, fromCredentials, paymentCredentials);
        String signedWithFromPayment = Numeric.toHexString(signedMessage);
        AbeySendTransaction abeySendTransaction = abeyjRequest.abeySendRawTransaction(signedWithFromPayment);
        return abeySendTransaction;
    }


}
