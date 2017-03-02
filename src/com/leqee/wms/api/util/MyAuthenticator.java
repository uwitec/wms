package com.leqee.wms.api.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuthenticator extends Authenticator {
	private String username;  
    private String password;  
  
    /** 
     *  
     * @author geloin 
     * @date 2012-5-8 ����2:48:53 
     * @param username 
     * @param password 
     */  
    public MyAuthenticator(String username, String password) {  
        super();  
        this.username = username;  
        this.password = password;  
    }  
  
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication(username, password);  
    }  
}
