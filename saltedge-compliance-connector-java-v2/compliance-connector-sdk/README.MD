# Salt Edge Compliance Connector SDK (v2)
  
This codebase contains SDK module built on Spring Boot Framework, 
which is the set of tools for simplification of communication between ASPSP/Bank and Salt Edge PSD2 Compliance Solution.    
The SDK module implements the Salt Edge PSD2 Connector v2 API compatible with **Berlin Group NextGenPSD2 XS2A API**.
[See how to add SDK](#SDK-Integration)  
**Last stable version of SDK library is "2.4.1".**   
  
## Application Requirements
  
1. JDK, at least version 8 
1. Spring Boot Framework, at least version 2.2.+
1. Registration & API Keys  
   Follow the guide on how to [register and add API keys](https://priora.saltedge.com/docs/aspsp/v1#registrationandapikeys)  
  
## SDK Integration  

Target application should be compatible with [following requirements](#Application-Requirements)

### 1. Add SDK to target application.

#### Add as project module
  1. Fork this repository
  2. In IntelliJ IDE Import module `File/New/Import Module...` in your project
  3. Build and run application on target device or emulator

#### Add as JAR library
copy jar file `compliance-connector-sdk-x.x.x-all.jar` 
from `https://github.com/saltedge/compliance-examples/tree/master/saltedge-compliance-connector-java-v2/out/`
to your project

#### Add as Maven dependency  
Add Salt Edge maven repository to application's build.gradle
```groovy
 repositories {
    maven {
      url 'https://raw.github.com/saltedge/compliance-examples/master/maven-repo/'
    }
  }
```
Add Connector SDK dependency to application build.gradle
```groovy
  implementation ('com.saltedge.connector.sdk:compliance-connector-sdk:x.x.x') {
    transitive = true
  }
```
    
### 2. Setup application as [described before](#example-application-quick-setup).

### 3. Add SDK package (`com.saltedge.connector.sdk`) to component scan annotation in Application class.

```java
  @SpringBootApplication(scanBasePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  @EnableJpaRepositories(basePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  @EntityScan(basePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  public class ExampleApplication {
   
  }
```
    
### 4. Add `extra` field to `Payment` entity of application.
  
The Connector will pass extra data on Payment creation and will require it back at the end of the Payment authentication.
Add `extra` field (if not exist) for storing extra data from Salt Edge Compliance solution (e.g. `prioraPaymentId`).  
  
### 5. Create a service designated for providing information to and receiving events from `Salt Edge PSD2 Compliance`:  

This service should create on application class which implement `ProviderServiceAbs` interface and should have `@Service` annotation because it is auto-wired in SDK;  
See template [ExampleProviderService.java](ExampleProviderService.java)  
  
* `getAuthorizationTypes(...)` - return list of registered [Authorization types](https://priora.saltedge.com/providers/settings#authorization_types);
  
* `getExchangeRates()` - return list of Exchange rates for today;
  
* `getAccountInformationAuthorizationPageUrl(...)` - return URL of authorization page for oAuth authorization of User and consent for accounts information;
  * **param** `sessionSecret` create consent session secret. should be returned on authentication success or fail.
  * **param** `userConsentIsRequired` if true then user consent for Account Information (balances, transactions) is required and should be returned on authentication success.
  * **return** URL string

* `getAccountsOfUser(...)` - return accounts list of User;
  * **param** `userId` User identifier on Provider side
  * **return** list of Account objects

* `getTransactionsOfAccount(...)` - provides transactions which belong to an account of user.;
  * **param** `userId` User identifier on Provider side
  * **param** `accountId` Account identifier on Provider side
  * **param** `fromDate` Specifies the starting date, from which transactions should be fetched. This value can be set to 90 days ago by default.
  * **param** `toDate` Specifies the ending date, to which transactions should be fetched. This value will always be the today’s date.
  * **param** `fromId` Specifies the ID of page for Pageable request.
  * **return** page object with list of Transaction objects and next page id
  
* `getCardAccountsOfUser(...)` - return list of card accounts of User;
  * **param** `userId` User identifier on Provider side
  * **return** list of Card Account objects
  
* `getTransactionsOfCardAccount(...)` - provides transactions which belong to a card account of user;
  * **param** `userId` User identifier on Provider side
  * **param** `accountId` Account identifier on Provider side
  * **param** `fromDate` Specifies the starting date, from which transactions should be fetched. This value can be set to 90 days ago by default.
  * **param** `toDate` Specifies the ending date, to which transactions should be fetched. This value will always be the today’s date.
  * **param** `fromId` Specifies the ID of page for Pageable request.
  * **return** page object with list of CardTransaction objects and next page id
  
* `createPayment(...)` - create a payment order and return payment id or null
  * **param** `creditorIban` of payment order
  * **param** `creditorName` of payment order
  * **param** `debtorIban` of payment order
  * **param** `amount` of payment order
  * **param** `currency` of payment order
  * **param** `description` of payment order
  * **param** `extraData` hash object
  * **return** unique identifier of payment or null if payment is not initiated
  
* `getPaymentAuthorizationPageUrl(...)` - return URL of authorization page for payment;
  * **param** `paymentId` unique identifier of payment order for which is required authorization
  * **return** URL string    
    
    
### 6. Use Connector's SDK callback service `ConnectorSDKCallbackService` for callback communication (e.g. notifying SDK about events):

* `isUserConsentRequired(sessionSecret)` - check if User Consent (Bank Offered Consent) is required for authorization session determined by sessionSecret:
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;

* `onAccountInformationAuthorizationSuccess(sessionSecret, userId, accessToken, consents)` - notify SDK about the successful authorization if User for AIS flow:  
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;       - `userId` - unique identifier of Provider's system User (Customer);  
  * `accessToken` unique access identifier;  
  * `consents` ProviderOfferedConsents object with user consent to provide account information;  

* `onAccountInformationAuthorizationFail(sessionSecret)`- notify SDK about the failed authorization:  
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;
      
* `onPaymentInitiationAuthorizationSuccess(paymentId, userId, extra)` - notify SDK about the successful authorization if User for AIS flow:  
  * `paymentId` unique payment id;  
  * `userId` unique identifier of Provider's system User (Customer);  
  * `extra` extra data from authenticated Payment passed to application via `getPaymentAuthorizationPageUrl(...)`;  
  * `onPaymentInitiationAuthorizationFail(paymentId, extra)`- notify SDK about the failed authorization:  
  * `paymentId` unique payment id;
  * `extra` extra data from authenticated Payment passed to application via `getPaymentAuthorizationPageUrl(...)`;
      
* `revokeAccountInformationConsent(userId, accessToken)`- Revoke Account information consent associated with userId and accessToken;
  * `userId` unique identifier of Provider's system User (Customer);
  * `accessToken` unique access identifier;  
  
  
## [Api Documentation](https://priora.banksalt.com/docs/aspsp/v2)
  
---
## For SDK developers  
 
### How to upload SDK changes  
If you are developer and have made changes to this SDK then you will need to deploy that changes.  
Just pass next steps:  
  * Update SDK version in `build.gradle`
  * Run gradle task `uploadShadow`
  ```bash
  ./gradlew uploadShadow
  ```
  * Commit 
  * Make a pull-request to repository
   
---
## [LICENSE](../LICENSE.txt)

---
Copyright © 2020 Salt Edge. https://www.saltedge.com