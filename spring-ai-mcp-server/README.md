# Spring AI MCP Server

This folder contains a basic MCP server sample application for my
[MCP with Spring AI](https://beaufume.fr/articles/spring-ai-mcp/) article.

It is implemented in Java with Spring Boot and Spring AI, and takes the form of a web application.

This application does not use any LLM. The LLM is on the MCP client side.

Start the application with `mvnw spring-boot:run` or from your IDE, then use the `http://localhost:8080/mcp` URL
in your local MCP client.

Notable source files:

- [pom.xml](pom.xml) declares the dependency management for Spring AI as well as the right starters.
- [application.properties](src/main/resources/application.properties) contains the configuration for Spring AI.
- [WeatherService.java](src/main/java/com/adeliosys/sample/service/WeatherService.java) is a sample business service
  used as a tool by the LLM to get weather information.