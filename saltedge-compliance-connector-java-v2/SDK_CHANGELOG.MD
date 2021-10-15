# Salt Edge Compliance Connector SDK Changelog

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
  This param is set by TPP. Consent (Token) life period can not be more than 90 days from now.     
  
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