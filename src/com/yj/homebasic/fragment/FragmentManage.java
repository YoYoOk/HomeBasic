package com.yj.homebasic.fragment;

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
	private List<Integer> drugCountList;
	private DrugListAdapter drugListAdapter;
	private ArrayAdapter<String> drugNameAdapter, drugRecordAdapter;
	private ArrayAdapter<Integer> drugCountAdapter;
	private int longClickPosition, selectedDrugNamePositon, selectedDrugCountPosition;//�����б�����λ��
	private Dialog longClickdialog;//����ɾ����dialog
	private AddDrugDialog addDrugDialog;
	private QueryDrugDialog queryDrugDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);//���ò˵���Ϊtrue
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
				//��������¼�
				int[] location = new int[2];
				// ��ȡ��ǰview����Ļ�еľ���λ��
				// ,location[0]��ʾview��x����ֵ,location[1]��ʾview������ֵ
				view.getLocationOnScreen(location);
				longClickPosition = position;
				DisplayMetrics displayMetrics = new DisplayMetrics();
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				display.getMetrics(displayMetrics);
				WindowManager.LayoutParams params = longClickdialog.getWindow().getAttributes();
				params.gravity = Gravity.BOTTOM;
//				params.y =display.getHeight() -  location[1];//getHeight�ѷ���
				Point size = new Point();
				display.getSize(size);
				params.y = size.y - location[1];
				longClickdialog.getWindow().setAttributes(params);
				longClickdialog.setCanceledOnTouchOutside(true);
				longClickdialog.show();
				return true;
			}
		});
		//���ҩ��
		btn_add_drug.setOnClickListener(this);
		//��ӷ���ҩ��ѡ��ؼ�
		btn_add_drug_choice.setOnClickListener(this);
		btn_add_drug_record.setOnClickListener(this);
		return view;
	}
	
	private void initParams() {
		//�˴�Ӧ���Ǵ����ݿ��ȡ
		drugList = new ArrayList<Drug>();
		drugNameList = new ArrayList<String>();
		drugCountList = new ArrayList<Integer>();
		//�����ݿ��ȡ
		SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
		Cursor cursor = db.query("drug", null, null, null, null, null, null);
		//�Ȳ�ѯ�������е�����		//��Ŀǰָ��ָ����е����ݵ�һ��
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
//		Drug drug1 = new Drug("��������Ƭ");
//		drugNameList.add(drug1.getDrugName());
//		Drug drug2 = new Drug("Ѫ˨������Ƭ");
//		drugNameList.add(drug2.getDrugName());
//		Drug drug3 = new Drug("��˾ƥ�ֳ���Ƭ");
//		drugNameList.add(drug3.getDrugName());
//		Drug drug4 = new Drug("����Ѫ˨ͨ����");
//		drugNameList.add(drug4.getDrugName());
//		drugList.add(drug4);
//		drugList.add(drug3);
//		drugList.add(drug2);
//		drugList.add(drug1);
		drugCountList.add(1);
		drugCountList.add(2);
		drugCountList.add(3);
		drugCountList.add(4);
		drugNameAdapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,drugNameList);
		drugNameAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice); 
		spinner_drugName_choice.setAdapter(drugNameAdapter);
		drugCountAdapter = new ArrayAdapter<Integer>(this.getActivity(), android.R.layout.simple_spinner_item,drugCountList);
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_drug_save:
			//����ҩ����Ϣ
			SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			String tempName = addDrugDialog.et_drug_name.getText().toString().trim();
			Drug tempDrug = new Drug();
			if(TextUtils.isEmpty(tempName)){
				Toast.makeText(getActivity(), "ҩ�����Ʋ���Ϊ�գ�", Toast.LENGTH_SHORT).show();
				return;
			}else{
				values.put("name", tempName);
				drugNameList.add(tempName);
				drugNameAdapter.notifyDataSetChanged();
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
				return;//˵������ҩ������Ϊ��ʲô��û��
			}
			String tempRecord = drugNameList.get(selectedDrugNamePositon).toString() + "---" +
					drugCountList.get(selectedDrugCountPosition).toString() + "---" + 
					(btn_unit_li.isChecked() ? "��":"��");
			drugRecordList.add(tempRecord);
			drugRecordAdapter.notifyDataSetInvalidated();
			break;
		case R.id.btn_add_drug_record:
			Toast.makeText(getActivity(), "Add successful !!", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}
	
}
