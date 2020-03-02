package cn.sanenen.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;

public class Param {
    private static Setting setting;
    
    private static String ip = "ip";
    private static String port = "port";
    private static String username = "username";
    private static String password = "password";
    private static String serviceid = "serviceid";
    private static String spcode = "spcode";
    private static String maxConnect = "maxConnect";
    private static String speed = "speed";
    
    private static String content = "content";
    private static String randomMobile = "randomMobile";
    private static String mobile = "mobile";
    private static String sendCount = "sendCount";
    private static String close = "close";
    static {
        setting = new Setting("set.setting");
        setting.autoLoad(true);
    }
    
    public static String get(String key){
        String s = setting.get(key);
        if (StrUtil.isNotBlank(s)){
            return s;
        }
        return "";
    }

    public static String getIp() {
        return get(ip);
    }

    public static String getPort() {
        return get(port);
    }

    public static String getUsername() {
        return get(username);
    }

    public static String getPassword() {
        return get(password);
    }

    public static String getServiceid() {
        return get(serviceid);
    }

    public static String getSpcode() {
        return get(spcode);
    }

    public static String getMaxConnect() {
        return get(maxConnect);
    }

    public static String getSpeed() {
        return get(speed);
    }

    public static String getContent() {
        return get(content);
    }

    public static boolean getRandomMobile() {
        String s = get(randomMobile);
        if ("1".equals(s)){
            return true;
        }
        return false;
    }

    public static String getMobile() {
        return get(mobile);
    }

    public static String getSendCount() {
        return get(sendCount);
    }

    public static boolean getClose() {
        String s = get(close);
        if ("1".equals(s)){
            return true;
        }
        return false;
    }
}
