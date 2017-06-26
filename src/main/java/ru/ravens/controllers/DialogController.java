package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DialogInfo;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/dialog")
public class DialogController {


    @GET
    @Path("/get/{token}++{conversationID}")
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про диалог
    public DialogInfo getDialogs(@PathParam("token")String token,
                                 @PathParam("conversationID") int convID)
    {
        try{
            User.getUserByToken(token);
            DialogInfo dialogInfo =  DialogInfo.getDialogInfoById(convID);
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
    @Path("/sendtr/{token}++{dialogID}++{money}++{cash}+{text}")
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в диалог
    public DefaultClass sendTrans(@PathParam("token")String token,
                                  @PathParam("dialogID") int dialogID,
                                  @PathParam("money") int money,
                                  @PathParam("cash") int cash,
                                  @PathParam("text") String text) {
        try{
            Transaction.SendTransactionDialog(User.getUserByToken(token).getUserID(), dialogID,
                    money, cash, text);
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/gettransactions/{token}++{groupID}++{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций
    public TransactionHist getLastTransactions(@PathParam("token") String token,
                                               @PathParam("groupID") int groupID,
                                               @PathParam("transactionID") int lastTransID) {
        try {
            User.getUserByToken(token);
            TransactionHist hist = TransactionHist.getHistByGroupIDAndTransactionID(groupID, lastTransID);
            hist.getDefaultClass().setOperationOutput(true);
            return hist;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/getnewtransactions/{token}++{conversationID}++{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // получение прошлых транзакций, когда пришло новое сообщение
    public TransactionHist getNewTransactions(@PathParam("token") String token,
                                              @PathParam("conversationID") int convID,
                                              @PathParam("transactionID") int lastTransID) {
        try {
            User.getUserByToken(token);

//            TransactionHist hist = TransactionHist.getHistByGroupIDAndTransactionID(convID, lastTransID);
//            hist.getDefaultClass().setOperationOutput(true);
//            return hist;
            return null;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }
}
