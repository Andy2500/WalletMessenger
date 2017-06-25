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

    private int balance;

    public static UserProfile getUserProfileByUser(User user)
    {
        return new UserProfile(user);
    }

    public static UserProfile getUserProfileByUserID(int userID) throws Exception
    {
        return new UserProfile(User.getUserByID(userID));
    }

    private UserProfile(User user)
    {
        userID = user.getUserID();
        name = user.getName();
        phone = user.getPhone();
        qiwi = user.getQiwi();
        image = user.getImage();
        balance = user.getBalance();
    }


}
