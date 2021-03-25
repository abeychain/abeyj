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

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.methods.response.AbeyCall;
import org.abeyj.protocol.core.methods.response.AbeyGetCode;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;
import org.abeyj.protocol.core.methods.response.TransactionReceipt;
import org.abeyj.protocol.exceptions.TransactionException;
import org.abeyj.tx.exceptions.ContractCallException;
import org.abeyj.tx.response.PollingTransactionReceiptProcessor;
import org.abeyj.tx.response.TransactionReceiptProcessor;

import java.io.IOException;
import java.math.BigInteger;

import static org.abeyj.protocol.core.JsonRpc2_0Abeyj.DEFAULT_BLOCK_TIME;

/**
 * Transaction manager abstraction for executing transactions with Ethereum client via various
 * mechanisms.
 */
public abstract class TransactionManager {

    public static final int DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH = 40;
    public static final long DEFAULT_POLLING_FREQUENCY = DEFAULT_BLOCK_TIME;
    public static final String REVERT_ERR_STR =
            "Contract Call has been reverted by the EVM with the reason: '%s'.";

    private final TransactionReceiptProcessor transactionReceiptProcessor;
    private final String fromAddress;

    protected TransactionManager(
            TransactionReceiptProcessor transactionReceiptProcessor, String fromAddress) {
        this.transactionReceiptProcessor = transactionReceiptProcessor;
        this.fromAddress = fromAddress;
    }

    protected TransactionManager(Abeyj abeyj, String fromAddress) {
        this(
                new PollingTransactionReceiptProcessor(
                        abeyj, DEFAULT_POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH),
                fromAddress);
    }

    protected TransactionManager(
            Abeyj abeyj, int attempts, long sleepDuration, String fromAddress) {
        this(new PollingTransactionReceiptProcessor(abeyj, sleepDuration, attempts), fromAddress);
    }

    protected TransactionReceipt executeTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value)
            throws IOException, TransactionException {

        return executeTransaction(gasPrice, gasLimit, to, data, value, false);
    }

    protected TransactionReceipt executeTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException, TransactionException {

        AbeySendTransaction abeySendTransaction =
                sendTransaction(gasPrice, gasLimit, to, data, value, constructor);
        return processResponse(abeySendTransaction);
    }

    protected TransactionReceipt executeTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value)
            throws IOException, TransactionException {

        return executeTransactionEIP1559(gasPremium, feeCap, gasLimit, to, data, value, false);
    }

    protected TransactionReceipt executeTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException, TransactionException {

        AbeySendTransaction abeySendTransaction =
                sendTransactionEIP1559(gasPremium, feeCap, gasLimit, to, data, value, constructor);
        return processResponse(abeySendTransaction);
    }

    public AbeySendTransaction sendTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value)
            throws IOException {
        return sendTransaction(gasPrice, gasLimit, to, data, value, false);
    }

    public AbeySendTransaction sendTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value)
            throws IOException {
        return sendTransactionEIP1559(gasPremium, feeCap, gasLimit, to, data, value, false);
    }

    public abstract AbeySendTransaction sendTransaction(
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException;

    public abstract AbeySendTransaction sendTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException;

    public abstract String sendCall(
            String to, String data, DefaultBlockParameter defaultBlockParameter) throws IOException;

    public abstract AbeyGetCode getCode(
            String contractAddress, DefaultBlockParameter defaultBlockParameter) throws IOException;

    public String getFromAddress() {
        return fromAddress;
    }

    private TransactionReceipt processResponse(AbeySendTransaction transactionResponse)
            throws IOException, TransactionException {
        if (transactionResponse.hasError()) {
            throw new RuntimeException(
                    "Error processing transaction request: "
                            + transactionResponse.getError().getMessage());
        }

        String transactionHash = transactionResponse.getTransactionHash();

        return transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
    }

    static void assertCallNotReverted(AbeyCall abeyCall) {
        if (abeyCall.isReverted()) {
            throw new ContractCallException(
                    String.format(REVERT_ERR_STR, abeyCall.getRevertReason()));
        }
    }
}
