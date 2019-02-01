gradle bootJar
sleep 1
java -jar ./build/libs/jsql-api.jar --spring.datasource.url=jdbc:postgresql://46.41.138.32:5432/test_jsql_db