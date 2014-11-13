package com.keyue.qlm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyue.qlm.R;




import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class popMenu {
	public PopupWindow window ;
	private Context context;
	public ListView contextlist;
	private Object[] objects;
	private LayoutInflater inflater;
	private View layout;
	public List<Map<String, Object>> data;
	
	public popMenu(Context context,Object[] objects){
		this.context=context;
		this.objects=objects;
	}
	
	public void init(){
		inflater = LayoutInflater.from(context);
		layout = inflater.inflate(R.layout.popmenu, null);
		contextlist = (ListView) layout.findViewById(R.id.poplist);
		TextView nodata=(TextView) layout.findViewById(R.id.nodata);
		if(objects.length<=0){
			nodata.setVisibility(View.VISIBLE);
			contextlist.setVisibility(View.GONE);
		}else{
			data= getPopMenuList(objects);
			SimpleAdapter sAdapter = new SimpleAdapter(context,data,R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
			contextlist.setAdapter(sAdapter);
		}
		
		window = new PopupWindow(layout,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		//window.setAnimationStyle(R.style.popstyle);
		window.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.white));
		window.setOutsideTouchable(true);
		window.setFocusable(true);
		window.update();
	}
	
	
	public void  dismiss(){
		window.dismiss();
	}
	
	public void show(View view){
		window.showAsDropDown(view);
	}
	
	

	
	
	
	
	
	
	public List<Map<String, Object>> getPopMenuList(Object[] objects) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		for (int i = 0; i < objects.length; i++) {
			map = new HashMap<String, Object>();
			map.put("contentname", objects[i]);
			data.add(map);
		}
		return data;
	}

}
