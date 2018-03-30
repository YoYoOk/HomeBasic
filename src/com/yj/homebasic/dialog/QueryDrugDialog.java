package com.yj.homebasic.dialog;

import com.yj.homebasic.activity.R;
import com.yj.homebasic.domain.Drug;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class QueryDrugDialog extends Dialog {
	
	private Activity context;
	private Drug drug;
	private TextView tv_drug_name, tv_drug_dosage, tv_drug_description;
	public QueryDrugDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public QueryDrugDialog(Activity context, int theme, Drug drug) {
		super(context, theme);
		this.context = context;
		this.drug = drug;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//指定布局
		this.setContentView(R.layout.dialog_query_drug);
		tv_drug_name = (TextView)findViewById(R.id.tv_drug_name);
		tv_drug_dosage = (TextView)findViewById(R.id.tv_drug_dosage);
		tv_drug_description = (TextView)findViewById(R.id.tv_drug_description);
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
        //给控件添加值
    	tv_drug_name.append(drug.getDrugName()==null ? "" : drug.getDrugName());
    	tv_drug_dosage.append(drug.getDrugDosage()==null ? "" : drug.getDrugDosage());
        tv_drug_description.append(drug.getDrugDescription()==null ? "" : drug.getDrugDescription());
        this.setCancelable(true); 
	}
}
