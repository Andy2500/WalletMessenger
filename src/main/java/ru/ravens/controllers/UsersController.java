package ru.ravens.controllers;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class UsersController {

    @GET
    @Path("/test/{a}")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@PathParam("a") Integer a) {
        return a.toString();
    }
}
