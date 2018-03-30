/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yj.homebasic.adapter;

import java.util.ArrayList;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.View;

public class AbFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> mFragmentList = null;

	public AbFragmentPagerAdapter(FragmentManager mFragmentManager,ArrayList<Fragment> fragmentList) {
		super(mFragmentManager);
		mFragmentList = fragmentList;
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public Fragment getItem(int position) {

		Fragment fragment = null;
		if (position < mFragmentList.size()){
			fragment = mFragmentList.get(position);
		}else{
			fragment = mFragmentList.get(0);
		}
		return fragment;

	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
