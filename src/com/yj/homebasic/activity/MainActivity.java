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
 * ���  ������Ƭ�Ļ
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
		tabTexts.add("�½����");
		tabTexts.add("��ҩ����");
		tabTexts.add("��ʷ��¼");
		tabTexts.add("������Ϣ");
		
		mBottomTabView.setTabBackgroundResource(R.drawable.tab_press);
		mBottomTabView.addItemViews(tabTexts, mFragments);
		
		mBottomTabView.setTabPadding(2,35, 2, 35);
		
	}
}
