FROM httpd:2.4

EXPOSE 80
EXPOSE 443

COPY ./dist/ /usr/local/apache2/htdocs/
COPY ./docker/httpd.conf /usr/local/apache2/conf/httpd.conf

RUN apt-get -qq  update; apt-get -qq -y --force-yes install curl
