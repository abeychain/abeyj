/*
 * Copyright 2020 Web3 Labs Ltd.
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

import org.abeyj.protocol.AbeyjService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BatchRequest {

    private AbeyjService abeyjService;
    private List<Request<?, ? extends Response<?>>> requests = new ArrayList<>();

    public BatchRequest(AbeyjService abeyjService) {
        this.abeyjService = abeyjService;
    }

    public BatchRequest add(Request<?, ? extends Response<?>> request) {
        requests.add(request);
        return this;
    }

    public List<Request<?, ? extends Response<?>>> getRequests() {
        return requests;
    }

    public BatchResponse send() throws IOException {
        return abeyjService.sendBatch(this);
    }

    public CompletableFuture<BatchResponse> sendAsync() {
        return abeyjService.sendBatchAsync(this);
    }
}
