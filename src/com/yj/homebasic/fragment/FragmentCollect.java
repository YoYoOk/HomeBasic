package com.yj.homebasic.fragment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import com.yj.homebasic.activity.MainActivity;
import com.yj.homebasic.activity.R;
import com.yj.homebasic.bluetooth.BluetoothLeService;
import com.yj.homebasic.domain.MyPointF;
import com.yj.homebasic.utils.ConvertUtils;
import com.yj.homebasic.utils.SaveActionUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentCollect extends Fragment implements OnClickListener{
	private static final long CONNECT_PRRIOD = 8000;//10s后停止如果还没有连接就停止连接
//	private isHasTemp = true;
	private ImageButton btn_connect;//蓝牙连接按钮
	private Button btn_start;//开始采集按钮
	private TextView tv_bar;//条形棒
	private TranslateAnimation animation;
	private UpdateBroadcastReceiver receiver;//动态注册广播
	//蓝牙模块
	private BluetoothLeService mBluetoothLeService;//蓝牙服务
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic characteristic;	//写数据
	private BluetoothGattService mnotyGattService;
	private BluetoothGattCharacteristic readCharacteristic;	//读数据
	private BluetoothGattService readMnotyGattService;
	private static String mDeviceAddress = "00:15:83:00:80:FB";// 要连接的目标蓝牙设备的地址 默认的
	private boolean mConnectedService = false;//是否找到服务  只有这个为true才算是真正连接上可以发送接收数据了
	private Handler mHandler;//连接蓝牙的时候  如果8秒钟没有连接成功 先断开  因为蓝牙有时候搜索不到服务
	/* 发送数据相关 */
	private String sendString = "861101011027e02e3202800200020000050068";
	private byte[] sendData = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020200050068");// 要发送的数据 字节数据 故此先将16进制字符串转换成字节发送
	private byte[] sendData_real = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020000050068");
	private byte[] sendData_stop = ConvertUtils.hexStringToBytes("8603030168");// 要发送停止扫描的数据 表示用来
	private boolean isFirstSend;//是否是第一次发送
	private boolean isHasData;///判断是否有数据来  如果没有数据来 就重新发送一次
	private boolean isRunning = false;//判断是否在运行，如果运行，则再次点击则是点击暂停
	private MyHandler handler;// 定义消息队列处理  主要是绘图+保存操作
	private int[] frequency_value = { 100, 120, 50 };// 从参数配置传上来的起止频率 步进频率 默认为100,120,50
	private float interval_time = 0;// s 秒级
	// 画曲线用得着
	private List<Double> xList;
	private List<Double> yList;// 一条一条曲线保存
	private List<Double> yListTemp;// 一条一条曲线保存
	private double[] source;
	private List<Byte> listByte;
	private int iCount = 0;// 判断当前扫描第几次的数据到来
	private int times_point = 10;//系统屏幕大概亮多少次的时候暗屏默认是3次的时候暗屏
	private String title_result = "凝血曲线";
	// 画最终结果的一些定义
	private float maxValue;//最大值
	private Date currentDate, beforeDate;
	String tempStr = "";// 测试用
	private int detectId;//点击开始检测之时便存储入库  返回ID  等到检测完毕 更新当前检测的结果
	private int pointCount;//一次多少个点
	private int times = 0;//默认是10次  
	private int test_times = 2;//测试的次数 默认是2次
	// 保存excel使用的变量
//		private String excelPath;// 保存到sd卡路径
	private String detectionDateStr;//保存的时候的当前时间字符串
	private String fileName;//文件名
	private File excel_Source_File, excel_Result_File;// excel文件保存原始数据, 保存结果数据, 保存原始数据滤波之后的数据
	private MyPointF pointf;//时间---值 
	private SharedPreferences sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//设置菜单栏为true
	}
	
	@Override
	public void onStart() {
		super.onStart();
		receiver = new UpdateBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.yj.broadcast.UPDATEADDRESS");
		filter.addAction("com.yj.broadcast.UPDATECONFIG");
		getActivity().registerReceiver(receiver, filter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_collect, null);
		initWidget(view);
		sp = getActivity().getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);////查询是否有默认的蓝牙  去SharedPreference
		mDeviceAddress = sp.getString("defaultAddress", "00:15:83:00:80:FB");
		sendString = sp.getString("defaultParams", "861101011027e02e3202800200020000050068");
		sendData_real = ConvertUtils.hexStringToBytes(sendString);//0次
		times = (sendData_real[15] & 0xff) * 256 + (sendData_real[14] & 0xff);
		Toast.makeText(getActivity(),times + "", Toast.LENGTH_SHORT).show();
		Log.e("#####", ConvertUtils.bytesToHexString(sendData_real));
		handler = new MyHandler();//自定义的消息队列  为了画图
		drawlineInit();
		getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//注册监听蓝牙一些服务的广播
		//注册服务 启动服务 ---蓝牙的服务
		Intent gattServiceIntent = new Intent(getActivity(),BluetoothLeService.class);
		getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);//表明活动和服务进行绑定后自动创建服务
		btn_connect.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		return view;
	}
	private void initWidget(View view){
		animation = (TranslateAnimation)AnimationUtils.loadAnimation(getActivity(), R.anim.bar_translate);
		btn_connect = (ImageButton)view.findViewById(R.id.btn_connect);
		btn_start = (Button)view.findViewById(R.id.btn_start_collect);
		tv_bar = (TextView)view.findViewById(R.id.tv_bar);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start_collect:
			if(!mConnectedService){
				Toast.makeText(getActivity(), "没有连接", Toast.LENGTH_SHORT).show();
				return;
			}//必须连接之后才能有后面的保存数据，发送采集
			
			if(!isRunning){
				frequency_value[0] = 100;
				frequency_value[1] = 120;//重新设置默认的  100 120  因为在第二次发送的时候起始频率已经改变了
				/*第一件事得到本次检测时间，患者、样本类型 保存数据库*/
				saveRecord();
				/* 采集相关 */
				isFirstSend = true;
				isHasData = true;
				sendData(true,false);//发送数据
			}else{
				AlertDialog.Builder builder = new Builder(getActivity());
				AlertDialog dialog = null;
				builder.setTitle("暂停吗？");
				builder.setPositiveButton("暂停", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendData(false, false);
						Toast.makeText(getActivity(), "暂停", Toast.LENGTH_SHORT).show();
					}
				});
            	dialog = builder.show();
			}
			break;
		case R.id.btn_connect:
			if(!mConnectedService){
				mBluetoothLeService.connect(mDeviceAddress);
				btn_connect.setBackgroundResource(R.drawable.bluetooth_connecting);
				mHandler = new Handler();
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(!mConnectedService){
							mBluetoothLeService.disconnect();
						}
					}
				}, CONNECT_PRRIOD);//如果等待5s之后还是没有连接 则先断开
			}else{
				mBluetoothLeService.disconnect();
			}
			break;
		default:
			break;
		}
	}
	private void saveRecord(){//保存采集记录
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", sp.getString("phone", "15922747849"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		detectionDateStr = format.format(new Date());
		values.put("createTime", detectionDateStr);
		db.insert("record", null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid() from record", null);
		if(cursor.moveToFirst()){
			detectId = cursor.getInt(0);//是为了最后结果求出来之后 修改结果 现在的结果是为空//获取新插入数据的id  
		}
	}
	
	//发送消息控制 抽取代码  第一个参数发送是否是开始采集命令，，，第二个参数是否是再次发送采集数据
	public void sendData(boolean isStart,boolean isRepeatStart){
		if(isStart){
			iCount = 0;// 次数要清空
			xList.removeAll(xList);
			yList.removeAll(yList);
			listByte.removeAll(listByte);// 先清掉数据
			// 设置x轴的值// 每次开始扫描都必须要重新设置x轴的值，因为在配置中是要发生变化的
			double tempVar = frequency_value[0] * 1000;
			pointCount = (frequency_value[1] - frequency_value[0]) * 1000 / frequency_value[2] + 1;
			for (int i = 0; i < pointCount; i++) {
				xList.add(tempVar / 1000);
				tempVar = tempVar + frequency_value[2];
			}
			source = new double[pointCount];//每次都创建太消耗内存了   解决方案在发送的时候根据x轴的数据创建数组
			if(!isRepeatStart){
				beforeDate = new Date();
			}
		}
		read();//读取数据
		final int charaProp = characteristic.getProperties();
		//如果该char可写
		if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //读取数据 在回调函数中
            if(isStart){
            	if(!isRepeatStart){
            		characteristic.setValue(sendData);
            		Log.e("#######", ConvertUtils.bytesToHexString(sendData));
	            }else{
	            	characteristic.setValue(sendData_real);//如果是第二次发送  则发送的是新的数据
	            	Log.e("#######", ConvertUtils.bytesToHexString(sendData_real));
	            }
	            mBluetoothLeService.writeCharacteristic(characteristic);
            }else{//发送停止信号
            	characteristic.setValue(sendData_stop);
	            mBluetoothLeService.writeCharacteristic(characteristic);
	            animation.cancel();
//		            setScreenBrightness(getSystemScreenBrightness());//发送停止也让其亮屏
            }
//		                Toast.makeText(getApplicationContext(), "写入成功！", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT).show();
			if(isStart){
				//发送成功才保存  不然一直创建文件
				if(!isRepeatStart){//因为只有第一次发送的时候 才新建文件
					saveInit();//执行保存数据操作的一些变量初始化  每次点击都创建新的csv文件
				}
				// 此处与保存数据有关 每次开始扫描的时候就在当前项目下的表中创建一个Sheet表
				SaveActionUtils.exportCSV(excel_Source_File, xList);
//						SaveActionUtils.exportCSV(excel_Result_File, new double[]{0d,0d});
				//不然画图部分看着是一条直线
			}
        }else{
        	Toast.makeText(getActivity(), "发送失败，请重新发送", Toast.LENGTH_SHORT).show();
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
	}
	public void saveInit(){
		// 每次打开软件的时候即默认新建一个excel表格 表名是当前时间
		fileName = detectionDateStr + "_Source" + ".csv";
//			excelPath = SaveActionUtils.getExcelDir() + File.separator + filename;
		// 当前路径/mnt/sdcart/Excel/Data/当前时间.xls
		excel_Source_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);// 得到当前这个文件=
		fileName = detectionDateStr + "_Result" + ".csv";
		excel_Result_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);
	}
	public void drawlineInit(){
		pointf = new MyPointF();
		//ll_result_curve_display//画最终的凝血曲线结果的LL控件
		xList = new ArrayList<Double>();
		yList = new ArrayList<Double>();
		yListTemp = new ArrayList<Double>();
		listByte = new ArrayList<Byte>();
	}
	//管理服务的生命周期
	private final ServiceConnection mServiceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
			//绑定服务的时候调用此方法  bindService调用的时候
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()){
				//初始化蓝牙适配器， 查看是否API>18 
				Toast.makeText(getActivity(), "您的设备不支持BLE", Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}//注：其实这一段有点多余，因为在MainActivity已经判断设备是否支持BLE
			//初始化成功之后自动连接设备
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetoothLeService = null;//unBindService解绑服务的时候调用此方法
		};
	};
	
	/**	 * 读函数 	 */
	private void read(){
		mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
	}
	
	//通过服务控制不同的事件
	//使用匿名类 使用广播监听  蓝牙连接、可读写的状态
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        	getActivity().invalidateOptionsMenu();//刷新当前的菜单//连接了  但是没有搜索到服务
        	btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_connecting));
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectedService = false;
            getActivity().invalidateOptionsMenu();//刷新当前的菜单
			//连接断开的同时 关闭保持屏幕常亮
            btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_normal));
        } 
        //发现有可支持的服务
        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        	//写数据的服务和characteristic
        	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //读数据的服务和characteristic
            readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //只有发现服务了 ，，我才当你是完全连接成功了
            mConnectedService = true;
            btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_connect));
        } 
        //显示数据
        else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
        		isRunning = true;
        		if(isHasData){
        			isHasData = false;//说明是真正发送且来数据了 为的是防止已经发送了 但是没有数据来的情况
        			tv_bar.startAnimation(animation);
        		}
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//有数据来了  短路逻辑  若包含就去判断包含的这个是不是最后的
            	tempStr = ConvertUtils.bytesToHexString(data);
            	if(!tempStr.contains("FF01")){//不管是2个字节还是4个字节  应该说是不管是多少字节的数据
            		for(int i = 0; i < data.length; i++){
            			listByte.add(data[i]);
            		}
            	}else{//包含 ff01--还得判断此ff01是不是结尾的那个ff01//判断此ff01是不是真的结尾的那个ff01 
            		Log.e("length", tempStr + "--" + data.length);
            		if(tempStr.indexOf("FF01") % 2 != 0){
            			Log.d("######", "#######" + tempStr + "#######"+listByte.size());
            			for(int i = 0; i < data.length; i++){
                			listByte.add(data[i]);
                		}
            		}else{
	            		byte[] tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(0, tempStr.indexOf("FF01")));
	            		//极有可能 tempByte为空  因为是以ff01起头的  ---20171121 报错了很多次 脑子呆笨没有发现
	            		if(tempByte != null){
		            		for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
	            		}
	//            		//？？？如何判断到底ff01是最后还是不是最后
	            		if(listByte.size() < (pointCount*2)){//说明数据中包含了 ff01
	            			tempByte = ConvertUtils.hexStringToBytes("FF01");
	            			Log.d("######", "#######"+listByte.size());
	            			for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
	            		}else{
	            			processBuffer();
	            		}
	            		//判断是不是以ff01结尾的 若是的话 说明后面没有数据了 再添加了
	            		if(!tempStr.endsWith("FF01")){
	            			tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(tempStr.indexOf("FF01") + 4));
	            			for(int i = 0; i < tempByte.length; i++){
	                			listByte.add(tempByte[i]);
	                		}
	            		}//此处仍然是有漏洞的，，，因为有可能接收到的数据并不是偶数  怎么破？----于20171121
            		}
            	}
            }
        }
    };  
    
    public void processBuffer() {
		// 已经来了一次扫描的数据了// 说明一次扫描正常结束 现在只考虑正常的情况下	//每次给yList添加数据之前都清空一次
		yList.removeAll(yList);
		yListTemp.removeAll(yListTemp);
//			source = new double[listByte.size()/2];//每次都创建太消耗内存了   解决方案在发送的时候根据x轴的数据长度创建数组
		for (int i = 0, j = 0; i < listByte.size() - 1; i = i + 2, j++) {
//				yList.add((listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff));//此处在添加滤波之后修改
			//因为最终画的曲线应该是滤波之后的曲线
			double temp = (listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff);
			if(j < source.length){
				source[j] = temp;
			}//此处添加是为了防止数据出错 没有判断到0xff01结果接收到的是两次的数据
			yListTemp.add(temp);
		}
		// 将数据保存了之后listByte得清空
		listByte.removeAll(listByte);
		handler.sendMessage(handler.obtainMessage(1));
	}
	// 更新界面的Hanlder 类
 	class MyHandler extends Handler {
 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 			case 1:
 				// 每次来数据时候都获取两次间隔时间
 				currentDate = new Date();
 				interval_time = (currentDate.getTime() - beforeDate.getTime());
 				interval_time = interval_time / 1000;
 				pointf.x = interval_time;
 				// 一定要在求两次时间之后，不然会影响凝血曲线时间的判断//画图之前计算最值之前先进行滤波处理//输入必须是double类型？
 				source = process_Data(source);//去对数据简单滤波
 				for(int i = 0; i < source.length; i++){
 					yList.add(source[i]);
 				}//yList和yListFilter中存储的都是滤波之后的数据，，yList是去画曲线，yListFilter是去保存
 				double tempData = Collections.max(yList);//此处求最大值是不合理的   因为原始数据需要做处理
 				maxValue = (float) tempData * 2 / 65536;
 				maxValue = (float)(Math.round(maxValue* 10000))/10000;//保留小数点后四位
 				pointf.y = maxValue;
 				//又来了一次的数据 先清空之前的数据
 				iCount++;
 				handler.sendMessage(handler.obtainMessage(3));
 				// 判断接收到的数据有没有扫描一次的数据以上 则开始画图// 将yList保存到excel中//TODO 下次测试一下导入数据的时间
 				SaveActionUtils.exportCSV(excel_Source_File,yListTemp);//保存原始数据
 				SaveActionUtils.exportCSV(excel_Result_File, pointf);//保存结果数据
 				break;
 			case 3:
 				//每次扫描到3次结束的时候暗屏  
 				if(iCount == times_point){
//	 					setScreenBrightness(20);
 				}
 				if(iCount == test_times && isFirstSend && times == 0){//说明测试4次已经完毕了
 					isRunning = false;
 					animation.cancel();
 				}
 				if(iCount == times && !isFirstSend){
 					isRunning = true;
 					animation.cancel();
 					//扫描结束恢复到系统自动的亮度
//	 					setScreenBrightness(getSystemScreenBrightness());
//	 					btn_Start_Scan.setKeepScreenOn(false);//扫描结束不需要再保持屏幕常亮
 				}
//	 				tv_times.setText("第" + iCount + "次扫描结束");
 				if(iCount == test_times && isFirstSend && times != 0){
 					isFirstSend = false;//说明是第一次发送然后重新计算起止频率
 					//最大值和最小值所在的频率点 强转成int
 					double minFrequency = xList.get(yList.indexOf(Collections.max(yList))) * 100;
 					double maxFrequency = xList.get(yList.indexOf(Collections.min(yList))) * 100;
 					if(minFrequency > maxFrequency){
 						double temp = minFrequency;
 						minFrequency = maxFrequency;
 						maxFrequency = temp;
 					}//为了防止意外，，，，，频率值大小 反了。
 					frequency_value[0] = (int)(minFrequency - (maxFrequency - minFrequency)*2.5)/100;
 					frequency_value[1] = (int)(maxFrequency + (maxFrequency - minFrequency)/2)/100;
 					//重新设置要发送的数据
 					sendData_real = ConvertUtils.hexStringToBytes(sendString.substring(0, 8) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[0] + "00")) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[1] + "00")) +
 							sendString.substring(16));//重新计算
 					Log.e("#####", ConvertUtils.bytesToHexString(sendData_real));
 					//if(){//若发送为0次代表仅是测试
 						sendData(true, true);
 					//}
 				}
 				break;
 			}
 		}
 	}
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }	
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	getActivity().unbindService(mServiceConnection);
		mBluetoothLeService = null; 
		getActivity().unregisterReceiver(mGattUpdateReceiver);//取消注册广播
		getActivity().unregisterReceiver(receiver);//一定要取消注册广播
    }
    
    class UpdateBroadcastReceiver extends BroadcastReceiver{
    	
    	public UpdateBroadcastReceiver() {
    		super();
		}
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.yj.broadcast.UPDATEADDRESS")){
				mDeviceAddress = sp.getString("defaultAddress", "00:15:83:00:80:FB");
			}
			if(intent.getAction().equals("com.yj.broadcast.UPDATECONFIG")){
				sendString = sp.getString("defaultParams", "861101011027e02e3202800200020000050068");
				sendData_real = ConvertUtils.hexStringToBytes(sendString);//0次
				times = (sendData_real[15] & 0xff) * 256 + (sendData_real[14] & 0xff);
			}
		}
    }
    
    //Start    Java的JNI技术
  	static{
  		System.loadLibrary("CALLC");
  	}
  	public native double[] process_Data(double[] source);//本地方法 对数据滤波处理的算法
  	//End      Java的JNI技术
    
}
