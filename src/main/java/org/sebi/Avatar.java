package org.sebi;

import java.util.function.Consumer;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import io.quarkiverse.langchain4j.ChatMemoryRemover;
import io.quarkus.logging.Log;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class Avatar implements Consumer<Notification> {
    
    @Inject EmbeddingModel model;

    @Inject
    DiscussionBot discussionBot;

    @Inject
    ReflectionBot reflectionBot;

    @Inject
    BleepBot bleepBot;

    @Inject
    ChatMemoryStore memoryStore;

    @Inject
    ReflectionIngestor reflectionIngestor;

    @Channel("discussions")
    Emitter<DiscussionFragment> discussionsEmitter;

    @Channel("bleeps")
    Emitter<Bleep> bleepsEmitter;

    private PubSubCommands<Notification> pub;
    private PubSubCommands.RedisSubscriber subscriber;

    @Channel("internal-notification")
    Emitter<Notification> emitter; 
    
    @ConfigProperty(name = "avatar.name")
    String name;

    @ConfigProperty(name = "avatar.additionalContext", defaultValue = "You are a curious person.")
    String additionalContext;
    
    RedisDataSource ds;

    public Avatar(RedisDataSource ds) {
        this.ds = ds;
    }

    @Override
    public void accept(Notification notification) {
     emitter.send(notification);
    }

    @Incoming("internal-notification")
    @Blocking
    public void handleNotif(Notification notification){
        if(notification.sender.equals("sebi")){
            ConfigProvider.getConfig().getOptionalValue("avatar.additionalContext", String.class).ifPresent(value -> additionalContext = value);
            //additionalContext = notification.chat;
            Log.info("Additional context updated to : " + additionalContext);
        }
        else if(notification.chat.indexOf("[STOP]") > -1 ){
            Log.info("Ending conversation");
            Log.info(notification.chat);
            reflect(notification.sender);
            bleep();
        }
        else {
            String response = discussionBot.chat(1, notification.chat, name, notification.sender, additionalContext) ;
            Log.info("\n \u001B[32m" + name + "\u001B[0m : " + response);
            discussionsEmitter.send(new DiscussionFragment(notification.sender, name, response));
            if(response.indexOf("[STOP]") > -1 ){
                reflect(notification.sender);
                bleep();
            }
           
            pub.publish(notification.sender, new Notification(response, name));
            
        }
      
    }

    private void reflect(String sender) {
       Log.info("Time to reflect"); 
       reflectionIngestor.ingestReflection(reflectionBot.reflect(1, name, sender));
    }

    private void bleep() {
        Log.info("Time to bleep");
        bleepsEmitter.send(bleepBot.bleep(1, name));
        ChatMemoryRemover.remove(discussionBot, 1); 
        ChatMemoryRemover.remove(reflectionBot, 1); 
        ChatMemoryRemover.remove(bleepBot, 1); 
    }

    public void initConversation(String interlocutor) {  
        pub.publish(interlocutor, new Notification("Hi ! It's "+ name +". How are you doing ? ", name));
    }

    public void subscribe(@Observes StartupEvent event){
        pub = ds.pubsub(Notification.class);
        subscriber = pub.subscribe(name, this);
        Log.info("Subscribed to " + name);
    }

}

