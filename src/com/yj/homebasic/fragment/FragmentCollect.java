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
	private static final long CONNECT_PRRIOD = 8000;//10s��ֹͣ�����û�����Ӿ�ֹͣ����
//	private isHasTemp = true;
	private ImageButton btn_connect;//�������Ӱ�ť
	private Button btn_start;//��ʼ�ɼ���ť
	private TextView tv_bar;//���ΰ�
	private TranslateAnimation animation;
	private UpdateBroadcastReceiver receiver;//��̬ע��㲥
	//����ģ��
	private BluetoothLeService mBluetoothLeService;//��������
	private BluetoothGattCharacteristic mNotifyCharacteristic;
	private BluetoothGattCharacteristic characteristic;	//д����
	private BluetoothGattService mnotyGattService;
	private BluetoothGattCharacteristic readCharacteristic;	//������
	private BluetoothGattService readMnotyGattService;
	private static String mDeviceAddress = "00:15:83:00:80:FB";// Ҫ���ӵ�Ŀ�������豸�ĵ�ַ Ĭ�ϵ�
	private boolean mConnectedService = false;//�Ƿ��ҵ�����  ֻ�����Ϊtrue���������������Ͽ��Է��ͽ���������
	private Handler mHandler;//����������ʱ��  ���8����û�����ӳɹ� �ȶϿ�  ��Ϊ������ʱ��������������
	/* ����������� */
	private String sendString = "861101011027e02e3202800200020000050068";
	private byte[] sendData = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020200050068");// Ҫ���͵����� �ֽ����� �ʴ��Ƚ�16�����ַ���ת�����ֽڷ���
	private byte[] sendData_real = ConvertUtils.hexStringToBytes("861101011027e02e3202800200020000050068");
	private byte[] sendData_stop = ConvertUtils.hexStringToBytes("8603030168");// Ҫ����ֹͣɨ������� ��ʾ����
	private boolean isFirstSend;//�Ƿ��ǵ�һ�η���
	private boolean isHasData;///�ж��Ƿ���������  ���û�������� �����·���һ��
	private boolean isRunning = false;//�ж��Ƿ������У�������У����ٴε�����ǵ����ͣ
	private MyHandler handler;// ������Ϣ���д���  ��Ҫ�ǻ�ͼ+�������
	private int[] frequency_value = { 100, 120, 50 };// �Ӳ������ô���������ֹƵ�� ����Ƶ�� Ĭ��Ϊ100,120,50
	private float interval_time = 0;// s �뼶
	// �������õ���
	private List<Double> xList;
	private List<Double> yList;// һ��һ�����߱���
	private List<Double> yListTemp;// һ��һ�����߱���
	private double[] source;
	private List<Byte> listByte;
	private int iCount = 0;// �жϵ�ǰɨ��ڼ��ε����ݵ���
	private int times_point = 10;//ϵͳ��Ļ��������ٴε�ʱ����Ĭ����3�ε�ʱ����
	private String title_result = "��Ѫ����";
	// �����ս����һЩ����
	private float maxValue;//���ֵ
	private Date currentDate, beforeDate;
	String tempStr = "";// ������
	private int detectId;//�����ʼ���֮ʱ��洢���  ����ID  �ȵ������� ���µ�ǰ���Ľ��
	private int pointCount;//һ�ζ��ٸ���
	private int times = 0;//Ĭ����10��  
	private int test_times = 2;//���ԵĴ��� Ĭ����2��
	// ����excelʹ�õı���
//		private String excelPath;// ���浽sd��·��
	private String detectionDateStr;//�����ʱ��ĵ�ǰʱ���ַ���
	private String fileName;//�ļ���
	private File excel_Source_File, excel_Result_File;// excel�ļ�����ԭʼ����, ����������, ����ԭʼ�����˲�֮�������
	private MyPointF pointf;//ʱ��---ֵ 
	private SharedPreferences sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//���ò˵���Ϊtrue
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
		sp = getActivity().getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);////��ѯ�Ƿ���Ĭ�ϵ�����  ȥSharedPreference
		mDeviceAddress = sp.getString("defaultAddress", "00:15:83:00:80:FB");
		sendString = sp.getString("defaultParams", "861101011027e02e3202800200020000050068");
		sendData_real = ConvertUtils.hexStringToBytes(sendString);//0��
		times = (sendData_real[15] & 0xff) * 256 + (sendData_real[14] & 0xff);
		Toast.makeText(getActivity(),times + "", Toast.LENGTH_SHORT).show();
		Log.e("#####", ConvertUtils.bytesToHexString(sendData_real));
		handler = new MyHandler();//�Զ������Ϣ����  Ϊ�˻�ͼ
		drawlineInit();
		getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//ע���������һЩ����Ĺ㲥
		//ע����� �������� ---�����ķ���
		Intent gattServiceIntent = new Intent(getActivity(),BluetoothLeService.class);
		getActivity().bindService(gattServiceIntent, mServiceConnection, getActivity().BIND_AUTO_CREATE);//������ͷ�����а󶨺��Զ���������
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
				Toast.makeText(getActivity(), "û������", Toast.LENGTH_SHORT).show();
				return;
			}//��������֮������к���ı������ݣ����Ͳɼ�
			
			if(!isRunning){
				frequency_value[0] = 100;
				frequency_value[1] = 120;//��������Ĭ�ϵ�  100 120  ��Ϊ�ڵڶ��η��͵�ʱ����ʼƵ���Ѿ��ı���
				/*��һ���µõ����μ��ʱ�䣬���ߡ��������� �������ݿ�*/
				saveRecord();
				/* �ɼ���� */
				isFirstSend = true;
				isHasData = true;
				sendData(true,false);//��������
			}else{
				AlertDialog.Builder builder = new Builder(getActivity());
				AlertDialog dialog = null;
				builder.setTitle("��ͣ��");
				builder.setPositiveButton("��ͣ", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						sendData(false, false);
						Toast.makeText(getActivity(), "��ͣ", Toast.LENGTH_SHORT).show();
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
				}, CONNECT_PRRIOD);//����ȴ�5s֮����û������ ���ȶϿ�
			}else{
				mBluetoothLeService.disconnect();
			}
			break;
		default:
			break;
		}
	}
	private void saveRecord(){//����ɼ���¼
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("name", sp.getString("phone", "15922747849"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		detectionDateStr = format.format(new Date());
		values.put("createTime", detectionDateStr);
		db.insert("record", null, values);
		Cursor cursor = db.rawQuery("select last_insert_rowid() from record", null);
		if(cursor.moveToFirst()){
			detectId = cursor.getInt(0);//��Ϊ������������֮�� �޸Ľ�� ���ڵĽ����Ϊ��//��ȡ�²������ݵ�id  
		}
	}
	
	//������Ϣ���� ��ȡ����  ��һ�����������Ƿ��ǿ�ʼ�ɼ���������ڶ��������Ƿ����ٴη��Ͳɼ�����
	public void sendData(boolean isStart,boolean isRepeatStart){
		if(isStart){
			iCount = 0;// ����Ҫ���
			xList.removeAll(xList);
			yList.removeAll(yList);
			listByte.removeAll(listByte);// ���������
			// ����x���ֵ// ÿ�ο�ʼɨ�趼����Ҫ��������x���ֵ����Ϊ����������Ҫ�����仯��
			double tempVar = frequency_value[0] * 1000;
			pointCount = (frequency_value[1] - frequency_value[0]) * 1000 / frequency_value[2] + 1;
			for (int i = 0; i < pointCount; i++) {
				xList.add(tempVar / 1000);
				tempVar = tempVar + frequency_value[2];
			}
			source = new double[pointCount];//ÿ�ζ�����̫�����ڴ���   ��������ڷ��͵�ʱ�����x������ݴ�������
			if(!isRepeatStart){
				beforeDate = new Date();
			}
		}
		read();//��ȡ����
		final int charaProp = characteristic.getProperties();
		//�����char��д
		if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification( mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            //��ȡ���� �ڻص�������
            if(isStart){
            	if(!isRepeatStart){
            		characteristic.setValue(sendData);
            		Log.e("#######", ConvertUtils.bytesToHexString(sendData));
	            }else{
	            	characteristic.setValue(sendData_real);//����ǵڶ��η���  ���͵����µ�����
	            	Log.e("#######", ConvertUtils.bytesToHexString(sendData_real));
	            }
	            mBluetoothLeService.writeCharacteristic(characteristic);
            }else{//����ֹͣ�ź�
            	characteristic.setValue(sendData_stop);
	            mBluetoothLeService.writeCharacteristic(characteristic);
	            animation.cancel();
//		            setScreenBrightness(getSystemScreenBrightness());//����ֹͣҲ��������
            }
//		                Toast.makeText(getApplicationContext(), "д��ɹ���", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "���ͳɹ�", Toast.LENGTH_SHORT).show();
			if(isStart){
				//���ͳɹ��ű���  ��Ȼһֱ�����ļ�
				if(!isRepeatStart){//��Ϊֻ�е�һ�η��͵�ʱ�� ���½��ļ�
					saveInit();//ִ�б������ݲ�����һЩ������ʼ��  ÿ�ε���������µ�csv�ļ�
				}
				// �˴��뱣�������й� ÿ�ο�ʼɨ���ʱ����ڵ�ǰ��Ŀ�µı��д���һ��Sheet��
				SaveActionUtils.exportCSV(excel_Source_File, xList);
//						SaveActionUtils.exportCSV(excel_Result_File, new double[]{0d,0d});
				//��Ȼ��ͼ���ֿ�����һ��ֱ��
			}
        }else{
        	Toast.makeText(getActivity(), "����ʧ�ܣ������·���", Toast.LENGTH_SHORT).show();
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
	}
	public void saveInit(){
		// ÿ�δ������ʱ��Ĭ���½�һ��excel��� �����ǵ�ǰʱ��
		fileName = detectionDateStr + "_Source" + ".csv";
//			excelPath = SaveActionUtils.getExcelDir() + File.separator + filename;
		// ��ǰ·��/mnt/sdcart/Excel/Data/��ǰʱ��.xls
		excel_Source_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);// �õ���ǰ����ļ�=
		fileName = detectionDateStr + "_Result" + ".csv";
		excel_Result_File = new File(SaveActionUtils.getExcelDir() + File.separator + fileName);
	}
	public void drawlineInit(){
		pointf = new MyPointF();
		//ll_result_curve_display//�����յ���Ѫ���߽����LL�ؼ�
		xList = new ArrayList<Double>();
		yList = new ArrayList<Double>();
		yListTemp = new ArrayList<Double>();
		listByte = new ArrayList<Byte>();
	}
	//����������������
	private final ServiceConnection mServiceConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(android.content.ComponentName name, android.os.IBinder service) {
			//�󶨷����ʱ����ô˷���  bindService���õ�ʱ��
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()){
				//��ʼ�������������� �鿴�Ƿ�API>18 
				Toast.makeText(getActivity(), "�����豸��֧��BLE", Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}//ע����ʵ��һ���е���࣬��Ϊ��MainActivity�Ѿ��ж��豸�Ƿ�֧��BLE
			//��ʼ���ɹ�֮���Զ������豸
			mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBluetoothLeService = null;//unBindService�������ʱ����ô˷���
		};
	};
	
	/**	 * ������ 	 */
	private void read(){
		mBluetoothLeService.setCharacteristicNotification(readCharacteristic, true);
	}
	
	//ͨ��������Ʋ�ͬ���¼�
	//ʹ�������� ʹ�ù㲥����  �������ӡ��ɶ�д��״̬
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
        	getActivity().invalidateOptionsMenu();//ˢ�µ�ǰ�Ĳ˵�//������  ����û������������
        	btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_connecting));
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
            mConnectedService = false;
            getActivity().invalidateOptionsMenu();//ˢ�µ�ǰ�Ĳ˵�
			//���ӶϿ���ͬʱ �رձ�����Ļ����
            btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_normal));
        } 
        //�����п�֧�ֵķ���
        else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
        	//д���ݵķ����characteristic
        	mnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            characteristic = mnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //�����ݵķ����characteristic
            readMnotyGattService = mBluetoothLeService.getSupportedGattServices(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
            readCharacteristic = readMnotyGattService.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"));
            //ֻ�з��ַ����� �����Ҳŵ�������ȫ���ӳɹ���
            mConnectedService = true;
            btn_connect.setBackground(getResources().getDrawable(R.drawable.bluetooth_connect));
        } 
        //��ʾ����
        else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
        		isRunning = true;
        		if(isHasData){
        			isHasData = false;//˵���������������������� Ϊ���Ƿ�ֹ�Ѿ������� ����û�������������
        			tv_bar.startAnimation(animation);
        		}
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
            	//����������  ��·�߼�  ��������ȥ�жϰ���������ǲ�������
            	tempStr = ConvertUtils.bytesToHexString(data);
            	if(!tempStr.contains("FF01")){//������2���ֽڻ���4���ֽ�  Ӧ��˵�ǲ����Ƕ����ֽڵ�����
            		for(int i = 0; i < data.length; i++){
            			listByte.add(data[i]);
            		}
            	}else{//���� ff01--�����жϴ�ff01�ǲ��ǽ�β���Ǹ�ff01//�жϴ�ff01�ǲ�����Ľ�β���Ǹ�ff01 
            		Log.e("length", tempStr + "--" + data.length);
            		if(tempStr.indexOf("FF01") % 2 != 0){
            			Log.d("######", "#######" + tempStr + "#######"+listByte.size());
            			for(int i = 0; i < data.length; i++){
                			listByte.add(data[i]);
                		}
            		}else{
	            		byte[] tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(0, tempStr.indexOf("FF01")));
	            		//���п��� tempByteΪ��  ��Ϊ����ff01��ͷ��  ---20171121 �����˺ܶ�� ���Ӵ���û�з���
	            		if(tempByte != null){
		            		for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
	            		}
	//            		//����������жϵ���ff01������ǲ������
	            		if(listByte.size() < (pointCount*2)){//˵�������а����� ff01
	            			tempByte = ConvertUtils.hexStringToBytes("FF01");
	            			Log.d("######", "#######"+listByte.size());
	            			for(int i = 0; i < tempByte.length; i++){
		            			listByte.add(tempByte[i]);
		            		}
	            		}else{
	            			processBuffer();
	            		}
	            		//�ж��ǲ�����ff01��β�� ���ǵĻ� ˵������û�������� �������
	            		if(!tempStr.endsWith("FF01")){
	            			tempByte = ConvertUtils.hexStringToBytes(tempStr.substring(tempStr.indexOf("FF01") + 4));
	            			for(int i = 0; i < tempByte.length; i++){
	                			listByte.add(tempByte[i]);
	                		}
	            		}//�˴���Ȼ����©���ģ�������Ϊ�п��ܽ��յ������ݲ�����ż��  ��ô�ƣ�----��20171121
            		}
            	}
            }
        }
    };  
    
    public void processBuffer() {
		// �Ѿ�����һ��ɨ���������// ˵��һ��ɨ���������� ����ֻ���������������	//ÿ�θ�yList�������֮ǰ�����һ��
		yList.removeAll(yList);
		yListTemp.removeAll(yListTemp);
//			source = new double[listByte.size()/2];//ÿ�ζ�����̫�����ڴ���   ��������ڷ��͵�ʱ�����x������ݳ��ȴ�������
		for (int i = 0, j = 0; i < listByte.size() - 1; i = i + 2, j++) {
//				yList.add((listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff));//�˴�������˲�֮���޸�
			//��Ϊ���ջ�������Ӧ�����˲�֮�������
			double temp = (listByte.get(i) & 0xff) * 256 + (listByte.get(i + 1) & 0xff);
			if(j < source.length){
				source[j] = temp;
			}//�˴������Ϊ�˷�ֹ���ݳ��� û���жϵ�0xff01������յ��������ε�����
			yListTemp.add(temp);
		}
		// �����ݱ�����֮��listByte�����
		listByte.removeAll(listByte);
		handler.sendMessage(handler.obtainMessage(1));
	}
	// ���½����Hanlder ��
 	class MyHandler extends Handler {
 		@Override
 		public void handleMessage(Message msg) {
 			switch (msg.what) {
 			case 1:
 				// ÿ��������ʱ�򶼻�ȡ���μ��ʱ��
 				currentDate = new Date();
 				interval_time = (currentDate.getTime() - beforeDate.getTime());
 				interval_time = interval_time / 1000;
 				pointf.x = interval_time;
 				// һ��Ҫ��������ʱ��֮�󣬲�Ȼ��Ӱ����Ѫ����ʱ����ж�//��ͼ֮ǰ������ֵ֮ǰ�Ƚ����˲�����//���������double���ͣ�
 				source = process_Data(source);//ȥ�����ݼ��˲�
 				for(int i = 0; i < source.length; i++){
 					yList.add(source[i]);
 				}//yList��yListFilter�д洢�Ķ����˲�֮������ݣ���yList��ȥ�����ߣ�yListFilter��ȥ����
 				double tempData = Collections.max(yList);//�˴������ֵ�ǲ������   ��Ϊԭʼ������Ҫ������
 				maxValue = (float) tempData * 2 / 65536;
 				maxValue = (float)(Math.round(maxValue* 10000))/10000;//����С�������λ
 				pointf.y = maxValue;
 				//������һ�ε����� �����֮ǰ������
 				iCount++;
 				handler.sendMessage(handler.obtainMessage(3));
 				// �жϽ��յ���������û��ɨ��һ�ε��������� ��ʼ��ͼ// ��yList���浽excel��//TODO �´β���һ�µ������ݵ�ʱ��
 				SaveActionUtils.exportCSV(excel_Source_File,yListTemp);//����ԭʼ����
 				SaveActionUtils.exportCSV(excel_Result_File, pointf);//����������
 				break;
 			case 3:
 				//ÿ��ɨ�赽3�ν�����ʱ����  
 				if(iCount == times_point){
//	 					setScreenBrightness(20);
 				}
 				if(iCount == test_times && isFirstSend && times == 0){//˵������4���Ѿ������
 					isRunning = false;
 					animation.cancel();
 				}
 				if(iCount == times && !isFirstSend){
 					isRunning = true;
 					animation.cancel();
 					//ɨ������ָ���ϵͳ�Զ�������
//	 					setScreenBrightness(getSystemScreenBrightness());
//	 					btn_Start_Scan.setKeepScreenOn(false);//ɨ���������Ҫ�ٱ�����Ļ����
 				}
//	 				tv_times.setText("��" + iCount + "��ɨ�����");
 				if(iCount == test_times && isFirstSend && times != 0){
 					isFirstSend = false;//˵���ǵ�һ�η���Ȼ�����¼�����ֹƵ��
 					//���ֵ����Сֵ���ڵ�Ƶ�ʵ� ǿת��int
 					double minFrequency = xList.get(yList.indexOf(Collections.max(yList))) * 100;
 					double maxFrequency = xList.get(yList.indexOf(Collections.min(yList))) * 100;
 					if(minFrequency > maxFrequency){
 						double temp = minFrequency;
 						minFrequency = maxFrequency;
 						maxFrequency = temp;
 					}//Ϊ�˷�ֹ���⣬��������Ƶ��ֵ��С ���ˡ�
 					frequency_value[0] = (int)(minFrequency - (maxFrequency - minFrequency)*2.5)/100;
 					frequency_value[1] = (int)(maxFrequency + (maxFrequency - minFrequency)/2)/100;
 					//��������Ҫ���͵�����
 					sendData_real = ConvertUtils.hexStringToBytes(sendString.substring(0, 8) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[0] + "00")) + 
 							ConvertUtils.HighExchangeLow(ConvertUtils.dataConvertHex(frequency_value[1] + "00")) +
 							sendString.substring(16));//���¼���
 					Log.e("#####", ConvertUtils.bytesToHexString(sendData_real));
 					//if(){//������Ϊ0�δ�����ǲ���
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
		getActivity().unregisterReceiver(mGattUpdateReceiver);//ȡ��ע��㲥
		getActivity().unregisterReceiver(receiver);//һ��Ҫȡ��ע��㲥
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
				sendData_real = ConvertUtils.hexStringToBytes(sendString);//0��
				times = (sendData_real[15] & 0xff) * 256 + (sendData_real[14] & 0xff);
			}
		}
    }
    
    //Start    Java��JNI����
  	static{
  		System.loadLibrary("CALLC");
  	}
  	public native double[] process_Data(double[] source);//���ط��� �������˲�������㷨
  	//End      Java��JNI����
    
}
