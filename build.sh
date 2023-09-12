#!/bin/bash
#docker build -t marszhu2541/webchat-backend:2700547209 .
#docker push marszhu2541/webchat-backend:2700547209
docker stop bs
docker rm bs
docker run -d --name bs -p 8081:8081 marszhu2541/webchat-backend:2700547209

