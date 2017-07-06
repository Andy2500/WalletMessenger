package ru.ravens.models;

import ru.ravens.models.InnerModel.Dialog;
import ru.ravens.models.InnerModel.Group;
import ru.ravens.models.InnerModel.User;
import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//Так пусть это все будет на всякий случай, вдруг что-то изменится
@XmlRootElement
public class Conversations implements Serializable
{
    private ArrayList<Dialog> dialogs;
    private ArrayList<Group> groups;
    private DefaultClass defaultClass = new DefaultClass();





    public static Conversations getConversationsByUserID(int userID) throws Exception
    {
        ArrayList<Dialog> dialogList = new ArrayList<>();
        ArrayList<Group> groupList;

        int rows = 20; //всего 20 вернем
        //получаем диалоги и парсим
        String query = "Select top " + rows + " * from Dialogs Where (UserID_1 = " + userID +" OR UserID_2 = " + userID +" ) Order by Date DESC";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }

        //получаем группы
        groupList = getGroupList(rows,userID);
        //формируем и отправляем результат
        return prepareResult(dialogList, groupList,rows);
    }

    public static Conversations getConversationsHistByUserIdAndDate(int userID, long lastDate) throws Exception
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String date = format.format(lastDate);
        ArrayList<Dialog> dialogList = new ArrayList<>();
        ArrayList<Group> groupList;

        int rows = 20;
        //получаем диалоги по дате раньше чем ласт и парсим
        String query = "Select top " + rows + " * from Dialogs Where ((UserID_1 = " + userID +" OR UserID_2 = " + userID +" )  AND (Date < '"+ date + "')) Order by Date DESC";
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        while (resultSet.next())
        {
            dialogList.add(Dialog.parseDialog(resultSet, userID));
        }

        groupList = getGroupListBeforeDate(rows,userID,date);
        return prepareResult(dialogList, groupList,rows);
    }

    private static Conversations prepareResult(ArrayList<Dialog> dialogList, ArrayList<Group> groupList, int rows) throws Exception
    {
        //Сортируем если элементов более 1
        if (dialogList.size() > 1)
            Collections.sort(dialogList, getDialogComp());
        if (groupList.size() > 1)
            Collections.sort(groupList, getGroupComp());

        //Итоговые листы для отправки пользователю потом
        ArrayList<Dialog> dialogListFinish = new ArrayList<>();
        ArrayList<Group> groupListFinish = new ArrayList<>();

        //Здесь мы собираем массивы к отправке
        //Идем до минимума из 20 и суммы групп и диалогов (вдруг их там в сумме всего 10 или вообще 0)
        int dialogIter = 0, groupIter = 0, maxCount = Math.min(rows, dialogList.size() + groupList.size());
        for(int i = 0; i < maxCount; i++)
        {
            //Доп проверка индекса, чтобы не вылететь с эксепшеном
            if(dialogIter ==dialogList.size())
            {
                //значит диалоги кончились, берем группу
                groupListFinish.add(groupList.get(groupIter));
                groupIter++;
            }
            else if(groupIter == groupList.size())
            {
                //Значит группы кончились, и берем диалог
                dialogListFinish.add(dialogList.get(dialogIter));
                dialogIter++;
            }
            //значит и то и это есть,поэтому сравним по дате последней транзакции
            //Проверка: Если дата в диалогах случилась ПОСЛЕ даты в группе (обновлялась недавно!!!)
            //Тут все правильно !
            else if (dialogList.get(dialogIter).getDate().after(groupList.get(groupIter).getDate()))
            {
                //То мы берем диалог (он обновлялся не так давно как группа)
                dialogListFinish.add(dialogList.get(dialogIter));
                dialogIter++;
            }
            else
            {
                //Иначе мы берем группу
                groupListFinish.add(groupList.get(groupIter));
                groupIter++;
            }
        }
        //Прикрепляем юзеров к диалогам
        //Если имеется хоть один диалог для отправки
        if(dialogListFinish.size() > 0)
        {
            //Получим всех необходимые профили юзеров для диалогов
            String query = "SELECT * FROM Users where UserID in (";
            for(int i = 0; i < dialogListFinish.size(); i++)
            {
                query += dialogListFinish.get(i).getUserID()+ ",";
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
            for(int i = 0; i < dialogListFinish.size(); i++)
            {
                dialogListFinish.get(i).setUserProfile(userProfileHashMap.get(dialogListFinish.get(i).getUserID()));
            }
    }

        Conversations conversations = new Conversations();
        //Если нет диалогов или групп, то лист будет пустой
        conversations.setDialogs(dialogListFinish);
        conversations.setGroups(groupListFinish);
        return conversations;
    }

    private static ArrayList<Group> getGroupList(int rows, int userID) throws Exception
    {
        ArrayList<Group> groupList = new ArrayList<>();
        //получаем группы и парсим
        String query = "Select * from GroupBalances where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        //Если мы состоим в группах, то получим их (а если нет, то не надо получать)
        if(resultSet.next())
        {
            HashMap<Integer,Integer> mapGroupBalances = new HashMap<>();
            query = "SELECT top " + rows + " * FROM Groups WHERE GroupID in (";
            int groupID = 0;
            do
            {
                groupID = resultSet.getInt("GroupID");
                mapGroupBalances.put(groupID, resultSet.getInt("Balance"));
                query += groupID + ",";
            }while (resultSet.next());
            //Без последнего символа!
            query = query.substring(0, query.length()-1) +") Order by Date DESC";

            //Получаем все группы
            resultSet = DBManager.getSelectResultSet(query);
            while (resultSet.next())
            {   //парсим группы
                groupList.add(Group.parseGroup(resultSet));
            }
            for(int i=0; i < groupList.size(); i++)
            {
                //Ставим баланс нашего пользователя в каждой группе из мапы по ID группы
                groupList.get(i).setMyBalance(mapGroupBalances.get(groupList.get(i).getGroupID()));
            }
        }
        return groupList; //Если что вернет пустой лист с размером 0
    }

    private static ArrayList<Group> getGroupListBeforeDate(int rows, int userID, String date) throws Exception
    {
        ArrayList<Group> groupList = new ArrayList<>();
        //получаем группы и парсим
        String query = "Select * from GroupBalances where UserID = " + userID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        //Если мы состоим в группах, то получим их (а если нет, то не надо получать)
        if(resultSet.next())
        {
            HashMap<Integer, Integer> mapGroupBalances = new HashMap<>();
            query = "SELECT top " + rows + " * FROM Groups WHERE (Date < '" + date + "') AND (GroupID in (";
            int groupID = 0;
            do {
                groupID = resultSet.getInt("GroupID");
                mapGroupBalances.put(groupID, resultSet.getInt("Balance"));
                query += groupID + ",";
            } while (resultSet.next());

            //Без последнего символа!
            query = query.substring(0, query.length() - 1) + ")) Order by Date DESC";

            //Получаем все группы
            resultSet = DBManager.getSelectResultSet(query);
            while (resultSet.next())
            {   //парсим группы
                groupList.add(Group.parseGroup(resultSet));
            }
            for (int i = 0; i < groupList.size(); i++)
            {
                //Ставим баланс нашего пользователя в каждой группе из мапы по ID группы
                groupList.get(i).setMyBalance(mapGroupBalances.get(groupList.get(i).getGroupID()));
            }
        }
        return groupList; //Если что вернет пустой лист с размером 0
    }

    //Компаратор по дате(!)
    private static Comparator<Dialog> getDialogComp()
    {
        Comparator comp = new Comparator<Dialog>(){
            @Override
            public int compare(Dialog dialog1, Dialog dialog2)
            {
                return dialog1.getDate().compareTo(dialog2.getDate());
            }
        };
        return comp;
    }
    private static Comparator<Group> getGroupComp()
    {
        Comparator comp = new Comparator<Group>(){
            @Override
            public int compare(Group group1, Group group2)
            {
                return group1.getDate().compareTo(group2.getDate());
            }
        };
        return comp;
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

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}




//так короче это было в getConversations, но щас это не надо, поэтому пусть лежит, удалять не спешим

