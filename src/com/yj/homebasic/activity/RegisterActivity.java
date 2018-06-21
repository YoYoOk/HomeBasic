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
 * 普通版登录注册模块
 */
public class RegisterActivity extends Activity implements OnClickListener{
	
	String APPKEY = "25adce5701a60";    
    String APPSECRETE = "39397c561515af12365cef50ac3bc5f3";    
    // 手机号输入框        // 验证码输入框   
    private EditText et_input_phone, et_input_code, et_password;    
    // 注册按钮     // 获取验证码按钮   
    private Button btn_commit, btn_request_code;  
    private ImageButton imgbtn_return;
	private CheckBox cb_agree;
	private SQLiteDatabase db;
	private int iCountDown = 30;//倒计时
	private int userId;//当前用户插入的ID 为了下个页面添加用户信息
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		ActivityController.addActivity(this);
		db = MainActivity.dbHelper.getWritableDatabase();
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		//注册事件
		btn_request_code.setOnClickListener(this);
		btn_commit.setOnClickListener(this);
		imgbtn_return.setOnClickListener(this);
		 // 启动短信验证sdk    
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
        //注册回调监听接口    
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
		cb_agree.setText(Html.fromHtml("我已接收并阅读<font color='#4690E6'>《用户使用条款》</font>"));
	}

	@Override
	public void onClick(View v) {
		String phoneNums = et_input_phone.getText().toString();  
		switch (v.getId()) {
		case R.id.imgbtn_return:
			this.finish();
			break;
		case R.id.btn_request_code:
			// 1. 通过规则判断手机号    
            if (!judgePhoneNums(phoneNums)) {    
                return;    
            } // 2. 通过sdk发送短信验证    
            SMSSDK.getVerificationCode("86", phoneNums);    
            // 3. 把按钮变成不可点击，并且显示倒计时（正在获取）    
            btn_request_code.setClickable(false);    
            btn_request_code.setText("重新发送(" + iCountDown + ")");    
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
			//判断电话号码在数据库里面是否存在
			if(TextUtils.isEmpty(phoneNums) || !judgePhoneNums(phoneNums)){
				return;
			}
			String sql = "select count(*) from user where phone = '" + phoneNums + "'";
			SQLiteStatement statement = db.compileStatement(sql);
			long count = statement.simpleQueryForLong();
			if(count > 0){
				Toast.makeText(RegisterActivity.this, "手机号已经注册~~", Toast.LENGTH_SHORT).show();
				return;
			}
			//将收到的验证码和手机号提交再次核对    
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
	                btn_request_code.setText("重新发送(" + iCountDown + ")");    
	            } else if (msg.what == -8) {    
	            	btn_request_code.setText("获取验证码");    
	            	btn_request_code.setClickable(true);    
	            	iCountDown = 30;    
	            } else {    
	                int event = msg.arg1;    
	                int result = msg.arg2;    
	                Object data = msg.obj;    
	                Log.e("event", "event=" + event);    
	                if (result == SMSSDK.RESULT_COMPLETE) {    
	                    // 短信注册成功后，返回MainActivity,然后提示    
	                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功    
	                    	//保存数据
	                    	ContentValues values = new ContentValues();
	                    	values.put("phone", et_input_phone.getText().toString().trim());
	                    	values.put("password", et_password.getText().toString().trim());
	                    	db.insert("user", null, values);
	                    	//获取新插入数据的id  
	            			Cursor cursor = db.rawQuery("select last_insert_rowid() from user", null);
	            			if(cursor.moveToFirst()){
	            				userId = cursor.getInt(0);
	            			}
	            			Editor editor = sp.edit();
	            			editor.putInt("userId", userId);
	            			editor.putString("phone", et_input_phone.getText().toString().trim());
	            			editor.putString("password", et_password.getText().toString().trim());
	            			editor.commit();//将当前用户信息保存到sharedPerference存储
	                        Intent intent = new Intent(RegisterActivity.this,    
	                                InformationSettingActivity.class);    
	                        intent.putExtra("userId", userId);
	                        startActivity(intent); 
	                        ActivityController.finishAll();
	                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {    
//	                        Toast.makeText(getApplicationContext(), "正在获取验证码",    
//	                                Toast.LENGTH_SHORT).show();    
	                    } else {    
	                        ((Throwable) data).printStackTrace();    
	                    }    
	                }    
	            }    
	        }    
	    };    
	
	 /**  
     * 判断手机号码是否合理  
     *   
     * @param phoneNums  
     */    
    private boolean judgePhoneNums(String phoneNums) {    
        if (isMatchLength(phoneNums, 11)    
                && isMobileNO(phoneNums)) {    
            return true;    
        }    
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();    
        return false;    
    }    
    
    /**  
     * 判断一个字符串的位数  
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
     * 验证手机格式  
     */    
    public static boolean isMobileNO(String mobileNums) {    
        /*  
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188  
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）  
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9  
         */    
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。    
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
