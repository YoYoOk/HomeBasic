package com.yj.homebasic.dialog;

import com.yj.homebasic.activity.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class UpdateInformationDialog extends Dialog{
	
	public EditText et_username, et_age, et_height, et_weight;
	public RadioButton rbtn_male;
	private Activity context;
	private View.OnClickListener mClickListener;
	private Button btn_update_infor;
	public UpdateInformationDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public UpdateInformationDialog(Activity context, int theme, View.OnClickListener mClickListener){
		super(context, theme);
		this.context = context;
		this.mClickListener = mClickListener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_update_information);
		btn_update_infor = (Button)findViewById(R.id.btn_update_infor);
		et_username = (EditText)findViewById(R.id.et_update_username);
		et_age = (EditText)findViewById(R.id.et_update_age);
		et_height = (EditText)findViewById(R.id.et_update_height);
		et_weight = (EditText)findViewById(R.id.et_update_weight);
		rbtn_male = (RadioButton)findViewById(R.id.rbtn_update_male);
		/* 
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window 
         * 对象,这样这可以以同样的方式改变这个Activity的属性. 
         */  
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值  
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6  
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.8  
        dialogWindow.setAttributes(p);  
        // 为按钮绑定点击事件监听器  
        btn_update_infor.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
}
