package ru.ravens.models;

import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

@XmlRootElement
public class TransactionHist implements Serializable
{

    private ArrayList<Transaction> transactions;
    private DefaultClass defaultClass = new DefaultClass();

    //Возвращает историю транзакций (20) по ID диалога
    public static TransactionHist getHistByDialogIDAndTransactionID(int dialogID, int transactionID) throws Exception
    {
        int rows = 20;

        String query = "SELECT * FROM Transactions WHERE (ConversationID = "+ dialogID +" AND TransactionID <" + transactionID;
        query += " ORDER BY TransactionID DESC OFFSET 0 ROWS FETCH NEXT " + rows + " ROWS ONLY";

        return getTransactionsHist(query);
    }

    //Возвращает историю транзакций (20) по ID группы и самому раннему transactionID (самый маленький!)
    public static TransactionHist getHistByGroupIDAndTransactionID(int groupID, int transactionID) throws Exception
    {
        int rows = 20;

        String query = "SELECT * FROM Transactions WHERE (ConversationID = "+ groupID +" AND TransactionID <" + transactionID;
        query += " ORDER BY TransactionID DESC OFFSET 0 ROWS FETCH NEXT " + rows + " ROWS ONLY";

        return getTransactionsHist(query);
    }

    //Возвращает историю транзакций (20) по ID группы и самому раннему transactionID (самый маленький!)
    private static TransactionHist getTransactionsHist(String query) throws Exception
    {
        TransactionHist transactionHist = new TransactionHist();
        ArrayList<Transaction> list = new ArrayList<>();

        ResultSet resultSet = DBManager.getSelectResultSet(query);

        while (resultSet.next())
        {
            list.add(Transaction.parseTransaction(resultSet));
        }

        transactionHist.setTransactions(list);

        return transactionHist;
    }


    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }
}
