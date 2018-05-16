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
         * ��ȡʥ����Ĵ��ڶ��󼰲����������޸ĶԻ���Ĳ�������, ����ֱ�ӵ���getWindow(),��ʾ������Activity��Window 
         * ����,�����������ͬ���ķ�ʽ�ı����Activity������. 
         */  
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // ��ȡ��Ļ������  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // ��ȡ�Ի���ǰ�Ĳ���ֵ  
        // p.height = (int) (d.getHeight() * 0.6); // �߶�����Ϊ��Ļ��0.6  
        p.width = (int) (d.getWidth() * 0.8); // �������Ϊ��Ļ��0.8  
        dialogWindow.setAttributes(p);  
        // Ϊ��ť�󶨵���¼�������  
        btn_update_password.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
}
