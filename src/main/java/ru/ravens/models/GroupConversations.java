package ru.ravens.models;

import ru.ravens.models.InnerModel.Group;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement
public class GroupConversations implements Serializable
{
    private ArrayList<Group> groups;
    private DefaultClass defaultClass = new DefaultClass();

    public static GroupConversations getConversationsByUserID(int userID) throws Exception
    {
        int rows = 20;
        //получаем группы
        ArrayList<Group> groupList = getGroupList(rows, userID);
        //формируем и отправляем результат
        GroupConversations groupConversations = new GroupConversations();
        groupConversations.setGroups(groupList);
        return groupConversations;
    }

    public static GroupConversations getConversationsHistByUserIdAndDate(int userID, long lastDate) throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = format.format(lastDate);
        int rows = 20;

        ArrayList<Group> groupList  = getGroupListBeforeDate(rows,userID,date);
        //формируем и отправляем результат
        GroupConversations groupConversations = new GroupConversations();
        groupConversations.setGroups(groupList);
        return groupConversations;
    }

    private static ArrayList<Group> getGroupList(int rows, int userID) throws Exception
    {
        ArrayList<Group> groupList = new ArrayList<>();
        //получаем группы и парсим
        String query = "Select * from GroupBalances where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        //Если мы состоим в группах, то получим их (а если нет, то не надо получать)
        if(resultSet.next())
        {
            HashMap<Integer,Integer> mapGroupBalances = new HashMap<>();
            query = "SELECT top " + rows + " * FROM Groups WHERE GroupID in (";
            int groupID = 0;
            do
            {
                groupID = resultSet.getInt("GroupID");
                mapGroupBalances.put(groupID, resultSet.getInt("Balance"));
                query += groupID + ",";
            }while (resultSet.next());
            //Без последнего символа!
            query = query.substring(0, query.length()-1) +") Order by Date DESC";

            //Получаем все группы
            resultSet = DBManager.getSelectResultSet(query);
            while (resultSet.next())
            {   //парсим группы
                groupList.add(Group.parseGroup(resultSet));
            }
            for(int i = 0; i < groupList.size(); i++)
            {
                //Ставим баланс нашего пользователя в каждой группе из мапы по ID группы
                groupList.get(i).setMyBalance(mapGroupBalances.get(groupList.get(i).getGroupID()));
            }
        }
        return groupList; //Если что вернет пустой лист с размером 0
    }

    private static ArrayList<Group> getGroupListBeforeDate(int rows, int userID, String date) throws Exception
    {
        ArrayList<Group> groupList = new ArrayList<>();
        //получаем группы и парсим
        String query = "Select * from GroupBalances where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        //Если мы состоим в группах, то получим их (а если нет, то не надо получать)
        if(resultSet.next())
        {
            HashMap<Integer, Integer> mapGroupBalances = new HashMap<>();
            query = "SELECT top " + rows + " * FROM Groups WHERE (Date < '" + date + "') AND (GroupID in (";
            int groupID = 0;
            do {
                groupID = resultSet.getInt("GroupID");
                mapGroupBalances.put(groupID, resultSet.getInt("Balance"));
                query += groupID + ",";
            } while (resultSet.next());

            //Без последнего символа!
            query = query.substring(0, query.length() - 1) + ")) Order by Date DESC";

            //Получаем все группы
            resultSet = DBManager.getSelectResultSet(query);
            while (resultSet.next())
            {   //парсим группы
                groupList.add(Group.parseGroup(resultSet));
            }
            for (int i = 0; i < groupList.size(); i++)
            {
                //Ставим баланс нашего пользователя в каждой группе из мапы по ID группы
                groupList.get(i).setMyBalance(mapGroupBalances.get(groupList.get(i).getGroupID()));
            }
        }
        return groupList; //Если что вернет пустой лист с размером 0
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}
