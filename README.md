# Currency Exchange API

A simple foreign exchange application with Spring Boot and maven.

You can get the exchange rate of currency pairs, convert your currency to other currencies, and get the list of previous conversions. Uses [exchangerate.host](https://exchangerate.host/) as service provider for FX rates.

## build
Make sure you have [java 11](https://www.oracle.com/tr/java/technologies/javase/jdk11-archive-downloads.html) and [maven](https://maven.apache.org/download.cgi) installed and added to path.

```
mvn clean package
cd target
java -jar currency-exchange-0.0.1-SNAPSHOT.jar
```
## api-docs
http://localhost:8080/swagger-ui/index.html

http://localhost:8080/api-docs

