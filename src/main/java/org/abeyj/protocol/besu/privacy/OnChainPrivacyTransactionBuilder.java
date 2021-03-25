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
package org.abeyj.protocol.besu.privacy;

import org.abeyj.abi.FunctionEncoder;
import org.abeyj.abi.Utils;
import org.abeyj.abi.datatypes.DynamicArray;
import org.abeyj.abi.datatypes.Function;
import org.abeyj.abi.datatypes.generated.Bytes32;
import org.abeyj.crypto.Credentials;
import org.abeyj.protocol.eea.crypto.PrivateTransactionEncoder;
import org.abeyj.protocol.eea.crypto.RawPrivateTransaction;
import org.abeyj.tx.gas.BesuPrivacyGasProvider;
import org.abeyj.utils.Base64String;
import org.abeyj.utils.Numeric;
import org.abeyj.utils.Restriction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OnChainPrivacyTransactionBuilder {

    private static final BesuPrivacyGasProvider ZERO_GAS_PROVIDER =
            new BesuPrivacyGasProvider(BigInteger.valueOf(0));

    public static String getEncodedRemoveFromGroupFunction(
            Base64String enclaveKey, byte[] participant) {
        final Function function =
                new Function(
                        "removeParticipant",
                        Arrays.asList(new Bytes32(enclaveKey.raw()), new Bytes32(participant)),
                        Collections.emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String getEncodedAddToGroupFunction(
            Base64String enclaveKey, List<byte[]> participants) {
        final Function function =
                new Function(
                        "addParticipants",
                        Arrays.asList(
                                new Bytes32(enclaveKey.raw()),
                                new DynamicArray<>(
                                        Bytes32.class, Utils.typeMap(participants, Bytes32.class))),
                        Collections.emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String getEncodedSingleParamFunction(final String functionName) {
        final Function function =
                new Function(functionName, Collections.emptyList(), Collections.emptyList());
        return FunctionEncoder.encode(function);
    }

    public static String buildOnChainPrivateTransaction(
            Base64String privacyGroupId,
            Credentials credentials,
            Base64String enclaveKey,
            final BigInteger nonce,
            String call) {

        RawPrivateTransaction rawTransaction =
                RawPrivateTransaction.createTransaction(
                        nonce,
                        ZERO_GAS_PROVIDER.getGasPrice(),
                        ZERO_GAS_PROVIDER.getGasLimit(),
                        "0x000000000000000000000000000000000000007c",
                        call,
                        enclaveKey,
                        privacyGroupId,
                        Restriction.RESTRICTED);

        return Numeric.toHexString(
                PrivateTransactionEncoder.signMessage(rawTransaction, 2018, credentials));
    }
}
