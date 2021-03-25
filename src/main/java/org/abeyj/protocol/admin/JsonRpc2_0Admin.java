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
package org.abeyj.protocol.admin;

import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.methods.response.NewAccountIdentifier;
import org.abeyj.protocol.admin.methods.response.PersonalListAccounts;
import org.abeyj.protocol.admin.methods.response.PersonalUnlockAccount;
import org.abeyj.protocol.admin.methods.response.TxPoolContent;
import org.abeyj.protocol.core.JsonRpc2_0Abeyj;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/** JSON-RPC 2.0 factory implementation for common Parity and Geth. */
public class JsonRpc2_0Admin extends JsonRpc2_0Abeyj implements Admin {

    public JsonRpc2_0Admin(AbeyjService abeyjService) {
        super(abeyjService);
    }

    public JsonRpc2_0Admin(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        super(abeyjService, pollingInterval, scheduledExecutorService);
    }

    @Override
    public Request<?, PersonalListAccounts> personalListAccounts() {
        return new Request<>(
                "personal_listAccounts",
                Collections.<String>emptyList(),
                abeyjService,
                PersonalListAccounts.class);
    }

    @Override
    public Request<?, NewAccountIdentifier> personalNewAccount(String password) {
        return new Request<>(
                "personal_newAccount",
                Arrays.asList(password),
                abeyjService,
                NewAccountIdentifier.class);
    }

    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String accountId, String password, BigInteger duration) {
        List<Object> attributes = new ArrayList<>(3);
        attributes.add(accountId);
        attributes.add(password);

        if (duration != null) {
            // Parity has a bug where it won't support a duration
            // See https://github.com/ethcore/parity/issues/1215
            attributes.add(duration.longValue());
        } else {
            // we still need to include the null value, otherwise Parity rejects request
            attributes.add(null);
        }

        return new Request<>(
                "personal_unlockAccount", attributes, abeyjService, PersonalUnlockAccount.class);
    }

    @Override
    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String accountId, String password) {

        return personalUnlockAccount(accountId, password, null);
    }

    @Override
    public Request<?, AbeySendTransaction> personalSendTransaction(
            Transaction transaction, String passphrase) {
        return new Request<>(
                "personal_sendTransaction",
                Arrays.asList(transaction, passphrase),
                abeyjService,
                AbeySendTransaction.class);
    }

    @Override
    public Request<?, TxPoolContent> txPoolContent() {
        return new Request<>(
                "txpool_content",
                Collections.<String>emptyList(),
                abeyjService,
                TxPoolContent.class);
    }
}
