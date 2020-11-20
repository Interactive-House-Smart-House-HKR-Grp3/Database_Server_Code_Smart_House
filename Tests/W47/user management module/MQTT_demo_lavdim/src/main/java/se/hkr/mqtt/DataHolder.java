package se.hkr.mqtt;

import java.util.ArrayList;

public class DataHolder {

    static ArrayList<User> users = new ArrayList<User>();


    static boolean userExists(String username){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                return true;
            }
        }
        return false;
    }


    static boolean passwordCorrect(String username, String password){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                if(user.passWord.equalsIgnoreCase(password)){
                    return true;
                }
            }
        }
        return false;
    }


    static boolean login(String username, String password){
        if(userExists(username)){
            if(passwordCorrect(username,password)){

                return true;
            }
        }
        return false;
    }



    static boolean register(String userName, String passWord, String email, String name){
        if(!userExists(userName)){
            User user = new User(userName, passWord, email, name);
            DataHolder.users.add(user);
            return true;
        }

        return false;
    }

    static boolean editUser(String username,String password,String email,String name){
             User user =findUserByUsernam(username);
             if (user.userName.equalsIgnoreCase(username)){
               user.setName(name);
               user.setPassWord(password);
               user.setEmail(email);
               return true;
           }else
               return false;

    }



    static User findUserByUsernam(String username){
        for(User user : users){
            if(user.userName.equalsIgnoreCase(username)){
                return user;
            }
        }
        return null;
    }



}
