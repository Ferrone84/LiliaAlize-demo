package com.example.duret.lilia_alize_demo;

public class Globals {
    private static Globals instance;

    // Global variable
    private String ip, port;
    private boolean humour;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public void setIP(String ip){
        this.ip = ip;
    }
    public String getIP(){
        return this.ip;
    }

    public void setPORT(String port){
        this.port = port;
    }
    public String getPORT(){
        return this.port;
    }

    public void setHUMOUR(boolean humour){
        this.humour = humour;
    }
    public boolean getHUMOUR(){
        return this.humour;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}