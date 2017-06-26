package ru.ravens.controllers;

import ru.ravens.models.Conversations;
import ru.ravens.models.DefaultClass;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/conv")
public class ConversationController {

    @GET
    @Path("/gets/{token}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public Conversations getConversations(@PathParam("token") String token) {
        try {
            Conversations conv = Conversations.getConversationByUserID(User.getUserByToken(token).getUserID());
            conv.getDefaultClass().setOperationOutput(true);
            conv.getDefaultClass().setToken(token);
            return conv;
        } catch (Exception ex) {
            Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }



    @GET
    @Path("/accepttr/{token}/{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // подтвердить транзакцию
    public DefaultClass acceptTrans(@PathParam("token") String token,
                                    @PathParam("transactionID") int transID) {
        try {
            Transaction.AcceptTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/declinetr/{token}/{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // отклонить транзакцию
    public DefaultClass declineTrans(@PathParam("token") String token,
                                     @PathParam("transactionID") int transID) {
        try {
            Transaction.DeclineTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
