# Webchat

An online chat website based on:

Traditional Vue3 + Element plus  + Springboot + Redis + Keycloak + Docker

## Recommended IDE Setup

[Idea](https://www.jetbrains.com.cn/idea/)

## Project Setup

```sh
# change urls to localhost
mvn clean install
sh build.sh

cd keycloak
sh build.sh

cd frontend
sh build.sh



# run qsign
docker run -d --restart=always --name qsign -p 8080:8080 -e BASE_PATH=/srv/qsign/qsign/txlib/8.9.63 xzhouqd/qsign:core-1.1.9
```
