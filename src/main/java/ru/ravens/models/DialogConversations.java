package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.User;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement
public class DialogConversations implements Serializable
{

    private ArrayList<Dialog> dialogs;
    private DefaultClass defaultClass = new DefaultClass();

    public static DialogConversations getDialogConversationsByUserID(int userID) throws Exception
    {
        ArrayList<Dialog> dialogList = new ArrayList<>();
        int rows = 20;
        //получаем диалоги и парсим
        String query = "Select top " + rows + " * from Dialogs Where (UserID_1 = " + userID +" OR UserID_2 = " + userID +" ) Order by Date DESC";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }
        //формируем и отправляем результат
        return FindUsersForDialogList(dialogList);
    }

    public static DialogConversations getDialogConversationsHistByUserIdAndDate(int userID, long lastDate) throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = format.format(lastDate);
        ArrayList<Dialog> dialogList = new ArrayList<>();

        int rows = 20;
        //получаем диалоги по дате раньше чем ласт и парсим
        String query = "Select top " + rows + " * from Dialogs Where ((UserID_1 = " + userID +" OR UserID_2 = " + userID +" )  AND (Date < '"+ date + "')) Order by Date DESC";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }
        //формируем и отправляем результат
        return FindUsersForDialogList(dialogList);
    }

    private static DialogConversations FindUsersForDialogList(ArrayList<Dialog> dialogList) throws Exception
    {
        //Прикрепляем юзеров к диалогам
        //Если имеется хоть один диалог для отправки
        if(dialogList.size() > 0)
        {
            //Получим всех необходимые профили юзеров для диалогов
            String query = "SELECT * FROM Users where UserID in (";
            for(int i = 0; i < dialogList.size(); i++)
            {
                query += dialogList.get(i).getUserID()+ ",";
            }
            query = query.substring(0,query.length()-1) + ")";
            ResultSet resultSet = DBManager.getSelectResultSet(query);

            //Парсим юзера и получаем профиль и кладем в мап (userID, UserProfile)
            HashMap<Integer, UserProfile> userProfileHashMap =  new HashMap<>();
            if(resultSet.next())
            {
                do{
                    UserProfile userProfile = UserProfile.getUserProfileByUser(User.parseUser(resultSet));
                    userProfileHashMap.put(userProfile.getUserID(), userProfile);
                }while (resultSet.next());
            }

            //Ставим юзер профили для каждого диалога через мапу
            for(int i = 0; i < dialogList.size(); i++)
            {
                dialogList.get(i).setUserProfile(userProfileHashMap.get(dialogList.get(i).getUserID()));
            }
        }

        DialogConversations dialogConversations = new DialogConversations();
        dialogConversations.setDialogs(dialogList);
        //формируем и отправляем результат
        return dialogConversations;
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public ArrayList<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(ArrayList<Dialog> dialogs) {
        this.dialogs = dialogs;
    }
}
