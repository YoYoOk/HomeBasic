package com.yj.homebasic.activity;


import com.yj.homebasic.database.MyDatabaseHelper;
import com.yj.homebasic.utils.ActivityController;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author liaoyao
 * 普通版登录注册模块
 */
public class LoginActivity extends Activity implements OnClickListener{
	private EditText et_username, et_password;
	private Button btn_login, btn_register;
	private SharedPreferences sp;
	private int userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().hide();//隐藏actionbar
		initWidget();
		ActivityController.addActivity(this);
		sp = getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		prepareData();
		MainActivity.dbHelper = new MyDatabaseHelper(this, "HomeBasic.db", null, 4);
		MainActivity.dbHelper.getWritableDatabase();//在调用这个方法的时候，，会调用重写的onCreate方法  即在此处写创建表的方法
		//注册事件
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
	}

	private void initWidget() {
		btn_login = (Button)findViewById(R.id.btn_login);
		btn_register = (Button)findViewById(R.id.btn_register);
		et_username = (EditText)this.findViewById(R.id.et_username);
		et_password = (EditText)findViewById(R.id.et_password);
	}
	
	private void prepareData(){
		et_username.setText(sp.getString("phone", "15922747849"));
		et_password.setText(sp.getString("password", "admin"));
	}
	
	private boolean judgeLogin(String username, String password){
		boolean result = false;
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		String sql = "select count(*) from user where phone = '"
				+ username + "' and password = '" + password + "'";
		SQLiteStatement statement = db.compileStatement(sql);
		
		long count = statement.simpleQueryForLong();
		if(count > 0){
			result = true;
		}
		Cursor cursor = db.query("user", null, "phone = ? ", new String[]{username}, null, null, null);
		if(cursor.moveToFirst()){
			userId = cursor.getInt(cursor.getColumnIndex("id"));
		}
		return result;	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			if(!judgeLogin(et_username.getText().toString().trim(), et_password.getText().toString().trim())){
				return;
			}
			Editor editor = sp.edit();
			editor.putString("phone", et_username.getText().toString().trim());
			editor.putString("password", et_password.getText().toString().trim());
			editor.putInt("userId", userId);
			editor.commit();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.btn_register:
			Intent registerIntent = new Intent(this,RegisterActivity.class);
			startActivity(registerIntent);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		ActivityController.removeActivity(this);
		super.onDestroy();
	}
}
