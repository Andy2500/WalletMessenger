package ru.ravens.models.InnerModel;

import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Exchanger;

@XmlRootElement
public class Transaction implements Serializable
{
    private int transactionID;
    private int userID;
    private int groupID;
    private int dialogID;
    private float money;
    private Date date;
    private int cash;
    private int proof;
    private String text;

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


    //методы для получения Истории транзакций (20) по ID группы или диалога
    public static ArrayList<Transaction> getTransactionsHistByDialogID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where GroupID = " + id + "Order by GroupID DECS";
        return getTransactionsHist(query);
    }

    public static ArrayList<Transaction> getTransactionsHistByGroupID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where DialogID = " + id + "Order by GroupID DECS";
        return getTransactionsHist(query);
    }

    //закрытый метод!
    private static ArrayList<Transaction> getTransactionsHist(String query) throws Exception
    {
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        ArrayList<Transaction> list = new ArrayList<>();
        while (resultSet.next())
        {
            list.add(parseTransaction(resultSet));
        }
        return list;
    }


    //Отправка транзакции в диалоге
    public static void SendTransactionDialog(int userID, int dialogID, int money, int cash, String text) throws Exception
    {
        text = "'"+text+"'";
        String query = "SELECT * FROM Dialogs where DialogID = " + dialogID;

        ResultSet resultSet = DBManager.getSelectResultSet(query);

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

        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES ((SELECT MAX (TransactionID) from Transactions) + 1, " + userID + ", " + dialogID + ", 0, "+money+
                ", " + DateWorker.getNowMomentInUTC() + ", " + cash +", 0, " + text;

        //пояснения: groupID = 0, так как это для диалога метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        if(cash == 0)
        {
            //обновим информацию в диалогах!
            command = "UPDATE Dialogs set Balance_1 = " + balance + " , Balance_2 = " + (-1)*balance + " where DialogID = " +dialogID;
            DBManager.execCommand(command);
        }
        //Иначе! считаем, что информация неподтверждена!!
    }


    public static void AcceptTransaction(int userID, int transactionID) throws Exception
    {
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = "+ transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        Transaction transaction = parseTransaction(resultSet);

        //получаем диалог
        query = "SELECT * FROM Dialogs where DialogID = " +transaction.getDialogID();
        resultSet = DBManager.getSelectResultSet(query);

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
        command = "UPDATE Dialogs set Balance_1 = " + balance + ", set Balance_2 = " + (-1)*balance + "where DialogID = "+ transaction.getDialogID();
        DBManager.execCommand(command);
    }

    public static void DeclineTransaction(int userID, int transactionID) throws Exception
    {
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = "+ transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        Transaction transaction = parseTransaction(resultSet);

        //обновляем док-ва, -1 значит отказано
        String command  = "UPDATE Transactions set Proof = -1 where TransactionID = " + transactionID;
        DBManager.execCommand(command);
    }


    public static void SendTransactionGroup(int userID, int groupID, float money, int cash, String text) throws Exception
    {
        text = "'"+text+"'";

        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES ((SELECT MAX (TransactionID) from Transactions) + 1, " + userID + ", 0, " +groupID+ ", " + money+
                ", " + DateWorker.getNowMomentInUTC() + ", " + cash +", 0, " + text;

        //пояснения: dialogID = 0, так как это для групп! метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        //Если это безналичный перевод
        if(cash == 0)
        {
            //найдем счет этого пользователя в группе
            String query = "SELECT * FROM GroupBalances where (GroupID = "+groupID +" AND UserID = "+userID +")";
            ResultSet resultSet = DBManager.getSelectResultSet(query);

            float balance = resultSet.getFloat("Balance");

            //прибавим ему счет
            balance = balance + money;

            //Если там больше нуля, то надо пересчитать баланс группы и обновить всем счета
            if(balance > 0)
            {
                query = "SELECT * FROM Groups where GroupID = "+ groupID;
                resultSet = DBManager.getSelectResultSet(query);

                //Не закончено!!


                command = "UPDATE Groups set Sum = "+resultSet.getInt("Sum");
            }
            else
            {
                command = "UPDATE GroupBalances set Balance = " + balance + "where ( UserID = " +userID +" AND GroupID = "+ groupID + ")";
                DBManager.execCommand(command);

            }
        }
        //Если это наличные, то ничего не делаем и ждем подтверждения от администратора группы !
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
