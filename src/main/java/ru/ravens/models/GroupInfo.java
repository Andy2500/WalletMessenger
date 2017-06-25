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
    private ArrayList<UserProfile> userProfiles;
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
        ArrayList<UserProfile> userProfiles = new ArrayList<>();

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
            userProfiles.add(UserProfile.getUserProfileByUser(User.parseUser(resultSet)));
        }

        Collections.sort(userProfiles, getUserComp());

        //наверно можно проще сделать, но пока так...
        Iterator it = userBalanceMap.entrySet().iterator();
        int i = 0;
        for(int value: userBalanceMap.values())
        {
            userProfiles.get(i).setBalance(value);
            i++;
        }

        groupInfo.setUserProfiles(userProfiles);
        groupInfo.setTransactions(Transaction.getTransactionsHistByGroupID(groupID));

        return groupInfo;
    }

    private static Comparator<UserProfile> getUserComp()
    {
        Comparator comp = new Comparator<UserProfile>(){
            @Override
            public int compare(UserProfile user1, UserProfile user2)
            {
                return ((Integer)user1.getUserID()).compareTo(user2.getUserID());
            }
        };
        return comp;
    }


    public ArrayList<UserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(ArrayList<UserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }
}
