# Salt Edge Compliance Connector Java Example & SDK (v2)

This codebase is a full-stack application built on Spring Boot Framework.  
Is designated to demonstrate (simulate) communication between ASPSP/Bank and TPP/Client.  
_This application is just a Proof Of Concept._  
[See how to setup application](example/README.MD).
  
This codebase contains SDK module which is the set of tools for simplification of communication between ASPSP/Bank and Priora PSD2 Compliance Solution.    
The SDK module implements the Priora Connector v2 API compatible with **Berlin Group NextGenPSD2 XS2A API**.
[See how to add SDK to your application](saltedge-connector-sdk/README.MD)  
  
> `Priora` is the alias of Salt Edge PSD2 Compliance Solution
  
### Codebase consists of modules:
* **example** - simulates work of ASPSP/Bank Application.  
* **saltedge-connector-sdk** - set of tools for integration in existing Spring Applications.   
  
## Application Requirements
1. JDK, at least version 8 
1. Spring Boot Framework, at least version 2.2.+
1. Registration & API Keys  
   Follow the guide on how to [register and add API keys](https://priora.saltedge.com/docs/aspsp/v1#registrationandapikeys)

## [How To Quick Setup Example Application](example/README.MD)  
  
## [How To Integrate Connector SDK In Your Application](saltedge-connector-sdk/README.MD)  
  
<<<<<<< HEAD
=======
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
  
  
## [Api Documentation](https://priora.saltedge.com/docs/aspsp/v2)
  
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
   
>>>>>>> master
---
## [LICENSE](LICENSE.txt)

---
Copyright © 2020 Salt Edge. https://www.saltedge.com
