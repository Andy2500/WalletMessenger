package ru.ravens.models;

import ru.ravens.models.InnerModel.Transaction;
import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.*;

import static java.sql.Timestamp.valueOf;

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
    public static DefaultClassAndDateAndID createNewDialog(int creatorID, int otherUserID) throws Exception
    {
        String query = "SELECT * FROM Dialogs where ( ( UserID_1 = " + creatorID + " AND UserID_2 = " + otherUserID + " ) OR" +
                " ( UserID_1 =" + otherUserID + " AND UserID_2 = " + creatorID + " ) )";
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        if(resultSet.next())
        {
            //Могу здесь ошибку создания либо делать вид, что все "ОК" и дать им старый диалог
           DefaultClassAndDateAndID defaultClassAndDateAndID =  new DefaultClassAndDateAndID(resultSet.getInt("DialogID"));
           defaultClassAndDateAndID.setDate(resultSet.getTimestamp("Date"));
           return defaultClassAndDateAndID;
        }
        else
        {
            query = "SELECT MAX(DialogID) FROM Dialogs";
            resultSet = DBManager.getSelectResultSet(query);
            if(!resultSet.next())
            {
                throw new Exception("Диалог не найден.");
            }
            int dialogID = resultSet.getInt(1) + 1;
            String date = DateWorker.getNowMomentInUTC();
            String command = "INSERT INTO Dialogs (DialogID, UserID_1, Balance_1, UserID_2, Balance_2, Date) VALUES("+
                    + dialogID +", "+ creatorID + ", 0, " + otherUserID +" , 0 , '" + date + "' )";
            //Балансы по нулям пока что
            DBManager.execCommand(command);

            DefaultClassAndDateAndID def = new DefaultClassAndDateAndID(dialogID);
            def.setDate(valueOf(date));
            return def;
        }
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
