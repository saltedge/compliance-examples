# Salt Edge Compliance Connector SDK Changelog

## 2.12.5 (2024-10-29)
### Add
- Add getAisTokens, getPiisTokens methods.
### Update
- Fix consent status management for edge cases.

## 2.14.3 (2024-10-29)
### Add
- Add getAisTokens, getPiisTokens methods.
### Update
- Fix consent status management for edge cases.

## 2.14.2 (2024-08-06)
### Add
- Add tpp_app_name to payment extra.

## 2.12.4 (2024-08-06)
### Add
- Add tpp_app_name to payment extra.

## 2.12.3 (2024-07-18)
### Dependency Upgrades
- Add Detailed Consent feature
- Update Spring Boot to 2.7.18
- Update JJWT version to 0.12.6 (Fix CVE-2024-31033).
  
## 2.14.1 (2024-07-12)
### Update
- Update JJWT version to 0.12.6 (Fix CVE-2024-31033).

## 2.14.0 (2024-07-06)
### Add
- Add Detailed Consent feature

## 2.13.0 (2024-05-08)
### Dependency Upgrades
- Update Spring Boot to 3.2.5
- Update Java to 17

## 2.11.2 (2022-07-11)
### Dependency Upgrades
- Update to Spring Boot 2.7.1
- Update to Spring Dependency Management 1.0.12.RELEASE
- Update to JJWT 0.11.5

## 2.12.2 (2022-07-04)
### Dependency Upgrades
- Update to Spring Boot 2.7.1
- Update to Spring Dependency Management 1.0.12.RELEASE
- Update to JJWT 0.11.5

## 2.12.1 (2022-04-01)
### Dependency Upgrades
- Update to Spring Boot 2.6.6 (fix for CVE-2022-22965)

## 2.11.1 (2022-04-01)
### Dependency Upgrades
- Update to Spring Boot 2.6.6 (fix for CVE-2022-22965)

## 2.12.0 (2022-03-21)
### Change:
- Add Funds Confirmation Feature
- Add new domain model `PiisToken` (migration could be required)

## 2.11.0.1 (2022-03-01)
### Change:
- Update fields for TransactionExtra

## 2.11.0 (2022-02-25)
### Change:
- Update Spring Boot version up to 2.6.3

## 2.10.0 (2022-02-18)
### Change:
- Update object parameters and validation

## 2.9.0 (2021-12-03)
### Add:
- Add updatePaymentFundsInformation() feature for payment session update callback

### Change:
- Update parameters for the session update request

## 2.8.0 (2021-10-17)
### Add:
- Add support of `psu_ip_address` header in AIS token creation and PIS initiation

## 2.7.0.2 (2021-08-31)
### Add:
- Add execution priority order `0` for `ApiExceptionsHandler`

## 2.7.0 (2021-08-17)
### Add:
- Add `private_key_file_path` field to application properties
- Add `public_key_file_path` field to application properties
- Add `org.springframework.boot:spring-boot-starter-validation` dependency

## 2.6.1 (2021-07-01)
### Change:
- fix payment validation scheme
  
## 2.6.0 (2021-07-01)
### Add:
- add `creditorAgentName` on payment create 

### Change:
- set Debtor account params optional on payment create
- set `extra` as string on payment create
- set `ProviderServiceAbs.createPayment(...)` return payment authentication page url
- set `ProviderServiceAbs.createFPSPayment(...)` return payment authentication page url

### Remove:
- remove `ProviderServiceAbs.getPaymentAuthorizationPageUrl(...)`

## 2.5.2 (2021-01-11)
### Add:
- Add a getter and a setter for `ApplicationProperties.Priora.publicKeyPem` property 

## 2.5.1 (2021-01-06)  
### Add:  
- Add `public_key_pem` field to properties

### Change:
- Refactor ResourceTools
- Replace printStackTrace with logger

## 2.5.0 (2020-12-01)
Add support for the FPS (Faster Payment Service)  

### Add:  
- Add `sort_code` field to the Account.
- Create ParticipantAddress class
- Create ProviderServiceAbs.createFPSPayment(...)
- Add support of private key as string in configuration

### Change:  
- Move `name` field from Transaction.ParticipantDetails.Account to the Transaction.ParticipantDetails.
- Modify ProviderServiceAbs.createPayment(...)
- Modify ConnectorSDKCallbackService (Add `paymentProduct` param)
- Update JJWT version up to 0.11.2   
  
## 2.4.3 (2020-11-02)
### Change:  
- Add targetCompatibility 1.8

## 2.4.2 (2020-10-23)
### Change:  
- First Non-fat JAR

## 2.4.1 (2020-07-15)
### Change:  
- Fix payload format in `token revoke` callback request.

## 2.4.0 (2020-06-12)
### Add:  
- Pagination support for transactions and card transactions

## 2.3.3 (2020-05-27)
### Remove:  
- Remove redundant application properties: `public_key_name`, `connection_expires_in_minutes`.
  
## 2.3.2 (2020-05-13)
### Add:  
- Add `fail session` callback call to ConnectorSDKCallbackService.onAccountInformationAuthorizationFail(...).  

## 2.3.1 (2020-05-08)
### Add:  
- Add valid_until param to Consent (Token) create request.   
  This param is set by TPP. Consent (Token) life period can not be more than 180 days from now.     
  
### Change:  
- In ConnectorSDKCallbackService deprecated onAccountInformationAuthorizationSuccess(...) method.  
  Replaced with onAccountInformationAuthorizationSuccess(...) without accessTokenExpiresAt param.
  
### Remove:
- Removed tokenExpiresAt param in session success callback  
  
## 2.3.0 (2020-04-28)
### Add:
- Global Consent support (TPP offered consent)

## 2.2.0 (2020-04-16)
### Add:
- Funds confirmation feature for Payment Instrument Issuer Service Providers

## 2.1.1 (2020-04-08)
### Add:
- ConnectorCallbackService.revokeAccountInformationConsent(...)

### Change:
- Replaced Date object with Instant/LocalDate object
- Updated `JJWT` version up to 0.11.1

## 2.1.0 (2020-03-24)
### Add:
- Berlin group PISP flow
  
## 2.0.0 (2020-02-27)
### Add:
- Berlin group AISP flow

---
Copyright © 2020 Salt Edge. https://www.saltedge.com
