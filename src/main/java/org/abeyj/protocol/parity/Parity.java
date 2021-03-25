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
package org.abeyj.protocol.parity;

import org.abeyj.crypto.WalletFile;
import org.abeyj.protocol.AbeyjService;
import org.abeyj.protocol.admin.Admin;
import org.abeyj.protocol.admin.methods.response.BooleanResponse;
import org.abeyj.protocol.admin.methods.response.NewAccountIdentifier;
import org.abeyj.protocol.admin.methods.response.PersonalSign;
import org.abeyj.protocol.core.DefaultBlockParameter;
import org.abeyj.protocol.core.Request;
import org.abeyj.protocol.parity.methods.request.Derivation;
import org.abeyj.protocol.parity.methods.response.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

/** JSON-RPC Request object building factory for Parity. */
public interface Parity extends Admin, Trace {

    /**
     * Construct a new Parity instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP or IPC
     * @return new Parity instance
     */
    static Parity build(AbeyjService abeyjService) {
        return new JsonRpc2_0Parity(abeyjService);
    }

    /**
     * Construct a new Parity instance.
     *
     * @param abeyjService abeyj service instance - i.e. HTTP or IPC
     * @param pollingInterval polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks. <strong>You are
     *     responsible for terminating this thread pool</strong>
     * @return new Parity instance
     */
    static Parity build(
            AbeyjService abeyjService,
            long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Parity(abeyjService, pollingInterval, scheduledExecutorService);
    }

    Request<?, ParityAllAccountsInfo> parityAllAccountsInfo();

    Request<?, BooleanResponse> parityChangePassword(
            String accountId, String oldPassword, String newPassword);

    Request<?, ParityDeriveAddress> parityDeriveAddressHash(
            String accountId, String password, Derivation hashType, boolean toSave);

    Request<?, ParityDeriveAddress> parityDeriveAddressIndex(
            String accountId, String password, List<Derivation> indicesType, boolean toSave);

    Request<?, ParityExportAccount> parityExportAccount(String accountId, String password);

    Request<?, ParityAddressesResponse> parityGetDappAddresses(String dAppId);

    Request<?, ParityDefaultAddressResponse> parityGetDappDefaultAddress(String dAppId);

    Request<?, ParityAddressesResponse> parityGetNewDappsAddresses();

    Request<?, ParityDefaultAddressResponse> parityGetNewDappsDefaultAddress();

    Request<?, ParityAddressesResponse> parityImportGethAccounts(ArrayList<String> gethAddresses);

    Request<?, BooleanResponse> parityKillAccount(String accountId, String password);

    Request<?, ParityAddressesResponse> parityListAccounts(
            BigInteger quantity, String accountId, DefaultBlockParameter blockParameter);

    Request<?, ParityAddressesResponse> parityListGethAccounts();

    Request<?, ParityListRecentDapps> parityListRecentDapps();

    Request<?, NewAccountIdentifier> parityNewAccountFromPhrase(String phrase, String password);

    Request<?, NewAccountIdentifier> parityNewAccountFromSecret(String secret, String password);

    Request<?, NewAccountIdentifier> parityNewAccountFromWallet(
            WalletFile walletFile, String password);

    Request<?, BooleanResponse> parityRemoveAddress(String accountId);

    Request<?, BooleanResponse> paritySetAccountMeta(
            String accountId, Map<String, Object> metadata);

    Request<?, BooleanResponse> paritySetAccountName(String address, String name);

    Request<?, BooleanResponse> paritySetDappAddresses(
            String dAppId, ArrayList<String> availableAccountIds);

    Request<?, BooleanResponse> paritySetDappDefaultAddress(String dAppId, String defaultAddress);

    Request<?, BooleanResponse> paritySetNewDappsAddresses(ArrayList<String> availableAccountIds);

    Request<?, BooleanResponse> paritySetNewDappsDefaultAddress(String defaultAddress);

    Request<?, BooleanResponse> parityTestPassword(String accountId, String password);

    Request<?, PersonalSign> paritySignMessage(
            String accountId, String password, String hexMessage);
}
