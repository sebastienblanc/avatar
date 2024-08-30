package org.sebi;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/start")
public class AvatarResource {

    @Inject
    Avatar avatar;

    @GET
    @Path("/{interlocutor}")
    @Blocking
    public void initChat(@PathParam("interlocutor") String interlocutor){
        Log.info(interlocutor);
        avatar.initConversation(interlocutor);
    }
    
}
