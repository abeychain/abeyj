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
package org.abeyj.protocol.geth;

import io.reactivex.Flowable;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.JsonRpc2_0Admin;
import org.abeyj.protocol.admin.methods.response.BooleanResponse;
import org.abeyj.protocol.admin.methods.response.PersonalSign;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.AbeySubscribe;
import org.abeyj.protocol.core.methods.response.MinerStartResponse;
import org.abeyj.protocol.geth.response.PersonalEcRecover;
import org.abeyj.protocol.geth.response.PersonalImportRawKey;
import org.abeyj.protocol.websocket.events.PendingTransactionNotification;
import org.abeyj.protocol.websocket.events.SyncingNotfication;

import java.util.Arrays;
import java.util.Collections;

/** JSON-RPC 2.0 factory implementation for Geth. */
public class JsonRpc2_0Geth extends JsonRpc2_0Admin implements Geth {

    public JsonRpc2_0Geth(AbeyjService abeyjService) {
        super(abeyjService);
    }

    @Override
    public Request<?, PersonalImportRawKey> personalImportRawKey(String keydata, String password) {
        return new Request<>(
                "personal_importRawKey",
                Arrays.asList(keydata, password),
                abeyjService,
                PersonalImportRawKey.class);
    }

    @Override
    public Request<?, BooleanResponse> personalLockAccount(String accountId) {
        return new Request<>(
                "personal_lockAccount",
                Arrays.asList(accountId),
                abeyjService,
                BooleanResponse.class);
    }

    @Override
    public Request<?, PersonalSign> personalSign(
            String message, String accountId, String password) {
        return new Request<>(
                "personal_sign",
                Arrays.asList(message, accountId, password),
                abeyjService,
                PersonalSign.class);
    }

    @Override
    public Request<?, PersonalEcRecover> personalEcRecover(
            String hexMessage, String signedMessage) {
        return new Request<>(
                "personal_ecRecover",
                Arrays.asList(hexMessage, signedMessage),
                abeyjService,
                PersonalEcRecover.class);
    }

    @Override
    public Request<?, MinerStartResponse> minerStart(int threadCount) {
        return new Request<>(
                "miner_start", Arrays.asList(threadCount), abeyjService, MinerStartResponse.class);
    }

    @Override
    public Request<?, BooleanResponse> minerStop() {
        return new Request<>(
                "miner_stop", Collections.<String>emptyList(), abeyjService, BooleanResponse.class);
    }

    public Flowable<PendingTransactionNotification> newPendingTransactionsNotifications() {
        return abeyjService.subscribe(
                new Request<>(
                        "abey_subscribe",
                        Arrays.asList("newPendingTransactions"),
                        abeyjService,
                        AbeySubscribe.class),
                "abey_unsubscribe",
                PendingTransactionNotification.class);
    }

    @Override
    public Flowable<SyncingNotfication> syncingStatusNotifications() {
        return abeyjService.subscribe(
                new Request<>(
                        "abey_subscribe",
                        Arrays.asList("syncing"),
                        abeyjService,
                        AbeySubscribe.class),
                "abey_unsubscribe",
                SyncingNotfication.class);
    }
}
