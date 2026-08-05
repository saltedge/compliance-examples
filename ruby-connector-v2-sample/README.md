## This is a demo application of SaltEdge Connector API v2  
Strictly not recommended to be used in a production environment as-is.

### SaltEdge Connector API v2  
https://priora.saltedge.com/docs/aspsp/v2

### SaltEdge BG1.3 TPP API
https://priora.saltedge.com/docs/berlingroup


### Run connector
1. $ `bundle`
2. Setup config
$ `rails credentials:edit`
> *copy content from config/application.example.yml*
```yml
defaults: &defaults
  host: "localhost:3000"
  url: "http://localhost:3000" # expose it into external network
  priora:
    host: "https://priora.banksalt.com" # priora API host, differs between dev and prod
    app_id: app_id # your app-id from priora.saltedge.com/providers dashboard
    app_secret: app_secret # your app-id from priora.saltedge.com/providers dashboard
    private_key_file: "config/private_key.pem" # connector's key. generate a 4096 rsa key for JWT
    public_key: "config/priora_public_key.pem" # priora.saltedge.com public key from documentation
  application: &application
    url: "http://localhost:3000"
    host: "localhost:3000"
    domain: "connector.com"
  sidekiq: # for async jobs
    port: 6379
    host: "localhost"
    database: 0
    password: ""
  devise:
    secret_key: "test" # change it
  
development:
  <<: *defaults

test:
  <<: *defaults
```

3. $ `cp config/database.example.yml config/database.yml` and adjust `config/database.yml`
4. $ `bundle exec rake db:create db:migrate`
5. $ `rails s -p 8000`
6. $ `bundle exec sidekiq`

---
Copyright © 2015 Salt Edge. https://www.saltedge.com
