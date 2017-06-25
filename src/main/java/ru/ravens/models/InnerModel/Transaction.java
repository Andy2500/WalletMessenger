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
    private int money;
    private Date date;
    private int cash;
    private int proof;
    private String text;

    public static Transaction parseTransaction(ResultSet resultSet) throws Exception
    {
        Transaction transaction = new Transaction();

        transaction.setTransactionID(resultSet.getInt("TransactionID"));
        transaction.setUserID(resultSet.getInt("UserID"));
        transaction.setMoney(resultSet.getInt("Money"));
        transaction.setDate(resultSet.getDate("Date"));
        transaction.setCash(resultSet.getInt("Cash"));
        transaction.setProof(resultSet.getInt("Proof"));
        transaction.setText(resultSet.getString("Text"));

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


    //Отправка транзакции в диалог
    public static void SendTransactionDialog(int userID, int dialogID, int money, int cash, String text) throws Exception
    {
        String query = "SELECT * FROM Dialogs where DialogID = " + dialogID;

        ResultSet resultSet = DBManager.getSelectResultSet(query);

        int balance;
        if(resultSet.getInt("UserID_1") == userID)
        {
            balance = resultSet.getInt("Balance_1") + money;
        }
        else if(resultSet.getInt("UserID_2") == userID)
        {
            balance = resultSet.getInt("Balance_1") - money;
        }
        else
        {
            throw new Exception("Этот пользователь не относится к диалогу!");
        }

        //обновим информацию в диалогах!
        String command = "UPDATE Dialogs set Balance_1 = " + balance + " , Balance_2 = " + (-1)*balance + " where DialogID = " +dialogID;
        DBManager.execCommand(command);

        //добавим новую запись в транзакции
        command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES ((SELECT MAX (TransactionID) from Transactions) + 1, " + userID + ", " + dialogID + ", 0, "+money+
                ", " + DateWorker.getNowMomentInUTC() + ", " + cash +", 0, " + text;

        //пояснения: groupID = 0, так как это для диалога метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);
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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
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
