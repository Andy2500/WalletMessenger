package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DialogInfo;
import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/dialog")
public class DialogController {
    @GET
    @Path("/test/{a}")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@PathParam("a") Integer a) {
        return a.toString();
    }

    @GET
    @Path("/get/")
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про диалог
    public DialogInfo getDialogs(@FormParam("token")String token, @FormParam("conversationID") String convID)
    {
        try{
            User user = User.getUserByToken(token);
            DialogInfo dialogInfo =  DialogInfo.getDialogInfoById(Integer.valueOf(convID));
            dialogInfo.setDefaultClass(new DefaultClass(true, token));
            return dialogInfo;
        } catch (Exception ex){
            DialogInfo dialogInfo = new DialogInfo();
            dialogInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return dialogInfo;
        }
    }

    @GET
    @Path("/sendtr/")
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в диалог
    public DefaultClass sendTrans(@FormParam("token")String token, @FormParam("dialogID") String dialogID, @FormParam("money") String money,
                                @FormParam("cash") String cash, @FormParam("text") String text) {
        try{
            User user = User.getUserByToken(token);
            DialogInfo dialogInfo =  DialogInfo.getDialogInfoById(Integer.valueOf(dialogID));
            // TODO: addTransaction
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
