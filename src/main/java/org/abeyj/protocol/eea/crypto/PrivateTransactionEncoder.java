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
package org.abeyj.protocol.eea.crypto;

import org.abeyj.crypto.Credentials;
import org.abeyj.crypto.Sign;
import org.abeyj.crypto.TransactionEncoder;
import org.abeyj.rlp.RlpEncoder;
import org.abeyj.rlp.RlpList;
import org.abeyj.rlp.RlpString;
import org.abeyj.rlp.RlpType;
import org.abeyj.utils.Base64String;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/** Create signed RLP encoded private transaction. */
public class PrivateTransactionEncoder {

    public static byte[] signMessage(
            final RawPrivateTransaction rawTransaction, final Credentials credentials) {
        final byte[] encodedTransaction = encode(rawTransaction);
        final Sign.SignatureData signatureData =
                Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());

        return encode(rawTransaction, signatureData);
    }

    public static byte[] signMessage(
            final RawPrivateTransaction rawTransaction,
            final long chainId,
            final Credentials credentials) {
        final byte[] encodedTransaction = encode(rawTransaction, chainId);
        final Sign.SignatureData signatureData =
                Sign.signMessage(encodedTransaction, credentials.getEcKeyPair());

        final Sign.SignatureData eip155SignatureData =
                TransactionEncoder.createEip155SignatureData(signatureData, chainId);
        return encode(rawTransaction, eip155SignatureData);
    }

    public static byte[] encode(final RawPrivateTransaction rawTransaction) {
        return encode(rawTransaction, null);
    }

    public static byte[] encode(final RawPrivateTransaction rawTransaction, final long chainId) {
        final Sign.SignatureData signatureData =
                new Sign.SignatureData(longToBytes(chainId), new byte[] {}, new byte[] {});
        return encode(rawTransaction, signatureData);
    }

    private static byte[] encode(
            final RawPrivateTransaction rawTransaction, final Sign.SignatureData signatureData) {
        final List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        final RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    private static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static List<RlpType> asRlpValues(
            final RawPrivateTransaction privateTransaction,
            final Sign.SignatureData signatureData) {

        final List<RlpType> result =
                new ArrayList<>(
                        TransactionEncoder.asRlpValues(
                                privateTransaction.asRawTransaction(), signatureData));

        result.add(privateTransaction.getPrivateFrom().asRlp());

        privateTransaction
                .getPrivateFor()
                .ifPresent(privateFor -> result.add(Base64String.unwrapListToRlp(privateFor)));

        privateTransaction.getPrivacyGroupId().map(Base64String::asRlp).ifPresent(result::add);

        result.add(RlpString.create(privateTransaction.getRestriction().getRestriction()));

        return result;
    }
}
