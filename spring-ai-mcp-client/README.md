# Spring AI MCP Client

This folder contains a basic MCP client sample application for my
[MCP with Spring AI](https://beaufume.fr/articles/spring-ai-mcp/) article.

It is implemented in Java with Spring Boot and Spring AI, and takes the form of a command line application.

By default, it requires Ollama with the Gemma 4 model, so first install Ollama then run `ollama pull gemma4:12b`
to install the model.

If you want to use another Ollama model, update `application.properties`. I added some examples.

If you want to use another LLM provider (OpenAI, Mistral, etc.) use the right starter in `pom.xml`,
then declare the specific configuration (such as the API key) in `application.properties`.
See [Spring AI reference documentation](https://docs.spring.io/spring-ai/reference/) for more details.

Start the application with `mvnw spring-boot:run` or from your IDE.

Notable source files:

- [pom.xml](pom.xml) declares the dependency management for Spring AI as well as the right starters.
- [application.properties](src/main/resources/application.properties) contains the configuration for Spring AI.
- [SpringAiMcpClientApplication.java](src/main/java/com/adeliosys/sample/SpringAiMcpClientApplication.java)
  is the main class and calls the methods from `BusinessService`.
