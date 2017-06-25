package ru.ravens.controllers;

import ru.ravens.models.Conversation;
import ru.ravens.models.DefaultClass;
import ru.ravens.models.DialogInfo;
import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/conv")
public class ConversationController {

    @GET
    @Path("/gets/")
    @Produces(MediaType.APPLICATION_JSON)
    public Conversation getConversations(@FormParam("token") String token)
    {
        try{
            return Conversation.getConversationByUserID(User.getUserByToken(token).getUserID());
        } catch (Exception ex){
            Conversation conv = new Conversation();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }

    @GET
    @Path("/gettransactions/")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionHist getTransactions(@FormParam("conversationID")String convID, @FormParam("transactionID") String lastTransID)
    {
        try{
            return TransactionHist.getHistByDialogIDAndTransactionID(Integer.valueOf(convID), Integer.valueOf(lastTransID));
        } catch (Exception ex){
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/getnewtransactions/")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionHist getNewTransactions(@FormParam("conversationID")String convID, @FormParam("transactionID") String lastTransID)
    {
        try{
            return TransactionHist.getHistByDialogIDAndTransactionID(Integer.valueOf(convID), Integer.valueOf(lastTransID));
        } catch (Exception ex){
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/accepttr/")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultClass acceptTrans(@FormParam("token")String token, @FormParam("transactionID") String transID)
    {
        try{
            User user = User.getUserByToken(token);
           // TODO:  user.acceptTrans
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/declinetr/")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultClass declineTrans(@FormParam("token")String token, @FormParam("transactionID") String transID)
    {
        try{
            User user = User.getUserByToken(token);
            // TODO:  user.declineTrans
            return new DefaultClass(true, token);
        } catch (Exception ex){
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
