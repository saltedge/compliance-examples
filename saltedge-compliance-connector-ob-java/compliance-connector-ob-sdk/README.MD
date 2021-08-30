![Maven Central](https://img.shields.io/maven-central/v/com.saltedge.connector.sdk/compliance-connector-ob-sdk?style=for-the-badge)  
# Salt Edge Compliance Connector SDK (Open Banking)
  
This codebase contains SDK module built on Spring Boot Framework, 
which is the set of tools for simplification of communication between ASPSP/Bank and Salt Edge PSD2 Compliance Solution.    
The SDK module implements the Salt Edge PSD2 Connector API compatible with **Open Banking API**.  
[See how to add SDK](#SDK-Integration)  
When you migrate to the new version of Salt Edge Compliance Connector SDK, please read carefully [CHANGELOG](SDK_CHANGELOG.MD) since many changes have been made.  
  
## Application Requirements
  
1. JDK, at least version 8 
1. Spring Boot Framework, at least version 2.5.+
3. Registration & API Keys. [Follow the guide](https://priora.saltedge.com/docs/aspsp/ob#registration_and_api_keys)
4. Private and public keys. [Follow the guide](https://priora.saltedge.com/docs/aspsp/ob#private_and_public_keys)  
  
## SDK Integration  

Target application should be compatible with [following requirements](#Application-Requirements)

### 1. Add SDK to target application.

#### Add as Maven dependency
Find last stable version of SDK in ![Maven Central](https://mvnrepository.com/artifact/com.saltedge.connector.sdk/compliance-connector-ob-sdk).  
    
Add Maven Central repository to application's build.gradle
```groovy
 repositories {
    mavenCentral()
  }
```  
Add SDK dependency to `build.gradle` of application  
```groovy
  implementation 'com.saltedge.connector.sdk:compliance-connector-ob-sdk:x.x.x'
```  

SDK requires next third-party dependencies:  
```groovy
  implementation "org.springframework.boot:spring-boot-starter:$springVersion"
  implementation "org.springframework.boot:spring-boot-starter-web:$springVersion"
  implementation "org.springframework.boot:spring-boot-starter-data-jpa:$springVersion"
  implementation "org.springframework.boot:spring-boot-starter-validation:$springVersion"

  def jjwtVersion = '0.11.2'
  implementation "io.jsonwebtoken:jjwt-api:$jjwtVersion"
  runtime "io.jsonwebtoken:jjwt-impl:$jjwtVersion", "io.jsonwebtoken:jjwt-jackson:$jjwtVersion"
  implementation "io.jsonwebtoken:jjwt-impl:$jjwtVersion"
  implementation "io.jsonwebtoken:jjwt-jackson:$jjwtVersion"
```  
    
#### Add as project module
  1. Fork this repository
  2. In IntelliJ IDE Import module `File/New/Import Module...` in your project
  3. Build and run application on target device or emulator
    
### 2. Setup application as [described before](../README.MD#example-application-quick-setup).

### 3. Add SDK package (`com.saltedge.connector.sdk`) to component scan annotation in Application class.

```java
  @SpringBootApplication(scanBasePackages = {APPLICATION_PACKAGE, SDKConstants.SDK_PACKAGE})
  @EnableJpaRepositories(basePackages = {APPLICATION_PACKAGE, SDKConstants.SDK_PACKAGE})
  @EntityScan(basePackages = {APPLICATION_PACKAGE, SDKConstants.SDK_PACKAGE})
  public class ConnectorApplication {
    public static final String APPLICATION_PACKAGE = "com.company.connector.ob";
  
    public static void main(String[] args) {
      SpringApplication.run(ConnectorApplication.class, args);
    }
  }
```
  
### 4. Create a service designated for receiving events from `Salt Edge PSD2 Compliance Service`:  

This service implement `ProviderServiceAbs` interface and should have `@Service` annotation because it is auto-wired by SDK;
See interface [ProviderServiceAbs.java](src/main/java/com/saltedge/connector/ob/sdk/provider/ProviderServiceAbs.java)  
See implementation example [ComplianceConnectorService.java](../src/main/java/com/saltedge/connector/example/compliance_connector/ComplianceConnectorService.java)       
    
### 5. Use Connector's SDK service `ConnectorSDKService` for notifying from Identity Service or Bank Core about authorization and payment events:

See [ConnectorSDKService.java](src/main/java/com/saltedge/connector/ob/sdk/provider/ConnectorSDKService.java)
  
### 6. Fill configuration  
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

---

## [Salt Edge API Documentation](https://priora.saltedge.com/docs/aspsp/ob)

## [LICENSE](../LICENSE.txt)

---
Copyright © 2021 Salt Edge. www.saltedge.com
