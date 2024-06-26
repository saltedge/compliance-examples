# Salt Edge Compliance Connector Java Example & SDK (Open Banking standard)

This codebase is a full-stack application built on Spring Boot Framework.  
Is designated to demonstrate (simulate) communication between ASPSP/Bank and TPP/Client.  
_This application is just a Proof Of Concept._  
    
This codebase contains SDK module which is the set of tools for simplification of communication between ASPSP/Bank and Salt Edge PSD2 Compliance Solution.    
The SDK module implements the Salt Edge PSD2 Compliance API compatible with **Open Banking API**.
  
> `Priora` is the alias of Salt Edge PSD2 Compliance Solution
   
## Application Requirements
1. JDK, at least version 8 
2. Spring Boot Framework, version 2.6.+, minimal version 2.6.6
3. Registration & API Keys. [Follow the guide](https://priora.saltedge.com/docs/aspsp/ob#registration_and_api_keys)
4. Private and public keys. [Follow the guide](https://priora.saltedge.com/docs/aspsp/ob#private_and_public_keys)
  
## [How To Integrate Connector SDK In Your Application](compliance-connector-ob-sdk/README.MD)

## Example Application Quick Setup

#### 1. Clone the project

#### 2. Navigate to project's root folder

```bash
cd saltedge-compliance-connector-ob-java
```

#### 3. Add secret keys

**SDK expects private key in PKCS#8 format**  
You can generate your RSA key pair using this commands:
```bash
openssl genpkey -out connector_private_prod.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in connector_private_prod.pem -out connector_public_prod.pem
```  
Add the content of Public Key (`connector_public_prod.pem)` in Connector [Environment](https://priora.saltedge.com/providers/environments).     
Save your private key generated by openssl in `/resources` folder of application.

#### 4. Create configuration files

```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
cp src/main/resources/application.example.properties src/main/resources/application.properties
```
  
#### 5. Fill configuration files

* Example application uses a `H2 in memory` database (for now), can be changed easily in the `application.properties` for any other database type.
* Set your external host name in `application.properties`
```yaml
app.url=https://my_host.org
```  
* Configuration properties application.yml
```yaml
spring:
  profiles: prod
connector:
  private_key_name: connector_private_prod.pem
  private_key_pem: -----BEGIN PRIVATE KEY-----\nXXXXX\n-----END PRIVATE KEY-----
  private_key_file_path: /Users/bank/connector_private_prod.pem
  priora:
    app_code: spring_connector_example_md
    app_id: xxxxxxxxx
    app_secret: xxxxxxxxx
    base_url: https://priora.saltedge.com/
    public_key_name: priora_public_prod.pem
    public_key_pem: -----BEGIN PUBLIC KEY-----\nXXXXX\n-----END PUBLIC KEY-----
    public_key_file_path: /Users/bank/priora_public_prod.pem
```

Is required to be defined one of `private_key` fields and one of `public_key` fields.

## [Salt Edge API Documentation](https://priora.saltedge.com/docs/aspsp/ob)

## [LICENSE](LICENSE.txt)

---
Copyright © 2021 Salt Edge. www.saltedge.com
