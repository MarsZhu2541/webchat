#!/bin/bash
docker build -t marszhu2541/webchat-backend:2700547209 .
docker push marszhu2541/webchat-backend:2700547209
#docker stop bs
#docker rm bs
sudo docker run -d --name bs -p 6379:6379 marszhu2541/webchat-backend:2700547209

