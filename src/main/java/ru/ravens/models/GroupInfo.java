package ru.ravens.models;

import ru.ravens.models.InnerModel.Group;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

@XmlRootElement
public class GroupInfo implements Serializable
{
    private ArrayList<User> users;
    private int adminID;
    private String name;
    private int sum;


    public static GroupInfo getGroupInfoById(int groupID) throws Exception
    {
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.users = new ArrayList<>();
        String query = "Select * from GroupBalances where GroupID = " + groupID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        Map<Integer, Integer> userBalanceMap = new HashMap<>();
        return null;
//        //собираем соответствие юзеров и их балансов
//        while (resultSet.next())
//        {
//            userBalanceMap.put(resultSet.getInt("UserID"), resultSet.getInt("Balance"));
//        }
//
//        //Собираем запрос на юзеров
//
//        query = "Select * from Users where ";
//        //итерация по ключам
//        for(int key: userBalanceMap.keySet())
//        {
//            query += "UserID = " + key + ""
//        }
//
//
//
//        return groupInfo;
    }



    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
