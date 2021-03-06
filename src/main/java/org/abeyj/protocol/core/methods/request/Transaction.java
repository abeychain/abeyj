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

import com.fasterxml.jackson.annotation.JsonInclude;
import org.abeyj.utils.Numeric;

import java.math.BigInteger;

/**
 * Transaction request object used the below methods.
 *
 * <ol>
 *   <li>eth_call
 *   <li>eth_sendTransaction
 *   <li>eth_estimateGas
 * </ol>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    // default as per https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_sendtransaction
    public static final BigInteger DEFAULT_GAS = BigInteger.valueOf(9000);

    private String from;
    private String to;
    private BigInteger gas;
    private BigInteger gasPrice;
    private BigInteger value;
    private String data;
    private BigInteger nonce; // nonce field is not present on eth_call/eth_estimateGas
    private BigInteger gasPremium;
    private BigInteger feeCap;

    public Transaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            BigInteger value,
            String data) {
        this(from, nonce, gasPrice, gasLimit, to, value, data, null, null);
    }

    public Transaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            BigInteger value,
            String data,
            BigInteger gasPremium,
            BigInteger feeCap) {
        this.from = from;
        this.to = to;
        this.gas = gasLimit;
        this.gasPrice = gasPrice;
        this.value = value;

        if (data != null) {
            this.data = Numeric.prependHexPrefix(data);
        }

        this.nonce = nonce;
        this.gasPremium = gasPremium;
        this.feeCap = feeCap;
    }

    public static Transaction createContractTransaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            BigInteger value,
            String init) {

        return new Transaction(from, nonce, gasPrice, gasLimit, null, value, init);
    }

    public static Transaction createContractTransaction(
            String from, BigInteger nonce, BigInteger gasPrice, String init) {

        return createContractTransaction(from, nonce, gasPrice, null, null, init);
    }

    public static Transaction createEtherTransaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            BigInteger value) {

        return new Transaction(from, nonce, gasPrice, gasLimit, to, value, null);
    }

    public static Transaction createFunctionCallTransaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            BigInteger value,
            String data) {

        return new Transaction(from, nonce, gasPrice, gasLimit, to, value, data);
    }

    public static Transaction createFunctionCallTransaction(
            String from,
            BigInteger nonce,
            BigInteger gasPrice,
            BigInteger gasLimit,
            String to,
            String data) {

        return new Transaction(from, nonce, gasPrice, gasLimit, to, null, data);
    }

    public static Transaction createEthCallTransaction(String from, String to, String data) {

        return new Transaction(from, null, null, null, to, null, data);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getGas() {
        return convert(gas);
    }

    public String getGasPrice() {
        return convert(gasPrice);
    }

    public String getValue() {
        return convert(value);
    }

    public String getData() {
        return data;
    }

    public String getNonce() {
        return convert(nonce);
    }

    private static String convert(BigInteger value) {
        if (value != null) {
            return Numeric.encodeQuantity(value);
        } else {
            return null; // we don't want the field to be encoded if not present
        }
    }
}
