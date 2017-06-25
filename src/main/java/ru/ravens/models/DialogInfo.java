package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Transaction;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;

@XmlRootElement
public class DialogInfo implements Serializable
{
    private Dialog dialogs;
    private ArrayList<Transaction> transactions;

    public Dialog getDialogs() {
        return dialogs;
    }

    public void setDialogs(Dialog dialogs) {
        this.dialogs = dialogs;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
