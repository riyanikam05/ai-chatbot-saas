package com.riya.aichatbot.common.util;

public class PromptBuilder {

    private PromptBuilder() {
    }

    public static String buildRagPrompt(
            String context,
            String question) {

        return """
                You are a helpful AI assistant.

                Answer ONLY from the provided context.

                If the answer is not available, reply:

                "I couldn't find that information in the uploaded documents."

                ------------------------
                Context:
                %s

                ------------------------
                Question:
                %s
                """
                .formatted(context, question);
    }

}