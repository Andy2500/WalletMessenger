package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Group;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

@XmlRootElement
public class Conversation implements Serializable
{
    private ArrayList<Dialog> dialogs;
    private ArrayList<Group> groups;
    private DefaultClass defaultClass;

    public static Conversation getConversationByUserID(int userID) throws Exception
    {
        Conversation conversation = new Conversation();

        ArrayList<Dialog> dialogList = new ArrayList<>();
        ArrayList<Group> groupList = new ArrayList<>();


        //получаем диалоги и парсим
        String query = "Select * from Dialogs where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet));
        }



        //получаем группы и парсим
        query = "Select * from Groups where UserID = " + userID;
        resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            groupList.add(Group.parseGroup(resultSet));
        }

        conversation.setDialogs(dialogList);
        conversation.setGroups(groupList);

        return conversation;
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }


    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}
