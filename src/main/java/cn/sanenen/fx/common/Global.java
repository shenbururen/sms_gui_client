package cn.sanenen.fx.common;

import cn.hutool.setting.Setting;

public class Global {
    private static Setting setting;

    static {
        setting = new Setting("global.setting");
    }

    public static void put(String key, Object val) {
        setting.put(key, val.toString());
    }

    public static String get(String key) {
        return setting.get(key);
    }

    public static void save() {
        setting.store(setting.getSettingPath());
    }
}
