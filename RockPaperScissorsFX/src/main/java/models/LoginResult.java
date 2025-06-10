package models;

public class LoginResult {
    public final String username;
    public final String password;
    public final boolean isLogin;

    public LoginResult(String username, String password, boolean isLogin) {
        this.username = username;
        this.password = password;
        this.isLogin = isLogin;
    }
}
