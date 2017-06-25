package ru.ravens.models.InnerModel;

import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

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
