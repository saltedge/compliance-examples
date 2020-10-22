# Salt Edge Compliance Connector Java Example & SDK (v2)

This codebase is a full-stack application built on Spring Boot Framework.  
Is designated to demonstrate (simulate) communication between ASPSP/Bank and TPP/Client.  
_This application is just a Proof Of Concept._  
[See how to setup application](example/README.MD).
  
This codebase contains SDK module which is the set of tools for simplification of communication between ASPSP/Bank and Priora PSD2 Compliance Solution.    
The SDK module implements the Priora Connector v2 API compatible with **Berlin Group NextGenPSD2 XS2A API**.
[See how to add SDK to your application](compliance-connector-sdk/README.MD)  
  
> `Priora` is the alias of Salt Edge PSD2 Compliance Solution
  
### Codebase consists of modules:
* **example** - simulates work of ASPSP/Bank Application.  
* **compliance-connector-sdk** - set of tools for integration in existing Spring Applications.   
  
## Application Requirements
1. JDK, at least version 8 
1. Spring Boot Framework, at least version 2.2.+
1. Registration & API Keys  
   Follow the guide on how to [register and add API keys](https://priora.saltedge.com/docs/aspsp/v1#registrationandapikeys)

## [How To Quick Setup Example Application](example/README.MD)  
  
## [How To Integrate Connector SDK In Your Application](compliance-connector-sdk/README.MD)  
  
---
## [LICENSE](LICENSE.txt)

---
Copyright © 2020 Salt Edge. https://www.saltedge.com
