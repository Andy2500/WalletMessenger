package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Group;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

@XmlRootElement
public class Conversations implements Serializable
{
    private ArrayList<Dialog> dialogs;
    private ArrayList<Group> groups;
    private DefaultClass defaultClass = new DefaultClass();

    public static Conversations getConversationByUserID(int userID) throws Exception
    {
        Conversations conversations = new Conversations();

        ArrayList<Dialog> dialogList = new ArrayList<>();
        ArrayList<Group> groupList = new ArrayList<>();

        //получаем диалоги и парсим
        String query = "Select * from Dialogs where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }

        //получаем группы и парсим
        query = "Select * from Groups where UserID = " + userID;
        resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            groupList.add(Group.parseGroup(resultSet));
        }

        conversations.setDialogs(dialogList);
        conversations.setGroups(groupList);

        return conversations;
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
