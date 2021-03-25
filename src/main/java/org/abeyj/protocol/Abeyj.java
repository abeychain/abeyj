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
package org.abeyj.protocol;

import org.abeyj.protocol.core.Batcher;
import org.abeyj.protocol.core.Ethereum;
import org.abeyj.protocol.core.JsonRpc2_0Abeyj;
import org.abeyj.protocol.rx.AbeyjRx;

import java.util.concurrent.ScheduledExecutorService;

/** JSON-RPC Request object building factory. */
public interface Abeyj extends Ethereum, AbeyjRx, Batcher {

    /**
     * Construct a new Abeyj instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP or IPC
     * @return new Abeyj instance
     */
    static Abeyj build(AbeyjService abeyjService) {
        return new JsonRpc2_0Abeyj(abeyjService);
    }

    /**
     * Construct a new Abeyj instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP or IPC
     * @param pollingInterval polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks. <strong>You are
     *     responsible for terminating this thread pool</strong>
     * @return new Abeyj instance
     */
    static Abeyj build(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Abeyj(abeyjService, pollingInterval, scheduledExecutorService);
    }

    /** Shutdowns a Abeyj instance and closes opened resources. */
    void shutdown();
}
