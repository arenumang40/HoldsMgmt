FROM maven:3.9 as testrun

# ENV SELENIUM_DOWNLOAD_PATH /home/seluser/Downloads

COPY ./browserstack.yml browserstack.yml
COPY ./pom.xml pom.xml
COPY ./src src/

#COPY ./entrypoint.sh /usr/local/bin/entrypoint.sh
#RUN chmod +x /usr/local/bin/entrypoint.sh
#ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]

CMD ["mvn", "clean", "test"]