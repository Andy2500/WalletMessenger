package ru.ravens.models.InnerModel;

import ru.ravens.models.DefaultClass;
import ru.ravens.models.UserProfile;
import ru.ravens.service.DBManager;
import ru.ravens.service.DateWorker;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;

@XmlRootElement
public class User implements Serializable{

    private int userID;
    private String name;
    private String phone;
    private String hashpsd;
    private String image;
    private String token;

    private DefaultClass defaultClass = new DefaultClass();

    private float balance;


    //получение пользователя по ID
    public static User getUserByID(int ID) throws Exception
    {
        String query = "Select * From Users Where PersonID = " + ID;
        return checkUser(query, "Такого пользователя не существует");
    }

    //получение пользователя по токену
    public static User getUserByToken(String token) throws Exception
    {
        String query = "Select * From Users Where Token = '" + token + "'";
        return checkUser(query, "token error");
    }

    //получение пользователя по токену
    public static User getUserByPhone(String phone) throws Exception
    {
        String query = "Select * From Users Where Phone = '" + phone + "'";
        return checkUser(query, "phone error");
    }

    private static User checkUser(String query, String exMessage) throws Exception {

        ResultSet resultSet = DBManager.getSelectResultSet(query);

        if(resultSet.next())
        {
            User user = User.parseUser(resultSet);
            if (user.getUserID() == 0)
                throw new Exception(exMessage);

            return user;
        }
        else
            throw new Exception("Пользователя не существует");

    }

    //парсинг юзера из result set
    public static User parseUser(ResultSet resultSet) throws Exception
    {
        User user = new User();

        user.setUserID(resultSet.getInt("UserID"));
        user.setName(resultSet.getString("Name"));
        user.setPhone(resultSet.getString("Phone"));
        user.setImage(resultSet.getString("Image"));
        user.setHashpsd(resultSet.getString("Hashpsd"));
        user.setToken(resultSet.getString("Token"));

        return user;
    }

    public static UserProfile registerUser(String name, String phone, String hashpsd, String token) throws Exception
    {
        name = "'" + name +"'";
        phone = "'" + phone +"'";
        hashpsd = "'" + hashpsd +"'";
        token = "'" + token +"'";
        //добавим новую запись в юзеров
        //надо про prepared Statement !
        String command = "Insert into Users (UserID, Name, Phone, Hashpsd, Qiwi, Image, Token)" +
                "VALUES ((SELECT MAX (UserID) from Users) + 1, " + name + ", " + phone + ", 0, " + hashpsd +", 0, 0," + token + ")";
        //пояснения: groupID = 0, так как это для диалога метод, proof = 0, так как даже если там кэш\не кэш то все равно идет "отправка" транзакции
        DBManager.execCommand(command);

        return UserProfile.getUserProfileByUser(User.getUserByToken(token));
    }

    public static void changePsd(int userID, String newPsd) throws Exception
    {
        newPsd = "'" + newPsd +"'";
        String command = "UPDATE Users set Hashpsd = " + newPsd + "where UserID = " + userID;
        DBManager.execCommand(command);
    }

    public static void changeImage(int userID, String image) throws Exception
    {
        byte[] arr = javax.xml.bind.DatatypeConverter.parseBase64Binary(image);
        String command = "UPDATE Users set Image = ? where UserID = " + userID;
        DBManager.loadPhoto(command,arr);
    }


    public static void changeName(int userID, String newName) throws Exception
    {
        newName = "'" + newName +"'";
        String command = "UPDATE Users set Name = " + newName + "where UserID = " + userID;
        DBManager.execCommand(command);
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHashpsd() {
        return hashpsd;
    }

    public void setHashpsd(String hashpsd) {
        this.hashpsd = hashpsd;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DefaultClass getDefaultClass() {
        return defaultClass;
    }

    public void setDefaultClass(DefaultClass defaultClass) {
        this.defaultClass = defaultClass;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
