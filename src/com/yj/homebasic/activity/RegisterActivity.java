package com.yj.homebasic.activity;



import com.mob.MobSDK;
import com.yj.homebasic.utils.ActivityController;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @author liaoyao
 * ��ͨ���¼ע��ģ��
 */
public class RegisterActivity extends Activity implements OnClickListener{
	
	String APPKEY = "25adce5701a60";    
    String APPSECRETE = "39397c561515af12365cef50ac3bc5f3";    
    // �ֻ��������        // ��֤�������   
    private EditText et_input_phone, et_input_code, et_password;    
    // ע�ᰴť     // ��ȡ��֤�밴ť   
    private Button btn_commit, btn_request_code;  
    private ImageButton imgbtn_return;
	private CheckBox cb_agree;
	private SQLiteDatabase db;
	private int iCountDown = 30;//����ʱ
	private int userId;//��ǰ�û������ID Ϊ���¸�ҳ������û���Ϣ
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().hide();//����actionbar
		initWidget();
		ActivityController.addActivity(this);
		db = MainActivity.dbHelper.getWritableDatabase();
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		//ע���¼�
		btn_request_code.setOnClickListener(this);
		btn_commit.setOnClickListener(this);
		imgbtn_return.setOnClickListener(this);
		 // ����������֤sdk    
//        SMSSDK.initSDK(RegisterActivity.this, APPKEY, APPSECRETE);   
        MobSDK.init(RegisterActivity.this, APPKEY, APPSECRETE);
        EventHandler eventHandler = new EventHandler(){    
            @Override    
            public void afterEvent(int event, int result, Object data) {    
                Message msg = new Message();    
                msg.arg1 = event;    
                msg.arg2 = result;    
                msg.obj = data;    
                handler.sendMessage(msg);    
            }    
        };    
        //ע��ص������ӿ�    
        SMSSDK.registerEventHandler(eventHandler);    
	}

	private void initWidget() {
		et_input_phone = (EditText)findViewById(R.id.et_input_phone);
		et_input_code = (EditText)findViewById(R.id.et_input_code);
		et_password = (EditText)findViewById(R.id.et_password);
		btn_request_code = (Button)findViewById(R.id.btn_request_code);
		btn_commit = (Button)findViewById(R.id.btn_commit);
		imgbtn_return = (ImageButton)findViewById(R.id.imgbtn_return);
		cb_agree = (CheckBox)findViewById(R.id.cb_agree);
		cb_agree.setText(Html.fromHtml("���ѽ��ղ��Ķ�<font color='#4690E6'>���û�ʹ�����</font>"));
	}

	@Override
	public void onClick(View v) {
		String phoneNums = et_input_phone.getText().toString();  
		switch (v.getId()) {
		case R.id.imgbtn_return:
			this.finish();
			break;
		case R.id.btn_request_code:
			// 1. ͨ�������ж��ֻ���    
            if (!judgePhoneNums(phoneNums)) {    
                return;    
            } // 2. ͨ��sdk���Ͷ�����֤    
            SMSSDK.getVerificationCode("86", phoneNums);    
            // 3. �Ѱ�ť��ɲ��ɵ����������ʾ����ʱ�����ڻ�ȡ��    
            btn_request_code.setClickable(false);    
            btn_request_code.setText("���·���(" + iCountDown + ")");    
            new Thread(new Runnable() {    
                @Override    
                public void run() {    
                    for (; iCountDown > 0; iCountDown--) {    
                        handler.sendEmptyMessage(-9);    
                        if (iCountDown <= 0) {    
                            break;    
                        }    
                        try {    
                            Thread.sleep(1000);    
                        } catch (InterruptedException e) {    
                            e.printStackTrace();    
                        }    
                    }    
                    handler.sendEmptyMessage(-8);    
                }    
            }).start();   
			break;
		case R.id.btn_commit:
//			Intent intent = new Intent(RegisterActivity.this,    
//                    InformationSettingActivity.class);    
//            intent.putExtra("userId", userId);
//            startActivity(intent); 
//            ActivityController.finishAll();
			if(TextUtils.isEmpty(et_password.getText().toString().trim())){
				return;
			}
			if(!cb_agree.isChecked()){
				return;
			}
			//�жϵ绰���������ݿ������Ƿ����
			if(TextUtils.isEmpty(phoneNums) || !judgePhoneNums(phoneNums)){
				return;
			}
			String sql = "select count(*) from user where phone = '" + phoneNums + "'";
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(RegisterActivity.this, "�ֻ����Ѿ�ע��~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//���յ�����֤����ֻ����ύ�ٴκ˶�    
            SMSSDK.submitVerificationCode("86", phoneNums, et_input_code    
                    .getText().toString());    
			break;
		default:
			break;
		}
	}
	
	 Handler handler = new Handler() {    
	        public void handleMessage(Message msg) {    
	            if (msg.what == -9) {    
	                btn_request_code.setText("���·���(" + iCountDown + ")");    
	            } else if (msg.what == -8) {    
	            	btn_request_code.setText("��ȡ��֤��");    
	            	btn_request_code.setClickable(true);    
	            	iCountDown = 30;    
	            } else {    
	                int event = msg.arg1;    
	                int result = msg.arg2;    
	                Object data = msg.obj;    
	                Log.e("event", "event=" + event);    
	                if (result == SMSSDK.RESULT_COMPLETE) {    
	                    // ����ע��ɹ��󣬷���MainActivity,Ȼ����ʾ    
	                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// �ύ��֤��ɹ�    
	                    	//��������
	                    	ContentValues values = new ContentValues();
	                    	values.put("phone", et_input_phone.getText().toString().trim());
	                    	values.put("password", et_password.getText().toString().trim());
	                    	db.insert("user", null, values);
	                    	//��ȡ�²������ݵ�id  
	            			Cursor cursor = db.rawQuery("select last_insert_rowid() from user", null);
	            			if(cursor.moveToFirst()){
	            				userId = cursor.getInt(0);
	            			}
	            			Editor editor = sp.edit();
	            			editor.putInt("userId", userId);
	            			editor.putString("phone", et_input_phone.getText().toString().trim());
	            			editor.putString("password", et_password.getText().toString().trim());
	            			editor.commit();//����ǰ�û���Ϣ���浽sharedPerference�洢
	                        Intent intent = new Intent(RegisterActivity.this,    
	                                InformationSettingActivity.class);    
	                        intent.putExtra("userId", userId);
	                        startActivity(intent); 
	                        ActivityController.finishAll();
	                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {    
//	                        Toast.makeText(getApplicationContext(), "���ڻ�ȡ��֤��",    
//	                                Toast.LENGTH_SHORT).show();    
	                    } else {    
	                        ((Throwable) data).printStackTrace();    
	                    }    
	                }    
	            }    
	        }    
	    };    
	
	 /**  
     * �ж��ֻ������Ƿ����  
     *   
     * @param phoneNums  
     */    
    private boolean judgePhoneNums(String phoneNums) {    
        if (isMatchLength(phoneNums, 11)    
                && isMobileNO(phoneNums)) {    
            return true;    
        }    
        Toast.makeText(this, "�ֻ�������������",Toast.LENGTH_SHORT).show();    
        return false;    
    }    
    
    /**  
     * �ж�һ���ַ�����λ��  
     * @param str  
     * @param length  
     * @return  
     */    
    public static boolean isMatchLength(String str, int length) {    
        if (str.isEmpty()) {    
            return false;    
        } else {    
            return str.length() == length ? true : false;    
        }    
    }    
    
    /**  
     * ��֤�ֻ���ʽ  
     */    
    public static boolean isMobileNO(String mobileNums) {    
        /*  
         * �ƶ���134��135��136��137��138��139��150��151��157(TD)��158��159��187��188  
         * ��ͨ��130��131��132��152��155��156��185��186 ���ţ�133��153��180��189����1349��ͨ��  
         * �ܽ��������ǵ�һλ�ض�Ϊ1���ڶ�λ�ض�Ϊ3��5��8������λ�õĿ���Ϊ0-9  
         */    
        String telRegex = "[1][358]\\d{9}";// "[1]"�����1λΪ����1��"[358]"����ڶ�λ����Ϊ3��5��8�е�һ����"\\d{9}"��������ǿ�����0��9�����֣���9λ��    
        if (TextUtils.isEmpty(mobileNums))    
            return false;    
        else    
            return mobileNums.matches(telRegex);    
    }    
    
    @Override    
    protected void onDestroy() {    
        SMSSDK.unregisterAllEventHandler(); 
        ActivityController.removeActivity(this);
        super.onDestroy();    
    }    
}
