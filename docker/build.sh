#!/bin/bash
docker build -t marszhu2541/webchat-backend .
docker push marszhu2541/webchat-backend
docker run --name ng -p 80:80 -d  marszhu2541/webchat-backend

