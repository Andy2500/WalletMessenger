package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

@XmlRootElement
public class DialogInfo implements Serializable
{
    private ArrayList<Transaction> transactions;
    private DefaultClass defaultClass = new DefaultClass();


    //Получает историю последних транзакций для этого диалога
    public static DialogInfo getDialogInfoById (int dialogID) throws Exception
    {
        DialogInfo dialogInfo = new DialogInfo();

        dialogInfo.setTransactions(Transaction.getTransactionsHistByDialogID(dialogID));
        return dialogInfo;
    }

    //Создает новый диалог,
    // проверка на наличие пользователя по токену и по телефону в контроллере, там же и отправка соответствующих ошибок
    //Возвращает ID диалога
    public static DefaultClassAndId createNewDialog(int creatorID, int otherUserID) throws Exception
    {
        String query = "SELECT MAX(DialogID) FROM Dialogs";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        if(!resultSet.next())
        {
            throw new Exception("Диалог не найден.");
        }
        int dialogID = resultSet.getInt(1) + 1;

        String command = "INSERT INTO Dialogs (DialogID, UserID_1, Balance_1, UserID_2, Balance_2) VALUES("+
                + dialogID +", "+ creatorID + ", 0, " + otherUserID +" , 0 )";
        //Балансы по нулям пока что

        DBManager.execCommand(command);
        return new DefaultClassAndId(dialogID);
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
