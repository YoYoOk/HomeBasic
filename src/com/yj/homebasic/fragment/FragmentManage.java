package com.yj.homebasic.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.activity.MainActivity;
import com.yj.homebasic.activity.R;
import com.yj.homebasic.adapter.DrugListAdapter;
import com.yj.homebasic.domain.Drug;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.yj.homebasic.dialog.AddDrugDialog;
import com.yj.homebasic.dialog.QueryDrugDialog;

public class FragmentManage extends Fragment implements OnClickListener{
	
	private ListView drugListView, lv_drug_record;
	private Button btn_add_drug, btn_add_drug_choice, btn_add_drug_record;
	private Spinner spinner_drugName_choice,spinner_drugCount_choice;
	private RadioButton btn_unit_li;
	private List<Drug> drugList;
	private List<String> drugNameList;
	private List<String> drugRecordList;
	private List<Double> drugCountList;
	private DrugListAdapter drugListAdapter;
	private ArrayAdapter<String> drugNameAdapter, drugRecordAdapter;
	private ArrayAdapter<Double> drugCountAdapter;
	private int longClickPosition, selectedDrugNamePositon, selectedDrugCountPosition;//长按列表点击的位置
	private Dialog longClickdialog;//长按删除的dialog
	private AddDrugDialog addDrugDialog;
	private QueryDrugDialog queryDrugDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//设置菜单栏为true
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_manage, null);
		initWidget(view);
		initParams();
//		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
//		db.delete("drug", "id = ?", new String[]{1 + ""});
//		db.delete("drug", "id = ?", new String[]{2 + ""});
		spinner_drugName_choice.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedDrugNamePositon = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner_drugCount_choice.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedDrugCountPosition = position;
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		drugListAdapter = new DrugListAdapter(getActivity(), R.layout.druglist, drugList);
		drugListView.setAdapter(drugListAdapter);
		drugRecordList = new ArrayList<String>();
		drugRecordAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, drugRecordList);
		lv_drug_record.setAdapter(drugRecordAdapter);
		lv_drug_record.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				drugRecordList.remove(position);
				drugRecordAdapter.notifyDataSetInvalidated();
				return true;
			}
		});
		drugListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Toast.makeText(getActivity(), drugListAdapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
				queryDrugDialog = new QueryDrugDialog(getActivity(), R.style.loading_dialog, drugListAdapter.getItem(position));
				queryDrugDialog.show();
			}
		});
		drugListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//长按点击事件
//				int[] location = new int[2];
//				// 获取当前view在屏幕中的绝对位置
//				// ,location[0]表示view的x坐标值,location[1]表示view的坐标值
//				view.getLocationOnScreen(location);
//				longClickPosition = position;
//				DisplayMetrics displayMetrics = new DisplayMetrics();
//				Display display = getActivity().getWindowManager().getDefaultDisplay();
//				display.getMetrics(displayMetrics);
//				WindowManager.LayoutParams params = longClickdialog.getWindow().getAttributes();
//				params.gravity = Gravity.BOTTOM;
////				params.y =display.getHeight() -  location[1];//getHeight已废弃
//				Point size = new Point();
//				display.getSize(size);
//				params.y = size.y - location[1];
//				longClickdialog.getWindow().setAttributes(params);
//				longClickdialog.setCanceledOnTouchOutside(true);
//				longClickdialog.show();
				if(deleteDrugById(drugList.get(position).getDrugId())){
					drugList.remove(position);
					drugNameList.remove(position);
					drugListAdapter.notifyDataSetInvalidated();
//					drugNameAdapter.notifyDataSetInvalidated();
					((BaseAdapter)spinner_drugName_choice.getAdapter()).notifyDataSetChanged();
				}
				return true;
			}
		});
		//添加药物
		btn_add_drug.setOnClickListener(this);
		//添加服用药物选择控件
		btn_add_drug_choice.setOnClickListener(this);
		btn_add_drug_record.setOnClickListener(this);
		return view;
	}
	
	private void initParams() {
		//此处应该是从数据库获取
		drugList = new ArrayList<Drug>();
		drugNameList = new ArrayList<String>();
		drugCountList = new ArrayList<Double>();
		//从数据库读取
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("drug", null, null, null, null, null, null);
		//先查询表中所有的数据		//将目前指针指向表中的数据第一行
		if(cursor.moveToFirst()){
			do{
				Drug drug = new Drug();
				drug.setDrugId(cursor.getInt(cursor.getColumnIndex("id")));
				drug.setDrugName(cursor.getString(cursor.getColumnIndex("name")));
				drug.setDrugDescription(cursor.getString(cursor.getColumnIndex("descri")));
				drug.setDrugDosage(cursor.getString(cursor.getColumnIndex("dosage")));
				drugList.add(drug);
				drugNameList.add(drug.getDrugName());
			}while(cursor.moveToNext());
		}
		cursor.close();
//		if(drugList.size() == 0){
//			Drug drug1 = new Drug("华法林钠片");
//			drugNameList.add(drug1.getDrugName());
//			Drug drug2 = new Drug("血栓心脉宁片");
//			drugNameList.add(drug2.getDrugName());
//			Drug drug3 = new Drug("阿司匹林肠溶片");
//			drugNameList.add(drug3.getDrugName());
//			Drug drug4 = new Drug("复方血栓通胶囊");
//			drugNameList.add(drug4.getDrugName());
//			drugList.add(drug4);
//			drugList.add(drug3);
//			drugList.add(drug2);
//			drugList.add(drug1);
//		}
		
//		db.execSQL("delete from drug");
//		ContentValues values = new ContentValues();
//		values.put("name", "华法林钠片");
//		long count = db.insert("drug", null, values);
//		values.clear();
//		values.put("name", "血栓心脉宁片");
//		db.insert("drug", null, values);
//		values.clear();
//		values.put("name", "阿司匹林肠溶片");
//		db.insert("drug", null, values);
//		values.clear();
//		values.put("name", "复方血栓通胶囊");
//		db.insert("drug", null, values);
		
		drugCountList.add(1d);
		drugCountList.add(1.5d);
		drugCountList.add(2d);
		drugCountList.add(2.5);
		drugCountList.add(3d);
		drugCountList.add(3.5);
		drugCountList.add(4d);
		drugNameAdapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,drugNameList);
		drugNameAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice); 
		spinner_drugName_choice.setAdapter(drugNameAdapter);
		drugCountAdapter = new ArrayAdapter<Double>(this.getActivity(), android.R.layout.simple_spinner_item,drugCountList);
		drugCountAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner_drugCount_choice.setAdapter(drugCountAdapter);
	}
	public void initWidget(View view){
		lv_drug_record = (ListView)view.findViewById(R.id.lv_drug_record);
		drugListView = (ListView)view.findViewById(R.id.lv_drug_list);
		btn_add_drug = (Button)view.findViewById(R.id.btn_add_drug_dialog);
		btn_add_drug_choice = (Button)view.findViewById(R.id.btn_add_drug_choice);
		btn_add_drug_record = (Button)view.findViewById(R.id.btn_add_drug_record);
		spinner_drugName_choice = (Spinner)view.findViewById(R.id.spinner_drugName_choice);
		spinner_drugCount_choice = (Spinner)view.findViewById(R.id.spinner_drugCount_choice);
		btn_unit_li = (RadioButton)view.findViewById(R.id.btn_unit_li);
		longClickdialog = new Dialog(getActivity(), R.style.MyDialogStyle);
		longClickdialog.setContentView(R.layout.dialog_longclick);
	}
	
	private boolean deleteDrugById(Integer id){
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		int result = db.delete("drug", "id = ?", new String[] {id.toString()});
		if(result > 0){
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drug_save:
			//保存药物信息
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String tempName = addDrugDialog.et_drug_name.getText().toString().trim();
			Drug tempDrug = new Drug();
			if(TextUtils.isEmpty(tempName)){
				Toast.makeText(getActivity(), "药物名称不能为空！", Toast.LENGTH_SHORT).show();
				return;
			}else{
				values.put("name", tempName);
				drugNameList.add(tempName);
				((BaseAdapter)spinner_drugName_choice.getAdapter()).notifyDataSetChanged();
//				drugNameAdapter.notifyDataSetChanged();
//				drugNameAdapter.notifyDataSetInvalidated();
				tempDrug.setDrugName(tempName);
			}
			String tempDescri = addDrugDialog.et_drug_description.getText().toString().trim();
			if(!TextUtils.isEmpty(tempDescri)){
				values.put("descri", tempDescri);
				tempDrug.setDrugDescription(tempDescri);
			}
			String tempDosage = addDrugDialog.et_drug_dosage.getText().toString().trim();
			if(!TextUtils.isEmpty(tempDosage)){
				values.put("descri", tempDosage);
				tempDrug.setDrugDosage(tempDosage);
			}
			long count = db.insert("drug", null, values);
			if(count > 0){
				Toast.makeText(getActivity(), "add Success!", Toast.LENGTH_SHORT).show();
				Cursor cursor = db.rawQuery("select last_insert_rowid() from drug", null);
				if(cursor.moveToFirst()){
					tempDrug.setDrugId(cursor.getInt(0));
				}
				drugList.add(tempDrug);
				drugListAdapter.notifyDataSetInvalidated();
			}
			addDrugDialog.dismiss();
			break;
		case R.id.btn_add_drug_dialog:
			addDrugDialog = new AddDrugDialog(getActivity(),R.style.loading_dialog, this);
			addDrugDialog.show();
			break;
		case R.id.btn_add_drug_choice:
			if(drugNameList.size() == 0){
				return;//说明现在药物名称为空什么都没有
			}
			String tempRecord = drugNameList.get(selectedDrugNamePositon).toString() + "---" +
					drugCountList.get(selectedDrugCountPosition).toString() + "---" + 
					(btn_unit_li.isChecked() ? "粒":"袋");
			drugRecordList.add(tempRecord);
			drugRecordAdapter.notifyDataSetInvalidated();
			break;
		case R.id.btn_add_drug_record:
			Toast.makeText(getActivity(), "Add successful !!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.tv_dialog_delete:
			longClickdialog.dismiss();//然后将此对话框关闭
			boolean result = deleteDrugById(drugList.get(longClickPosition).getDrugId());
			if(result){
				drugList.remove(longClickPosition);
				drugNameList.remove(longClickPosition);
				drugListAdapter.notifyDataSetInvalidated();
//				drugNameAdapter.notifyDataSetInvalidated();
				((BaseAdapter)spinner_drugName_choice.getAdapter()).notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}
	
}
