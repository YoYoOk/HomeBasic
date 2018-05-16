package com.yj.homebasic.fragment;



import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.activity.MainActivity;
import com.yj.homebasic.activity.ParamsSettingActivity;
import com.yj.homebasic.activity.R;
import com.yj.homebasic.dialog.BluetoothScanDialog;
import com.yj.homebasic.dialog.UpdateInformationDialog;
import com.yj.homebasic.dialog.UpdatePasswordDialog;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentMine extends Fragment implements OnClickListener, OnItemClickListener{
	private static final int REQUEST_ENABLE_BT = 1;//选择是否打开蓝牙对话框
	private TextView tv_username, tv_password, tv_age, tv_gender, tv_height, tv_weight, tv_default_address;
	private Button btn_update_password_dialog, btn_search_dialog, btn_update_params, btn_update_information_dialog;
	private SharedPreferences sp;
	private UpdatePasswordDialog updatePasswordDialog;
	private UpdateInformationDialog informationDialog;
	private BluetoothScanDialog scanDialog;//弹出搜索蓝牙结果显示列表对话框
	private LeDeviceListAdapter mLeDeviceListAdapter;//蓝牙搜索列表适配器
	private ListView lv_bluetooth_scan_result;//蓝牙搜索显示列表
	private boolean mScanning, isOpenningBluetooth = true;//默认值是false
	private Handler mHandler;
	private static final long SCAN_PRRIOD = 10000;//10s后停止扫描
	private int userId;
	private SQLiteDatabase db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//设置菜单栏为true
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mine, null);
		sp = this.getActivity().getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		initWidget(view);
		prepareData();
		db = MainActivity.dbHelper.getWritableDatabase();
		btn_update_password_dialog.setOnClickListener(this);
		btn_search_dialog.setOnClickListener(this);
		btn_update_params.setOnClickListener(this);
		btn_update_information_dialog.setOnClickListener(this);
		return view;
	}
	
	private void initWidget(View view){
		tv_username = (TextView)view.findViewById(R.id.tv_username);
		tv_password = (TextView)view.findViewById(R.id.tv_password);
		tv_age = (TextView)view.findViewById(R.id.tv_age);
		tv_gender = (TextView)view.findViewById(R.id.tv_gender);
		tv_height = (TextView)view.findViewById(R.id.tv_height);
		tv_weight = (TextView)view.findViewById(R.id.tv_weight);
		tv_default_address = (TextView)view.findViewById(R.id.tv_default_address);
		btn_update_password_dialog = (Button)view.findViewById(R.id.btn_update_password_dialog);
		btn_search_dialog = (Button)view.findViewById(R.id.btn_search_dialog);
		btn_update_params = (Button)view.findViewById(R.id.btn_update_params);
		btn_update_information_dialog = (Button)view.findViewById(R.id.btn_update_information_dialog);
	}
	
	private void prepareData(){
		userId = sp.getInt("userId", 1);
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		Cursor cursor = db.query(true, "user", null, "id = ?", new String[]{userId + ""}, null, null, null, null, null);
		if(cursor.moveToFirst()){
			tv_username.setText(cursor.getString(cursor.getColumnIndex("username")));
			tv_password.setText(cursor.getString(cursor.getColumnIndex("password")));
			tv_gender.setText(cursor.getInt(cursor.getColumnIndex("gender")) == 1 ? "男" : "女");
			int age = cursor.getInt(cursor.getColumnIndex("age"));
			tv_age.setText(age == 0 ? "" : age + "");
			int height = cursor.getInt(cursor.getColumnIndex("height"));
			tv_height.setText(height == 0 ? "" : height + "cm");
			int weight = cursor.getInt(cursor.getColumnIndex("weight"));
			tv_weight.setText(weight == 0 ? "" : weight + "kg");
		}
		tv_default_address.setText(sp.getString("defaultAddress", ""));
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);//从主活动获取数据的方式之一
		
	}
	
	@Override
	public void onClick(View v) {
		ContentValues values = new ContentValues();
		switch (v.getId()) {
		case R.id.btn_update_information_dialog:
			informationDialog = new UpdateInformationDialog(getActivity(), R.style.loading_dialog, this);
			informationDialog.show();
			break;
		case R.id.btn_update_infor:
			String username = informationDialog.et_username.getText().toString().trim();
			String age = informationDialog.et_age.getText().toString().trim();
			String height = informationDialog.et_height.getText().toString().trim();
			String weight = informationDialog.et_weight.getText().toString().trim();
			values.clear();
			if(!TextUtils.isEmpty(username)){
				values.put("username", username);
				tv_username.setText(username);
			}
			if(!TextUtils.isEmpty(age)){
				values.put("age", age);
				tv_age.setText(age);
			}
			if(!TextUtils.isEmpty(height)){
				values.put("height", height);
				tv_height.setText(height);
			}
			if(!TextUtils.isEmpty(weight)){
				values.put("weight", weight);
				tv_weight.setText(weight);
			}
			values.put("gender", informationDialog.rbtn_male.isChecked() ? 1 : 0);
			tv_gender.setText(informationDialog.rbtn_male.isChecked() ? "男" : "女");
			int result = db.update("user", values, "id = ?", new String[]{userId + ""});
			if(result > 0){
				Toast.makeText(getActivity(), "信息修改成功！", Toast.LENGTH_SHORT).show();
				informationDialog.dismiss();
			}
			
			break;
		case R.id.btn_update_password_dialog:
			updatePasswordDialog = new UpdatePasswordDialog(getActivity(), R.style.loading_dialog, this);
			updatePasswordDialog.show();
			break;
		case R.id.btn_update_password://更新密码保存数据库
			String password1 = updatePasswordDialog.et_new_password.getText().toString().trim();
			String password2 = updatePasswordDialog.et_new_password2.getText().toString().trim();
			if(TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2)){
				return;
			}
			if(!TextUtils.equals(password1, password2)){
				Toast.makeText(getActivity(), "两次密码不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			values.clear();
			values.put("password", password1);
			int count = db.update("user", values, "id = ?", new String[]{userId + ""});
			if(count > 0){
				Toast.makeText(getActivity(), "密码修改成功！", Toast.LENGTH_SHORT).show();
				Editor editor = sp.edit();
				editor.putString("password", password1);
				editor.commit();
				tv_password.setText(password1);
				updatePasswordDialog.dismiss();
			}
			break;
		case R.id.btn_search_dialog:
			if(!MainActivity.mBluetoothAdapter.isEnabled()){
				if(!MainActivity.mBluetoothAdapter.isEnabled()){
					isOpenningBluetooth = false;
					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				}
			}
			if(isOpenningBluetooth){
				scanDialog = new BluetoothScanDialog(getActivity(), R.style.loading_dialog, this);
				scanDialog.show();
				lv_bluetooth_scan_result = scanDialog.getLv_bluetooth_scan_result();
				mHandler = new Handler();//开始扫描
				mLeDeviceListAdapter = new LeDeviceListAdapter();//初始化listviewAdapter
				lv_bluetooth_scan_result.setAdapter(mLeDeviceListAdapter);
				//已开启活动时候就开始扫描
				scanLeDevice(true);
			}
			break;
		case R.id.btn_update_params:
			Intent intent = new Intent(getActivity(), ParamsSettingActivity.class);
			startActivity(intent);
			break;
		
		default:
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:		
			if(resultCode == getActivity().RESULT_OK){
				//若点击了确定  蓝牙断开   
				isOpenningBluetooth = true;
				return;
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, intent);
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//bluetoothScanAdapter.notifyDataSetInvalidated();//刷新列表
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if(device == null){
			return;
		}
		if(mScanning){
			MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
		Editor editor = sp.edit();
		editor.putString("defaultAddress", device.getAddress());//选中就保存为默认
		editor.commit();
		//发送广播  让采集处重新去读取默认的地址
		Intent intent = new Intent("com.yj.broadcast.UPDATEADDRESS");
		getActivity().sendBroadcast(intent);
		tv_default_address.setText(device.getAddress());
		scanDialog.dismiss();
	}
	/*蓝牙搜索相关*/	//扫描蓝牙设备
	private void scanLeDevice(final boolean enable){
		if(enable){
			//在一个扫描周期（10s）之后停止扫描
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);
					scanDialog.getPb_scan().setVisibility(View.INVISIBLE);
				}
			}, SCAN_PRRIOD);//10s后执行run里面的 即 相当于停止扫描
			mScanning = true;
			MainActivity.mBluetoothAdapter.startLeScan(mLeScanCallback);
		}else{
			mScanning = false;
			MainActivity.mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}
	
	//扫描蓝牙设备回调 即扫描之后  回调接口被用于传输LE扫描后的结果
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();//实现动态刷新列表
				}
			});
		}
	};
	//查找设备的适配器
	private class LeDeviceListAdapter extends BaseAdapter{
		private List<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflater;
		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflater = getActivity().getLayoutInflater();
		}
		
		public void addDevice(BluetoothDevice device){
			if(!mLeDevices.contains(device)){
				mLeDevices.add(device);
			}
		}
		
		public BluetoothDevice getDevice(int position){
			return mLeDevices.get(position);
		}
		
		public void clear(){
			mLeDevices.clear();
		}
		
		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int position) {
			return mLeDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			ViewHolder viewHolder;
			if(view == null){
				view = mInflater.inflate(R.layout.item_bluetooth_list, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView)view.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView)view.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder)view.getTag();
			}
			BluetoothDevice device = mLeDevices.get(position);
			final String deviceName = device.getName();
			if(deviceName != null && deviceName.length() > 0){
				viewHolder.deviceName.setText(deviceName);
			}else{
				viewHolder.deviceName.setText(R.string.unknown_device);
			}
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
		
	}
	static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
