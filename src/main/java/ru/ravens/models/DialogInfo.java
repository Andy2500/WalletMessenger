package ru.ravens.models;

import ru.ravens.models.InnerModel.Transaction;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@XmlRootElement
public class DialogInfo implements Serializable
{
    private ArrayList<Transaction> transactions;
    private DefaultClass defaultClass;


    //Получает историю последних транзакций для этого диалога
    public static DialogInfo getDialogInfoById (int dialogID) throws Exception
    {
        DialogInfo dialogInfo = new DialogInfo();

        dialogInfo.setTransactions(Transaction.getTransactionsHistByDialogID(dialogID));

        return dialogInfo;
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
