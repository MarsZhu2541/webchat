#!/bin/bash
docker commit dreamy_kowalevski keycloak
docker tag keycloak marszhu2541/keycloak
docker push marszhu2541/keycloak
#docker run -d --name keycloak -p 8180:8080 marszhu2541/keycloak

