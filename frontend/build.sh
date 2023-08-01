#!/bin/bash

docker stop ng
docker rm ng
docker build -t marszhu2541/webchat-frontend .
#docker push marszhu2541/webchat-frontend
docker run --name ng -p 80:80 -d marszhu2541/webchat-frontend

