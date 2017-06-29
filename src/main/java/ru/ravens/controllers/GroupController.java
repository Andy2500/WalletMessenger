package ru.ravens.controllers;

import ru.ravens.models.*;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    @GET
    @Path("/get/{token}/{groupID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про групповой диалог
    public GroupInfo getGroupDialog(@PathParam("token")String token,
                                    @PathParam("groupID") int groupID)
    {
        try{
            User.getUserByToken(token);
            GroupInfo groupInfo = GroupInfo.getGroupInfoById(groupID);
            groupInfo.setDefaultClass(new DefaultClass(true,token));
            return groupInfo;
        } catch (Exception ex){
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return groupInfo;
        }
    }

    @GET
    @Path("/sendtr/{token}/{receiverID}/{groupID}/{money}/{cash}/{text}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в групп чат
    public DefaultClassAndDateAndID sendTransToGroupDialog(@PathParam("token")String token,
                                                           @PathParam("receiverID") int receiverID,
                                                           @PathParam("groupID") int groupID,
                                                           @PathParam("money") int money,
                                                           @PathParam("cash")  int cash,
                                                           @PathParam("text") String text)
    {
        try{
            //GroupInfo groupInfo = GroupInfo.getGroupInfoById(groupID);
            DefaultClassAndDateAndID defaultClassAndDateAndID = Transaction.SendTransactionGroup(User.getUserByToken(token).getUserID(),receiverID, groupID,
                    money, cash, text);
            defaultClassAndDateAndID.setDefaultClass(new DefaultClass(true,token));
            return defaultClassAndDateAndID;
        } catch (Exception ex){
            DefaultClassAndDateAndID defaultClassAndDateAndID = new DefaultClassAndDateAndID();
            defaultClassAndDateAndID.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return defaultClassAndDateAndID;
        }
    }

    @GET
    @Path("/gettransactions/{token}/{groupID}/{transactionID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций, приходит самая ранняя что есть на устройстве
    public TransactionHist getLastTransactions(@PathParam("token") String token,
                                               @PathParam("groupID") int groupID,
                                               @PathParam("transactionID") int earliestTransID) {
        try {
            User.getUserByToken(token); //Токен для проверки того, что это не "левый" запрос а от нашего клиента
            TransactionHist hist = TransactionHist.getHistByGroupIDAndTransactionID(groupID, earliestTransID);
            hist.setDefaultClass(new DefaultClass(true, token));
            return hist;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/getnewtransactions/{token}/{groupID}/{transactionID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение новых транзакций, нам приходит самая поздняя что есть на устройстве
    public TransactionHist getNewTransactions(@PathParam("token") String token,
                                              @PathParam("groupID") int groupID,
                                              @PathParam("transactionID") int lastTransID) {
        try {
            User.getUserByToken(token);
            //Саше надо дописать -> Саша дописал
            TransactionHist hist = TransactionHist.getNewTransactionsByGroupIDAndTransactionID(groupID,lastTransID);
            hist.setDefaultClass(new DefaultClass(true, token));
            return hist;
        } catch (Exception ex) {
            TransactionHist transHist = new TransactionHist();
            transHist.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return transHist;
        }
    }

    @GET
    @Path("/create/{token}/{name}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // создание группового диалога
    public DefaultClassAndDateAndID createGroup(@PathParam("token") String token,
                                                @PathParam("name") String name) {
        try {
            User user = User.getUserByToken(token);
            DefaultClassAndDateAndID defaultClassAndDateAndID = GroupInfo.createGroup(user.getUserID(), name);
            defaultClassAndDateAndID.setDefaultClass(new DefaultClass(true, token));
            return defaultClassAndDateAndID;
        } catch (Exception ex) {
            DefaultClassAndDateAndID defaultClassAndDateAndID = new DefaultClassAndDateAndID();
            defaultClassAndDateAndID.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return defaultClassAndDateAndID;
        }
    }

    @GET
    @Path("/add/{token}/{groupID}/{phone}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // добавить пользователя в диалог
    public DefaultClassWrapper addToGroup(@PathParam("token") String token,
                                        @PathParam("groupID") int groupID,
                                         @PathParam("phone") String phone) {
        try {
            User.getUserByToken(token);
            GroupInfo.addUserToGroupById(User.getUserByPhone(phone).getUserID(), groupID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @GET
    @Path("/deluser/{token}/{groupID}/{phone}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // удалить пользователя из диалога
    public DefaultClassWrapper deleteFromGroup(@PathParam("token") String token,
                                   @PathParam("groupID") int groupID,
                                   @PathParam("phone") String phone) {
        try {
            User.getUserByToken(token);
            GroupInfo.delUserFromGroupById(User.getUserByPhone(phone).getUserID(), groupID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @GET
    @Path("/quit/{token}/{groupID}")
    @Produces(MediaType.APPLICATION_JSON) // покинуть диалог
    public DefaultClassWrapper leaveGroup(@PathParam("token") String token,
                                   @PathParam("groupID") int groupID) {
        try {
            User user = User.getUserByToken(token);
            GroupInfo.delUserFromGroupById(user.getUserID(), groupID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @GET
    @Path("/leave/{token}/{groupID}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // удаление групповой беседф
    public DefaultClassWrapper deleteGroup(@PathParam("token") String token,
                                   @PathParam("groupID") int groupID) {
        try {
            User.getUserByToken(token); //(было закомменчено) надо пытаться получать юзера по токену, вдруг токен неверный послали, тогда эксепшен будет
            GroupInfo.deleteGroupById(groupID);
            return new DefaultClassWrapper(new DefaultClass(true, token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }


}
