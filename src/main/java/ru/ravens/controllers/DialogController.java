package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DialogInfo;
import ru.ravens.models.InnerModel.Transaction;
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
            DialogInfo dialogInfo =  DialogInfo.getDialogInfoById(Integer.valueOf(convID));
            dialogInfo.getDefaultClass().setOperationOutput(true);
            dialogInfo.getDefaultClass().setToken(token);
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
            Transaction.SendTransactionDialog(User.getUserByToken(token).getUserID(), Integer.valueOf(dialogID),
                    Integer.valueOf(money), Integer.valueOf(cash), text);
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
