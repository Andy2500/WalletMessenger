package ru.ravens.controllers;


import ru.ravens.models.DefaultClass;
import ru.ravens.models.DefaultClassWrapper;
import ru.ravens.models.InnerModel.User;
import ru.ravens.models.UserProfile;
import ru.ravens.service.TokenBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/user")
public class UsersController {

    
    @GET
    @Path("/reg/{phone}/{name}/{hashpsd}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public UserProfile register(@PathParam("phone") String phone,
                                @PathParam("name") String name,
                                @PathParam("hashpsd") String hashpsd) {
        try {
            String token = TokenBuilder.makeToken(phone);
            //Нужна проверка на отсутствие пользователя с таким номером телефона в базе
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public UserProfile auth(@PathParam("phone") String phone,
                            @PathParam("hashpsd") String hashpsd) {
        try {
            User user = User.getUserByPhone(phone);
            if (!user.getHashpsd().equals(hashpsd)) // если пароли не совпадают
            {
                throw new Exception("Incorrect password");
            }
            UserProfile userProfile = UserProfile.getUserProfileByUser(user);
            userProfile.setDefaultClass(new DefaultClass(true,user.getToken()));
            return userProfile;
        } catch (Exception ex) {
            UserProfile userProfile = new UserProfile();
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chpsd/{token}/{lastpsd}/{newpsd}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public DefaultClassWrapper changePsd(@PathParam("token") String token,
                                         @PathParam("lastpsd") String lastpsd,
                                         @PathParam("newpsd") String newpsd) {
        try {
            User user = User.getUserByToken(token);

            if (!user.getHashpsd().equals(lastpsd))
                throw new Exception("Incorrect password");
            if (lastpsd.equals(newpsd))
                throw new Exception("New password equals to last password");

            User.changePsd(user.getUserID(), newpsd);
            return new DefaultClassWrapper(new DefaultClass(true,  user.getToken()));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    @GET
    @Path("/chphoto/{token}/{photo}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public DefaultClassWrapper changePhoto(@PathParam("token") String token,
                                    @PathParam("photo") String photo) {
        try {
            User user = User.getUserByToken(token);
            User.changeImage(user.getUserID(), photo);
            return new DefaultClassWrapper(new DefaultClass(true,  token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

    //Исправлено. Перепроверить и доисправить так как привычнее без потери функционала.
    @GET
    @Path("/getubyphn/{token}/{phone}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public UserProfile getUserByPhone(@PathParam("token") String token,
                                      @PathParam("phone") String phone) {
        try {
            //Это тот кто подал запрос (его надо получить для того, чтобы удостовериться, что он существует и токен подан верный)
            User user = User.getUserByToken(token);
            //А этого вернем, так как его просили вернуть по номеру телефона
            UserProfile userProfile = UserProfile.getUserProfileByUser(User.getUserByPhone(phone));
            userProfile.setDefaultClass(new DefaultClass(true, token));
            return userProfile;
        }
        catch (Exception ex) {
            UserProfile userProfile = new UserProfile();
            userProfile.setDefaultClass(new DefaultClass(false, ex.getMessage()));
            return userProfile;
        }
    }

    @GET
    @Path("/chname/{token}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public DefaultClassWrapper changeName(@PathParam("token") String token,
                                  @PathParam("name") String name) {
        try {
            User user =User.getUserByToken(token);
            User.changeName(user.getUserID(),name);
            return new DefaultClassWrapper(new DefaultClass(true,  token));
        } catch (Exception ex) {
            return new DefaultClassWrapper(new DefaultClass(false, ex.getMessage()));
        }
    }

}
