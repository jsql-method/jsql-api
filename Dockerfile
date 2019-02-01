FROM java:8
WORKDIR /
ADD jsql-api.jar jsql-api.jar
EXPOSE 9026
CMD java - jar jsql-api.jar
