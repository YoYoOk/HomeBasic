package com.yj.homebasic.activity;


import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.database.MyDatabaseHelper;
import com.yj.homebasic.fragment.FragmentCollect;
import com.yj.homebasic.fragment.FragmentHistory;
import com.yj.homebasic.fragment.FragmentMine;
import com.yj.homebasic.fragment.FragmentManage;
import com.yj.homebasic.view.sliding.AbBottomTabView;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * @author liaoyao
 * 主活动  容纳碎片的活动
 */
public class MainActivity extends FragmentActivity {
	private static final int REQUEST_ENABLE_BT = 1;//选择是否打开蓝牙对话框
	public static MyDatabaseHelper dbHelper;//此数据库本应用程序所有的都应该可以访问 在此设置为静态的
	private AbBottomTabView mBottomTabView;
	private List<Drawable> tabDrawables = null;
	private SharedPreferences sp;
	private int userId;
	//蓝牙相关定义
	public static BluetoothAdapter mBluetoothAdapter;
	public static BluetoothManager bluetoothManager;
	private FragmentMine fragmentMine;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.getActionBar().hide();
		if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			//查看手机是否支持4.0以上蓝牙
			Toast.makeText(this, "不支持4.0蓝牙的设备", Toast.LENGTH_SHORT).show();
			this.finish();
		}
		//初始化蓝牙适配器， 查看是否API>18 
		bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
		if(bluetoothManager.getAdapter() == null){
			Toast.makeText(this, "您的设备不支持蓝牙", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		mBluetoothAdapter = bluetoothManager.getAdapter();//蓝牙
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		userId = sp.getInt("userId", 1);
		mBottomTabView = (AbBottomTabView) findViewById(R.id.mBottomTabView);
		mBottomTabView.getViewPager().setOffscreenPageLimit(5);
		
		FragmentCollect fragmentCollect = new FragmentCollect();
		FragmentHistory fragmentHistory = new FragmentHistory();
		FragmentManage fragmentManage = new FragmentManage();
		fragmentMine = new FragmentMine();
		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(fragmentCollect);
		mFragments.add(fragmentManage);
		mFragments.add(fragmentHistory);
		mFragments.add(fragmentMine);
		
		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("新建检测");
		tabTexts.add("服药管理");
		tabTexts.add("历史记录");
		tabTexts.add("个人信息");
		
		mBottomTabView.setTabBackgroundResource(R.drawable.tab_press);
		// 注意图片的顺序
		tabDrawables = new ArrayList<Drawable>();
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.collect_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.collect_press));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.manage_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.manage_press));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.history_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.history_press));
		tabDrawables.add(this.getResources()
				.getDrawable(R.drawable.mine_normal));
		tabDrawables.add(this.getResources().getDrawable(
				R.drawable.mine_press));
		mBottomTabView.addItemViews(tabTexts, mFragments, tabDrawables);
		mBottomTabView.setTabPadding(2,2, 2, 2);
		
	}
	public int getUserId() {
		return userId;
	}
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}
	public boolean isOpenTest() {
		return fragmentMine.isOpenTest();
	}
}
