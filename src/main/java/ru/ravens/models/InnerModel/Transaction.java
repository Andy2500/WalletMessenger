package ru.ravens.models.InnerModel;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.DefaultClassAndId;
import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;
import sun.text.resources.cldr.ki.FormatData_ki;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.Exchanger;
import java.util.stream.Collectors;

@XmlRootElement
public class Transaction implements Serializable
{
    private int transactionID;
    private int userID;
    private int groupID;
    private int dialogID;
    private float money;
    private Date date;
    private int cash;
    private int proof;
    private String text;



    public static Transaction parseTransaction(ResultSet resultSet) throws Exception
    {
        Transaction transaction = new Transaction();

        transaction.setTransactionID(resultSet.getInt("TransactionID"));
        transaction.setUserID(resultSet.getInt("UserID"));
        transaction.setMoney(resultSet.getFloat("Money"));
        transaction.setDate(resultSet.getDate("Date"));
        transaction.setCash(resultSet.getInt("Cash"));
        transaction.setProof(resultSet.getInt("Proof"));
        transaction.setText(resultSet.getString("Text"));
        transaction.setDialogID(resultSet.getInt("DialogID"));
        transaction.setGroupID(resultSet.getInt("GroupID"));

        return transaction;
    }


    //методы для получения Истории транзакций (20) по ID группы или диалога при его открытии
    //в других местах использоваться недолжны!
    public static ArrayList<Transaction> getTransactionsHistByDialogID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where GroupID = " + id + "Order by GroupID DECS";
        return getTransactionsHist(query);
    }

    public static ArrayList<Transaction> getTransactionsHistByGroupID(int id) throws Exception
    {
        int rows = 20;

        String query = "Select top " + rows + " * from Transactions Where DialogID = " + id + "Order by GroupID DECS";
        return getTransactionsHist(query);
    }

    //закрытый метод!
    private static ArrayList<Transaction> getTransactionsHist(String query) throws Exception
    {
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        ArrayList<Transaction> list = new ArrayList<>();
        while (resultSet.next())
        {
            list.add(parseTransaction(resultSet));
        }
        return list;
    }


    //Отправка транзакции в диалоге
    public static DefaultClassAndId SendTransactionDialog(int userID, int dialogID, int money, int cash, String text) throws Exception
    {
        text = "'"+text+"'";
        String query = "SELECT * FROM Dialogs where DialogID = " + dialogID;

        ResultSet resultSet = DBManager.getSelectResultSet(query);

        float balance;
        if(resultSet.getInt("UserID_1") == userID)
        {
            balance = resultSet.getFloat("Balance_1") + money;
        }
        else if(resultSet.getInt("UserID_2") == userID)
        {
            balance = resultSet.getFloat("Balance_1") - money;
        }
        else
        {
            throw new Exception("Этот пользователь не относится к диалогу!");
        }

        query = "SELECT MAX (TransactionID) from Transactions";
        int transactionID = DBManager.getSelectResultSet(query).getInt("TransactionID") + 1;
        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES (" + transactionID + ", " + userID + ", " + dialogID + ", 0, "+money+
                ", " + DateWorker.getNowMomentInUTC() + ", " + cash +", 0, " + text;

        //пояснения: groupID = 0, так как это для диалога метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        if(cash == 0)
        {
            //обновим информацию в диалогах!
            command = "UPDATE Dialogs set Balance_1 = " + balance + " , Balance_2 = " + (-1)*balance + " where DialogID = " +dialogID;
            DBManager.execCommand(command);
        } //Иначе! считаем, что информация неподтверждена!!

        return new DefaultClassAndId(transactionID);
    }


    public static void AcceptTransaction(int userID, int transactionID) throws Exception
    {
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = "+ transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        Transaction transaction = parseTransaction(resultSet);

        //получаем диалог
        query = "SELECT * FROM Dialogs where DialogID = " +transaction.getDialogID();
        resultSet = DBManager.getSelectResultSet(query);

        //обновляем транзакцию
        String command  = "UPDATE Transactions set Proof = 1 where TransactionID = " +transactionID;
        DBManager.execCommand(command);

        //вычисляем баланс
        float balance;
        if(resultSet.getInt("UserID_1") == userID)
        {
            balance = resultSet.getFloat("Balance_1") +transaction.getMoney();
        }
        else
        {
            balance = resultSet.getFloat("Balance_1")  - transaction.getMoney();
        }
        //обновляем баланс в диалоге
        command = "UPDATE Dialogs set Balance_1 = " + balance + ", set Balance_2 = " + (-1)*balance + "where DialogID = "+ transaction.getDialogID();
        DBManager.execCommand(command);
    }

    public static void DeclineTransaction(int userID, int transactionID) throws Exception
    {
        //получаем транзакцию
        String query = "SELECT * FROM Transactions where TransactionID = "+ transactionID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        Transaction transaction = parseTransaction(resultSet);

        //обновляем док-ва, -1 значит отказано
        String command  = "UPDATE Transactions set Proof = -1 where TransactionID = " + transactionID;
        DBManager.execCommand(command);
    }


    public static DefaultClassAndId SendTransactionGroup(int userID, int groupID, float money, int cash, String text) throws Exception
    {
        text = "'"+text+"'";
        String query = "SELECT MAX(TransactionID) from Transactions";
        int transactionID = DBManager.getSelectResultSet(query).getInt("TransactionID");

        //добавим новую запись в транзакции
        String command = "Insert into Transactions (TransactionID, UserID, DialogID, GroupID, Money, Date, Cash, Proof, Text)" +
                "VALUES (" + transactionID + ", " + userID + ", 0, " +groupID+ ", " + money+
                ", " + DateWorker.getNowMomentInUTC() + ", " + cash +", 0, " + text;

        //пояснения: dialogID = 0, так как это для групп! метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        //Если это безналичный перевод
        if(cash == 0)
        {
            //Считаем, что он подтвержден и пересчитываем балансы
            CalculateGroupBalances(groupID, userID, money);
        }
        //Иначе если это наличные, то ничего не делаем и ждем подтверждения от администратора группы !
        return new DefaultClassAndId(transactionID);
    }



    //Вынесено в отдельный метод, так как понадобится при подтверждении транзакции админом группы
    private static void CalculateGroupBalances(int groupID, int userID, float money) throws Exception
    {
        String command = "";
        //Найдем счета всех участников группы
        String query = "SELECT * FROM GroupBalances where GroupID = " + groupID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);
        HashMap<Integer, Float> userMap = new HashMap<>();

        while (resultSet.next())
        {   //Да, я знаю, что "Наш" юзер тоже здесь, НО ТАК И НАДО, если он должен денег (баланс < 0), то он будет платить долги всем кто имеет баланс > 0
            //и следовательно сам себе не заплатит!
            //Если у него нет долгов, то он нужен здесь в мапе чтобы пересчитать всем балансы
            userMap.put(resultSet.getInt("UserID"), resultSet.getFloat("Balance"));
        }

        //возьмем баланс нашего юзера
        float balance = userMap.get(userID);

        ArrayList<Map.Entry<Integer,Float>> userList = new ArrayList<>();
        int listIndex = 0;

        //Проверяем был ли вообще долг у этого юзера...
        if (balance < 0)
        {
            //Конкретно - при изменении данных в userList будут изменяться значения и в userMap, которая находится в этом методе.
            //Проверено в отдельном проекте. Можете сделать тоже самое и убедиться в этом.
            //именно ArrayList использован, так как он сортируется элементарным кодом.
            userList.addAll(userMap.entrySet());
            listIndex = CheckAndPayDebt(balance, money, userList);
        }
        //пересчитали баланс нашему юзеру
        balance = balance + money;
        userMap.replace(userID, balance);
        //Теперь посмотрим стал ли баланс положительным, если да, значит надо пересчитать сумму в группе и баланс всех участников!
        if(balance > 0)
        {
            //Уменьшим все балансы на дополнительное вложение средств в сумму группы
            ChangeBalances(groupID, balance, userMap);
            //Сохраним изменения в базе данных

            for (Map.Entry<Integer, Float> entry : userMap.entrySet())
            {
                //Да, много запросов подряд пойдет.
                //Да, я знаю что неэффективно, пока решение такое -  я гуглил другие варианты, там тоже не сахар ...
//                Решение такого типа я видел, у него тоже имеются недостатки (оно каждое к каждому проверяет, и при N юзерах, будет N*N проверок..)
//                UPDATE table SET Name = CASE
//                WHEN (LASTNAME = 'AAA') THEN 'BBB'
//                WHEN (LASTNAME = 'CCC') THEN 'DDD'
//                WHEN (LASTNAME = 'EEE') THEN 'FFF'   ELSE  (LASTNAME)
//                END )
                command = "UPDATE GroupBalances set Balance = " + entry.getValue() + "where ( UserID = " +entry.getKey() +" AND GroupID = "+ groupID + ")";
                DBManager.execCommand(command);
            }
        }
        else //Если баланс все ещё отрицательный, то пересчета не будет и следовательно надо обновить только текущего и тех, кому он "отдал долг"
        {
            //Апдейт нашего юзера !
            command = "UPDATE GroupBalances set Balance = " + userID + "where ( UserID = " + balance +" AND GroupID = "+ groupID + ")";
            DBManager.execCommand(command);

            //и ВСЕХ кому он изменил счет, но не абсолютно всех их списка!
            for(int i = 0; i < listIndex; i++)
            {
                //Да, я знаю что неэффективно, пока решение такое -  я гуглил другие варианты, там тоже не сахар ...
                command = "UPDATE GroupBalances set Balance = " + userList.get(i).getValue() + "where ( UserID = " + userList.get(i).getKey() +" AND GroupID = "+ groupID + ")";
                DBManager.execCommand(command);
            }
        }
    }

    //Конкретно - при изменении данных в userList будут изменяться значения и в userMap, которая находится в методе выше.
    //Проверено.
    private static int CheckAndPayDebt(float balance, float money, ArrayList<Map.Entry<Integer, Float>> userList)
    {
        Collections.sort(userList, Map.Entry.comparingByValue());
        //по убыванию баланса теперь!
        Collections.reverse(userList);

        //проверяем хватило ли ему внесенной суммы, чтобы расчитаться с долгами...
        float debtPayment = (money + balance > 0) ? (0 - balance) : money;

        //оплачиваем долг
        int i = 0;
        for (; i < userList.size(); i++)
        {
            float value = userList.get(i).getValue();
            if (value > debtPayment)
            {
                //деньги кончились
                userList.get(i).setValue(value - debtPayment);
                break; //выход из цикла
            }
            else
            {
                //денег хватает оплатить ещё нескольким пользователям
                //ниже нуля не ставим!
                userList.get(i).setValue(0f);
                debtPayment -= value;
            }
        }
        i++; //чтобы итерировать потом до < index;
        return i;
    }

    //экспериментальный метод, закрытый!
    private static int PayDebt(float debtPayment, ArrayList<Map.Entry<Integer, Float>> userList)
    {
        Collections.sort(userList, Map.Entry.comparingByValue());
        //по возрастанию баланса!
        int count = 0;
        float value;
        boolean finishFlag = true, countFlag = true;
        for(int i = 0; i < userList.size(); i++)
        {
            value = userList.get(i).getValue();
            //идем до "положительных" значений
            if(value > 0)
            {
                //сколько сейчас имеют положительный баланс
                int creditors = userList.size() - i;

                if(countFlag)
                {
                    count = creditors;
                    countFlag = false;
                }

                //Если денег нехватит чтобы погасить всех на самую маленькую сумму, то...
                if(debtPayment < value*creditors)
                {
                    //пересчитываем какую сумму вычитать
                    value = debtPayment/creditors;
                    finishFlag = false;
                }
                //иначе вычитаем полную сумму (и тогда счет юзера[i] станет = 0

                //вычитаем из всех у кого больше 0 эту сумму
                for(int j = i; j < userList.size(); j++)
                {
                    userList.get(j).setValue(userList.get(j).getValue() - value);
                }
            }

            //проверяем идти ли на след круг
            if(!finishFlag)
            {
                break;
            }
        }
        return count;
    }

    private static void ChangeBalances(int groupID, float balance, HashMap<Integer, Float> map) throws Exception
    {
        //Получим сумму в группе
        String query = "SELECT * FROM Groups where GroupID = " + groupID;
        ResultSet resultSet = DBManager.getSelectResultSet(query);

        //обновим сумму в группе
        float sum = resultSet.getFloat("Sum") + balance;
        String command = "UPDATE Groups set Sum = " + sum + " where GroupID = " + groupID;
        DBManager.execCommand(command);

        //делим сумму на всех включая себя!
        float diff = balance/map.size();

        //Уменьшим все балансы на дополнительное вложение средств
        for (Map.Entry<Integer, Float> entry : map.entrySet())
        {
            entry.setValue(entry.getValue() - diff);
        }
    }


    public void setMoney(float money) {
        this.money = money;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getDialogID() {
        return dialogID;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
    }
    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public float getMoney() {
        return money;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getProof() {
        return proof;
    }

    public void setProof(int proof) {
        this.proof = proof;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
