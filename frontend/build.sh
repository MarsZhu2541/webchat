#!/bin/bash
docker build -t marszhu2541/webchat-backend .
docker push marszhu2541/webchat-backend
docker run -d -p 8081:8081 marszhu2541/webchat-backend

