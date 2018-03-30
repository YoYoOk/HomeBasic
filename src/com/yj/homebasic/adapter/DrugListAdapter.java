package com.yj.homebasic.adapter;

import java.util.List;

import com.yj.homebasic.activity.R;
import com.yj.homebasic.domain.Drug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DrugListAdapter extends ArrayAdapter<Drug>{
	
	private int resourceId;	
	
	public DrugListAdapter(Context context, int resourceId, List<Drug> objects) {
		super(context, resourceId, objects);
		this.resourceId = resourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Drug drug = getItem(position); // 获取当前项的Fruit实例
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView drugName = (TextView) view.findViewById(R.id.tv_item_drug_name);
		drugName.setText(drug.getDrugName());
		return view;
	}

}
