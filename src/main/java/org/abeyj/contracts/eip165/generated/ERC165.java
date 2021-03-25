package org.abeyj.contracts.eip165.generated;

import org.abeyj.abi.TypeReference;
import org.abeyj.abi.datatypes.Bool;
import org.abeyj.abi.datatypes.Function;
import org.abeyj.abi.datatypes.Type;
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
public class ERC165 extends Contract {
    private static final String BINARY = "Bin file was not provided";

    public static final String FUNC_SUPPORTSINTERFACE = "supportsInterface";

    @Deprecated
    protected ERC165(String contractAddress, Abeyj abeyj, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, abeyj, credentials, gasPrice, gasLimit);
    }

    protected ERC165(String contractAddress, Abeyj abeyj, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, abeyj, credentials, contractGasProvider);
    }

    @Deprecated
    protected ERC165(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, abeyj, transactionManager, gasPrice, gasLimit);
    }

    protected ERC165(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, abeyj, transactionManager, contractGasProvider);
    }

    public RemoteCall<Boolean> supportsInterface(byte[] interfaceID) {
        final Function function = new Function(FUNC_SUPPORTSINTERFACE,
                Arrays.<Type>asList(new org.abeyj.abi.datatypes.generated.Bytes4(interfaceID)),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static ERC165 load(String contractAddress, Abeyj abeyj, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC165(contractAddress, abeyj, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ERC165 load(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ERC165(contractAddress, abeyj, transactionManager, gasPrice, gasLimit);
    }

    public static ERC165 load(String contractAddress, Abeyj abeyj, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ERC165(contractAddress, abeyj, credentials, contractGasProvider);
    }

    public static ERC165 load(String contractAddress, Abeyj abeyj, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ERC165(contractAddress, abeyj, transactionManager, contractGasProvider);
    }
}
