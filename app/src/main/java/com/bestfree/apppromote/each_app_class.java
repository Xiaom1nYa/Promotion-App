package com.bestfree.apppromote;


public class each_app_class {
    private String package_string;
    private String app_name;
    public each_app_class(String package_string , String app_name){
        this.package_string = package_string;
        this.app_name = app_name;
    }
    public String getPackage_string(){
        return package_string;
    }
    public String getApp_name(){
        return app_name;
    }
}
