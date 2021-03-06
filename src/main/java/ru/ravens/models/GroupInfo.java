package ru.ravens.models;

import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.models.InnerModel.User;
import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

import static java.sql.Timestamp.valueOf;

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
    //ВОзвращает ID группы
    public static DefaultClassAndDateAndID createGroup(int creatorID, String name) throws Exception
    {
        String date = DateWorker.getNowMomentInUTC();
        //запись группы
        String command = "INSERT INTO Groups (Name, Sum, AdminID, Date) VALUES("
                +" N'" + name + "' , 0, " + creatorID +", '" + date + "' )";
        //сумма 0
        DBManager.execCommand(command);


        String query = "SELECT * FROM Groups where Date = '"+date+"'";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Ни одной группы не существует.");
        }
        int groupID = resultSet.getInt("GroupID");


        //запись баланса этого пользователя в этой группе
        //Если сервер или база данных в один поток работает.. то и так сойдет, а иначе даже неясно как синхронизацию обеспечить...
        command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES(" +
                + groupID + "," + creatorID + ", 0 )";
        //Да, здесь берется MAX(GroupID) из таблицы Groups ! (а вставляется в таблицу GroupBalances

        DBManager.execCommand(command);
        DefaultClassAndDateAndID def = new DefaultClassAndDateAndID(groupID);
        def.setDate(valueOf(date));
        return def;
    }

    //После выполнения в контроллере можно добавить создателя в список userProfiles, а то тут его получать нет смысла
    //Так как он уже получен там в контроллере
    //ВОзвращает ID группы
    public static DefaultClassAndDateAndID createGroupWithUsers(int creatorID, String name, String users) throws Exception {
        String date = DateWorker.getNowMomentInUTC();
        //запись группы
        String command = "INSERT INTO Groups (Name, Sum, AdminID, Date) VALUES("
                +" N'" + name + "' , 0, " + creatorID +", '" + date + "' )";
        //сумма 0
        DBManager.execCommand(command);


        String query = "SELECT * FROM Groups where Date = '"+date+"'";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Ни одной группы не существует.");
        }
        int groupID = resultSet.getInt("GroupID");

        //Я конеш верю, что там пробелов не прислали...но я удалю все пробелы на всякий случай, ИНАЧЕ оно может не найти соответствующий номер!
        users = users.replace(" ","");

        //Теперь заменим запятые на запятые и кавычки слева и справа и +допишем ещё кавычкр
        users = "'" + users.replace("," , "' , '") + "'";

        //А теперь это можно отправлять в запрос (и без сплита справились, путем ужесточения требований к входным данным.
        query = "SELECT * FROM Users where Phone in ( " + users + ")";

        resultSet = DBManager.getSelectResultSet(query);
        //Проверяем нашли ли мы хоть кого-то из запроса
        if(resultSet.next())
        {
            //Да, я знаю что и без листа можно (сразу все по ходу делать, но ускорения будет 0 (по сравнению со временем выполнения запросов),
            //а читаемость и понятность кода снизится =(, поэтому так
            //добавим всех в группу
            ArrayList<Integer> usersID = new ArrayList<>();
            //считаем ID в лист
            do{
                usersID.add(resultSet.getInt("UserID"));
            }while (resultSet.next());

            //Формируем запрос на вставку
            command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES ";
            for(int i = 0; i < usersID.size(); i++)
            {
                command  += "(" + groupID + "," + usersID.get(i) + ", 0 ),"; //и запятая в конце все норм
            }
            command += "(" + groupID + "," + creatorID + ", 0 )"; // + сам создатель без запятой уже в конце и запрос готов
            DBManager.execCommand(command);
        }
        else
        {
            //запись баланса этого пользователя в этой группе
            command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES(" +
                    + groupID + "," + creatorID + ", 0 )";
            DBManager.execCommand(command);
        }
        //Да, здесь берется MAX(GroupID) из таблицы Groups ! (а вставляется в таблицу GroupBalances
        DefaultClassAndDateAndID def = new DefaultClassAndDateAndID(groupID);
        def.setDate(valueOf(date));
        return def;
    }


    //После выполнения в контроллере можно добавить юзера в список userProfiles, а то тут его получать нет смысла
    //Так как он уже получен там в контроллере
    public static void addUserToGroupById(int userID, int groupID) throws Exception
    {
        //запись баланса этого пользователя в этой группе
        String command = "INSERT INTO GroupBalances (GroupID, UserID, Balance) VALUES(" +
                groupID +" , "+ userID + ", 0 )";

        DBManager.execCommand(command);
        //В принципе я могу пересчитать все балансы.. Но правильно ли это?
        //Если он только присоединился, я могу ему дать 0 баланс и все будет ОК
        //А иначе ты ток вошел, а хоп на тебе уже 10 тыс долга висит...
        //А другие наоборот в "плюс" вышли, из-за перераспределения расходов

    }


    //ЗДЕСЬ ПОКА НЕ ТОЧНО ВСЕ, МОГУТ БЫТЬ ИЗМЕНЕНИЯ
    //Этим же методом можно выходить из группы самостоятельно(!) - просто проверяешь, что юзер по токену и по телефону это одинаковые UserID, и сюда подаешь его..
    //Админу нельзя так делать! Будет ОТДЕЛЬНЫЙ метод для закрытия групповой беседы для админа
    //Проверку на админ\нет осуществляет устройство..
    public static void delUserFromGroupById(int userID, int groupID) throws Exception
    {
        //удаление баланса пользователя из группы, без проверки сойдет
        String command = "DELETE FROM GroupBalances WHERE (GroupID = " +groupID +" AND UserID = "+userID +" )";
        DBManager.execCommand(command);
        //А здесь что делать с балансом? общим и индивидуальными...
        //Можно пересчитывать (но как..не расплатился же или с ним не расплатились..)
        //Вариант: если баланс в минусе, перераспределяем расходы на остальных
        //          если в плюсе, то считаем, что он "подарил" эти деньги группе и "начисляем" всем из этой суммы поровну
    }


    //Получаем всех юзеров и историю транзакций для этой группы
    //Те поля, который были в объекте Group не дублируются! Запоминайте их там!
    public static void deleteGroupById(int groupID) throws Exception
    {
        //Есть варианты:

        //Вариант 1: по минимуму
        //Удаляем запись о группе и все -> не найдут GroupID такой, значит и не будет отображаться в списках диалогов
        //а следовательно ни записи транзакций ни балансов никто не будет искать
        String command = "DELETE FROM Groups where GroupID = " + groupID;
        DBManager.execCommand(command);

        //Вариант 2: по максимуму
        //Дополнительно удаляем всех пользователей и ещё можно записи о транзакциях в этой группе
        //Плюсы - меньше "живых" строк в таблице, легче искать потом другие данные(особенно много транзакций уйдет)
        //Минусы - потеря информации о передаче денежных средств и факте участия в группах

        //Коды есть, закомментить можно
        command = "DELETE FROM GroupBalances WHERE GroupID = " + groupID;
        DBManager.execCommand(command);
        command = "DELETE FROM Transactions WHERE GroupID = " + groupID;
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
        if(!resultSet.next())
        {
            throw new Exception("Группа не найлена.");
        }
        //автоматически сортируется!
        Map<Integer, Float> userBalanceMap = new TreeMap<>();

        //собираем соответствие юзеров и их балансов
        do
        {
            userBalanceMap.put(resultSet.getInt("UserID"), resultSet.getFloat("Balance"));
        }
        while (resultSet.next());
        //Собираем запрос на юзеров
        //так должно работать!, проверено в Management Studio
        query = "Select * from Users where UserID in (";
        //итерация по ключам
        for(int key: userBalanceMap.keySet())
        {
            query += key + ",";
        }
        query = query.substring(0, query.length()-1) +")";

        //получаем и парсим юзеров в этой группе
        resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Нет пользователей в группе.");
        }
        do
        {
            userProfiles.add(UserProfile.getUserProfileByUser(User.parseUser(resultSet)));
        }
        while (resultSet.next());

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
