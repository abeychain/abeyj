package org.abeyj.response.transaction;

import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.Sign;
import org.abeyj.crypto.Sign.SignatureData;
import org.abeyj.rlp.RlpEncoder;
import org.abeyj.rlp.RlpList;
import org.abeyj.rlp.RlpString;
import org.abeyj.rlp.RlpType;
import org.abeyj.utils.Bytes;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Create RLP encoded transaction, implementation as per p4 of the <a href="http://gavwood.com/paper.pdf">yellow
 * paper</a>.
 */
public class AbeyTransactionEncoder {

    private static final int CHAIN_ID_INC = 35;
    private static final int LOWER_REAL_V = 27;
    
    public static byte[] signMessage(AbeyRawTransaction abeyRawTransaction, Credentials credentials) {
        byte[] encodedTransaction = encode(abeyRawTransaction);
        Sign.SignatureData signatureData = Sign.signMessage(
                encodedTransaction, credentials.getEcKeyPair());
        return encode(abeyRawTransaction, signatureData);
    }

    public static byte[] signMessageFrom(
            AbeyRawTransaction abeyRawTransaction, long chainId, Credentials credentials) {
        byte[] encodedTransaction = encode(abeyRawTransaction, chainId);

        Sign.SignatureData signatureData = Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());
        Sign.SignatureData eip155SignatureData = createEip155SignatureData(signatureData, chainId);

        return encode(abeyRawTransaction, eip155SignatureData);
    }

    public static byte[] signMessage_payment(AbeyRawTransaction abeyRawTransaction, SignatureData eip155SignatureData, long chainId, Credentials credentials_payment) {
        byte[] encodedTransactionP = encodeP(abeyRawTransaction, eip155SignatureData, chainId);
        Sign.SignatureData signatureDataP = Sign.signMessage(encodedTransactionP, credentials_payment.getEcKeyPair());
        Sign.SignatureData eip155SignatureDataP = createEip155SignatureData(signatureDataP, chainId);

        return encodeP(abeyRawTransaction, eip155SignatureData, eip155SignatureDataP);
    }

    public static byte[] signMessage_fromAndPayment(AbeyRawTransaction abeyRawTransaction, long chainId,
                                                    Credentials credentials, Credentials credentials_payment) {

        byte[] encodedTransaction = encode(abeyRawTransaction, chainId);
        Sign.SignatureData signatureData = Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());
        Sign.SignatureData eip155SignatureData = createEip155SignatureData(signatureData, chainId);

        byte[] encodedTransactionP = encodeP(abeyRawTransaction, eip155SignatureData, chainId);
        Sign.SignatureData signatureDataP = Sign.signMessage(encodedTransactionP, credentials_payment.getEcKeyPair());
        Sign.SignatureData eip155SignatureDataP = createEip155SignatureData(signatureDataP, chainId);

        return encodeP(abeyRawTransaction, eip155SignatureData, eip155SignatureDataP);
    }


    public static Sign.SignatureData createEip155SignatureData(
            Sign.SignatureData signatureData, long chainId) {
        BigInteger v = Numeric.toBigInt(signatureData.getV());
        v = v.subtract(BigInteger.valueOf(LOWER_REAL_V));
        v = v.add(BigInteger.valueOf(chainId * 2));
        v = v.add(BigInteger.valueOf(CHAIN_ID_INC));
        return new Sign.SignatureData(v.toByteArray(), signatureData.getR(), signatureData.getS());
    }

    public static byte[] encode(AbeyRawTransaction abeyRawTransaction) {
        return encode(abeyRawTransaction, null);
    }

    public static byte[] encode(AbeyRawTransaction abeyRawTransaction, long chainId) {
//        BigInteger v = BigInteger.valueOf(chainId);
//        Sign.SignatureData signatureData = new Sign.SignatureData(v.toByteArray(), new byte[]{}, new byte[]{});
        Sign.SignatureData signatureData = new Sign.SignatureData(longToBytes(chainId), new byte[] {}, new byte[] {});
        return encode(abeyRawTransaction, signatureData);
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }


//    public static byte[] encodeP(TrueRawTransaction trueRawTransaction, Sign.SignatureData signatureData, long chainId) {
//        BigInteger v = BigInteger.valueOf(chainId);
//
//        Sign.SignatureData signatureDataP = new Sign.SignatureData(v.toByteArray(), new byte[] {}, new byte[] {});
//        return encodeP(trueRawTransaction, signatureData, signatureDataP);
//    }

    public static byte[] encodeP(AbeyRawTransaction abeyRawTransaction, Sign.SignatureData signatureData, long chainId) {
        BigInteger v = BigInteger.valueOf(chainId);
        Sign.SignatureData signatureDataP = new Sign.SignatureData(v.toByteArray(), new byte[]{}, new byte[]{});
//        Sign.SignatureData signatureDataP = new Sign.SignatureData(longToBytes(chainId), new byte[] {}, new byte[] {});
        return encodeP(abeyRawTransaction, signatureData, signatureDataP);
    }

    private static byte[] encode(AbeyRawTransaction abeyRawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(abeyRawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private static byte[] encodeP(AbeyRawTransaction abeyRawTransaction, Sign.SignatureData signatureData,
                                  Sign.SignatureData signatureDataP) {
        List<RlpType> values = asRlpValuesP(abeyRawTransaction, signatureData, signatureDataP);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    static List<RlpType> asRlpValues(AbeyRawTransaction abeyRawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(abeyRawTransaction.getNonce()));
        result.add(RlpString.create(abeyRawTransaction.getGasPrice()));
        result.add(RlpString.create(abeyRawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = abeyRawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(abeyRawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(abeyRawTransaction.getData());
        result.add(RlpString.create(data));

        result.add(RlpString.create(Numeric.hexStringToByteArray(abeyRawTransaction.getPayment())));
        if (abeyRawTransaction.getFee() == null) {
            result.add(RlpString.create(0));
        } else {
            result.add(RlpString.create(abeyRawTransaction.getFee()));
        }

        if (signatureData != null) {
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getV())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }
        return result;
    }

    static List<RlpType> asRlpValuesP(AbeyRawTransaction abeyRawTransaction, Sign.SignatureData signatureData,
                                      Sign.SignatureData signatureDataP) {
        List<RlpType> result = new ArrayList<>();

        result.add(RlpString.create(abeyRawTransaction.getNonce()));
        result.add(RlpString.create(abeyRawTransaction.getGasPrice()));
        result.add(RlpString.create(abeyRawTransaction.getGasLimit()));

        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        String to = abeyRawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
            result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
        } else {
            result.add(RlpString.create(""));
        }

        result.add(RlpString.create(abeyRawTransaction.getValue()));

        // value field will already be hex encoded, so we need to convert into binary first
        byte[] data = Numeric.hexStringToByteArray(abeyRawTransaction.getData());
        result.add(RlpString.create(data));

        result.add(RlpString.create(Numeric.hexStringToByteArray(abeyRawTransaction.getPayment())));
        // result.add(RlpString.create(trueRawTransaction.getPayment()));
        if (abeyRawTransaction.getFee() == null) {
            result.add(RlpString.create(0));
        } else {
            result.add(RlpString.create(abeyRawTransaction.getFee()));
        }

        if (signatureData != null) {
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getV())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        if (signatureDataP != null) {
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureDataP.getV())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureDataP.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureDataP.getS())));
        }
        // result.add(RlpString.create(chainId));

        return result;
    }
}
