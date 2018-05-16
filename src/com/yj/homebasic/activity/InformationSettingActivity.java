package com.yj.homebasic.activity;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class InformationSettingActivity extends Activity implements OnClickListener{
	private EditText et_username, et_age, et_height, et_weight;
	private RadioButton rbtn_male;
	private ImageButton imgbtn_return;
	private Button btn_infor_complete;
	private int userId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitvity_information);
		getActionBar().hide();
		initWidget();
		userId = getIntent().getIntExtra("userId", 1);
		btn_infor_complete.setOnClickListener(this);
		imgbtn_return.setOnClickListener(this);
	}

	private void initWidget() {
		btn_infor_complete = (Button)findViewById(R.id.btn_infor_complete);
		imgbtn_return = (ImageButton)findViewById(R.id.imgbtn_return);
		et_username = (EditText)findViewById(R.id.et_username);
		et_age = (EditText)findViewById(R.id.et_age);
		et_height = (EditText)findViewById(R.id.et_height);
		et_weight = (EditText)findViewById(R.id.et_weight);
		rbtn_male = (RadioButton)findViewById(R.id.rbtn_male);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_infor_complete:
			ContentValues values = new ContentValues();
			if(!TextUtils.isEmpty(et_username.getText().toString().trim())){
				values.put("username", et_username.getText().toString().trim());
			}
			values.put("gender", rbtn_male.isChecked() ? 1 : 0);//ÄÐ-1 Å®-0
			if(!TextUtils.isEmpty(et_age.getText().toString().trim())){
				values.put("age", et_age.getText().toString().trim());
			}
			if(!TextUtils.isEmpty(et_height.getText().toString().trim())){
				values.put("height", et_height.getText().toString().trim());
			}
			if(!TextUtils.isEmpty(et_weight.getText().toString().trim())){
				values.put("weight", et_weight.getText().toString().trim());
			}
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
			db.update("user", values, "id = ?", new String[]{userId + ""});
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			this.finish();
			break;
		case R.id.imgbtn_return:
			this.finish();
			break;
		default:
			break;
		}
	}
	
}