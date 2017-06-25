package ru.ravens.models;

import ru.ravens.service.DBManager;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.ResultSet;

@XmlRootElement
public class User implements Serializable{

    private int userID;
    private String name;
    private String phone;
    private String image;
    private String qiwi;

    private int balance;

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


    private static User checkUser(String query, String exMessage) throws Exception {

        User user = DBManager.getUserByQuery(query);

        if (user.getUserID() == 0)
            throw new Exception(exMessage);

        return user;
    }

    //парсинг юзера из result set
    public static User parseUser(ResultSet resultSet) throws Exception
    {
        User user = new User();

        user.setUserID(resultSet.getInt("UserID"));
        user.setName(resultSet.getString("Name"));
        user.setPhone(resultSet.getString("Phone"));
        user.setImage(resultSet.getString("Image"));
        user.setQiwi(resultSet.getString("Qiwi"));

        return user;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
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


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getQiwi() {
        return qiwi;
    }

    public void setQiwi(String qiwi) {
        this.qiwi = qiwi;
    }

}
