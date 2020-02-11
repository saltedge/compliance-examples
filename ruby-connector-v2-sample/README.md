## This is a demo application that is strictly not recommended to be used in a production environment.

### Run connector
1. $ `bundle`
2. $ `rails credentials:edit`
> *copy content from config/credentials.example.yml*
```yml
appliction:
  host: "localhost:8000"
  url: "http://ceb39cf4.ngrok.io" # expose it into external network
priora:
  app_id: app_id # your app-id from priora.saltedge.com/providers dashboard
  app_secret: app_secret # your app-id from priora.saltedge.com/providers dashboard
  private_key_file: "config/private_key.pem" # generate a 4096 rsa key for JWT
  public_key: "config/priora_public_key.pem" # priora.saltedge.com public key from documentation
sidekiq: # for async jobs
    port: 6379
    host: "localhost"
    database: 0
    namespace: "connector"
    password: ""
devise:
  secret_key: "test" # change it
```

3. $ `cp config/database.example.yml config/database.yml` and adjust `config/database.yml`
4. $ `bundle exec rake db:create db:migrate`
5. $ `rails s -p 8000`
6. $ `bundle exec sidekiq`
