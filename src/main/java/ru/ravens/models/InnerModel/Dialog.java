package ru.ravens.models.InnerModel;

import ru.ravens.models.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;

@XmlRootElement
public class Dialog implements Serializable
{
    private int dialogID;
    private int balance_1;
    private User user;


    //парсинг диалога
    public static Dialog parseDialog(ResultSet resultSet) throws Exception
    {
        Dialog dialog = new Dialog();

        dialog.setDialogID(resultSet.getInt("DialogID"));
        dialog.setBalance_1(resultSet.getInt("Balance_1"));
        //получаем собеседника с фоткой\именем и прочим
        dialog.setUser(User.getUserByID(resultSet.getInt("UserID_2")));

        return dialog;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public int getDialogID() {
        return dialogID;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
    }


    public int getBalance_1() {
        return balance_1;
    }

    public void setBalance_1(int balance_1) {
        this.balance_1 = balance_1;
    }

}
