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

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/gets")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public Conversations getConversations(@FormParam("token") String token) {
        try {
            Conversations conv = Conversations.getConversationsByUserID(User.getUserByToken(token).getUserID());
            conv.getDefaultClass().setOperationOutput(true);
            conv.getDefaultClass().setToken(token);
            return conv;
        } catch (Exception ex) {
            Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }



    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/accepttr")
    @Produces(MediaType.APPLICATION_JSON) // подтвердить транзакцию
    public DefaultClass acceptTrans(@FormParam("token") String token,
                                    @FormParam("transactionID") int transID) {
        try {
            Transaction.AcceptTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/declinetr")
    @Produces(MediaType.APPLICATION_JSON) // отклонить транзакцию
    public DefaultClass declineTrans(@FormParam("token") String token,
                                     @FormParam("transactionID") int transID) {
        try {
            Transaction.DeclineTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
