package com.imwg.imwgmotti;

import java.util.LinkedList; 
import java.util.List; 
import android.app.Activity; 
import android.app.Application; 
 
public class MyApplication extends Application {  
	  
	private static List<Activity> activityList = new LinkedList<Activity>();   
	private static MyApplication instance;  
	
	   
    private MyApplication(){  
    }
    
    //����ģʽ�л�ȡΨһ��MyApplicationʵ��   
    public static MyApplication getInstance(){  
    	if(null == instance)
    		instance = new MyApplication();
    	return instance;               
    }  
    
    //���Activity��������  
    public static void addActivity(Activity activity){  
    	activityList.add(activity);  
	}
    
     //��������Activity��finish
    public static void exit(){ 
    	for(Activity activity:activityList){  
    		activity.finish();  
    	}  
    	System.exit(0);  
	}  
}  