package ru.ravens.models.InnerModel;

import ru.ravens.models.UserProfile;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;

@XmlRootElement
public class Dialog implements Serializable
{
    private int dialogID;
    private float balance_1;

    private UserProfile userProfile;

    //парсинг диалога
    public static Dialog parseDialog(ResultSet resultSet, int myID) throws Exception
    {
        Dialog dialog = new Dialog();

        dialog.setDialogID(resultSet.getInt("DialogID"));

        if(resultSet.getInt("UserID_1")==myID)
        {
            dialog.setBalance_1(resultSet.getFloat("Balance_1"));
            //получаем собеседника с фоткой\именем и прочим
            dialog.setUserProfile(UserProfile.getUserProfileByUserID(resultSet.getInt("UserID_2")));
        }
        else if(resultSet.getInt("UserID_2")==myID)
        {
            dialog.setBalance_1(resultSet.getFloat("Balance_2"));
            //получаем собеседника с фоткой\именем и прочим
            dialog.setUserProfile(UserProfile.getUserProfileByUserID(resultSet.getInt("UserID_1")));
        }
        else
        {
            throw new Exception("Этот пользователь не имеет отношения к диалогу.");
        }

        return dialog;
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

    public float getBalance_1() {
        return balance_1;
    }

    public void setBalance_1(float balance_1) {
        this.balance_1 = balance_1;
    }


}
