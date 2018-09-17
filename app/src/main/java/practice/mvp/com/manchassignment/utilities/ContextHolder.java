package practice.mvp.com.manchassignment.utilities;

import android.content.Context;

public class ContextHolder {
	
	private Context context;
	
	public static ContextHolder instance;
	
	private ContextHolder(){
		
	}
	
	public static ContextHolder getInstance(){
		if (instance == null){
			instance = new ContextHolder();
		}
		return instance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
