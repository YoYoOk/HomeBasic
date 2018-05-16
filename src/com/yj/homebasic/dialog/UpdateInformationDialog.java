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
        btn_update_infor.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
}
