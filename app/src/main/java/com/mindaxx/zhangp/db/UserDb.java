package com.mindaxx.zhangp.db;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2019/10/31.
 */

public class UserDb extends LitePalSupport {

    private String userName;
    private String userId;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public static void saveAll(UserDb bean) {
        try {
            UserDb db = new UserDb();
            db.userName = bean.getUserName();
            db.userId = bean.getUserId();
            db.passWord = bean.getPassWord();
            db.save();
        } catch (Exception e) {
        }
    }

    public static UserDb select() {
        UserDb db = null;
        try {
            db = LitePal.findFirst(UserDb.class);
        } catch (Exception e) {
        } finally {
            if (db == null) {
                db = new UserDb();
            }
            return db;
        }
    }

}
