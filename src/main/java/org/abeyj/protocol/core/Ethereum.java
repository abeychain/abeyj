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

import org.abeyj.protocol.core.methods.request.ShhFilter;
import org.abeyj.protocol.core.methods.response.*;
import org.abeyj.protocol.core.methods.response.admin.AdminDataDir;
import org.abeyj.protocol.core.methods.response.admin.AdminNodeInfo;
import org.abeyj.protocol.core.methods.response.admin.AdminPeers;

import java.math.BigInteger;

/** Core Ethereum JSON-RPC API. */
public interface Ethereum {
    Request<?, Web3ClientVersion> web3ClientVersion();

    Request<?, Web3Sha3> web3Sha3(String data);

    Request<?, NetVersion> netVersion();

    Request<?, NetListening> netListening();

    Request<?, NetPeerCount> netPeerCount();

    Request<?, AdminNodeInfo> adminNodeInfo();

    Request<?, AdminPeers> adminPeers();

    Request<?, BooleanResponse> adminAddPeer(String url);

    Request<?, BooleanResponse> adminRemovePeer(String url);

    Request<?, AdminDataDir> adminDataDir();

    Request<?, AbeyProtocolVersion> abeyProtocolVersion();

    Request<?, AbeyChainId> abeyChainId();

    Request<?, AbeyCoinbase> abeyCoinbase();

    Request<?, AbeySyncing> abeySyncing();

    Request<?, AbeyMining> abeyMining();

    Request<?, AbeyHashrate> abeyHashrate();

    Request<?, AbeyGasPrice> abeyGasPrice();

    Request<?, AbeyAccounts> abeyAccounts();

    Request<?, AbeyBlockNumber> abeyBlockNumber();

    Request<?, AbeyGetBalance> abeyGetBalance(
            String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyGetStorageAt> abeyGetStorageAt(
            String address, BigInteger position, DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyGetTransactionCount> abeyGetTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyGetBlockTransactionCountByHash> abeyGetBlockTransactionCountByHash(
            String blockHash);

    Request<?, AbeyGetBlockTransactionCountByNumber> abeyGetBlockTransactionCountByNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyGetUncleCountByBlockHash> abeyGetUncleCountByBlockHash(String blockHash);

    Request<?, AbeyGetUncleCountByBlockNumber> abeyGetUncleCountByBlockNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyGetCode> abeyGetCode(String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeySign> abeySign(String address, String sha3HashOfDataToSign);

    Request<?, AbeySendTransaction> abeySendTransaction(
            org.abeyj.protocol.core.methods.request.Transaction transaction);

    Request<?, AbeySendTransaction> abeySendRawTransaction(
            String signedTransactionData);

    Request<?, AbeyCall> abeyCall(
            org.abeyj.protocol.core.methods.request.Transaction transaction,
            DefaultBlockParameter defaultBlockParameter);

    Request<?, AbeyEstimateGas> abeyEstimateGas(
            org.abeyj.protocol.core.methods.request.Transaction transaction);

    Request<?, AbeyBlock> abeyGetBlockByHash(String blockHash, boolean returnFullTransactionObjects);

    Request<?, AbeyBlock> abeyGetBlockByNumber(
            DefaultBlockParameter defaultBlockParameter, boolean returnFullTransactionObjects);

    Request<?, AbeyTransaction> abeyGetTransactionByHash(String transactionHash);

    Request<?, AbeyTransaction> abeyGetTransactionByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex);

    Request<?, AbeyTransaction> abeyGetTransactionByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex);

    Request<?, AbeyGetTransactionReceipt> abeyGetTransactionReceipt(String transactionHash);

    Request<?, AbeyBlock> abeyGetUncleByBlockHashAndIndex(
            String blockHash, BigInteger transactionIndex);

    Request<?, AbeyBlock> abeyGetUncleByBlockNumberAndIndex(
            DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex);

    Request<?, AbeyGetCompilers> abeyGetCompilers();

    Request<?, AbeyCompileLLL> abeyCompileLLL(String sourceCode);

    Request<?, AbeyCompileSolidity> abeyCompileSolidity(String sourceCode);

    Request<?, AbeyCompileSerpent> abeyCompileSerpent(String sourceCode);

    Request<?, AbeyFilter> abeyNewFilter(org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter);

    Request<?, AbeyFilter> abeyNewBlockFilter();

    Request<?, AbeyFilter> abeyNewPendingTransactionFilter();

    Request<?, AbeyUninstallFilter> abeyUninstallFilter(BigInteger filterId);

    Request<?, AbeyLog> abeyGetFilterChanges(BigInteger filterId);

    Request<?, AbeyLog> abeyGetFilterLogs(BigInteger filterId);

    Request<?, AbeyLog> abeyGetLogs(org.abeyj.protocol.core.methods.request.AbeyFilter abeyFilter);

    Request<?, AbeyGetWork> abeyGetWork();

    Request<?, AbeySubmitWork> abeySubmitWork(String nonce, String headerPowHash, String mixDigest);

    Request<?, AbeySubmitHashrate> abeySubmitHashrate(String hashrate, String clientId);

    Request<?, DbPutString> dbPutString(String databaseName, String keyName, String stringToStore);

    Request<?, DbGetString> dbGetString(String databaseName, String keyName);

    Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore);

    Request<?, DbGetHex> dbGetHex(String databaseName, String keyName);

    Request<?, org.abeyj.protocol.core.methods.response.ShhPost> shhPost(
            org.abeyj.protocol.core.methods.request.ShhPost shhPost);

    Request<?, ShhVersion> shhVersion();

    Request<?, ShhNewIdentity> shhNewIdentity();

    Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress);

    Request<?, ShhNewGroup> shhNewGroup();

    Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress);

    Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter);

    Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId);

    Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId);

    Request<?, ShhMessages> shhGetMessages(BigInteger filterId);

    Request<?, TxPoolStatus> txPoolStatus();
}
