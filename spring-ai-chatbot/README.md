# Spring AI Chatbot

This folder contains a basic chatbot sample application for my
[Chatbot with Spring AI](https://beaufume.fr/articles/spring-ai-chatbot/) article.

It is implemented in Java with Spring Boot and Spring AI, and takes the form of a web application.
The UI is a simple HTML page provided by this application.

![Chatbot UI](doc/chatbot-ui.png)

By default, it requires Ollama with the Gemma 4 model, so first install Ollama then run `ollama pull gemma4:12b`
to install the model.

If you want to use another Ollama model, update `application.properties`. I added some examples.

If you want to use another LLM provider (OpenAI, Mistral, etc.) use the right starter in `pom.xml`,
then declare the specific configuration (such as the API key) in `application.properties`.
See [Spring AI reference documentation](https://docs.spring.io/spring-ai/reference/) for more details.

Start the application with `mvnw spring-boot:run` or from your IDE.

Notable source files and folders:

- [pom.xml](pom.xml) declares the dependency management for Spring AI as well as the right starters.
- [application.properties](src/main/resources/application.properties) contains the configuration for Spring AI.
- [AiConfig.java](src/main/java/com/adeliosys/sample/config/AiConfig.java) defines the AI beans used
  by other components.
- [ChatController.java](src/main/java/com/adeliosys/sample/api/ChatController.java) exposes the chat endpoint.
- [ConversationController.java](src/main/java/com/adeliosys/sample/api/ConversationController.java) exposes the
  endpoints to manage the persisted conversations.
- [WeatherService.java](src/main/java/com/adeliosys/sample/service/WeatherService.java) is a sample business service
  used as a tool by the LLM to get weather information.
- [index.html](src/main/resources/static/index.html) is the UI.
- [bruno](bruno) this folder contains a Bruno collection for the chatbot endpoints (chat, manage conversations).
