package ru.ravens.controllers;

import ru.ravens.models.Conversations;
import ru.ravens.models.DefaultClass;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/conv")
public class ConversationController {

    @GET
    @Path("/gets/")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public Conversations getConversations(@FormParam("token") String token)
    {
        try{
            Conversations conv = Conversations.getConversationByUserID(User.getUserByToken(token).getUserID());
            conv.getDefaultClass().setOperationOutput(true);
            conv.getDefaultClass().setToken(token);
            return conv;
        } catch (Exception ex){
            Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }

    @GET
    @Path("/gettransactions/")
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций
    public TransactionHist getTransactions(@FormParam("conversationID")String convID, @FormParam("transactionID") String lastTransID)
    {
        try{
            TransactionHist hist = TransactionHist.getHistByDialogIDAndTransactionID(Integer.valueOf(convID), Integer.valueOf(lastTransID));
            hist.getDefaultClass().setOperationOutput(true);
            return hist;
        } catch (Exception ex){
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/getnewtransactions/")
    @Produces(MediaType.APPLICATION_JSON) // получение прошлых транзакций, когда пришло новое сообщение (до меня так и не дошло, чем отличается от предыдущего метода:D )
    public TransactionHist getNewTransactions(@FormParam("conversationID")String convID, @FormParam("transactionID") String lastTransID)
    {
        try{
            TransactionHist hist = TransactionHist.getHistByDialogIDAndTransactionID(Integer.valueOf(convID), Integer.valueOf(lastTransID));
            hist.getDefaultClass().setOperationOutput(true);
            return hist;
        } catch (Exception ex){
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/accepttr/")
    @Produces(MediaType.APPLICATION_JSON) // подтвердить транзакцию
    public DefaultClass acceptTrans(@FormParam("token")String token, @FormParam("transactionID") String transID)
    {
        try{
            Transaction.AcceptTransaction(User.getUserByToken(token).getUserID(), Integer.valueOf(transID));
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/declinetr/")
    @Produces(MediaType.APPLICATION_JSON) // отклонить транзакцию
    public DefaultClass declineTrans(@FormParam("token")String token, @FormParam("transactionID") String transID)
    {
        try{
            Transaction.DeclineTransaction(User.getUserByToken(token).getUserID(), Integer.valueOf(transID));
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
