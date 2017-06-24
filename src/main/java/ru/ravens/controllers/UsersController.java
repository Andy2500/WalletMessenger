package ru.ravens.controllers;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/users")
public class UsersController {

    @GET
    @Path("/reg/")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] test() {
        return new String[]{"<%@ page contentType=\"text/html;charset=UTF-8\" language=\"java\" %>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <title>$Title$</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "  $END$\n" +
                "  </body>\n" +
                "</html>\n", "blablabla"};
    }
}
