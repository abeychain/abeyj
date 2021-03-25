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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import org.abeyj.protocol.besu.filters.PrivateLogFilter;
import org.abeyj.protocol.core.methods.request.AbeyFilter;
import org.abeyj.protocol.core.methods.response.Log;

import java.util.concurrent.ScheduledExecutorService;

public class JsonRpc2_0BesuRx {

    private final Besu besu;
    private final ScheduledExecutorService scheduledExecutorService;

    public JsonRpc2_0BesuRx(Besu besu, ScheduledExecutorService scheduledExecutorService) {
        this.besu = besu;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    public Flowable<Log> privLogFlowable(
            String privacyGroupId,
            AbeyFilter abeyFilter,
            long pollingInterval) {
        return Flowable.create(
                subscriber -> {
                    PrivateLogFilter logFilter =
                            new PrivateLogFilter(
                                    besu, subscriber::onNext, privacyGroupId, abeyFilter);

                    run(logFilter, subscriber, pollingInterval);
                },
                BackpressureStrategy.BUFFER);
    }

    private <T> void run(
            org.abeyj.protocol.core.filters.Filter<T> filter,
            FlowableEmitter<? super T> emitter,
            long pollingInterval) {

        filter.run(scheduledExecutorService, pollingInterval);
        emitter.setCancellable(filter::cancel);
    }
}
