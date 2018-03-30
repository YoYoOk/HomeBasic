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

/**
 * @author liaoyao
 * ��������ҩƷ�ĶԻ���
 */
public class AddDrugDialog extends Dialog {
	
	private Activity context;
	private Button btn_drug_save; 
	public EditText et_drug_name, et_drug_dosage, et_drug_description;
	private View.OnClickListener mClickListener;
	
	public AddDrugDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public AddDrugDialog(Activity context, int theme, View.OnClickListener mClickListener) {
		super(context, theme);
		this.context = context;
		this.mClickListener = mClickListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//ָ������  
		this.setContentView(R.layout.dialog_add_drug);
		et_drug_name = (EditText)findViewById(R.id.et_drug_name);
		et_drug_dosage = (EditText)findViewById(R.id.et_drug_dosage);
		et_drug_description = (EditText)findViewById(R.id.et_drug_description);
		btn_drug_save = (Button)findViewById(R.id.btn_drug_save);
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
        btn_drug_save.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
	
}
