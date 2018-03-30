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
		//ָ������
		this.setContentView(R.layout.dialog_query_drug);
		tv_drug_name = (TextView)findViewById(R.id.tv_drug_name);
		tv_drug_dosage = (TextView)findViewById(R.id.tv_drug_dosage);
		tv_drug_description = (TextView)findViewById(R.id.tv_drug_description);
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
        //���ؼ����ֵ
    	tv_drug_name.append(drug.getDrugName()==null ? "" : drug.getDrugName());
    	tv_drug_dosage.append(drug.getDrugDosage()==null ? "" : drug.getDrugDosage());
        tv_drug_description.append(drug.getDrugDescription()==null ? "" : drug.getDrugDescription());
        this.setCancelable(true); 
	}
}
