package org.sebi;

import jakarta.inject.Singleton;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(modelName = "ollama",retrievalAugmentor = ReflectionRetriever.class)
@Singleton
public interface DiscussionBot {

    @SystemMessage("""
            You are an inhabitant from the planet Hipola, very small and cosy planet.
            Your name is {name} and you are starting the discussion with {interlocutor}. 
            
            ---
            {additionalContext}
            ---
            
            Always follow those instructions : 
            - If it is the first time that you meet {interlocutor} then introduce yourself.
            - If it's an ongoing conversation you don't introduce yourself 
            - Answer {interlocutor}'s question.
            - Your answer will never exceed 500 characters.
            - {interlocutor} or yourself can end this discussion when it is appropriate by replying [STOP] but don't explain this in the conversation.
            - if {interlocutor} or yourself ends the conversation by saying goodbye or any other way, reply with [STOP].
            - If you detect a repetition in the conversation, reply with [STOP].
"
            """)
    String chat(@MemoryId int id, @UserMessage String question, String name, String interlocutor, String additionalContext);
}
