package org.sebi;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(modelName = "groq", retrievalAugmentor = ReflectionRetriever.class)
public interface BleepBot {

    @SystemMessage("""
            You are the brain and memory of {name} , an inhabitant from Hipola. 
            On Hipolal, there is a social media called Bleep. Based on the memories of you recent discussions, you are asked to generate a short text that could be posted on Bleep.
            You will always answer with a JSON document, and only with this JSON document.
            """)
    @UserMessage("""
            Generate a message like you would write a tweet summing up your feelings and thoughts about the recent discussions.: 
            * You can add hashtags.
            * The message must not be addressed to anyone in particular.
            * The maximum length of the text is 350 characters.
            * it should be returned as a JSON object and only that with the following structure:
              - the key 'avatar'  with the value {name}
              - the key 'message' with the value of the generated message
            Example: 
            {
                "avatar": "John",
                "message": "What a nice evening #life #friends #fun #Hipola #Bleep"
            }
            """)
    @Fallback(fallbackMethod = "bleepFallback")
    @Retry(maxRetries = 3)
    Bleep bleep(@MemoryId int id, String name);
    
    default Bleep bleepFallback(int id, String name) {
        return new Bleep(name, "I am a bot and I am generating this message because I am unable to generate a message based on the recent discussions.");
    }
}
