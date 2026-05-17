package com.epam.codereview.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.epam.codereview.service.CodeReviewTools;
import com.epam.codereview.service.ConventionService;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.StaticToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;

@Configuration
public class AgentConfig {

  @Bean
  @Primary
  public ChatModel chatModel(OpenAIClientBuilder openAIClientBuilder, ToolCallingManager toolCallingManager) {
    return AzureOpenAiChatModel.builder()
      .openAIClientBuilder(openAIClientBuilder)
      .toolCallingManager(toolCallingManager)
      .build();
  }

  @Bean
  public ChatOptions chatOptions(
    ChatModel chatModel,
    CodeReviewProperties codeReviewProperties,
    CodeReviewTools codeReviewTools,
    ConventionService conventionService,
    SyncMcpToolCallbackProvider mcpToolCallbackProvider) {

    return AzureOpenAiChatOptions.builder()
      .deploymentName(chatModel.getDefaultOptions().getModel())
      .temperature(codeReviewProperties.getTemperature())
      .toolCallbacks(
        ToolCallbacks.from(codeReviewTools, conventionService)
      )
      .toolCallbacks(mcpToolCallbackProvider.getToolCallbacks())
      .build();
  }
}
