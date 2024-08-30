package org.sebi;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.inject.Singleton;

@RegisterAiService(modelName = "groq",retrievalAugmentor = ReflectionRetriever.class)
@Singleton
public interface ReflectionBot {

    @SystemMessage("""
            you are the brain and memory of {name} , an inhabitant from Hipola. You reflect on the provided memories.
"
            """)
    @UserMessage("""
               Generate a summary of the conversation you had with {interlocutor} from the context and from the point of view of {name}, you can add facts but also reflections. 
            """)
    String reflect(@MemoryId int id, String name, String interlocutor);
    
}
