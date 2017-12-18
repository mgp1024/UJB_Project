package com.example.lxt.ujb_project.tool;

public class ClickDouble {
    private static long lastClickTime;
    
    
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 400) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }
}
