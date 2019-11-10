package com.example.chatapp.setting;

public class SettingOb {
    private int id;
    private int icon;
    private String nameSetting;

    public SettingOb(int id, int icon, String nameSetting) {
        this.id = id;
        this.icon = icon;
        this.nameSetting = nameSetting;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getNameSetting() {
        return nameSetting;
    }

    public void setNameSetting(String nameSetting) {
        this.nameSetting = nameSetting;
    }
}
