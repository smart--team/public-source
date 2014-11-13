package com.keyue.qlm.util;

import com.keyue.qlm.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyDialog extends Dialog {
	private Context context;
	private View resource;
	private String title;
	

	public MyDialog(Context context,View view,int theme,String title) {
		super(context,theme);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.resource=view;
		this.title=title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(resource);
		TextView title= ((TextView)resource.findViewById(R.id.title));
		if(null!=title){
			title.setText(this.title);
		}
	}
	
	
}
