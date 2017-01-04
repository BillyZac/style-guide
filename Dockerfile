FROM tutum/nginx
RUN rm /etc/nginx/sites-enabled/default
ADD ./nginx /etc/nginx
COPY build /usr/src/app
