/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.abeyj.tx;

import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.Hash;
import org.abeyj.crypto.RawTransaction;
import org.abeyj.crypto.TransactionEncoder;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeyCall;
import org.abeyj.protocol.core.methods.response.AbeyGetCode;
import org.abeyj.protocol.core.methods.response.AbeyGetTransactionCount;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;
import org.abeyj.tx.exceptions.TxHashMismatchException;
import org.abeyj.tx.response.TransactionReceiptProcessor;
import org.abeyj.utils.Numeric;
import org.abeyj.utils.TxHashVerifier;

import java.io.IOException;
import java.math.BigInteger;

/**
 * TransactionManager implementation using Ethereum wallet file to create and sign transactions
 * locally.
 *
 * <p>This transaction manager provides support for specifying the chain id for transactions as per
 * <a href="https://github.com/ethereum/EIPs/issues/155">EIP155</a>, as well as for locally signing
 * RawTransaction instances without broadcasting them.
 */
public class RawTransactionManager extends TransactionManager {

    private final Abeyj abeyj;
    final Credentials credentials;

    private final long chainId;

    protected TxHashVerifier txHashVerifier = new TxHashVerifier();

    public RawTransactionManager(Abeyj abeyj, Credentials credentials, long chainId) {
        super(abeyj, credentials.getAddress());

        this.abeyj = abeyj;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            Abeyj abeyj,
            Credentials credentials,
            long chainId,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(transactionReceiptProcessor, credentials.getAddress());

        this.abeyj = abeyj;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            Abeyj abeyj, Credentials credentials, long chainId, int attempts, long sleepDuration) {
        super(abeyj, attempts, sleepDuration, credentials.getAddress());

        this.abeyj = abeyj;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(Abeyj abeyj, Credentials credentials) {
        this(abeyj, credentials, ChainId.NONE);
    }

    public RawTransactionManager(
            Abeyj abeyj, Credentials credentials, int attempts, int sleepDuration) {
        this(abeyj, credentials, ChainId.NONE, attempts, sleepDuration);
    }

    protected BigInteger getNonce() throws IOException {
        AbeyGetTransactionCount abeyGetTransactionCount =
                abeyj.abeyGetTransactionCount(
                                credentials.getAddress(), DefaultBlockParameterName.PENDING)
                        .send();

        return abeyGetTransactionCount.getTransactionCount();
    }

    public TxHashVerifier getTxHashVerifier() {
        return txHashVerifier;
    }

    public void setTxHashVerifier(TxHashVerifier txHashVerifier) {
        this.txHashVerifier = txHashVerifier;
    }

    @Override
    public AbeySendTransaction sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException {

        BigInteger nonce = getNonce();

        RawTransaction rawTransaction =
                RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);

        return signAndSend(rawTransaction);
    }

    @Override
    public AbeySendTransaction sendTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException {

        BigInteger nonce = getNonce();

        RawTransaction rawTransaction =
                RawTransaction.createTransaction(
                        nonce, null, gasLimit, to, value, data, gasPremium, feeCap);

        return signAndSend(rawTransaction);
    }

    @Override
    public String sendCall(String to, String data, DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        AbeyCall abeyCall =
                abeyj.abeyCall(
                                Transaction.createEthCallTransaction(getFromAddress(), to, data),
                                defaultBlockParameter)
                        .send();

        assertCallNotReverted(abeyCall);
        return abeyCall.getValue();
    }

    @Override
    public AbeyGetCode getCode(
            final String contractAddress, final DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        return abeyj.abeyGetCode(contractAddress, defaultBlockParameter).send();
    }

    public String sign(RawTransaction rawTransaction) {

        byte[] signedMessage;

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        return Numeric.toHexString(signedMessage);
    }

    public AbeySendTransaction signAndSend(RawTransaction rawTransaction) throws IOException {
        String hexValue = sign(rawTransaction);
        AbeySendTransaction abeySendTransaction = abeyj.abeySendRawTransaction(hexValue).send();

        if (abeySendTransaction != null && !abeySendTransaction.hasError()) {
            String txHashLocal = Hash.sha3(hexValue);
            String txHashRemote = abeySendTransaction.getTransactionHash();
            if (!txHashVerifier.verify(txHashLocal, txHashRemote)) {
                throw new TxHashMismatchException(txHashLocal, txHashRemote);
            }
        }

        return abeySendTransaction;
    }
}
