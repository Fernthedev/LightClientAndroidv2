package me.fernthedev.lightclientandroidv2;

import android.support.annotation.Keep;

@Keep
public class AuthInfo {

    private String ip;
    private String password = null;
    private int port = 2000;

    public String getIp() {
        return ip;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
