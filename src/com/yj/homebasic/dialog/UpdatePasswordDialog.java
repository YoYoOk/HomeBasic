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

public class UpdatePasswordDialog extends Dialog{

	private Activity context;
	private View.OnClickListener mClickListener;
	public EditText et_new_password, et_new_password2;
	private Button btn_update_password;
	public UpdatePasswordDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public UpdatePasswordDialog(Activity context, int theme, View.OnClickListener mClickListener){
		super(context, theme);
		this.context = context;
		this.mClickListener = mClickListener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_update_password);
		et_new_password = (EditText)findViewById(R.id.et_new_password);
		et_new_password2 = (EditText)findViewById(R.id.et_new_password2);
		btn_update_password = (Button)findViewById(R.id.btn_update_password);
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
        btn_update_password.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
}
