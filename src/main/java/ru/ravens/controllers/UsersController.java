package ru.ravens.controllers;


import ru.ravens.models.DefaultClass;
import ru.ravens.models.InnerModel.*;
import ru.ravens.models.UserProfile;
import ru.ravens.service.TokenBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class UsersController {

    @GET
    @Path("/test/{a}")
    @Produces(MediaType.APPLICATION_JSON)
    public String test(@PathParam("a") Integer a) {
        return a.toString();
    }

    @GET
    @Path("/reg/")
    @Produces(MediaType.APPLICATION_JSON)
    public UserProfile register(@FormParam("phone") String phone, @FormParam("name") String name, @FormParam("hashpsd") String hashpsd) {
        try {
            String token = TokenBuilder.makeToken(phone);
            UserProfile userProfile = User.registerUser(name, phone, hashpsd, token);
            userProfile.getDefaultClass().setToken(token);
            userProfile.getDefaultClass().setOperationOutput(true);
            return userProfile;

        } catch (Exception ex) {
            UserProfile userProfile = UserProfile.getUserProfileByUser(new User());
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/log/")
    @Produces(MediaType.APPLICATION_JSON) // авторизация
    public UserProfile auth(@FormParam("phone") String phone, @FormParam("hashpsd") String hashpsd) {
        try {
            User user = User.getUserByPhone(phone);
            if (!user.getHashpsd().equals(hashpsd)) // если пароли не совпадают
            {
                throw new Exception("Incorrect password");
            }
            UserProfile userProfile = UserProfile.getUserProfileByUser(user);
            userProfile.getDefaultClass().setOperationOutput(true);
            userProfile.getDefaultClass().setToken(user.getToken());
            return userProfile;

        } catch (Exception ex) {
            UserProfile userProfile = UserProfile.getUserProfileByUser(new User());
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chpsd/")
    @Produces(MediaType.APPLICATION_JSON) // изменить пароль
    public DefaultClass changePsd(@FormParam("token") String token, @FormParam("lastpsd") String lastpsd, @FormParam("newpsd") String newpsd) {
        try {
            User user = User.getUserByToken(token);

            if (!user.getHashpsd().equals(lastpsd))
                throw new Exception("Incorrect password");
            if (lastpsd.equals(newpsd))
                throw new Exception("New password equals to last password");

            user.setHashpsd(newpsd); // смена пароля
            return new DefaultClass(true, token);

        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/chphoto/")
    @Produces(MediaType.APPLICATION_JSON) // изменить фотку
    public DefaultClass changePhoto(@FormParam("token") String token, @FormParam("photo") String photo) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            userProfile.setImage(photo);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/getubyphn/")
    @Produces(MediaType.APPLICATION_JSON) // получить пользователя по логину
    public UserProfile getUserByPhone(@FormParam("token") String token, @FormParam("phone") String phone) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            userProfile.getDefaultClass().setOperationOutput(true);
            userProfile.getDefaultClass().setToken(token);
            return userProfile;
        } catch (Exception ex) {
            UserProfile userProfile = UserProfile.getUserProfileByUser(new User());
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chname/")
    @Produces(MediaType.APPLICATION_JSON) // изменить имя
    public UserProfile changeName(@FormParam("token") String token, @FormParam("name") String name) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            userProfile.setName(name);
            userProfile.getDefaultClass().setOperationOutput(true);
            userProfile.getDefaultClass().setToken(token);
            return userProfile;
        } catch (Exception ex) {
            UserProfile userProfile = UserProfile.getUserProfileByUser(new User());
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

}
