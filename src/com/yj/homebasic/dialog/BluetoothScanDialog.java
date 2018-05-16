package com.yj.homebasic.dialog;


import com.yj.homebasic.activity.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class BluetoothScanDialog extends Dialog {
	
	private Activity context;
	private ListView lv_bluetooth_scan_result;
	private ProgressBar pb_scan;
	private OnItemClickListener mItemClickListener;
	public BluetoothScanDialog(Activity context) {
		super(context);
		this.context = context;
	}
	
	public BluetoothScanDialog(Activity context, int theme, OnItemClickListener mItemClickListener) {
		super(context, theme);
		this.context = context;
		this.mItemClickListener = mItemClickListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_bluetooth_scan);
		lv_bluetooth_scan_result = (ListView)findViewById(R.id.lv_bluetooth_scan_result);
		pb_scan = (ProgressBar)findViewById(R.id.pb_scan);
		/* 
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window 
         * 对象,这样这可以以同样的方式改变这个Activity的属性. 
         */  
        Window dialogWindow = this.getWindow();  
  
        WindowManager m = context.getWindowManager();  
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用  
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值 
		DisplayMetrics outMetrics = new DisplayMetrics();
		m.getDefaultDisplay().getMetrics(outMetrics);
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6  
        p.width = (int) (outMetrics.widthPixels * 0.8); // 宽度设置为屏幕的0.8 
        dialogWindow.setAttributes(p); 
        //按钮添加事件
        lv_bluetooth_scan_result.setOnItemClickListener(mItemClickListener);
        this.setCancelable(true); 
	}

	public ListView getLv_bluetooth_scan_result() {
		return lv_bluetooth_scan_result;
	}

	public ProgressBar getPb_scan() {
		return pb_scan;
	}
	
	
}
