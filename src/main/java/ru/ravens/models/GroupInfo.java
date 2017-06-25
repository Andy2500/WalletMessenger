package ru.ravens.models;

import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

@XmlRootElement
public class GroupInfo implements Serializable
{
    private ArrayList<User> users;
    private ArrayList<Transaction> transactions;
    private DefaultClass defaultClass;

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }


    //Получаем всех юзеров и историю транзакций для этой группы
    //Те поля, который были в объекте Group не дублируются! Запоминайте их там!
    public static GroupInfo getGroupInfoById(int groupID) throws Exception
    {
        GroupInfo groupInfo = new GroupInfo();
        ArrayList<User> users = new ArrayList<>();

        String query = "Select * from GroupBalances where GroupID = " + groupID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        //автоматически сортируется!
        Map<Integer, Integer> userBalanceMap = new TreeMap<>();

        //собираем соответствие юзеров и их балансов
        while (resultSet.next())
        {
            userBalanceMap.put(resultSet.getInt("UserID"), resultSet.getInt("Balance"));
        }

        //Собираем запрос на юзеров

        //так должно работать!, проверено в Management Studio
        query = "Select * from Users where UserID in (";
        //итерация по ключам
        for(int key: userBalanceMap.keySet())
        {
            query += key + " ";
        }
        query += ")";

        //получаем и парсим юзеров в этой группе
        resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            users.add(User.parseUser(resultSet));
        }

        Collections.sort(users, getUserComp());

        //наверно можно проще сделать, но пока так...
        Iterator it = userBalanceMap.entrySet().iterator();
        int i = 0;
        for(int value: userBalanceMap.values())
        {
            users.get(i).setBalance(value);
            i++;
        }

        groupInfo.setUsers(users);
        groupInfo.setTransactions(Transaction.getTransactionsHistByGroupID(groupID));

        return groupInfo;
    }

    private static Comparator<User> getUserComp()
    {
        Comparator comp = new Comparator<User>(){
            @Override
            public int compare(User user1, User user2)
            {
                return ((Integer)user1.getUserID()).compareTo(user2.getUserID());
            }
        };
        return comp;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

}
