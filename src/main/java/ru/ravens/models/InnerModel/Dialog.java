package ru.ravens.models.InnerModel;

import ru.ravens.models.UserProfile;
import ru.ravens.service.DBManager;

import javax.transaction.TransactionRequiredException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Date;

@XmlRootElement
public class Dialog implements Serializable
{
    private int dialogID;
    private float balance;

    private UserProfile userProfile;
    private Date date;
    private transient int userID;

    //парсинг диалога
    public static Dialog parseDialog(ResultSet resultSet, int myID) throws Exception
    {
        Dialog dialog = new Dialog();

        dialog.setDialogID(resultSet.getInt("DialogID"));
        dialog.setDate(resultSet.getTimestamp("Date"));
        if(resultSet.getInt("UserID_1")==myID)
        {
            dialog.setBalance(resultSet.getFloat("Balance_1"));
            //Запишем userID собеседника
            dialog.setUserID(resultSet.getInt("UserID_2"));
            //получаем собеседника с фоткой\именем и прочим
      //      dialog.setUserProfile(UserProfile.getUserProfileByUserID(resultSet.getInt("UserID_2")));
        }
        else if(resultSet.getInt("UserID_2")==myID)
        {
            dialog.setBalance(resultSet.getFloat("Balance_2"));
            //Запишем userID собеседника
            dialog.setUserID(resultSet.getInt("UserID_1"));
            //получаем собеседника с фоткой\именем и прочим
       //     dialog.setUserProfile(UserProfile.getUserProfileByUserID(resultSet.getInt("UserID_1")));
        }
        else
        {
            throw new Exception("Этот пользователь не имеет отношения к диалогу.");
        }
        return dialog;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDialogID() {
        return dialogID;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
    }

    public float getBalance() {
        return balance;
    }
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
