package com.yj.homebasic.activity;


import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.fragment.FragmentCollect;
import com.yj.homebasic.fragment.FragmentHistory;
import com.yj.homebasic.fragment.FragmentMine;
import com.yj.homebasic.fragment.FragmentManage;
import com.yj.homebasic.view.sliding.AbBottomTabView;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author liaoyao
 * 主活动  容纳碎片的活动
 */
public class MainActivity extends FragmentActivity {
	
	private AbBottomTabView mBottomTabView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mBottomTabView = (AbBottomTabView) findViewById(R.id.mBottomTabView);
		
		mBottomTabView.getViewPager().setOffscreenPageLimit(5);
		
		FragmentCollect fragmentCollect = new FragmentCollect();
		FragmentHistory fragmentHistory = new FragmentHistory();
		FragmentManage fragmentManage = new FragmentManage();
		FragmentMine fragmentMine = new FragmentMine();
		List<Fragment> mFragments = new ArrayList<Fragment>();
		mFragments.add(fragmentCollect);
		mFragments.add(fragmentManage);
		mFragments.add(fragmentHistory);
		mFragments.add(fragmentMine);
		
		List<String> tabTexts = new ArrayList<String>();
		tabTexts.add("新建检测");
		tabTexts.add("服药管理");
		tabTexts.add("历史记录");
		tabTexts.add("个人信息");
		
		mBottomTabView.setTabBackgroundResource(R.drawable.tab_press);
		mBottomTabView.addItemViews(tabTexts, mFragments);
		
		mBottomTabView.setTabPadding(2,35, 2, 35);
		
	}
}
