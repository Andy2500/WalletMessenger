package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DefaultClassAndDateAndID;
import ru.ravens.models.DialogInfo;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/dialog")
public class DialogController {

    @GET
    @Path("/get/{token}/{dialogID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про диалог
    public DialogInfo getDialogs(@PathParam("token") String token, @PathParam("dialogID") int dialogID)
    {
        try{
            User.getUserByToken(token);
            DialogInfo dialogInfo =  DialogInfo.getDialogInfoById(dialogID);
            dialogInfo.setDefaultClass(new DefaultClass(true,token));
            return dialogInfo;
        } catch (Exception ex){
            DialogInfo dialogInfo = new DialogInfo();
            dialogInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return dialogInfo;
        }
    }

    @GET
    @Path("/sendtr/{token}/{dialogID}/{money}/{cash}/{text}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в диалог
    public DefaultClassAndDateAndID sendTrans(@PathParam("token")String token,
                                              @PathParam("dialogID") int dialogID,
                                              @PathParam("money") int money,
                                              @PathParam("cash") int cash,
                                              @PathParam("text") String text) {
        try{
            DefaultClassAndDateAndID defaultClassAndDateAndID = Transaction.SendTransactionDialog(User.getUserByToken(token).getUserID(),
                    dialogID, money, cash, text);
            defaultClassAndDateAndID.setDefaultClass(new DefaultClass(true, token));
            return defaultClassAndDateAndID;

        } catch (Exception ex){
            DefaultClassAndDateAndID defClassAndID = new DefaultClassAndDateAndID();
            defClassAndID.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return defClassAndID;
        }
    }

    @GET
    @Path("/gettransactions/{token}/{dialogID}/{transactionID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций, приходит самая ранняя что есть на устройстве
    public TransactionHist getLastTransactions(@PathParam("token") String token,
                                               @PathParam("dialogID") int dialogID,
                                               @PathParam("transactionID") int earliestTransID) {
        try {
            User.getUserByToken(token);
            TransactionHist hist = TransactionHist.getHistByDialogIDAndTransactionID(dialogID, earliestTransID);
            hist.setDefaultClass(new DefaultClass(true, token));
            return hist;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/getnewtransactions/{token}/{dialogID}/{transactionID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение новых транзакций, нам приходит самая поздняя что есть на устройстве
    public TransactionHist getNewTransactions(@PathParam("token") String token,
                                              @PathParam("dialogID") int dialogID,
                                              @PathParam("transactionID") int lastTransID) {
        try {
            User.getUserByToken(token);
            TransactionHist hist = TransactionHist.getNewTransactionsByDialogIDAndTransactionID(dialogID, lastTransID);
            hist.setDefaultClass(new DefaultClass(true, token));
            return hist;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/create/{token}/{phone}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // создание диалога
    public DefaultClassAndDateAndID createDialog(@PathParam("token") String token,
                                                 @PathParam("phone") String phone) {
        try {
            User user = User.getUserByToken(token);
            DefaultClassAndDateAndID defClassAndID = DialogInfo.createNewDialog(user.getUserID(), User.getUserByPhone(phone).getUserID());
            defClassAndID.setDefaultClass(new DefaultClass(true, token));
            return defClassAndID;
        } catch (Exception ex) {
            DefaultClassAndDateAndID defClassAndID = new DefaultClassAndDateAndID();
            defClassAndID.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return defClassAndID;
        }
    }
}
