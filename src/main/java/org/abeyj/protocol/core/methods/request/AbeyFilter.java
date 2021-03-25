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
package org.abeyj.protocol.core.methods.request;

import org.abeyj.protocol.core.DefaultBlockParameter;

import java.util.Collections;
import java.util.List;

/**
 * Filter implementation as per <a
 * href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_newfilter">docs</a>.
 */
public class AbeyFilter extends Filter<AbeyFilter> {
    private DefaultBlockParameter fromBlock; // optional, params - defaults to latest for both
    private DefaultBlockParameter toBlock;
    private String blockHash; // optional, cannot be used together with fromBlock/toBlock
    private List<String> address; // spec. implies this can be single address as string or list

    public AbeyFilter() {
        super();
    }

    public AbeyFilter(
            DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock, List<String> address) {
        super();
        this.fromBlock = fromBlock;
        this.toBlock = toBlock;
        this.address = address;
    }

    public AbeyFilter(
            DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock, String address) {
        this(fromBlock, toBlock, Collections.singletonList(address));
    }

    public AbeyFilter(String blockHash) {
        super();
        this.blockHash = blockHash;
    }

    public AbeyFilter(String blockHash, String address) {
        this(null, null, Collections.singletonList(address));
        this.blockHash = blockHash;
    }

    public DefaultBlockParameter getFromBlock() {
        return fromBlock;
    }

    public DefaultBlockParameter getToBlock() {
        return toBlock;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public List<String> getAddress() {
        return address;
    }

    @Override
    AbeyFilter getThis() {
        return this;
    }
}
