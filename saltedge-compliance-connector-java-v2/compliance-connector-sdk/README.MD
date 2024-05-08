![Maven Central](https://img.shields.io/maven-central/v/com.saltedge.connector.sdk/compliance-connector-sdk?style=for-the-badge)  
# Salt Edge Compliance Connector SDK (v2)
  
This codebase contains SDK module built on Spring Boot Framework, 
which is the set of tools for simplification of communication between ASPSP/Bank and Salt Edge PSD2 Compliance Solution.    
The SDK module implements the Salt Edge PSD2 Connector v2 API compatible with **Berlin Group NextGenPSD2 XS2A API**.  
[See how to add SDK](#SDK-Integration)  
When you migrate to the new version of Salt Edge Compliance Connector SDK, please read carefully [CHANGELOG](../SDK_CHANGELOG.MD) since many changes have been made.  
  
## Application Requirements
  
1. JDK, at least version 17
1. Spring Boot Framework, version `3.2.+`
1. Registration & API Keys  
   Follow the guide on how to [register and add API keys](https://priora.saltedge.com/docs/aspsp/v1#registrationandapikeys)  
  
## SDK Integration  

Target application should be compatible with [following requirements](#Application-Requirements)

### 1. Add SDK to target application.

#### Add as Maven dependency
Find last stable version of SDK in ![Maven Central](https://mvnrepository.com/artifact/com.saltedge.connector.sdk/compliance-connector-sdk).  
    
Add Maven Central repository to application's build.gradle
```groovy
 repositories {
    mavenCentral()
  }
```  
Add SDK dependency to `build.gradle` of application  
```groovy
  implementation 'com.saltedge.connector.sdk:compliance-connector-sdk:x.x.x'
```  

SDK requires next third-party dependencies:  
```groovy
  implementation 'org.springframework.boot:spring-boot-starter'
  implementation 'org.springframework.boot:spring-boot-starter-web'
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

  def jjwtVersion = '0.12.5'
  implementation "io.jsonwebtoken:jjwt-api:$jjwtVersion"
  runtime "io.jsonwebtoken:jjwt-impl:$jjwtVersion", "io.jsonwebtoken:jjwt-jackson:$jjwtVersion"
  implementation "io.jsonwebtoken:jjwt-impl:$jjwtVersion"
  implementation "io.jsonwebtoken:jjwt-jackson:$jjwtVersion"
```  
    
#### Add as project module
  1. Fork this repository
  2. In IntelliJ IDE Import module `File/New/Import Module...` in your project
  3. Build and run application on target device or emulator
    
### 2. Setup application as [described before](../example/README.MD#example-application-quick-setup).

### 3. Add SDK package (`com.saltedge.connector.sdk`) to component scan annotation in Application class.

```java
  @SpringBootApplication(scanBasePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  @EnableJpaRepositories(basePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  @EntityScan(basePackages = {CURRENT_APP_PACKAGE, SDKConstants.CONNECTOR_PACKAGE})
  public class ExampleApplication {
   
  }
```
    
### 4. Store extra data of payment.
  
The Connector will pass extra data on Payment creation and will require it back at the end of the Payment authentication.
Add `extra` field (if not exist) for storing extra data from Salt Edge Compliance solution (e.g. `endToEndIdentification`) to `Payment` entity of application.  
  
**_This solution of storing of extra payment data is not mandatory and can be implemented in another convenient way._**     
  
### 5. Create a service designated for providing information to and receiving events from `Salt Edge PSD2 Compliance`:  

This service should create on application class which implement `ProviderServiceAbs` interface and should have `@Service` annotation because it is auto-wired in SDK;
See interface [ProviderServiceAbs.java](src/main/java/com/saltedge/connector/sdk/provider/ProviderServiceAbs.java)  
See template [ExampleProviderService.java](ExampleProviderService.java)       
    
### 6. Use Connector's SDK callback service `ConnectorSDKCallbackService` for callback communication (e.g. notifying SDK about events):

See interface [ConnectorCallbackAbs.java](src/main/java/com/saltedge/connector/sdk/provider/ConnectorSDKCallbackService.java)
  
* `isUserConsentRequired(sessionSecret)` - check if User Consent (Bank Offered Consent) is required for authorization session determined by sessionSecret:
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;

* `onAccountInformationAuthorizationSuccess(sessionSecret, userId, accessToken, consents)` - notify SDK about the successful authorization if User for AIS flow:  
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;       - `userId` - unique identifier of Provider's system User (Customer);  
  * `accessToken` unique access identifier;  
  * `consents` ProviderOfferedConsents object with user consent to provide account information;  

* `onAccountInformationAuthorizationFail(sessionSecret)`- notify SDK about the failed authorization:  
  * `sessionSecret` unique authorization secret code passed to application via `getAccountInformationAuthorizationPageUrl(...)`;

* `updatePaymentFundsInformation(fundsAvailable, extra, status)`- notify SDK about funds availability and intermediate payment status:
  * `fundsAvailable` **[optional]** a value that indicates whether user have enough funds to make a payment;
  * `extra` extra data from authenticated Payment passed to application via `getPaymentAuthorizationPageUrl(...)`;
  * `status` **[optional]** an intermediate payment status;
* `onPaymentInitiationAuthorizationSuccess(paymentId, userId, extra)` - notify SDK about the successful authorization if User for AIS flow:
  * `userId` unique identifier of Provider's system User (Customer);  
  * `extra` extra data from authenticated Payment passed to application via `getPaymentAuthorizationPageUrl(...)`;  
* `onPaymentInitiationAuthorizationFail(paymentId, extra)`- notify SDK about the failed authorization:  
  * `extra` extra data from authenticated Payment passed to application via `getPaymentAuthorizationPageUrl(...)`;
      
* `revokeAccountInformationConsent(userId, accessToken)`- Revoke Account information consent associated with userId and accessToken;
  * `userId` unique identifier of Provider's system User (Customer);
  * `accessToken` unique access identifier;
  
### 7. Configuration  
Configuration properties application.yml  
```yaml
spring:
  profiles: prod
connector:
  private_key_name: connector_private_prod.pem
  private_key_pem: -----BEGIN PRIVATE KEY-----\nXXXXX\n-----END PRIVATE KEY-----
  private_key_file_path: /Users/bank/connector_private_prod.pem
  priora:
    app_code: XXXXXXXXXX
    app_id: YYYYYYYYY
    app_secret: ZZZZZZZZZZ
    base_url: https://priora.saltedge.com/
    public_key_name: priora_public_prod.pem
    public_key_pem: -----BEGIN PUBLIC KEY-----\nXXXXX\n-----END PUBLIC KEY-----
    public_key_file_path: /Users/bank/priora_public_prod.pem
```   

Is required to be defined one of `private_key` fields and one of `public_key` fields.
  
### Notes:
* SDK implements `ApiExceptionsHandler` which extends `ResponseEntityExceptionHandler` with order of execution `0`.  

## [Api Documentation](https://priora.banksalt.com/docs/aspsp/v2)
  
---
## [LICENSE](../LICENSE.txt)

---
Copyright © 2020 Salt Edge. www.saltedge.com
