package ru.ravens.controllers;

import ru.ravens.models.Conversations;
import ru.ravens.models.DefaultClass;
import ru.ravens.models.DefaultClassWrapper;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

@Path("/conv")
public class ConversationController {

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/gets/{token}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public Conversations getConversations(@PathParam("token") String token) {
        try {
            Conversations conv = Conversations.getConversationsByUserID(User.getUserByToken(token).getUserID());
            conv.setDefaultClass(new DefaultClass(true, token));
            return conv;
        } catch (Exception ex) {
            Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/gethist/{token}/{date}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public Conversations getConversationsHist(@PathParam("token") String token,
                                              @PathParam("date") Long date) {
        try {
            Conversations conv = Conversations.getConversationsHistByUserIdAndDate(User.getUserByToken(token).getUserID(),date);
            conv.setDefaultClass(new DefaultClass(true, token));
            return conv;
        } catch (Exception ex) {
            Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/accepttr/{token}/{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // подтвердить транзакцию
    public DefaultClassWrapper acceptTrans(@PathParam("token") String token,
                                    @PathParam("transactionID") int transID) {
        try {
            Transaction.AcceptTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/declinetr/{token}/{transactionID}")
    @Produces(MediaType.APPLICATION_JSON) // отклонить транзакцию
    public DefaultClassWrapper declineTrans(@PathParam("token") String token,
                                     @PathParam("transactionID") int transID) {
        try {
            Transaction.DeclineTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

}
