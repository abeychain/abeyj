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
package org.abeyj.ens;

import org.abeyj.crypto.Keys;
import org.abeyj.crypto.WalletUtils;
import org.abeyj.ens.contracts.generated.ENS;
import org.abeyj.ens.contracts.generated.PublicResolver;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.DefaultBlockParameterName;
import org.abeyj.protocol.core.methods.response.AbeyBlock;
import org.abeyj.protocol.core.methods.response.AbeySyncing;
import org.abeyj.protocol.core.methods.response.NetVersion;
import org.abeyj.tx.ClientTransactionManager;
import org.abeyj.tx.TransactionManager;
import org.abeyj.tx.gas.DefaultGasProvider;
import org.abeyj.utils.Numeric;

/** Resolution logic for contract addresses. */
public class EnsResolver {

    public static final long DEFAULT_SYNC_THRESHOLD = 1000 * 60 * 3;
    public static final String REVERSE_NAME_SUFFIX = ".addr.reverse";

    private final Abeyj abeyj;
    private final int addressLength;
    private final TransactionManager transactionManager;
    private long syncThreshold; // non-final in case this value needs to be tweaked

    public EnsResolver(Abeyj abeyj, long syncThreshold, int addressLength) {
        this.abeyj = abeyj;
        transactionManager = new ClientTransactionManager(abeyj, null); // don't use empty string
        this.syncThreshold = syncThreshold;
        this.addressLength = addressLength;
    }

    public EnsResolver(Abeyj abeyj, long syncThreshold) {
        this(abeyj, syncThreshold, Keys.ADDRESS_LENGTH_IN_HEX);
    }

    public EnsResolver(Abeyj abeyj) {
        this(abeyj, DEFAULT_SYNC_THRESHOLD);
    }

    public void setSyncThreshold(long syncThreshold) {
        this.syncThreshold = syncThreshold;
    }

    public long getSyncThreshold() {
        return syncThreshold;
    }

    /**
     * Provides an access to a valid public resolver in order to access other API methods.
     *
     * @param ensName our user input ENS name
     * @return PublicResolver
     */
    protected PublicResolver obtainPublicResolver(String ensName) {
        if (isValidEnsName(ensName, addressLength)) {
            try {
                if (!isSynced()) {
                    throw new EnsResolutionException("Node is not currently synced");
                } else {
                    return lookupResolver(ensName);
                }
            } catch (Exception e) {
                throw new EnsResolutionException("Unable to determine sync status of node", e);
            }

        } else {
            throw new EnsResolutionException("EnsName is invalid: " + ensName);
        }
    }

    public String resolve(String contractId) {
        if (isValidEnsName(contractId, addressLength)) {
            PublicResolver resolver = obtainPublicResolver(contractId);

            byte[] nameHash = NameHash.nameHashAsBytes(contractId);
            String contractAddress = null;
            try {
                contractAddress = resolver.addr(nameHash).send();
            } catch (Exception e) {
                throw new RuntimeException("Unable to execute Ethereum request", e);
            }

            if (!WalletUtils.isValidAddress(contractAddress)) {
                throw new RuntimeException("Unable to resolve address for name: " + contractId);
            } else {
                return contractAddress;
            }
        } else {
            return contractId;
        }
    }

    /**
     * Reverse name resolution as documented in the <a
     * href="https://docs.ens.domains/contract-api-reference/reverseregistrar">specification</a>.
     *
     * @param address an ethereum address, example: "0x00000000000C2E074eC69A0dFb2997BA6C7d2e1e"
     * @return a EnsName registered for provided address
     */
    public String reverseResolve(String address) {
        if (WalletUtils.isValidAddress(address, addressLength)) {
            String reverseName = Numeric.cleanHexPrefix(address) + REVERSE_NAME_SUFFIX;
            PublicResolver resolver = obtainPublicResolver(reverseName);

            byte[] nameHash = NameHash.nameHashAsBytes(reverseName);
            String name;
            try {
                name = resolver.name(nameHash).send();
            } catch (Exception e) {
                throw new RuntimeException("Unable to execute Ethereum request", e);
            }

            if (!isValidEnsName(name, addressLength)) {
                throw new RuntimeException("Unable to resolve name for address: " + address);
            } else {
                return name;
            }
        } else {
            throw new EnsResolutionException("Address is invalid: " + address);
        }
    }

    private PublicResolver lookupResolver(String ensName) throws Exception {
        NetVersion netVersion = abeyj.netVersion().send();
        String registryContract = Contracts.resolveRegistryContract(netVersion.getNetVersion());

        ENS ensRegistry =
                ENS.load(registryContract, abeyj, transactionManager, new DefaultGasProvider());

        byte[] nameHash = NameHash.nameHashAsBytes(ensName);
        String resolverAddress = ensRegistry.resolver(nameHash).send();

        return PublicResolver.load(
                resolverAddress, abeyj, transactionManager, new DefaultGasProvider());
    }

    boolean isSynced() throws Exception {
        AbeySyncing abeySyncing = abeyj.abeySyncing().send();
        if (abeySyncing.isSyncing()) {
            return false;
        } else {
            AbeyBlock abeyBlock =
                    abeyj.abeyGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            long timestamp = abeyBlock.getBlock().getTimestamp().longValueExact() * 1000;

            return System.currentTimeMillis() - syncThreshold < timestamp;
        }
    }

    public static boolean isValidEnsName(String input) {
        return isValidEnsName(input, Keys.ADDRESS_LENGTH_IN_HEX);
    }

    public static boolean isValidEnsName(String input, int addressLength) {
        return input != null // will be set to null on new Contract creation
                && (input.contains(".") || !WalletUtils.isValidAddress(input, addressLength));
    }
}
