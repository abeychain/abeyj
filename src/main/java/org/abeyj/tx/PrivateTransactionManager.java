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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.abeyj.crypto.Credentials;
import org.abeyj.protocol.besu.Besu;
import org.abeyj.protocol.besu.response.privacy.PrivateEnclaveKey;
import org.abeyj.protocol.besu.response.privacy.PrivateTransactionReceipt;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.methods.response.AbeyGetCode;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;
import org.abeyj.protocol.core.methods.response.TransactionReceipt;
import org.abeyj.protocol.eea.crypto.PrivateTransactionEncoder;
import org.abeyj.protocol.eea.crypto.RawPrivateTransaction;
import org.abeyj.protocol.exceptions.TransactionException;
import org.abeyj.tx.exceptions.ContractCallException;
import org.abeyj.tx.gas.BesuPrivacyGasProvider;
import org.abeyj.tx.response.PollingPrivateTransactionReceiptProcessor;
import org.abeyj.tx.response.PrivateTransactionReceiptProcessor;
import org.abeyj.utils.Base64String;
import org.abeyj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static org.abeyj.utils.Restriction.RESTRICTED;
import static org.abeyj.utils.RevertReasonExtractor.extractRevertReason;

public abstract class PrivateTransactionManager extends TransactionManager {
    private static final Logger log = LoggerFactory.getLogger(PrivateTransactionManager.class);

    private final PrivateTransactionReceiptProcessor transactionReceiptProcessor;

    private final Besu besu;
    private final BesuPrivacyGasProvider gasProvider;
    private final Credentials credentials;
    private final long chainId;
    private final Base64String privateFrom;

    protected PrivateTransactionManager(
            final Besu besu,
            final BesuPrivacyGasProvider gasProvider,
            final Credentials credentials,
            final long chainId,
            final Base64String privateFrom,
            final PrivateTransactionReceiptProcessor transactionReceiptProcessor) {
        super(transactionReceiptProcessor, credentials.getAddress());
        this.besu = besu;
        this.gasProvider = gasProvider;
        this.credentials = credentials;
        this.chainId = chainId;
        this.privateFrom = privateFrom;
        this.transactionReceiptProcessor = transactionReceiptProcessor;
    }

    protected PrivateTransactionManager(
            final Besu besu,
            final BesuPrivacyGasProvider gasProvider,
            final Credentials credentials,
            final long chainId,
            final Base64String privateFrom,
            final int attempts,
            final int sleepDuration) {
        this(
                besu,
                gasProvider,
                credentials,
                chainId,
                privateFrom,
                new PollingPrivateTransactionReceiptProcessor(besu, sleepDuration, attempts));
    }

    protected PrivateTransactionManager(
            final Besu besu,
            final BesuPrivacyGasProvider gasProvider,
            final Credentials credentials,
            final long chainId,
            final Base64String privateFrom) {
        this(
                besu,
                gasProvider,
                credentials,
                chainId,
                privateFrom,
                new PollingPrivateTransactionReceiptProcessor(
                        besu, DEFAULT_POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH));
    }

    @Override
    protected TransactionReceipt executeTransaction(
            BigInteger gasPrice, BigInteger gasLimit, String to, String data, BigInteger value)
            throws IOException, TransactionException {

        AbeySendTransaction abeySendTransaction =
                sendTransaction(gasPrice, gasLimit, to, data, value);
        return processResponse(abeySendTransaction);
    }

    public Base64String getPrivateFrom() {
        return privateFrom;
    }

    protected abstract Base64String getPrivacyGroupId();

    protected abstract Object privacyGroupIdOrPrivateFor();

    @SuppressWarnings("unchecked")
    @Override
    public AbeySendTransaction sendTransaction(
            final BigInteger gasPrice,
            final BigInteger gasLimit,
            final String to,
            final String data,
            final BigInteger value,
            boolean constructor)
            throws IOException {

        final BigInteger nonce =
                besu.privGetTransactionCount(credentials.getAddress(), getPrivacyGroupId())
                        .send()
                        .getTransactionCount();

        final Object privacyGroupIdOrPrivateFor = privacyGroupIdOrPrivateFor();

        final RawPrivateTransaction transaction;
        if (privacyGroupIdOrPrivateFor instanceof Base64String) {
            transaction =
                    RawPrivateTransaction.createTransaction(
                            nonce,
                            gasPrice,
                            gasLimit,
                            to,
                            data,
                            privateFrom,
                            (Base64String) privacyGroupIdOrPrivateFor,
                            RESTRICTED);
        } else {
            transaction =
                    RawPrivateTransaction.createTransaction(
                            nonce,
                            gasPrice,
                            gasLimit,
                            to,
                            data,
                            privateFrom,
                            (List<Base64String>) privacyGroupIdOrPrivateFor,
                            RESTRICTED);
        }

        return signAndSend(transaction);
    }

    public AbeySendTransaction sendTransactionEIP1559(
            BigInteger gasPremium,
            BigInteger feeCap,
            BigInteger gasLimit,
            String to,
            String data,
            BigInteger value,
            boolean constructor)
            throws IOException {
        final BigInteger nonce =
                besu.privGetTransactionCount(credentials.getAddress(), getPrivacyGroupId())
                        .send()
                        .getTransactionCount();

        final Object privacyGroupIdOrPrivateFor = privacyGroupIdOrPrivateFor();

        final RawPrivateTransaction transaction;
        if (privacyGroupIdOrPrivateFor instanceof Base64String) {
            transaction =
                    RawPrivateTransaction.createTransactionEIP1559(
                            nonce,
                            gasPremium,
                            feeCap,
                            gasLimit,
                            to,
                            data,
                            privateFrom,
                            (Base64String) privacyGroupIdOrPrivateFor,
                            RESTRICTED);
        } else {
            transaction =
                    RawPrivateTransaction.createTransactionEIP1559(
                            nonce,
                            gasPremium,
                            feeCap,
                            gasLimit,
                            to,
                            data,
                            privateFrom,
                            (List<Base64String>) privacyGroupIdOrPrivateFor,
                            RESTRICTED);
        }

        return signAndSend(transaction);
    }

    @Override
    public String sendCall(
            final String to, final String data, final DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        try {
            AbeySendTransaction est =
                    sendTransaction(
                            gasProvider.getGasPrice(),
                            gasProvider.getGasLimit(),
                            to,
                            data,
                            BigInteger.ZERO);
            final TransactionReceipt ptr = processResponse(est);

            if (!ptr.isStatusOK()) {
                throw new ContractCallException(
                        String.format(REVERT_ERR_STR, extractRevertReason(ptr, data, besu, false)));
            }
            return ((PrivateTransactionReceipt) ptr).getOutput();
        } catch (TransactionException e) {
            log.error("Failed to execute call", e);
            return null;
        }
    }

    private TransactionReceipt processResponse(final AbeySendTransaction transactionResponse)
            throws IOException, TransactionException {
        if (transactionResponse.hasError()) {
            throw new RuntimeException(
                    "Error processing transaction request: "
                            + transactionResponse.getError().getMessage());
        }

        final String transactionHash = transactionResponse.getTransactionHash();

        return transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
    }

    @Override
    public AbeyGetCode getCode(
            final String contractAddress, final DefaultBlockParameter defaultBlockParameter)
            throws IOException {
        return this.besu
                .privGetCode(
                        this.getPrivacyGroupId().toString(), contractAddress, defaultBlockParameter)
                .send();
    }

    public String sign(RawPrivateTransaction rawTransaction) {

        byte[] signedMessage;

        if (chainId > ChainIdLong.NONE) {
            signedMessage =
                    PrivateTransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = PrivateTransactionEncoder.signMessage(rawTransaction, credentials);
        }

        return Numeric.toHexString(signedMessage);
    }

    public AbeySendTransaction signAndSend(RawPrivateTransaction rawTransaction) throws IOException {
        String hexValue = sign(rawTransaction);
        return this.besu.eeaSendRawTransaction(hexValue).send();
    }

    public PrivateEnclaveKey signAndDistribute(RawPrivateTransaction rawTransaction)
            throws IOException {
        String hexValue = sign(rawTransaction);
        return this.besu.privDistributeRawTransaction(hexValue).send();
    }
}
