# kafkaAuthenticationAndAuthorized

Create Differet type for producer/consumer for kakfa security at client side

## Build the project
```
mvn package
```

## Prerequisites
- java 8

## Test
Test Authentication and Authorized process using KakfaProducerUsingSaslAclWithNoSSL or PlainLogin Module process with ACL which is  part is jass config and ACL is stored in Zookeeper and verified using SimpleAclAuthorizer
Test Authentication and Authorized process using KakfaProducerUsingOauthBearerAclWithNoSSL/KafkaConsumerUsingOauthBearerAclWithNoSSL for OauthBearer Client Crediential process with ACL which is is part fot JWT Token Sub 
