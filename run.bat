@echo off
set MAVEN_OPTS=--add-opens java.base/java.lang=ALL-UNNAMED
mvn clean package exec:java
