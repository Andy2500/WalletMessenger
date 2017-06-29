package ru.ravens.controllers;

import ru.ravens.models.*;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/conv")
public class ConversationController {

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/getdialogs/{token}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public DialogConversations getDialogConversations(@PathParam("token") String token) {
        try {

            DialogConversations dialogConv = DialogConversations.getDialogConversationsByUserID(User.getUserByToken(token).getUserID());
            dialogConv.setDefaultClass(new DefaultClass(true, token));
            return dialogConv;
            //Пусть пока останется на всякий случай.
            /*Conversations conv = Conversations.getConversationsByUserID(User.getUserByToken(token).getUserID());
            conv.setDefaultClass(new DefaultClass(true, token));
            return conv;*/
        } catch (Exception ex) {
            DialogConversations dialogConversations = new DialogConversations();
            dialogConversations.setDefaultClass(new DefaultClass(false, ex.getMessage()));;
            return dialogConversations;
            /*Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;*/
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/getgroups/{token}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public GroupConversations getGroupConversations(@PathParam("token") String token) {
        try {

            GroupConversations groupConv = GroupConversations.getConversationsByUserID(User.getUserByToken(token).getUserID());
            groupConv.setDefaultClass(new DefaultClass(true, token));
            return groupConv;
            //Пусть пока останется на всякий случай.
            /*Conversations conv = Conversations.getConversationsByUserID(User.getUserByToken(token).getUserID());
            conv.setDefaultClass(new DefaultClass(true, token));
            return conv;*/
        } catch (Exception ex) {
            GroupConversations groupConv = new GroupConversations();
            groupConv.setDefaultClass(new DefaultClass(false, ex.getMessage()));;
            return groupConv;
            /*Conversations conv = new Conversations();
            conv.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return conv;*/
        }
    }


    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/gethistdialogs/{token}/{date}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public DialogConversations getDialogHist(@PathParam("token") String token,
                                       @PathParam("date") Long date) {
        try {
            DialogConversations dialogConversations = DialogConversations.getDialogConversationsHistByUserIdAndDate(User.getUserByToken(token).getUserID(),date);
            dialogConversations.setDefaultClass(new DefaultClass(true, token));
            return dialogConversations;
        } catch (Exception ex) {
            DialogConversations dialogConversations = new DialogConversations();
            dialogConversations.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return dialogConversations;
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/gethistgroups/{token}/{date}")
    @Produces(MediaType.APPLICATION_JSON) // получить список бесед
    public GroupConversations getGroupHist(@PathParam("token") String token,
                                                    @PathParam("date") Long date) {
        try {
            GroupConversations groupConversations = GroupConversations.getConversationsHistByUserIdAndDate(User.getUserByToken(token).getUserID(),date);
            groupConversations.setDefaultClass(new DefaultClass(true, token));
            return groupConversations;
        } catch (Exception ex) {
            GroupConversations groupConversations = new GroupConversations();
            groupConversations.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return groupConversations;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/accepttr")
    @Produces(MediaType.APPLICATION_JSON) // подтвердить транзакцию
    public DefaultClassWrapper acceptTrans(@FormParam("token") String token,
                                           @FormParam("transactionID") int transID) {
        try {
            Transaction.AcceptTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/declinetr")
    @Produces(MediaType.APPLICATION_JSON) // отклонить транзакцию
    public DefaultClassWrapper declineTrans(@FormParam("token") String token,
                                            @FormParam("transactionID") int transID) {
        try {
            Transaction.DeclineTransaction(User.getUserByToken(token).getUserID(), transID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

}
