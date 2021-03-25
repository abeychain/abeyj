## Build

###  Maven 

```java
<!-- https://mvnrepository.com/artifact/com.abey/abeyj -->
<dependency>
    <groupId>com.abey</groupId>
    <artifactId>abeyj</artifactId>
    <version>1.0.0.0</version>
</dependency>

```

### Gradle

```
// https://mvnrepository.com/artifact/com.abey/abeyj
implementation group: 'com.abey', name: 'abeyj', version: '1.0.0.0'
```



## DEMO

#### Abeyj 

```
public Abeyj abeyj = Web3j.build(new HttpService(Http service));
```

#### 

```java
GasPrice:1200000000
GasLimit:30000(Transfer)/60000(ERC20)
```





#### Get Balance

```java
    public BigInteger getBalance(String address) {
        BigInteger balance = BigInteger.ZERO;
        try {
            AbeyGetBalance abeyGetBalance = abeyj.ethGetBalance(address, 					DefaultBlockParameterName.LATEST).send();
            if (abeyGetBalance != null) {
                balance = abeyGetBalance.getBalance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }
```

#### Get Nonce


``` java
    public BigInteger getTransactionNonce(String address) {
        BigInteger nonce = BigInteger.ZERO;
        try {
            AbeyGetTransactionCount abeyGetTransactionCount = abeyj.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            nonce = abeyGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nonce;
    }
```

#### Get Gasprice

```java
 public static BigInteger getGasPrice() {
        BigInteger gasPrice = null;
        try {
            AbeyGasPrice abeyGasPrice = abeyj.ethGasPrice().send();
            gasPrice = abeyGasPrice.getGasPrice();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gasPrice;
    }

```


#### Send Transaction

```
	String service = "http service";
        Abeyj web3j = Abeyj.build(new HttpService(service));
        Credentials credentials = Credentials.create("User privateKey");
        String from_address = credentials.getAddress();
        BigInteger nonce = BigInteger.ZERO;
        try {
            AbeyGetTransactionCount ethGetTransactionCount = web3j.abeyGetTransactionCount(from_address, DefaultBlockParameterName.PENDING).send();
            nonce = ethGetTransactionCount.getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, new BigInteger("150000000000"),
                new BigInteger("21000"), "toaddress",new BigInteger("10000000"));
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, 178, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        AbeySendTransaction abdySendTransaction = null;
        try {
            abdySendTransaction = abeyj.abeySendRawTransaction(hexValue).sendAsync().get();
            String transactionHash = abdySendTransaction.getTransactionHash();
            System.out.println(transactionHash);
        }catch (Exception e){
            e.printStackTrace();
        }
```

