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
package org.abeyj.protocol.core;

import io.reactivex.Flowable;
import org.abeyj.address.AddressHelper;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.core.methods.request.ShhFilter;
import org.abeyj.protocol.core.methods.request.ShhPost;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.*;
import org.abeyj.protocol.core.methods.response.admin.AdminDataDir;
import org.abeyj.protocol.core.methods.response.admin.AdminNodeInfo;
import org.abeyj.protocol.core.methods.response.admin.AdminPeers;
import org.abeyj.protocol.rx.JsonRpc2_0Rx;
import org.abeyj.protocol.websocket.events.LogNotification;
import org.abeyj.protocol.websocket.events.NewHeadsNotification;
import org.abeyj.utils.Async;
import org.abeyj.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/** JSON-RPC 2.0 factory implementation. */
public class JsonRpc2_0Abeyj implements Abeyj {

    public static final int DEFAULT_BLOCK_TIME = 15 * 1000;

    protected final AbeyjService abeyjService;
    private final JsonRpc2_0Rx abeyjRx;
    private final long blockTime;
    private final ScheduledExecutorService scheduledExecutorService;

    public JsonRpc2_0Abeyj(AbeyjService abeyjService) {
        this(abeyjService, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpc2_0Abeyj(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        this.abeyjService = abeyjService;
        this.abeyjRx = new JsonRpc2_0Rx(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public Request<?, Web3ClientVersion> web3ClientVersion() {
        return new Request<>(
                "web3_clientVersion",
                Collections.<String>emptyList(),
                abeyjService,
                Web3ClientVersion.class);
    }

    @Override
    public Request<?, Web3Sha3> web3Sha3(String data) {
        return new Request<>("web3_sha3", Arrays.asList(data), abeyjService, Web3Sha3.class);
    }

    @Override
    public Request<?, NetVersion> netVersion() {
        return new Request<>(
                "net_version", Collections.<String>emptyList(), abeyjService, NetVersion.class);
    }

    @Override
    public Request<?, NetListening> netListening() {
        return new Request<>(
                "net_listening", Collections.<String>emptyList(), abeyjService, NetListening.class);
    }

    @Override
    public Request<?, NetPeerCount> netPeerCount() {
        return new Request<>(
                "net_peerCount", Collections.<String>emptyList(), abeyjService, NetPeerCount.class);
    }

    @Override
    public Request<?, AdminNodeInfo> adminNodeInfo() {
        return new Request<>(
                "admin_nodeInfo", Collections.emptyList(), abeyjService, AdminNodeInfo.class);
    }

    @Override
    public Request<?, AdminPeers> adminPeers() {
        return new Request<>(
                "admin_peers", Collections.emptyList(), abeyjService, AdminPeers.class);
    }

    @Override
    public Request<?, BooleanResponse> adminAddPeer(String url) {
        return new Request<>(
                "admin_addPeer", Arrays.asList(url), abeyjService, BooleanResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> adminRemovePeer(String url) {
        return new Request<>(
                "admin_removePeer", Arrays.asList(url), abeyjService, BooleanResponse.class);
    }

    @Override
    public Request<?, AdminDataDir> adminDataDir() {
        return new Request<>(
                "admin_datadir", Collections.emptyList(), abeyjService, AdminDataDir.class);
    }

    @Override
    public Request<?, AbeyProtocolVersion> abeyProtocolVersion() {
        return new Request<>(
                "abey_protocolVersion",
                Collections.<String>emptyList(),
                abeyjService,
                AbeyProtocolVersion.class);
    }

    @Override
    public Request<?, AbeyChainId> abeyChainId() {
        return new Request<>(
                "abey_chainId", Collections.<String>emptyList(), abeyjService, AbeyChainId.class);
    }

    @Override
    public Request<?, AbeyCoinbase> abeyCoinbase() {
        return new Request<>(
                "abey_coinbase", Collections.<String>emptyList(), abeyjService, AbeyCoinbase.class);
    }

    @Override
    public Request<?, AbeySyncing> abeySyncing() {
        return new Request<>(
                "abey_syncing", Collections.<String>emptyList(), abeyjService, AbeySyncing.class);
    }

    @Override
    public Request<?, AbeyMining> abeyMining() {
        return new Request<>(
                "abey_mining", Collections.<String>emptyList(), abeyjService, AbeyMining.class);
    }

    @Override
    public Request<?, AbeyHashrate> abeyHashrate() {
        return new Request<>(
                "abey_hashrate", Collections.<String>emptyList(), abeyjService, AbeyHashrate.class);
    }

    @Override
    public Request<?, AbeyGasPrice> abeyGasPrice() {
        return new Request<>(
                "abey_gasPrice", Collections.<String>emptyList(), abeyjService, AbeyGasPrice.class);
    }

    @Override
    public Request<?, AbeyAccounts> abeyAccounts() {
        return new Request<>(
                "abey_accounts", Collections.<String>emptyList(), abeyjService, AbeyAccounts.class);
    }

    @Override
    public Request<?, AbeyBlockNumber> abeyBlockNumber() {
        return new Request<>(
                "abey_blockNumber",
                Collections.<String>emptyList(),
                abeyjService,
                AbeyBlockNumber.class);
    }

    @Override
    public Request<?, AbeyGetBalance> abeyGetBalance(
            String address, DefaultBlockParameter defaultBlockParameter) {
        address = AddressHelper.changeAddressToHex(address);
        return new Request<>(
                "abey_getBalance",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetBalance.class);
    }

    @Override
    public Request<?, AbeyGetStorageAt> abeyGetStorageAt(
            String address, BigInteger position, DefaultBlockParameter defaultBlockParameter) {
        address = AddressHelper.changeAddressToHex(address);
        return new Request<>(
                "abey_getStorageAt",
                Arrays.asList(
                        address,
                        Numeric.encodeQuantity(position),
                        defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetStorageAt.class);
    }

    @Override
    public Request<?, AbeyGetTransactionCount> abeyGetTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter) {
        address = AddressHelper.changeAddressToHex(address);
        return new Request<>(
                "abey_getTransactionCount",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetTransactionCount.class);
    }

    @Override
    public Request<?, AbeyGetBlockTransactionCountByHash> abeyGetBlockTransactionCountByHash(
            String blockHash) {
        return new Request<>(
                "abey_getBlockTransactionCountByHash",
                Arrays.asList(blockHash),
                abeyjService,
                AbeyGetBlockTransactionCountByHash.class);
    }

    @Override
    public Request<?, AbeyGetBlockTransactionCountByNumber> abeyGetBlockTransactionCountByNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "abey_getBlockTransactionCountByNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetBlockTransactionCountByNumber.class);
    }

    @Override
    public Request<?, AbeyGetUncleCountByBlockHash> abeyGetUncleCountByBlockHash(String blockHash) {
        return new Request<>(
                "abey_getUncleCountByBlockHash",
                Arrays.asList(blockHash),
                abeyjService,
                AbeyGetUncleCountByBlockHash.class);
    }

    @Override
    public Request<?, AbeyGetUncleCountByBlockNumber> abeyGetUncleCountByBlockNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "abey_getUncleCountByBlockNumber",
                Arrays.asList(defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetUncleCountByBlockNumber.class);
    }

    @Override
    public Request<?, AbeyGetCode> abeyGetCode(
            String address, DefaultBlockParameter defaultBlockParameter) {
        address = AddressHelper.changeAddressToHex(address);
        return new Request<>(
                "abey_getCode",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                abeyjService,
                AbeyGetCode.class);
    }

    @Override
    public Request<?, AbeySign> abeySign(String address, String sha3HashOfDataToSign) {
        address = AddressHelper.changeAddressToHex(address);
        return new Request<>(
                "abey_sign",
                Arrays.asList(address, sha3HashOfDataToSign),
                abeyjService,
                AbeySign.class);
    }

    @Override
    public Request<?, AbeySendTransaction>
            abeySendTransaction(Transaction transaction) {
        return new Request<>(
                "abey_sendTransaction",
                Arrays.asList(transaction),
                abeyjService,
                AbeySendTransaction.class);
    }

    @Override
    public Request<?, AbeySendTransaction>
            abeySendRawTransaction(String signedTransactionData) {
        return new Request<>(
                "abey_sendRawTransaction",
                Arrays.asList(signedTransactionData),
                abeyjService,
                AbeySendTransaction.class);
    }

    @Override
    public Request<?, AbeyCall> abeyCall(
            Transaction transaction, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "abey_call",
                Arrays.asList(transaction, defaultBlockParameter),
                abeyjService,
                AbeyCall.class);
    }

    @Override
    public Request<?, AbeyEstimateGas> abeyEstimateGas(Transaction transaction) {
        return new Request<>(
                "abey_estimateGas", Arrays.asList(transaction), abeyjService, AbeyEstimateGas.class);
    }

    @Override
    public Request<?, AbeyBlock> abeyGetBlockByHash(
            String blockHash, boolean returnFullTransactionObjects) {
        return new Request<>(
                "abey_getBlockByHash",
                Arrays.asList(blockHash, returnFullTransactionObjects),
                abeyjService,
                AbeyBlock.class);
    }

    @Override
    public Request<?, AbeyBlock> abeyGetBlockByNumber(
            DefaultBlockParameter defaultBlockParameter, boolean returnFullTransactionObjects) {
        return new Request<>(
                "abey_getBlockByNumber",
                Arrays.asList(defaultBlockParameter.getValue(), returnFullTransactionObjects),
                abeyjService,
                AbeyBlock.class);
    }

    @Override
    public Request<?, AbeyTransaction> abeyGetTransactionByHash(String transactionHash) {
        return new Request<>(
                "abey_getTransactionByHash",
                Arrays.asList(transactionHash),
                abeyjService,
                AbeyTransaction.class);
    }

    @Override
    public Request<?, AbeyTransaction> abeyGetTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex) {
        return new Request<>(
                "abey_getTransactionByBlockHashAndIndex",
                Arrays.asList(blockHash, Numeric.encodeQuantity(transactionIndex)),
                abeyjService,
                AbeyTransaction.class);
    }

    @Override
    public Request<?, AbeyTransaction> abeyGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex) {
        return new Request<>(
                "abey_getTransactionByBlockNumberAndIndex",
                Arrays.asList(
                        defaultBlockParameter.getValue(), Numeric.encodeQuantity(transactionIndex)),
                abeyjService,
                AbeyTransaction.class);
    }

    @Override
    public Request<?, AbeyGetTransactionReceipt> abeyGetTransactionReceipt(String transactionHash) {
        return new Request<>(
                "abey_getTransactionReceipt",
                Arrays.asList(transactionHash),
                abeyjService,
                AbeyGetTransactionReceipt.class);
    }

    @Override
    public Request<?, AbeyBlock> abeyGetUncleByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex) {
        return new Request<>(
                "abey_getUncleByBlockHashAndIndex",
                Arrays.asList(blockHash, Numeric.encodeQuantity(transactionIndex)),
                abeyjService,
                AbeyBlock.class);
    }

    @Override
    public Request<?, AbeyBlock> abeyGetUncleByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger uncleIndex) {
        return new Request<>(
                "abey_getUncleByBlockNumberAndIndex",
                Arrays.asList(defaultBlockParameter.getValue(), Numeric.encodeQuantity(uncleIndex)),
                abeyjService,
                AbeyBlock.class);
    }

    @Override
    public Request<?, AbeyGetCompilers> abeyGetCompilers() {
        return new Request<>(
                "abey_getCompilers",
                Collections.<String>emptyList(),
                abeyjService,
                AbeyGetCompilers.class);
    }

    @Override
    public Request<?, AbeyCompileLLL> abeyCompileLLL(String sourceCode) {
        return new Request<>(
                "abey_compileLLL", Arrays.asList(sourceCode), abeyjService, AbeyCompileLLL.class);
    }

    @Override
    public Request<?, AbeyCompileSolidity> abeyCompileSolidity(String sourceCode) {
        return new Request<>(
                "abey_compileSolidity",
                Arrays.asList(sourceCode),
                abeyjService,
                AbeyCompileSolidity.class);
    }

    @Override
    public Request<?, AbeyCompileSerpent> abeyCompileSerpent(String sourceCode) {
        return new Request<>(
                "abey_compileSerpent",
                Arrays.asList(sourceCode),
                abeyjService,
                AbeyCompileSerpent.class);
    }

    @Override
    public Request<?, AbeyFilter> abeyNewFilter(
            org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter) {
        return new Request<>(
                "abey_newFilter", Arrays.asList(abeyFilter), abeyjService, AbeyFilter.class);
    }

    @Override
    public Request<?, AbeyFilter> abeyNewBlockFilter() {
        return new Request<>(
                "abey_newBlockFilter",
                Collections.<String>emptyList(),
                abeyjService,
                AbeyFilter.class);
    }

    @Override
    public Request<?, AbeyFilter> abeyNewPendingTransactionFilter() {
        return new Request<>(
                "abey_newPendingTransactionFilter",
                Collections.<String>emptyList(),
                abeyjService,
                AbeyFilter.class);
    }

    @Override
    public Request<?, AbeyUninstallFilter> abeyUninstallFilter(BigInteger filterId) {
        return new Request<>(
                "abey_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                AbeyUninstallFilter.class);
    }

    @Override
    public Request<?, AbeyLog> abeyGetFilterChanges(BigInteger filterId) {
        return new Request<>(
                "abey_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                AbeyLog.class);
    }

    @Override
    public Request<?, AbeyLog> abeyGetFilterLogs(BigInteger filterId) {
        return new Request<>(
                "abey_getFilterLogs",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                AbeyLog.class);
    }

    @Override
    public Request<?, AbeyLog> abeyGetLogs(
            org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter) {
        return new Request<>("abey_getLogs", Arrays.asList(abeyFilter), abeyjService, AbeyLog.class);
    }

    @Override
    public Request<?, AbeyGetWork> abeyGetWork() {
        return new Request<>(
                "abey_getWork", Collections.<String>emptyList(), abeyjService, AbeyGetWork.class);
    }

    @Override
    public Request<?, AbeySubmitWork> abeySubmitWork(
            String nonce, String headerPowHash, String mixDigest) {
        return new Request<>(
                "abey_submitWork",
                Arrays.asList(nonce, headerPowHash, mixDigest),
                abeyjService,
                AbeySubmitWork.class);
    }

    @Override
    public Request<?, AbeySubmitHashrate> abeySubmitHashrate(String hashrate, String clientId) {
        return new Request<>(
                "abey_submitHashrate",
                Arrays.asList(hashrate, clientId),
                abeyjService,
                AbeySubmitHashrate.class);
    }

    @Override
    public Request<?, DbPutString> dbPutString(
            String databaseName, String keyName, String stringToStore) {
        return new Request<>(
                "db_putString",
                Arrays.asList(databaseName, keyName, stringToStore),
                abeyjService,
                DbPutString.class);
    }

    @Override
    public Request<?, DbGetString> dbGetString(String databaseName, String keyName) {
        return new Request<>(
                "db_getString",
                Arrays.asList(databaseName, keyName),
                abeyjService,
                DbGetString.class);
    }

    @Override
    public Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore) {
        return new Request<>(
                "db_putHex",
                Arrays.asList(databaseName, keyName, dataToStore),
                abeyjService,
                DbPutHex.class);
    }

    @Override
    public Request<?, DbGetHex> dbGetHex(String databaseName, String keyName) {
        return new Request<>(
                "db_getHex", Arrays.asList(databaseName, keyName), abeyjService, DbGetHex.class);
    }

    @Override
    public Request<?, org.abeyj.protocol.core.methods.response.ShhPost> shhPost(ShhPost shhPost) {
        return new Request<>(
                "shh_post",
                Arrays.asList(shhPost),
                abeyjService,
                org.abeyj.protocol.core.methods.response.ShhPost.class);
    }

    @Override
    public Request<?, ShhVersion> shhVersion() {
        return new Request<>(
                "shh_version", Collections.<String>emptyList(), abeyjService, ShhVersion.class);
    }

    @Override
    public Request<?, ShhNewIdentity> shhNewIdentity() {
        return new Request<>(
                "shh_newIdentity",
                Collections.<String>emptyList(),
                abeyjService,
                ShhNewIdentity.class);
    }

    @Override
    public Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress) {
        return new Request<>(
                "shh_hasIdentity",
                Arrays.asList(identityAddress),
                abeyjService,
                ShhHasIdentity.class);
    }

    @Override
    public Request<?, ShhNewGroup> shhNewGroup() {
        return new Request<>(
                "shh_newGroup", Collections.<String>emptyList(), abeyjService, ShhNewGroup.class);
    }

    @Override
    public Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress) {
        return new Request<>(
                "shh_addToGroup",
                Arrays.asList(identityAddress),
                abeyjService,
                ShhAddToGroup.class);
    }

    @Override
    public Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter) {
        return new Request<>(
                "shh_newFilter", Arrays.asList(shhFilter), abeyjService, ShhNewFilter.class);
    }

    @Override
    public Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId) {
        return new Request<>(
                "shh_uninstallFilter",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                ShhUninstallFilter.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId) {
        return new Request<>(
                "shh_getFilterChanges",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                ShhMessages.class);
    }

    @Override
    public Request<?, ShhMessages> shhGetMessages(BigInteger filterId) {
        return new Request<>(
                "shh_getMessages",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                abeyjService,
                ShhMessages.class);
    }

    @Override
    public Request<?, TxPoolStatus> txPoolStatus() {
        return new Request<>(
                "txpool_status", Collections.<String>emptyList(), abeyjService, TxPoolStatus.class);
    }

    @Override
    public Flowable<NewHeadsNotification> newHeadsNotifications() {
        return abeyjService.subscribe(
                new Request<>(
                        "abey_subscribe",
                        Collections.singletonList("newHeads"),
                        abeyjService,
                        AbeySubscribe.class),
                "abey_unsubscribe",
                NewHeadsNotification.class);
    }

    @Override
    public Flowable<LogNotification> logsNotifications(
            List<String> addresses, List<String> topics) {

        Map<String, Object> params = createLogsParams(addresses, topics);

        return abeyjService.subscribe(
                new Request<>(
                        "abey_subscribe",
                        Arrays.asList("logs", params),
                        abeyjService,
                        AbeySubscribe.class),
                "abey_unsubscribe",
                LogNotification.class);
    }

    private Map<String, Object> createLogsParams(List<String> addresses, List<String> topics) {
        Map<String, Object> params = new HashMap<>();
        if (!addresses.isEmpty()) {
            params.put("address", addresses);
        }
        if (!topics.isEmpty()) {
            params.put("topics", topics);
        }
        return params;
    }

    @Override
    public Flowable<String> ethBlockHashFlowable() {
        return abeyjRx.ethBlockHashFlowable(blockTime);
    }

    @Override
    public Flowable<String> ethPendingTransactionHashFlowable() {
        return abeyjRx.ethPendingTransactionHashFlowable(blockTime);
    }

    @Override
    public Flowable<Log> ethLogFlowable(
            org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter) {
        return abeyjRx.ethLogFlowable(abeyFilter, blockTime);
    }

    @Override
    public Flowable<org.abeyj.protocol.core.methods.response.Transaction> transactionFlowable() {
        return abeyjRx.transactionFlowable(blockTime);
    }

    @Override
    public Flowable<org.abeyj.protocol.core.methods.response.Transaction>
            pendingTransactionFlowable() {
        return abeyjRx.pendingTransactionFlowable(blockTime);
    }

    @Override
    public Flowable<AbeyBlock> blockFlowable(boolean fullTransactionObjects) {
        return abeyjRx.blockFlowable(fullTransactionObjects, blockTime);
    }

    @Override
    public Flowable<AbeyBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock,
            boolean fullTransactionObjects) {
        return abeyjRx.replayBlocksFlowable(startBlock, endBlock, fullTransactionObjects);
    }

    @Override
    public Flowable<AbeyBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock,
            boolean fullTransactionObjects,
            boolean ascending) {
        return abeyjRx.replayBlocksFlowable(
                startBlock, endBlock, fullTransactionObjects, ascending);
    }

    @Override
    public Flowable<AbeyBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock,
            boolean fullTransactionObjects,
            Flowable<AbeyBlock> onCompleteFlowable) {
        return abeyjRx.replayPastBlocksFlowable(
                startBlock, fullTransactionObjects, onCompleteFlowable);
    }

    @Override
    public Flowable<AbeyBlock> replayPastBlocksFlowable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return abeyjRx.replayPastBlocksFlowable(startBlock, fullTransactionObjects);
    }

    @Override
    public Flowable<org.abeyj.protocol.core.methods.response.Transaction>
            replayPastTransactionsFlowable(
                    DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        return abeyjRx.replayTransactionsFlowable(startBlock, endBlock);
    }

    @Override
    public Flowable<org.abeyj.protocol.core.methods.response.Transaction>
            replayPastTransactionsFlowable(DefaultBlockParameter startBlock) {
        return abeyjRx.replayPastTransactionsFlowable(startBlock);
    }

    @Override
    public Flowable<AbeyBlock> replayPastAndFutureBlocksFlowable(
            DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        return abeyjRx.replayPastAndFutureBlocksFlowable(
                startBlock, fullTransactionObjects, blockTime);
    }

    @Override
    public Flowable<org.abeyj.protocol.core.methods.response.Transaction>
            replayPastAndFutureTransactionsFlowable(DefaultBlockParameter startBlock) {
        return abeyjRx.replayPastAndFutureTransactionsFlowable(startBlock, blockTime);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            abeyjService.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close abeyj service", e);
        }
    }

    @Override
    public BatchRequest newBatch() {
        return new BatchRequest(abeyjService);
    }
}
