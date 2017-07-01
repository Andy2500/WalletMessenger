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

    @POST
    @Path("/get")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про диалог
    public DialogInfo getDialogs(@FormParam("token") String token, @FormParam("dialogID") int dialogID)
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

    @POST
    @Path("/sendtr")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в диалог
    public DefaultClassAndDateAndID sendTrans(@FormParam("token")String token,
                                              @FormParam("dialogID") int dialogID,
                                              @FormParam("money") float money,
                                              @FormParam("cash") int cash,
                                              @FormParam("text") String text) {
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

    @POST
    @Path("/gettransactions")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций, приходит самая ранняя что есть на устройстве
    public TransactionHist getLastTransactions(@FormParam("token") String token,
                                               @FormParam("dialogID") int dialogID,
                                               @FormParam("transactionID") int earliestTransID) {
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

    @POST
    @Path("/getnewtransactions")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение новых транзакций, нам приходит самая поздняя что есть на устройстве
    public TransactionHist getNewTransactions(@FormParam("token") String token,
                                              @FormParam("dialogID") int dialogID,
                                              @FormParam("transactionID") int lastTransID) {
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

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // создание диалога
    public DefaultClassAndDateAndID createDialog(@FormParam("token") String token,
                                                 @FormParam("phone") String phone) {
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
