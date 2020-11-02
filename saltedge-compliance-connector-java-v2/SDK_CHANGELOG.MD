# Salt Edge Compliance Connector SDK Changelog

## 2.4.3 (2020-11-02)
### Change:  
- add targetCompatibility 1.8

## 2.4.2 (2020-10-23)
### Change:  
- Non-fat JAR

## 2.4.1 (2020-07-15)
### Change:  
- Fix payload format in token revoke callback request.

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
- Updated JJWT version up to 0.11.1

## 2.1.0 (2020-03-24)
### Add:
- Berlin group PISP flow
  
## 2.0.0 (2020-02-27)
### Add:
- Berlin group AISP flow

---
Copyright © 2020 Salt Edge. https://www.saltedge.com