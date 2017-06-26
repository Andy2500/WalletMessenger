package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.GroupInfo;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    //После полного тестирования GET запросов, надо будет поменять на POST, для более удобного тестирования в браузере
    //После полного тестирования метода изменяйте заголовки на такие:

    @GET
    @Path("/get/{token}++{dialogID}")
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про групповой диалог
    public GroupInfo getGroupDialog(@PathParam("token")String token,
                                    @PathParam("dialogID") int dialogID)
    {
        try{
            User.getUserByToken(token);
            GroupInfo groupInfo = GroupInfo.getGroupInfoById(dialogID);
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
    @Path("/sendtr/{token}++{dialogID}++{money}++{cash}+{text}")
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в групп чат
    public GroupInfo sendTransToGroupDialog(@PathParam("token")String token,
                                            @PathParam("dialogID") int dialogID,
                                            @PathParam("money") int money,
                                            @PathParam("cash")  int cash,
                                            @PathParam("text") String text)
    {
        try{
            GroupInfo groupInfo = GroupInfo.getGroupInfoById(dialogID);
            Transaction.SendTransactionDialog(User.getUserByToken(token).getUserID(), dialogID,
                    money, cash, text);
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
    @Path("/gettransactions/{token}++{conversationID}++{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций
    public TransactionHist getLastTransactions(@PathParam("token") String token,
                                               @PathParam("conversationID") int convID,
                                               @PathParam("transactionID") int lastTransID) {
        try {
            User.getUserByToken(token);
            TransactionHist hist = TransactionHist.getHistByDialogIDAndTransactionID(convID, lastTransID);
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
            //Саше надо дописать
//            TransactionHist hist = TransactionHist.getHistByDialogIDAndTransactionID(convID, lastTransID);
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
