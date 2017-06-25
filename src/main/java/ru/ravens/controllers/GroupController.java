package ru.ravens.controllers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    //После полного тестирования GET запросов, надо будет поменять на POST, для более удобного тестирования в браузере
    //После полного тестирования метода изменяйте заголовки на такие:

    @POST
    @Path("/test/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@FormParam("a") Integer a) {
        return a.toString();
    }
}
