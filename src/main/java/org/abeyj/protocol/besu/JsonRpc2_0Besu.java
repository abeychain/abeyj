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
package org.abeyj.protocol.besu;

import io.reactivex.Flowable;
import org.abeyj.crypto.Credentials;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.methods.response.BooleanResponse;
import org.abeyj.protocol.besu.privacy.OnChainPrivacyTransactionBuilder;
import org.abeyj.protocol.besu.request.CreatePrivacyGroupRequest;
import org.abeyj.protocol.besu.response.BesuEthAccountsMapResponse;
import org.abeyj.protocol.besu.response.BesuFullDebugTraceResponse;
import org.abeyj.protocol.besu.response.BesuSignerMetrics;
import org.abeyj.protocol.besu.response.privacy.*;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.request.AbeyFilter;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.*;
import org.abeyj.protocol.eea.JsonRpc2_0Eea;
import org.abeyj.protocol.exceptions.TransactionException;
import org.abeyj.tx.response.PollingPrivateTransactionReceiptProcessor;
import org.abeyj.utils.Async;
import org.abeyj.utils.Base64String;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class JsonRpc2_0Besu extends JsonRpc2_0Eea implements Besu {

    private final JsonRpc2_0BesuRx besuRx;
    private final long blockTime;

    public JsonRpc2_0Besu(final AbeyjService abeyjService) {
        this(abeyjService, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpc2_0Besu(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        super(abeyjService, pollingInterval, scheduledExecutorService);
        this.besuRx = new JsonRpc2_0BesuRx(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
    }

    @Override
    public Request<?, MinerStartResponse> minerStart() {
        return new Request<>(
                "miner_start",
                Collections.<String>emptyList(),
                abeyjService,
                MinerStartResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> minerStop() {
        return new Request<>(
                "miner_stop", Collections.<String>emptyList(), abeyjService, BooleanResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> cliqueDiscard(String address) {
        return new Request<>(
                "clique_discard", Arrays.asList(address), abeyjService, BooleanResponse.class);
    }

    @Override
    public Request<?, AbeyAccounts> cliqueGetSigners(DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "clique_getSigners",
                Arrays.asList(defaultBlockParameter.getValue()),
                abeyjService,
                AbeyAccounts.class);
    }

    @Override
    public Request<?, AbeyAccounts> cliqueGetSignersAtHash(String blockHash) {
        return new Request<>(
                "clique_getSignersAtHash",
                Arrays.asList(blockHash),
                abeyjService,
                AbeyAccounts.class);
    }

    @Override
    public Request<?, BooleanResponse> cliquePropose(String address, Boolean signerAddition) {
        return new Request<>(
                "clique_propose",
                Arrays.asList(address, signerAddition),
                abeyjService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, BesuEthAccountsMapResponse> cliqueProposals() {
        return new Request<>(
                "clique_proposals",
                Collections.<String>emptyList(),
                abeyjService,
                BesuEthAccountsMapResponse.class);
    }

    @Override
    public Request<?, BesuFullDebugTraceResponse> debugTraceTransaction(
            String transactionHash, Map<String, Boolean> options) {
        return new Request<>(
                "debug_traceTransaction",
                Arrays.asList(transactionHash, options),
                abeyjService,
                BesuFullDebugTraceResponse.class);
    }

    public Request<?, BooleanResponse> ibftDiscardValidatorVote(String address) {
        return new Request<>(
                "ibft_discardValidatorVote",
                Arrays.asList(address),
                abeyjService,
                BooleanResponse.class);
    }

    public Request<?, BesuEthAccountsMapResponse> ibftGetPendingVotes() {
        return new Request<>(
                "ibft_getPendingVotes",
                Collections.<String>emptyList(),
                abeyjService,
                BesuEthAccountsMapResponse.class);
    }

    public Request<?, BesuSignerMetrics> ibftGetSignerMetrics() {
        return new Request<>(
                "ibft_getSignerMetrics",
                Collections.<String>emptyList(),
                abeyjService,
                BesuSignerMetrics.class);
    }

    public Request<?, AbeyAccounts> ibftGetValidatorsByBlockNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "ibft_getValidatorsByBlockNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                abeyjService,
                AbeyAccounts.class);
    }

    public Request<?, AbeyAccounts> ibftGetValidatorsByBlockHash(String blockHash) {
        return new Request<>(
                "ibft_getValidatorsByBlockHash",
                Arrays.asList(blockHash),
                abeyjService,
                AbeyAccounts.class);
    }

    public Request<?, BooleanResponse> ibftProposeValidatorVote(
            String address, Boolean validatorAddition) {
        return new Request<>(
                "ibft_proposeValidatorVote",
                Arrays.asList(address, validatorAddition),
                abeyjService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, AbeyGetTransactionCount> privGetTransactionCount(
            final String address, final Base64String privacyGroupId) {
        return new Request<>(
                "priv_getTransactionCount",
                Arrays.asList(address, privacyGroupId.toString()),
                abeyjService,
                AbeyGetTransactionCount.class);
    }

    @Override
    public Request<?, PrivGetPrivateTransaction> privGetPrivateTransaction(
            final String transactionHash) {
        return new Request<>(
                "priv_getPrivateTransaction",
                Collections.singletonList(transactionHash),
                abeyjService,
                PrivGetPrivateTransaction.class);
    }

    @Override
    public Request<?, PrivateEnclaveKey> privDistributeRawTransaction(
            final String signedTransactionData) {
        return new Request<>(
                "priv_distributeRawTransaction",
                Collections.singletonList(signedTransactionData),
                abeyjService,
                PrivateEnclaveKey.class);
    }

    @Override
    public Request<?, PrivGetPrivacyPrecompileAddress> privGetPrivacyPrecompileAddress() {
        return new Request<>(
                "priv_getPrivacyPrecompileAddress",
                Collections.emptyList(),
                abeyjService,
                PrivGetPrivacyPrecompileAddress.class);
    }

    @Override
    public Request<?, PrivCreatePrivacyGroup> privCreatePrivacyGroup(
            final List<Base64String> addresses, final String name, final String description) {
        requireNonNull(addresses);
        return new Request<>(
                "priv_createPrivacyGroup",
                Collections.singletonList(
                        new CreatePrivacyGroupRequest(addresses, name, description)),
                abeyjService,
                PrivCreatePrivacyGroup.class);
    }

    public Request<?, AbeySendTransaction> privOnChainSetGroupLockState(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Boolean lock)
            throws IOException {
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getTransactionCount();
        String lockContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedSingleParamFunction(
                        lock ? "lock" : "unlock");

        String lockPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        lockContractCall);

        return eeaSendRawTransaction(lockPrivacyGroupTransactionPayload);
    }

    @Override
    public Request<?, AbeySendTransaction> privOnChainAddToPrivacyGroup(
            Base64String privacyGroupId,
            Credentials credentials,
            Base64String enclaveKey,
            List<Base64String> participants)
            throws IOException, TransactionException {

        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getTransactionCount();
        String lockContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedSingleParamFunction("lock");

        String lockPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        lockContractCall);

        String lockTransactionHash =
                eeaSendRawTransaction(lockPrivacyGroupTransactionPayload)
                        .send()
                        .getTransactionHash();

        PollingPrivateTransactionReceiptProcessor processor =
                new PollingPrivateTransactionReceiptProcessor(this, 1000, 15);
        PrivateTransactionReceipt receipt =
                processor.waitForTransactionReceipt(lockTransactionHash);

        if (receipt.isStatusOK()) {
            return privOnChainCreatePrivacyGroup(
                    privacyGroupId, credentials, enclaveKey, participants);
        } else {
            throw new TransactionException(
                    "Lock transaction failed - the group may already be locked", receipt);
        }
    }

    @Override
    public Request<?, AbeySendTransaction> privOnChainCreatePrivacyGroup(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final List<Base64String> participants)
            throws IOException {
        List<byte[]> participantsAsBytes =
                participants.stream().map(Base64String::raw).collect(Collectors.toList());
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getTransactionCount();
        String addToContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedAddToGroupFunction(
                        enclaveKey, participantsAsBytes);

        String addToPrivacyGroupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        addToContractCall);

        return eeaSendRawTransaction(addToPrivacyGroupTransactionPayload);
    }

    @Override
    public Request<?, AbeySendTransaction> privOnChainRemoveFromPrivacyGroup(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Base64String participant)
            throws IOException {
        BigInteger transactionCount =
                privGetTransactionCount(credentials.getAddress(), privacyGroupId)
                        .send()
                        .getTransactionCount();
        String removeFromContractCall =
                OnChainPrivacyTransactionBuilder.getEncodedRemoveFromGroupFunction(
                        enclaveKey, participant.raw());

        String removeFromProupTransactionPayload =
                OnChainPrivacyTransactionBuilder.buildOnChainPrivateTransaction(
                        privacyGroupId,
                        credentials,
                        enclaveKey,
                        transactionCount,
                        removeFromContractCall);

        return eeaSendRawTransaction(removeFromProupTransactionPayload);
    }

    @Override
    public Request<?, PrivFindPrivacyGroup> privOnChainFindPrivacyGroup(
            final List<Base64String> addresses) {
        return new Request<>(
                "privx_findOnChainPrivacyGroup",
                Collections.singletonList(addresses),
                abeyjService,
                PrivFindPrivacyGroup.class);
    }

    @Override
    public Request<?, PrivFindPrivacyGroup> privFindPrivacyGroup(
            final List<Base64String> addresses) {
        return new Request<>(
                "priv_findPrivacyGroup",
                Collections.singletonList(addresses),
                abeyjService,
                PrivFindPrivacyGroup.class);
    }

    @Override
    public Request<?, BooleanResponse> privDeletePrivacyGroup(final Base64String privacyGroupId) {
        return new Request<>(
                "priv_deletePrivacyGroup",
                Collections.singletonList(privacyGroupId.toString()),
                abeyjService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, PrivGetTransactionReceipt> privGetTransactionReceipt(
            final String transactionHash) {
        return new Request<>(
                "priv_getTransactionReceipt",
                Collections.singletonList(transactionHash),
                abeyjService,
                PrivGetTransactionReceipt.class);
    }

    @Override
    public Request<?, AbeyGetCode> privGetCode(
            final String privacyGroupId,
            final String address,
            final DefaultBlockParameter defaultBlockParameter) {
        ArrayList<String> result =
                new ArrayList<>(
                        Arrays.asList(privacyGroupId, address, defaultBlockParameter.getValue()));
        return new Request<>("priv_getCode", result, abeyjService, AbeyGetCode.class);
    }

    @Override
    public Request<?, AbeyCall> privCall(
            String privacyGroupId,
            final Transaction transaction,
            final DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "priv_call",
                Arrays.asList(privacyGroupId, transaction, defaultBlockParameter),
                abeyjService,
                AbeyCall.class);
    }

    @Override
    public Request<?, AbeyLog> privGetLogs(
            final String privacyGroupId,
            final AbeyFilter abeyFilter) {
        return new Request<>(
                "priv_getLogs",
                Arrays.asList(privacyGroupId, abeyFilter),
                abeyjService,
                AbeyLog.class);
    }

    @Override
    public Request<?, org.abeyj.protocol.core.methods.response.AbeyFilter> privNewFilter(
            String privacyGroupId, AbeyFilter abeyFilter) {
        return new Request<>(
                "priv_newFilter",
                Arrays.asList(privacyGroupId, abeyFilter),
                abeyjService,
                org.abeyj.protocol.core.methods.response.AbeyFilter.class);
    }

    @Override
    public Request<?, AbeyUninstallFilter> privUninstallFilter(
            String privacyGroupId, String filterId) {
        return new Request<>(
                "priv_uninstallFilter",
                Arrays.asList(privacyGroupId, filterId),
                abeyjService,
                AbeyUninstallFilter.class);
    }

    @Override
    public Request<?, AbeyLog> privGetFilterChanges(String privacyGroupId, String filterId) {
        return new Request<>(
                "priv_getFilterChanges",
                Arrays.asList(privacyGroupId, filterId),
                abeyjService,
                AbeyLog.class);
    }

    @Override
    public Request<?, AbeyLog> privGetFilterLogs(String privacyGroupId, String filterId) {
        return new Request<>(
                "priv_getFilterLogs",
                Arrays.asList(privacyGroupId, filterId),
                abeyjService,
                AbeyLog.class);
    }

    @Override
    public Flowable<Log> privLogFlowable(final String privacyGroupId, final AbeyFilter abeyFilter) {
        return besuRx.privLogFlowable(privacyGroupId, abeyFilter, blockTime);
    }
}
