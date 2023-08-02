FROM nginx
LABEL authors="marszhu"
COPY ./index.html /usr/share/nginx/html
COPY ./main.js /usr/share/nginx/html
COPY ./static /usr/share/nginx/html/static
RUN chmod -R 755 /usr/share/nginx/html
