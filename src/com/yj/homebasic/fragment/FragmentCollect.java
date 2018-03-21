package com.yj.homebasic.fragment;




import com.yj.homebasic.activity.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCollect extends Fragment {
	
	private ImageButton btn_connect;//蓝牙连接按钮
	private Button btn_start;//开始采集按钮
	private LinearLayout ll;
	private TextView tv_bar;
	boolean isRight = true;
	private boolean isConnected;//是否连接
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//设置菜单栏为true
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_collect, null);
		btn_connect = (ImageButton)view.findViewById(R.id.btn_connect);
		btn_start = (Button)view.findViewById(R.id.btn_start_collect);
		ll = (LinearLayout)view.findViewById(R.id.ll);
		tv_bar = (TextView)view.findViewById(R.id.tv_bar);
		btn_connect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isConnected){
					isConnected = true;
					btn_connect.setBackgroundResource(R.drawable.bluetooth_connect);
				}else{
					isConnected = false;
					btn_connect.setBackgroundResource(R.drawable.bluetooth_normal);
				}
			}
		});
		btn_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "you click", Toast.LENGTH_SHORT).show();
			}
		});
		final Handler timeHandler = new Handler();		
		Runnable runnable = new Runnable() {  
		    @Override  
		    public void run() {
		    	int off =tv_bar.getScrollX() - ll.getScrollX();//绝对位置
		    	Log.e("gege", off + " " + (ll.getMeasuredWidth() - tv_bar.getMeasuredWidth()));
		        if(isRight){
		        	off++;
		        	if(off == (ll.getMeasuredWidth() - tv_bar.getMeasuredWidth())){
		        		isRight = false;
		        	}
		        }else{
		        	off--;
		        	if(off == 0){
		        		isRight = true;
		        	}
		        }
		        tv_bar.scrollTo(off + ll.getScrollX(), 0);
		        timeHandler.postDelayed(this, 1000);  
		    }  
		};
		Thread thread = new Thread(runnable);
		thread.start();
		return view;
	}
}
