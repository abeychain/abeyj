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

import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.methods.response.NewAccountIdentifier;
import org.abeyj.protocol.admin.methods.response.PersonalListAccounts;
import org.abeyj.protocol.admin.methods.response.PersonalUnlockAccount;
import org.abeyj.protocol.admin.methods.response.TxPoolContent;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.request.Transaction;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;

import java.math.BigInteger;
import java.util.concurrent.ScheduledExecutorService;

/** JSON-RPC Request object building factory for common Parity and Geth. */
public interface Admin extends Abeyj {

    static Admin build(AbeyjService abeyjService) {
        return new JsonRpc2_0Admin(abeyjService);
    }

    static Admin build(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Admin(abeyjService, pollingInterval, scheduledExecutorService);
    }

    public Request<?, PersonalListAccounts> personalListAccounts();

    public Request<?, NewAccountIdentifier> personalNewAccount(String password);

    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String address, String passphrase, BigInteger duration);

    public Request<?, PersonalUnlockAccount> personalUnlockAccount(
            String address, String passphrase);

    public Request<?, AbeySendTransaction> personalSendTransaction(
            Transaction transaction, String password);

    public Request<?, TxPoolContent> txPoolContent();
}
