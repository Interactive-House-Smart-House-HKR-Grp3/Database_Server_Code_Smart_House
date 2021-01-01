package se.hkr.mqtt;

public class User {
    String userName = "JohnDoe";
    String passWord = "12345";
    String email = "randomemail@randomprovider.com";
    String name = "John Doe";


    public User(String userName, String passWord, String email, String name) {
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.name = name;
    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
