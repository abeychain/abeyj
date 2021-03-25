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

import org.abeyj.crypto.Credentials;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.methods.response.BooleanResponse;
import org.abeyj.protocol.besu.response.BesuEthAccountsMapResponse;
import org.abeyj.protocol.besu.response.BesuFullDebugTraceResponse;
import org.abeyj.protocol.besu.response.BesuSignerMetrics;
import org.abeyj.protocol.besu.response.privacy.*;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.*;
import org.abeyj.protocol.eea.Eea;
import org.abeyj.protocol.exceptions.TransactionException;
import org.abeyj.utils.Base64String;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public interface Besu extends Eea, BesuRx {

    /**
     * Construct a new Besu instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP
     * @return new Besu instance
     */
    static Besu build(AbeyjService abeyjService) {
        return new JsonRpc2_0Besu(abeyjService);
    }

    /**
     * Construct a new Besu instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP
     * @param pollingInterval polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks. <strong>You are
     *     responsible for terminating this thread pool</strong>
     * @return new Besu instance
     */
    static Besu build(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Besu(abeyjService, pollingInterval, scheduledExecutorService);
    }

    Request<?, MinerStartResponse> minerStart();

    Request<?, BooleanResponse> minerStop();

    Request<?, BooleanResponse> cliqueDiscard(String address);

    Request<?, AbeyAccounts> cliqueGetSigners(DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyAccounts> cliqueGetSignersAtHash(String blockHash);

    Request<?, BooleanResponse> cliquePropose(String address, Boolean signerAddition);

    Request<?, BesuEthAccountsMapResponse> cliqueProposals();

    Request<?, BesuFullDebugTraceResponse> debugTraceTransaction(
            String transactionHash, Map<String, Boolean> options);

    Request<?, BooleanResponse> ibftDiscardValidatorVote(String address);

    Request<?, BesuEthAccountsMapResponse> ibftGetPendingVotes();

    Request<?, BesuSignerMetrics> ibftGetSignerMetrics();

    Request<?, AbeyAccounts> ibftGetValidatorsByBlockNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyAccounts> ibftGetValidatorsByBlockHash(String blockHash);

    Request<?, BooleanResponse> ibftProposeValidatorVote(String address, Boolean validatorAddition);

    Request<?, AbeyGetTransactionCount> privGetTransactionCount(
            final String address, final Base64String privacyGroupId);

    Request<?, PrivGetPrivateTransaction> privGetPrivateTransaction(final String transactionHash);

    Request<?, PrivateEnclaveKey> privDistributeRawTransaction(final String signedTransactionData);

    Request<?, PrivGetPrivacyPrecompileAddress> privGetPrivacyPrecompileAddress();

    Request<?, PrivCreatePrivacyGroup> privCreatePrivacyGroup(
            final List<Base64String> addresses, final String name, final String description);

    Request<?, AbeySendTransaction> privOnChainSetGroupLockState(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Boolean lock)
            throws IOException;

    Request<?, AbeySendTransaction> privOnChainAddToPrivacyGroup(
            Base64String privacyGroupId,
            Credentials credentials,
            Base64String enclaveKey,
            List<Base64String> participants)
            throws IOException, TransactionException;

    Request<?, AbeySendTransaction> privOnChainCreatePrivacyGroup(
            Base64String privacyGroupId,
            Credentials credentials,
            Base64String enclaveKey,
            List<Base64String> participants)
            throws IOException;

    Request<?, AbeySendTransaction> privOnChainRemoveFromPrivacyGroup(
            final Base64String privacyGroupId,
            final Credentials credentials,
            final Base64String enclaveKey,
            final Base64String participant)
            throws IOException;

    Request<?, PrivFindPrivacyGroup> privOnChainFindPrivacyGroup(
            final List<Base64String> addresses);

    Request<?, PrivFindPrivacyGroup> privFindPrivacyGroup(final List<Base64String> addresses);

    Request<?, BooleanResponse> privDeletePrivacyGroup(final Base64String privacyGroupId);

    Request<?, PrivGetTransactionReceipt> privGetTransactionReceipt(final String transactionHash);

    Request<?, AbeyGetCode> privGetCode(
            String privacyGroupId, String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyCall> privCall(
            String privacyGroupId,
            org.abeyj.protocol.core.methods.request.Transaction transaction,
            DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyLog> privGetLogs(
            String privacyGroupId, org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter);

    Request<?, AbeyFilter> privNewFilter(
            String privacyGroupId, org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter);

    Request<?, AbeyUninstallFilter> privUninstallFilter(String privacyGroupId, String filterId);

    Request<?, AbeyLog> privGetFilterChanges(String privacyGroupId, String filterId);

    Request<?, AbeyLog> privGetFilterLogs(String privacyGroupId, String filterId);
}
