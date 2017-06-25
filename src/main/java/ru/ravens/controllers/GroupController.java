package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.GroupInfo;
import ru.ravens.models.InnerModel.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    //После полного тестирования GET запросов, надо будет поменять на POST, для более удобного тестирования в браузере
    //После полного тестирования метода изменяйте заголовки на такие:

    @GET
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про диалог
    public GroupInfo getGroupDialog(@FormParam("token")String token, @FormParam("dialogID") String dialogID)
    {
        try{
            User user = User.getUserByToken(token);
            return GroupInfo.getGroupInfoById(Integer.valueOf(dialogID));
        } catch (Exception ex){
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return groupInfo;
        }
    }

}
