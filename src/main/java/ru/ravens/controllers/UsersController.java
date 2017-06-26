package ru.ravens.controllers;


import ru.ravens.models.DefaultClass;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.UserProfile;
import ru.ravens.service.TokenBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class UsersController {


    @GET
    @Path("/reg/{phone}/{name}/{hashpsd}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserProfile register(@PathParam("phone") String phone,
                                @PathParam("name") String name,
                                @PathParam("hashpsd") String hashpsd) {
        try {
            String token = TokenBuilder.makeToken(phone);
            UserProfile userProfile = User.registerUser(name, phone, hashpsd, token);
            userProfile.getDefaultClass().setToken(token);
            userProfile.getDefaultClass().setOperationOutput(true);
            return userProfile;

        } catch (Exception ex) {
            UserProfile userProfile = new UserProfile();
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/log/{phone}/{hashpsd}")
    @Produces(MediaType.APPLICATION_JSON) // авторизация
    public UserProfile auth(@PathParam("phone") String phone,
                            @PathParam("hashpsd") String hashpsd) {
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
            UserProfile userProfile = new UserProfile();
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chpsd/{token}/{lastpsd}/{newpsd}")
    @Produces(MediaType.APPLICATION_JSON) // изменить пароль
    public DefaultClass changePsd(@PathParam("token") String token,
                                  @PathParam("lastpsd") String lastpsd,
                                  @PathParam("newpsd") String newpsd) {
        try {
            User user = User.getUserByToken(token);

            if (!user.getHashpsd().equals(lastpsd))
                throw new Exception("Incorrect password");
            if (lastpsd.equals(newpsd))
                throw new Exception("New password equals to last password");

            User.changePsd(user.getUserID(), newpsd);
            return new DefaultClass(true, token);

        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/chphoto/{token}/{photo}")
    @Produces(MediaType.APPLICATION_JSON) // изменить фотку
    public DefaultClass changePhoto(@PathParam("token") String token,
                                    @PathParam("photo") String photo) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            User.changeImage(userProfile.getUserID(), photo);
            return new DefaultClass(true, token);
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

    @GET
    @Path("/getp/{token}/{phone}")
    @Produces(MediaType.APPLICATION_JSON) // получить пользователя по логину
    public UserProfile getUserByPhone(@PathParam("token") String token,
                                      @PathParam("phone") String phone) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            userProfile.getDefaultClass().setOperationOutput(true);
            userProfile.getDefaultClass().setToken(token);
            return userProfile;
        } catch (Exception ex) {
            UserProfile userProfile = new UserProfile();
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chname/{token}/{name}")
    @Produces(MediaType.APPLICATION_JSON) // изменить имя
    public DefaultClass changeName(@PathParam("token") String token,
                                  @PathParam("name") String name) {
        try {
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByToken(token));
            User.changeName(userProfile.getUserID(), name);
            userProfile.getDefaultClass().setOperationOutput(true);
            userProfile.getDefaultClass().setToken(token);
            return userProfile.getDefaultClass();
        } catch (Exception ex) {
            return new DefaultClass(false, ex.getMessage());
        }
    }

}
