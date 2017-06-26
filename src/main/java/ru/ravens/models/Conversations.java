package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Group;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        String query = "Select * from Dialogs where (UserID_1 = " + userID +" OR UserID_2 = " + userID +" )";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }

        //получаем группы и парсим
        query = "Select * from GroupBalances where UserID = " + userID;
        resultSet = DBManager.getSelectResultSet(query);

        //Если мы состоим в группах, то получим их (а если нет, то не надо получать)
        if(resultSet.next())
        {
            HashMap<Integer,Integer> mapGroupBalances = new HashMap<>();
            query = "SELECT * FROM Groups WHERE GroupID in (";
            do
            {
                int groupID = resultSet.getInt("GroupID");
                mapGroupBalances.put(groupID, resultSet.getInt("Balance"));
                query += groupID + ",";
            }while (resultSet.next());
            //Без последнего символа!
            query = query.substring(0, query.length()-1) +")";

            //Получаем все группы
            resultSet = DBManager.getSelectResultSet(query);
            while (resultSet.next())
            {   //парсим группы
                groupList.add(Group.parseGroup(resultSet));
            }
            for(int i=0; i < groupList.size(); i++)
            {
                //Ставим баланс нашего пользователя в каждой группе из мапы по ID группы
                groupList.get(i).setMyBalance(mapGroupBalances.get(groupList.get(i).getGroupID()));
            }
        }
        //Если нет диалогов или групп, то лист будет пустой
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
