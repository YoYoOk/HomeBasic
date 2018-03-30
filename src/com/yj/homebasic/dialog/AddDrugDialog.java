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
 * 弹出新增药品的对话框
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
		//指定布局  
		this.setContentView(R.layout.dialog_add_drug);
		et_drug_name = (EditText)findViewById(R.id.et_drug_name);
		et_drug_dosage = (EditText)findViewById(R.id.et_drug_dosage);
		et_drug_description = (EditText)findViewById(R.id.et_drug_description);
		btn_drug_save = (Button)findViewById(R.id.btn_drug_save);
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
        btn_drug_save.setOnClickListener(mClickListener);  
        
        this.setCancelable(true); 
	}
	
}
