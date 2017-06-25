package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.GroupInfo;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    //После полного тестирования GET запросов, надо будет поменять на POST, для более удобного тестирования в браузере
    //После полного тестирования метода изменяйте заголовки на такие:

    @GET
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про групповой диалог
    public GroupInfo getGroupDialog(@FormParam("token")String token, @FormParam("dialogID") String dialogID)
    {
        try{
            GroupInfo groupInfo = GroupInfo.getGroupInfoById(Integer.valueOf(dialogID));
            groupInfo.getDefaultClass().setOperationOutput(true);
            groupInfo.getDefaultClass().setToken(token);
            return groupInfo;
        } catch (Exception ex){
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return groupInfo;
        }
    }

    @GET
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в групп чат
    public GroupInfo sendTransToGroupDialog(@FormParam("token")String token, @FormParam("dialogID") String dialogID,
                                            @FormParam("money")String money, @FormParam("cash") String cash, @FormParam("text") String text)
    {
        try{
            GroupInfo groupInfo = GroupInfo.getGroupInfoById(Integer.valueOf(dialogID));
            Transaction.SendTransactionDialog(User.getUserByToken(token).getUserID(), Integer.valueOf(dialogID),
                    Integer.valueOf(money), Integer.valueOf(cash), text);
            groupInfo.getDefaultClass().setOperationOutput(true);
            groupInfo.getDefaultClass().setToken(token);
            return groupInfo;
        } catch (Exception ex){
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return groupInfo;
        }
    }


}
