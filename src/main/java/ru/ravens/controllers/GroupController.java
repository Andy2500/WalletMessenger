package ru.ravens.controllers;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DefaultClassAndDateAndID;
import ru.ravens.models.GroupInfo;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.TransactionHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/group")
public class GroupController {

    @POST
    @Path("/get")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получить инфу про групповой диалог
    public GroupInfo getGroupDialog(@FormParam("token")String token,
                                    @FormParam("groupID") int groupID)
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

    @POST
    @Path("/sendtr")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // отправить транзакцию в групп чат
    public DefaultClassAndDateAndID sendTransToGroupDialog(@FormParam("token")String token,
                                                           @FormParam("receiverID") int receiverID,
                                                           @FormParam("groupID") int groupID,
                                                           @FormParam("money") int money,
                                                           @FormParam("cash")  int cash,
                                                           @FormParam("text") String text)
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

    @POST
    @Path("/gettransactions")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение истории транзакций, приходит самая ранняя что есть на устройстве
    public TransactionHist getLastTransactions(@FormParam("token") String token,
                                               @FormParam("groupID") int groupID,
                                               @FormParam("transactionID") int earliestTransID) {
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

    @POST
    @Path("/getnewtransactions")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // получение новых транзакций, нам приходит самая поздняя что есть на устройстве
    public TransactionHist getNewTransactions(@FormParam("token") String token,
                                              @FormParam("groupID") int groupID,
                                              @FormParam("transactionID") int lastTransID) {
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

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // создание группового диалога
    public DefaultClassAndDateAndID createGroup(@FormParam("token") String token,
                                                @FormParam("name") String name) {
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

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // добавить пользователя в диалог
    public DefaultClass addToGroup(@FormParam("token") String token,
                                        @FormParam("groupID") int groupID,
                                         @FormParam("phone") String phone) {
        try {
            User.getUserByToken(token);
            GroupInfo.addUserToGroupById(User.getUserByPhone(phone).getUserID(), groupID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @POST
    @Path("/deluser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // удалить пользователя из диалога
    public DefaultClass deleteFromGroup(@FormParam("token") String token,
                                   @FormParam("groupID") int groupID,
                                   @FormParam("phone") String phone) {
        try {
            User.getUserByToken(token);
            GroupInfo.delUserFromGroupById(User.getUserByPhone(phone).getUserID(), groupID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @POST
    @Path("/quit")
    @Produces(MediaType.APPLICATION_JSON) // покинуть диалог
    public DefaultClass leaveGroup(@FormParam("token") String token,
                                   @FormParam("groupID") int groupID) {
        try {
            User user = User.getUserByToken(token);
            GroupInfo.delUserFromGroupById(user.getUserID(), groupID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @POST
    @Path("/leave")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON) // удаление групповой беседф
    public DefaultClass deleteGroup(@FormParam("token") String token,
                                   @FormParam("groupID") int groupID) {
        try {
            User.getUserByToken(token); //(было закомменчено) надо пытаться получать юзера по токену, вдруг токен неверный послали, тогда эксепшен будет
            GroupInfo.deleteGroupById(groupID);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }


}
