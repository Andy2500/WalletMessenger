package ru.ravens.models.InnerModel;

import ru.ravens.models.DefaultClasssAndDateAndID;
import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

@XmlRootElement
public class Transaction implements Serializable
{
    private int transactionID;
    private int userID;
    private int receiverID;
    private int groupID;
    private int dialogID;
    private float money;
    private Date date;
    private int cash;
    private int proof;
    private String text;



    public static Transaction parseTransaction(ResultSet resultSet) throws Exception
    {
        Transaction transaction = new Transaction();

        transaction.setTransactionID(resultSet.getInt("TransactionID"));
        transaction.setUserID(resultSet.getInt("UserID"));
        transaction.setMoney(resultSet.getFloat("Money"));
        transaction.setDate(resultSet.getDate("Date"));
        transaction.setCash(resultSet.getInt("Cash"));
        transaction.setProof(resultSet.getInt("Proof"));
        transaction.setText(resultSet.getString("Text"));
        transaction.setDialogID(resultSet.getInt("DialogID"));
        transaction.setGroupID(resultSet.getInt("GroupID"));

        return transaction;
    }


    //методы для получения Истории транзакций (20) по ID группы или диалога при его открытии
    //в других местах использоваться недолжны!
    public static ArrayList<Transaction> getTransactionsHistByDialogID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where DialogID = " + id + " Order by DialogID DESC";
        return getTransactionsHist(query);
    }

    public static ArrayList<Transaction> getTransactionsHistByGroupID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where GroupID = " + id + " Order by GroupID DESC";
        return getTransactionsHist(query);
    }

    //закрытый метод!
    private static ArrayList<Transaction> getTransactionsHist(String query) throws Exception
    {
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        ArrayList<Transaction> list = new ArrayList<>();

        //Здесь не надо проверять на if(resultset.next()), так как если нету, то вернем пустой лист - > ибо нет ещё транзакций.. это норма!
        while (resultSet.next())
        {
            list.add(parseTransaction(resultSet));
        }
        return list;
    }


    //Отправка транзакции в диалоге
    public static DefaultClasssAndDateAndID SendTransactionDialog(int userID, int dialogID, int money, int cash, String text) throws Exception
    {
        String query = "SELECT * FROM Dialogs where DialogID = " + dialogID;

        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Диалог не найден.");
        }

        float balance;
        if(resultSet.getInt("UserID_1") == userID)
        {
            balance = resultSet.getFloat("Balance_1") + money;
        }
        else if(resultSet.getInt("UserID_2") == userID)
        {
            balance = resultSet.getFloat("Balance_1") - money;
        }
        else
        {
            throw new Exception("Этот пользователь не относится к диалогу!");
        }

        query = "SELECT MAX (TransactionID) from Transactions";
        resultSet =  DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Транзакций нет.");
        }
        int transactionID = resultSet.getInt(1) + 1;

        String date = DateWorker.getNowMomentInUTC();
        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text) " +
                "VALUES (" + transactionID + ", " + userID + ", " + dialogID + ", 0, " + money +
                ", '" + date + "', " + cash +", 0, N'" + text + "')";
        //пояснения: groupID = 0, так как это для диалога метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        //обновим дату последней транзакции в диалоге
        command = "UPDATE Dialogs set Date = '" + date + "' WHERE DialogID = "+dialogID;
        DBManager.execCommand(command);


        if(cash == 0)
        {
            //обновим информацию в диалогах!
            command = "UPDATE Dialogs set Balance_1 = " + balance + " , Balance_2 = " + (-1)*balance + " where DialogID = " +dialogID;
            DBManager.execCommand(command);
        } //Иначе! считаем, что информация неподтверждена!!
        // id  и дату вернули
        DefaultClasssAndDateAndID defaultClasssAndDateAndID = new DefaultClasssAndDateAndID(transactionID);
        defaultClasssAndDateAndID.setDate(date);
        return defaultClasssAndDateAndID;
    }


    public static void AcceptTransaction(int userID, int transactionID) throws Exception
    {
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = " + transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Транзакция не найдена.");
        }
        Transaction transaction = parseTransaction(resultSet);

        //Проверяем это транзакция диалога или нет
        if(transaction.getDialogID() > 0)
        {
            //получаем диалог
            query = "SELECT * FROM Dialogs where DialogID = " +transaction.getDialogID();
            resultSet = DBManager.getSelectResultSet(query);
            if(!resultSet.next())
            {
                throw new Exception("Диалог не найден.");
            }
            //обновляем транзакцию
            String command  = "UPDATE Transactions set Proof = 1 where TransactionID = " +transactionID;
            DBManager.execCommand(command);

            //вычисляем баланс
            float balance;
            if(resultSet.getInt("UserID_1") == userID)
            {
                balance = resultSet.getFloat("Balance_1") +transaction.getMoney();
            }
            else
            {
                balance = resultSet.getFloat("Balance_1")  - transaction.getMoney();
            }
            //обновляем баланс в диалоге
            command = "UPDATE Dialogs set Balance_1 = " + balance + ", Balance_2 = " + (-1)*balance + " where DialogID = "+ transaction.getDialogID();
            DBManager.execCommand(command);
        }
        //Иначе это транзакция в группе, проверяем она для пользователя или всем и вызываем соответствующие методы обновления баланса
        else if(transaction.getReceiverID() > 0)
        {
            SendToUserInGroup(transaction.getGroupID(),transaction.getUserID(), transaction.getReceiverID(),transaction.getMoney());
        }
        else
        {
            AddToGroupBalance(transaction.getGroupID(),transaction.getUserID(),transaction.getMoney());
        }
    }

    public static void DeclineTransaction(int userID, int transactionID) throws Exception
    {
        /*  и без этого сойдет, а нам -1 запрос лишний и UserID можно из параметров убрать в принципе
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = "+ transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Транзакция не найдена.");
        }
        Transaction transaction = parseTransaction(resultSet);
        */
        //обновляем док-ва, -1 значит отказано
        String command  = "UPDATE Transactions set Proof = -1 where TransactionID = " + transactionID;
        DBManager.execCommand(command);
    }


    public static DefaultClasssAndDateAndID SendTransactionGroup(int userID, int receiverID, int groupID, float money, int cash, String text) throws Exception
    {
        String query = "SELECT MAX(TransactionID) from Transactions";
        ResultSet resultSet =  DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Транзакций нет.");
        }
        int transactionID = resultSet.getInt(1) + 1;

        String date = DateWorker.getNowMomentInUTC();
        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, ReceiverID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES (" + transactionID + ", " + userID + ", " + receiverID + ", 0, "  +groupID+ ", " + money+
                ", '" + date + "', " + cash +", 0, N'" + text + "')";

        //пояснения: dialogID = 0, так как это для групп! метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        //обновим дату последней транзакции в группе
        command = "UPDATE Groups set Date = '" + date + "' WHERE GroupID = " + groupID;
        DBManager.execCommand(command);

        //Если это безналичный перевод
        if(cash == 0)
        {
            //Считаем, что он подтвержден и пересчитываем балансы
            if (receiverID > 0) // если мы хотим переслать конкретному человеку
                SendToUserInGroup(groupID, userID, receiverID, money);
            else
                AddToGroupBalance(groupID, userID, money);
        }
        //Иначе если это наличные, то ничего не делаем и ждем подтверждения от администратора группы !
        // id  и дату вернули
        DefaultClasssAndDateAndID defaultClasssAndDateAndID = new DefaultClasssAndDateAndID(transactionID);
        defaultClasssAndDateAndID.setDate(date);
        return defaultClasssAndDateAndID;
    }

    // добавить деньги в конфу
    private static void AddToGroupBalance(int groupID, int userID, float money) throws Exception {
        String command = "";
        //Найдем счета всех участников группы
        String query = "SELECT * FROM GroupBalances where GroupID = " + groupID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Ни одного баланса для этой группы не найдено.");
        }

        HashMap<Integer, Float> userMap = new HashMap<>(); // все участники
        do
        {   //Да, я знаю, что "Наш" юзер тоже здесь, НО ТАК И НАДО, если он должен денег (баланс < 0), то он будет платить долги всем кто имеет баланс > 0
            //и следовательно сам себе не заплатит!
            //Если у него нет долгов, то он нужен здесь в мапе чтобы пересчитать всем балансы
            userMap.put(resultSet.getInt("UserID"), resultSet.getFloat("Balance"));
        }
        while (resultSet.next());

        //Получим сумму в группе
        query = "SELECT * FROM Groups where GroupID = " + groupID;
        resultSet = DBManager.getSelectResultSet(query); //группа имеется, иначе выше бы выпал эксепшен
        //обновим сумму в группе
        if(!resultSet.next())
        {
            throw new Exception();
        }
        float sum = resultSet.getFloat("Sum") + money;
        command = "UPDATE Groups set Sum = " + sum + " where GroupID = " + groupID;
        DBManager.execCommand(command);

        float userBalance = userMap.get(userID) + money;
        userMap.replace(userID, userBalance); // добавили сумму к юзеру, который вносит в конфу еще денег

        //делим сумму на всех включая себя!
        float diff = money/userMap.size(); // новый добавочный долг для всех

        //Уменьшим все балансы на дополнительное вложение средств и обновим
        for (Map.Entry<Integer, Float> entry : userMap.entrySet())
        {
            //Да, много запросов подряд пойдет.
            //Да, я знаю что неэффективно, пока решение такое -  я гуглил другие варианты, там тоже не сахар ...
//                Решение такого типа я видел, у него тоже имеются недостатки (оно каждое к каждому проверяет, и при N юзерах, будет N*N/2 проверок..)
//                UPDATE table SET Name = CASE
//                WHEN (LASTNAME = 'AAA') THEN 'BBB'
//                WHEN (LASTNAME = 'CCC') THEN 'DDD'
//                WHEN (LASTNAME = 'EEE') THEN 'FFF'   ELSE  (LASTNAME)
//                END )
            entry.setValue(entry.getValue() - diff);
            command = "UPDATE GroupBalances set Balance = " + entry.getValue() + "where ( UserID = "
                    + entry.getKey() +" AND GroupID = "+ groupID + ")";
            DBManager.execCommand(command);
        }
    }

    //Вынесено в отдельный метод, так как понадобится при подтверждении транзакции админом группы
    private static void SendToUserInGroup(int groupID, int userID, int receiverID, float money) throws Exception
    {
        String command = "";
        //Найдем счета всех участников группы
        String query = "SELECT * FROM GroupBalances where GroupID = " + groupID + " AND (UserID = " + receiverID +
                " OR UserID = " + userID + ")";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Ни одного баланса для этой группы не найдено.");
        }
        int user_id1 = resultSet.getInt("UserID");
        float balance1 = resultSet.getFloat("Balance");
        resultSet.next();
        int user_id2 = resultSet.getInt("UserID");
        float balance2 = resultSet.getFloat("Balance");

        if (user_id1 == userID) { // если он отправляет деньги receiver-у
            balance1 -= money;
            balance2 += money;
        }
        else{
            balance1 += money;
            balance2 -= money;
        }

        command = "UPDATE GroupBalances set Balance = " + balance1 + "where ( UserID = " + user_id1 +" AND GroupID = "+ groupID + ")";
        DBManager.execCommand(command);
        command = "UPDATE GroupBalances set Balance = " + balance2 + "where ( UserID = " + user_id2 +" AND GroupID = "+ groupID + ")";
        DBManager.execCommand(command);
    }



    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }
    public void setMoney(float money) {
        this.money = money;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getDialogID() {
        return dialogID;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
    }
    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public float getMoney() {
        return money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getProof() {
        return proof;
    }

    public void setProof(int proof) {
        this.proof = proof;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
