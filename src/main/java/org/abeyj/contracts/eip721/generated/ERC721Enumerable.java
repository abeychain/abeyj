package org.abeyj.contracts.eip721.generated;

import org.abeyj.abi.TypeReference;
import org.abeyj.abi.datatypes.Function;
import org.abeyj.abi.datatypes.Type;
import org.abeyj.abi.datatypes.generated.Uint256;
import org.abeyj.crypto.Credentials;
import org.abeyj.protocol.Abeyj;
import org.abeyj.protocol.core.RemoteCall;
import org.abeyj.tx.Contract;
import org.abeyj.tx.TransactionManager;
import org.abeyj.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.1.1.
 */
public class ERC721Enumerable extends Contract {
    private static final String BINARY = "Bin file was not provided";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TOKENOFOWNERBYINDEX = "tokenOfOwnerByIndex";

    public static final String FUNC_TOKENBYINDEX = "tokenByIndex";

    @Deprecated
    protected ERC721Enumerable(String contractAddress, Abeyj abeyj, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, abeyj, credentials, gasPrice, gasLimit);
    }

    protected ERC721Enumerable(String contractAddress, Abeyj abeyj, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, abeyj, credentials, contractGasProvider);
    }

    @Deprecated
    protected ERC721Enumerable(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, abeyj, transactionManager, gasPrice, gasLimit);
    }

    protected ERC721Enumerable(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, abeyj, transactionManager, contractGasProvider);
    }

    public RemoteCall<BigInteger> totalSupply() {
        final Function function = new Function(FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokenOfOwnerByIndex(String _owner, BigInteger _index) {
        final Function function = new Function(FUNC_TOKENOFOWNERBYINDEX,
                Arrays.<Type>asList(new org.abeyj.abi.datatypes.Address(_owner),
                new org.abeyj.abi.datatypes.generated.Uint256(_index)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> tokenByIndex(BigInteger _index) {
        final Function function = new Function(FUNC_TOKENBYINDEX,
                Arrays.<Type>asList(new org.abeyj.abi.datatypes.generated.Uint256(_index)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static ERC721Enumerable load(String contractAddress, Abeyj abeyj, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC721Enumerable(contractAddress, abeyj, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ERC721Enumerable load(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC721Enumerable(contractAddress, abeyj, transactionManager, gasPrice, gasLimit);
    }

    public static ERC721Enumerable load(String contractAddress, Abeyj abeyj, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ERC721Enumerable(contractAddress, abeyj, credentials, contractGasProvider);
    }

    public static ERC721Enumerable load(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ERC721Enumerable(contractAddress, abeyj, transactionManager, contractGasProvider);
    }
}
