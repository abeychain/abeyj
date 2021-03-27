package org.abeyj.response.transaction;

import org.abeyj.address.AddressHelper;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;

/**
 * Transaction class used for signing transactions locally.<br>
 * For the specification, refer to p4 of the <a href="http://gavwood.com/paper.pdf">
 * yellow paper</a>.
 */
public class AbeyRawTransaction {

    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String data;

    private String payment;
    private BigInteger fee;

    protected AbeyRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
                                 BigInteger value, String data, BigInteger fee, String payment) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.to = to;
        this.value = value;

        this.fee = fee;
        this.payment = payment;

        if (data == null) {
            data = "";
        }
        this.data = Numeric.cleanHexPrefix(data);
    }

    public AbeyRawTransaction(SignedAbeyRawTransaction signedAbeyRawTransaction) {
        this.nonce = signedAbeyRawTransaction.getNonce();
        this.gasPrice = signedAbeyRawTransaction.getGasPrice();
        this.gasLimit = signedAbeyRawTransaction.getGasLimit();
        this.to = signedAbeyRawTransaction.getTo();
        this.value = signedAbeyRawTransaction.getValue();

        this.fee = signedAbeyRawTransaction.getFee();
        this.payment = signedAbeyRawTransaction.getPayment();

        if (signedAbeyRawTransaction.getData() != null) {
            this.data = Numeric.cleanHexPrefix(signedAbeyRawTransaction.getData());
        }
    }

    /**
     * cantains payment transaction
     *
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param data
     * @param payment
     * @return
     */
    public static AbeyRawTransaction createAbeyPaymentTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data, String payment) {
        return new AbeyRawTransaction(nonce, gasPrice, gasLimit, to, value, data, null, payment);
    }

    /**
     * contains fee  transaction
     *
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param data
     * @param fee
     * @return
     */
    public static AbeyRawTransaction createAbeyFeeTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data, BigInteger fee) {
        return new AbeyRawTransaction(nonce, gasPrice, gasLimit, to, value, data, fee, null);
    }

    /**
     * contains payment and fee transaction
     *
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param data
     * @param fee
     * @param payment
     * @return
     */
    public static AbeyRawTransaction createAbeyPaymentAndFeeTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data, BigInteger fee, String payment) {
        return new AbeyRawTransaction(nonce, gasPrice, gasLimit, to, value, data, fee, payment);
    }

    /**
     * contains basic info transaction
     *
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param data
     * @return
     */
    public static AbeyRawTransaction createTransaction(
            BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to,
            BigInteger value, String data) {
        return new AbeyRawTransaction(nonce, gasPrice, gasLimit, to, value, data, null, null);
    }


    public BigInteger getNonce() {
        return nonce;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public String getTo() {
        return AddressHelper.changeAddressToHex(to);
    }

    public BigInteger getValue() {
        return value;
    }

    public String getData() {
        return data;
    }

    public BigInteger getFee() {
        return fee;
    }

    public String getPayment() {
        return AddressHelper.changeAddressToHex(payment);
    }
}
