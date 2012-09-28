package com.jimcloudy.updateinspectionloc;

import android.app.Application;

public class InspectionApplication extends Application{
	private boolean getInspectionRunning;
	
	@Override
	public void onCreate(){
		super.onCreate();
		this.getInspectionRunning = false;
	}
	
	@Override
	public void onTerminate(){
		super.onTerminate();
	}
	
	public void setGetInspectionRunning(boolean flag){
		this.getInspectionRunning = flag;
	}
	
	public boolean isGetInspectionRunning(){
		return this.getInspectionRunning;
	}
}
