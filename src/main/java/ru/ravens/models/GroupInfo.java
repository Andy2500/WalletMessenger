package ru.ravens.models;

import ru.ravens.models.InnerModel.Group;
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
    private DefaultClass defaultClass = new DefaultClass();

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }



    //После выполнения в контроллере можно добавить создателя в список userProfiles, а то тут его получать нет смысла
    //Так как он уже получен там в контроллере
    public static GroupInfo createGroup(int creatorID, String name) throws Exception
    {
        name = "'" + name + "'";
        GroupInfo groupInfo = new GroupInfo();

        //запись группы
        String command = "INSERT INTO Groups (GroupID, Name, Sum, AdminID) VALUES(" +
                " (SELECT MAX (GroupID) from Groups) + 1, " + name + ", 0, " + creatorID;
        //сумма 0
        DBManager.execCommand(command);

        //запись баланса этого пользователя в этой группе
        //Если сервер или база данных в один поток работает.. то и так сойдет, а иначе даже неясно как синхронизацию обеспечить...
        command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES(" +
                " (SELECT MAX (GroupID) from Groups), " + creatorID + ", 0 )";
        //Да, здесь берется MAX(GroupID) из таблицы Groups ! (а вставляется в таблицу GroupBalances

        DBManager.execCommand(command);

        //пустой класс
        return groupInfo;
    }

    //После выполнения в контроллере можно добавить юзера в список userProfiles, а то тут его получать нет смысла
    //Так как он уже получен там в контроллере
    public static GroupInfo addUserToGroupById(int userID, int groupID) throws Exception
    {
        //запись баланса этого пользователя в этой группе
        String command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES(" +
                groupID + userID + ", 0 )";

        DBManager.execCommand(command);
        //В принципе я могу пересчитать все балансы.. Но правильно ли это?
        //Если он только присоединился, я могу ему дать 0 баланс и все будет ОК
        //А иначе ты ток вошел, а хоп на тебе уже 10 тыс долга висит...
        //А другие наоборот в "плюс" вышли, из-за перераспределения расходов

        //Можно опционально сделать... это
        GroupInfo groupInfo = new GroupInfo();
        //И здесь могу пустой возвращать, а могу вот так:
        //  return getGroupInfoById(groupID);
        return groupInfo;
    }




    //ЗДЕСЬ ПОКА НЕ ТОЧНО ВСЕ, МОГУТ БЫТЬ ИЗМЕНЕНИЯ
    //Этим же методом можно выходить из группы самостоятельно(!) - просто проверяешь, что юзер по токену и по телефону это одинаковые UserID, и сюда подаешь его..
    //Админу нельзя так делать! Будет ОТДЕЛЬНЫЙ метод для закрытия групповой беседы для админа
    //Проверку на админ\нет осуществляет устройство..
    public static GroupInfo delUserFromGroupById(int userID, int groupID) throws Exception
    {
        //Проверьте на существование юзера в этой группе
        String query = "SELECT * FROM GroupBalances WHERE WHERE (GroupID = " +groupID +" AND UserID = "+userID +" )";
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        if(resultSet.next())
        {
            //удаление баланса пользователя из группы
            String command = "DELETE FROM GroupBalances WHERE (GroupID = " +groupID +" AND UserID = "+userID +" )";
            DBManager.execCommand(command);
        }
        else
        {
            //Сюда не должны попасть при нормальной работе (это для отладки)
            //Сообщение и эксепшен можно редактировть(!), Включая сам факт наличия эксепшена здесь
            throw new Exception("Пользователя нет в этой группе, нельзя удалить.");
        }
        //А здесь что делать с балансом? общим и индивидуальными...
        //Можно пересчитывать (но как..не расплатился же или с ним не расплатились..)
        //Вариант: если баланс в минусе, перераспределяем расходы на остальных
        //          если в плюсе, то считаем, что он "подарил" эти деньги группе и "начисляем" всем из этой суммы поровну
        GroupInfo groupInfo = new GroupInfo();
        //И здесь могу пустой возвращать, а могу вот так:
        // return getGroupInfoById(groupID);
        return groupInfo;
    }


    //Получаем всех юзеров и историю транзакций для этой группы
    //Те поля, который были в объекте Group не дублируются! Запоминайте их там!
    public static void deleteGroupById(int groupID) throws Exception
    {
        //Есть варианты:

        //Вариант 1: по минимуму
        //Удаляем запись о группе и все -> не найдут GroupID такой, значит и не будет отображаться в списках диалогов
        //а следовательно ни записи транзакций ни балансов никто не будет искать
        String command = "DELETE * FROM Groups where GroupID = "+groupID;
        DBManager.execCommand(command);

        //Вариант 2: по максимуму
        //Дополнительно удаляем всех пользователей и ещё можно записи о транзакциях в этой группе
        //Плюсы - меньше "живых" строк в таблице, легче искать потом другие данные(особенно много транзакций уйдет)
        //Минусы - потеря информации о передаче денежных средств и факте участия в группах

        //Коды есть, закомментить можно
        command = "DELETE * FROM GroupBalances WHERE GroupID = " + groupID;
        DBManager.execCommand(command);
        command = "DELETE * FROM Transactions WHERE GroupID = " + groupID;
        DBManager.execCommand(command);
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
        Map<Integer, Float> userBalanceMap = new TreeMap<>();

        //собираем соответствие юзеров и их балансов
        while (resultSet.next())
        {
            userBalanceMap.put(resultSet.getInt("UserID"), resultSet.getFloat("Balance"));
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
        for(float value: userBalanceMap.values())
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
