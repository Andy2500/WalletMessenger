package ru.ravens.models;


import ru.ravens.models.InnerModel.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class UserProfile implements Serializable
{
    private int userID;
    private String name;
    private String phone;
    private String qiwi;
    private String image;
    private float balance;
    private DefaultClass defaultClass;


    public static UserProfile getUserProfileByUser(User user)
    {
        return new UserProfile(user);
    }

    public static UserProfile getUserProfileByUserID(int userID) throws Exception
    {
        return new UserProfile(User.getUserByID(userID));
    }

    public UserProfile()
    {
        defaultClass = new DefaultClass();
    }

    private UserProfile(User user)
    {
        defaultClass = user.getDefaultClass();
        userID = user.getUserID();
        name = user.getName();
        phone = user.getPhone();
        qiwi = user.getQiwi();
        image = user.getImage();
        balance = user.getBalance();
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

    public String getQiwi() {
        return qiwi;
    }

    public void setQiwi(String qiwi) {
        this.qiwi = qiwi;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
