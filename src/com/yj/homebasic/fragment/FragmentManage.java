package com.yj.homebasic.fragment;

import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.activity.R;
import com.yj.homebasic.adapter.DrugListAdapter;
import com.yj.homebasic.domain.Drug;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.yj.homebasic.dialog.AddDrugDialog;
import com.yj.homebasic.dialog.QueryDrugDialog;;

public class FragmentManage extends Fragment {
	
	private ListView drugListView;
	private Button btn_add_drug, btn_add_drug_choice, btn_add_drug_record;
	private Spinner spinner_drugName_choice,spinner_drugCount_choice;
	private LinearLayout ll_drug_choice;
	private RadioButton btn_unit_li;
	private List<Drug> drugList;
	private List<String> drugNameList;
	private List<Integer> drugCountList;
	private DrugListAdapter drugListAdapter;
	private ArrayAdapter<String> drugNameAdapter;
	private ArrayAdapter<Integer> drugCountAdapter;
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
		drugNameAdapter=new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item,drugNameList);
		drugNameAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice); 
		spinner_drugName_choice.setAdapter(drugNameAdapter);
		drugCountAdapter = new ArrayAdapter<Integer>(this.getActivity(), android.R.layout.simple_spinner_item,drugCountList);
		drugCountAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		spinner_drugCount_choice.setAdapter(drugCountAdapter);
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
				int[] location = new int[2];
				// 获取当前view在屏幕中的绝对位置
				// ,location[0]表示view的x坐标值,location[1]表示view的坐标值
				view.getLocationOnScreen(location);
				longClickPosition = position;
				DisplayMetrics displayMetrics = new DisplayMetrics();
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				display.getMetrics(displayMetrics);
				WindowManager.LayoutParams params = longClickdialog.getWindow().getAttributes();
				params.gravity = Gravity.BOTTOM;
//				params.y =display.getHeight() -  location[1];//getHeight已废弃
				Point size = new Point();
				display.getSize(size);
				params.y = size.y - location[1];
				longClickdialog.getWindow().setAttributes(params);
				longClickdialog.setCanceledOnTouchOutside(true);
				longClickdialog.show();
				return true;
			}
		});
		//添加药物
		btn_add_drug.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Toast.makeText(getActivity(), "弹出添加 药物界面", Toast.LENGTH_SHORT).show();
				addDrugDialog = new AddDrugDialog(getActivity(),R.style.loading_dialog, onClickListener);
				addDrugDialog.show();
			}
		});
		//添加服用药物选择控件
		btn_add_drug_choice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//动态添加TextView显示服药情况  然后可 长按移除此控件
				TextView tv = new TextView(getActivity());
				tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				tv.setText(drugNameList.get(selectedDrugNamePositon).toString() + "---" +
						drugCountList.get(selectedDrugCountPosition).toString() + "---" + 
						(btn_unit_li.isChecked() ? "粒":"袋"));
				tv.setTextSize(20f);
				tv.setGravity(Gravity.CENTER);
				//设置其margin-top 10dp
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tv.getLayoutParams());
				lp.setMargins(0, 30, 0, 0);
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				lp.height = 100;
				tv.setLayoutParams(lp);
				tv.setBackground(getResources().getDrawable(R.drawable.tab_press));
				ll_drug_choice.addView(tv);
			}
		});
		btn_add_drug_record.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//将记录保存至数据库
//				Toast.makeText(getActivity(), drugNameAdapter.getItem(selectedDrugNamePositon) + " , " + 
//						drugCountAdapter.getItem(selectedDrugCountPosition).toString(), Toast.LENGTH_SHORT).show();
				Toast.makeText(getActivity(), "Add successful !!", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}
	
	private void initParams() {
		//此处应该是从数据库获取
		drugList = new ArrayList<Drug>();
		drugNameList = new ArrayList<String>();
		drugCountList = new ArrayList<Integer>();
		Drug drug1 = new Drug("华法林钠片");
		drugNameList.add(drug1.getDrugName());
		Drug drug2 = new Drug("血栓心脉宁片");
		drugNameList.add(drug2.getDrugName());
		Drug drug3 = new Drug("阿司匹林肠溶片");
		drugNameList.add(drug3.getDrugName());
		Drug drug4 = new Drug("复方血栓通胶囊");
		drugNameList.add(drug4.getDrugName());
		drugList.add(drug4);
		drugList.add(drug3);
		drugList.add(drug2);
		drugList.add(drug1);
		drugCountList.add(1);
		drugCountList.add(2);
		drugCountList.add(3);
		drugCountList.add(4);
	}
	public void initWidget(View view){
		drugListView = (ListView)view.findViewById(R.id.lv_drug_list);
		btn_add_drug = (Button)view.findViewById(R.id.btn_add_drug_dialog);
		btn_add_drug_choice = (Button)view.findViewById(R.id.btn_add_drug_choice);
		btn_add_drug_record = (Button)view.findViewById(R.id.btn_add_drug_record);
		spinner_drugName_choice = (Spinner)view.findViewById(R.id.spinner_drugName_choice);
		spinner_drugCount_choice = (Spinner)view.findViewById(R.id.spinner_drugCount_choice);
		ll_drug_choice = (LinearLayout)view.findViewById(R.id.ll_drug_choice);
		btn_unit_li = (RadioButton)view.findViewById(R.id.btn_unit_li);
		longClickdialog = new Dialog(getActivity(), R.style.MyDialogStyle);
		longClickdialog.setContentView(R.layout.dialog_longclick);
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_drug_save:
				Toast.makeText(getActivity(), addDrugDialog.et_drug_name.getText().toString().trim(), 
						Toast.LENGTH_SHORT).show();
				addDrugDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
}
