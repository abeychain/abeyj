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
package org.abeyj.protocol.eea;

import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.core.JsonRpc2_0Abeyj;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.core.methods.response.AbeySendTransaction;

import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

public class JsonRpc2_0Eea extends JsonRpc2_0Abeyj implements Eea {
    public JsonRpc2_0Eea(AbeyjService abeyjService) {
        super(abeyjService);
    }

    public JsonRpc2_0Eea(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        super(abeyjService, pollingInterval, scheduledExecutorService);
    }

    @Override
    public Request<?, AbeySendTransaction> eeaSendRawTransaction(
            final String signedTransactionData) {
        return new Request<>(
                "eea_sendRawTransaction",
                Collections.singletonList(signedTransactionData),
                abeyjService,
                AbeySendTransaction.class);
    }
}
