package ru.ravens.controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/conv")
public class ConversationController {

    @GET
    @Path("/test/{a}")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@PathParam("a") Integer a)
    {
        return a.toString();
    }
}
