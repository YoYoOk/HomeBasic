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
package com.yj.homebasic.view.sliding;


import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yj.homebasic.activity.R;
import com.yj.homebasic.adapter.AbFragmentPagerAdapter;
  

/**
 * 漏 2012 amsoft.cn
 * 鍚嶇О锛欰bSlidingTabView.java 
 * 鎻忚堪锛氭粦鍔ㄧ殑tab.
 *
 * @author 杩樺涓�姊︿腑
 * @version v1.0
 * @date锛�2013-05-17 涓嬪崍6:46:29
 */
public class AbBottomTabView extends LinearLayout {
	
	/** The context. */
	private Context context;
	
	/** tab鐨勭嚎鎬у竷灞�. */
	private LinearLayout mTabLayout = null;
	
	/** The m view pager. */
	private ViewPager mViewPager;
	
	/** The m listener. */
	private ViewPager.OnPageChangeListener mListener;
	
	/** tab鐨勫垪琛�. */
	private ArrayList<TextView> tabItemList = null;
	
	/** 鍐呭鐨刅iew. */
	private ArrayList<Fragment> pagerItemList = null;
	
	/** tab鐨勬枃瀛�. */
	private List<String> tabItemTextList = null;
	
	/** tab鐨勫浘鏍�. */
	private List<Drawable> tabItemDrawableList = null;
	
	/** 褰撳墠閫変腑缂栧彿. */
	private int mSelectedTabIndex = 0;
	
	/** 鍐呭鍖哄煙鐨勯�傞厤鍣�. */
	private AbFragmentPagerAdapter mFragmentPagerAdapter = null;

	/** tab鐨勮儗鏅�. */
    private int tabBackgroundResource = -1;
    
	/** tab鐨勬枃瀛楀ぇ灏�. */
	private int tabTextSize = 15;
	
	/** tab鐨勬枃瀛楅鑹�. */
	private int tabTextColor = Color.WHITE;
	
	/** tab鐨勯�変腑鏂囧瓧棰滆壊. */
	private int tabSelectColor = Color.WHITE;
	
	/** The m tab click listener. */
	private OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
        	AbTabItemView tabView = (AbTabItemView)view;
            setCurrentItem(tabView.getIndex());
        }
    };
	
	
	/**
	 * Instantiates a new ab bottom tab view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public AbBottomTabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		this.setOrientation(LinearLayout.VERTICAL);
//		this.setBackgroundColor(Color.rgb(255, 255, 255));
		
		mTabLayout = new LinearLayout(context);
		mTabLayout.setOrientation(LinearLayout.HORIZONTAL);
		mTabLayout.setGravity(Gravity.CENTER);
		//鍐呭鐨刅iew鐨勯�傞厤
		mViewPager = new ViewPager(context);
		//鎵嬪姩鍒涘缓鐨刅iewPager,蹇呴』璋冪敤setId()鏂规硶璁剧疆涓�涓猧d
		mViewPager.setId(1985);
		pagerItemList = new ArrayList<Fragment>();
		this.addView(mViewPager,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,0,1));
		addView(mTabLayout, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		//瀹氫箟Tab鏍�
  		tabItemList = new ArrayList<TextView>();
  		tabItemTextList = new ArrayList<String>();
  		tabItemDrawableList = new ArrayList<Drawable>();
		//瑕佹眰蹇呴』鏄疐ragmentActivity鐨勫疄渚�
		if(!(this.context instanceof FragmentActivity)){
			
		}
		
		FragmentManager mFragmentManager = ((FragmentActivity)this.context).getFragmentManager();
		mFragmentPagerAdapter = new AbFragmentPagerAdapter(
				mFragmentManager, pagerItemList);
		mViewPager.setAdapter(mFragmentPagerAdapter);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setOffscreenPageLimit(3);
		
	}

	
	
	/**
	 * The listener interface for receiving myOnPageChange events.
	 * The class that is interested in processing a myOnPageChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addMyOnPageChangeListener<code> method. When
	 * the myOnPageChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see MyOnPageChangeEvent
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener{

		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
		 */
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
		 */
		@Override
		public void onPageSelected(int arg0) {
			setCurrentItem(arg0);
		}
		
	}
	
	/**
	 * 鎻忚堪锛氳缃樉绀哄摢涓�涓�.
	 *
	 * @param index the new current item
	 */
    public void setCurrentItem(int index) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedTabIndex = index;
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final AbTabItemView child = (AbTabItemView)mTabLayout.getChildAt(i);
            final boolean isSelected = (i == index);
            child.setSelected(isSelected);
            if (isSelected) {
            	child.setTabTextColor(tabSelectColor);
            	if(tabBackgroundResource!=-1){
            		 child.setTabBackgroundResource(tabBackgroundResource);
                }
            	if(tabItemDrawableList.size() >= tabCount*2){
             		 child.setTabCompoundDrawables(null, tabItemDrawableList.get(index*2+1), null, null);
             	}else if(tabItemDrawableList.size() >= tabCount){
    			     child.setTabCompoundDrawables(null, tabItemDrawableList.get(index), null, null);
    		    }
            	mViewPager.setCurrentItem(index);
            }else{
            	if(tabBackgroundResource!=-1){
//           		   child.setBackgroundDrawable(null);
            		child.setBackgroundResource(R.drawable.tab_normal);
                }
            	if(tabItemDrawableList.size() >= tabCount*2){
            		child.setTabCompoundDrawables(null, tabItemDrawableList.get(i*2), null, null);
            	}
            	child.setTabTextColor(tabTextColor);
            }
        }
    }
    
    /**
     * 鎻忚堪锛氳缃竴涓閮ㄧ殑鐩戝惉鍣�.
     *
     * @param listener the new on page change listener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }
    
	/* (non-Javadoc)
	 * @see android.widget.LinearLayout#onMeasure(int, int)
	 */
	@Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
	/**
	 * 鎻忚堪锛氳缃崟涓猼ab鐨勮儗鏅�夋嫨鍣�.
	 *
	 * @param resid the new tab background resource
	 */
	public void setTabBackgroundResource(int resid) {
    	tabBackgroundResource = resid;
    }
	
	/**
	 * 鎻忚堪锛氳缃甌ab鐨勮儗鏅�.
	 *
	 * @param resid the new tab layout background resource
	 */
	public void setTabLayoutBackgroundResource(int resid) {
		this.mTabLayout.setBackgroundResource(resid);
	}
    public void setTabBackgroundColor(int color){
    	mTabLayout.setBackgroundColor(color);
    	
    }
	/**
	 * Gets the tab text size.
	 *
	 * @return the tab text size
	 */
	public int getTabTextSize() {
		return tabTextSize;
	}

	/**
	 * Sets the tab text size.
	 *
	 * @param tabTextSize the new tab text size
	 */
	public void setTabTextSize(int tabTextSize) {
		this.tabTextSize = tabTextSize;
	}
	
	/**
	 * 鎻忚堪锛氳缃畉ab鏂囧瓧鐨勯鑹�.
	 *
	 * @param tabColor the new tab text color
	 */
	public void setTabTextColor(int tabColor) {
		this.tabTextColor = tabColor;
	}

	/**
	 * 鎻忚堪锛氳缃�変腑鐨勯鑹�.
	 *
	 * @param tabColor the new tab select color
	 */
	public void setTabSelectColor(int tabColor) {
		this.tabSelectColor = tabColor;
	}
    
    /**
     * 鎻忚堪锛氬垱閫犱竴涓猅ab.
     *
     * @param text the text
     * @param index the index
     */
    private void addTab(String text, int index) {
    	addTab(text,index,null);
    }
    
    /**
     * 鎻忚堪锛氬垱閫犱竴涓猅ab.
     *
     * @param text the text
     * @param index the index
     * @param top the top
     */
    private void addTab(String text, int index,Drawable top) {
   	
    	AbTabItemView tabView = new AbTabItemView(this.context);
       
        if(top!=null){
        	tabView.setTabCompoundDrawables(null, top, null, null);
        }
    	tabView.setTabTextColor(tabTextColor);
    	tabView.setTabTextSize(tabTextSize);
        
        tabView.init(index,text);
        tabItemList.add(tabView.getTextView());
        tabView.setOnClickListener(mTabClickListener);
        mTabLayout.addView(tabView, new LayoutParams(0,LayoutParams.WRAP_CONTENT,1));
    }
    
    /**
     * 鎻忚堪锛歵ab鏈夊彉鍖栧埛鏂�.
     */
    public void notifyTabDataSetChanged() {
        mTabLayout.removeAllViews();
        tabItemList.clear();
        final int count = mFragmentPagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
        	if(tabItemDrawableList.size()>=count*2){
        		addTab(tabItemTextList.get(i), i,tabItemDrawableList.get(i*2));
        	}else if(tabItemDrawableList.size()>=count){
        		addTab(tabItemTextList.get(i), i,tabItemDrawableList.get(i));
        	}else{
        		addTab(tabItemTextList.get(i), i);
        	}
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }
	
	
    /**
     * 鎻忚堪锛氬鍔犱竴缁勫唴瀹逛笌tab.
     *
     * @param tabTexts the tab texts
     * @param fragments the fragments
     */
	public void addItemViews(List<String> tabTexts,List<Fragment> fragments){
		
		tabItemTextList.addAll(tabTexts);
		pagerItemList.addAll(fragments);
		
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	/**
	 * 鎻忚堪锛氬鍔犱竴缁勫唴瀹逛笌tab闄勫甫椤堕儴鍥剧墖.
	 *
	 * @param tabTexts the tab texts
	 * @param fragments the fragments
	 * @param drawables the drawables
	 */
	public void addItemViews(List<String> tabTexts,List<Fragment> fragments,List<Drawable> drawables){
		
		tabItemTextList.addAll(tabTexts);
		pagerItemList.addAll(fragments);
		tabItemDrawableList.addAll(drawables);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	/**
	 * 鎻忚堪锛氬鍔犱竴涓唴瀹逛笌tab.
	 *
	 * @param tabText the tab text
	 * @param fragment the fragment
	 */
	public void addItemView(String tabText,Fragment fragment){
		tabItemTextList.add(tabText);
		pagerItemList.add(fragment);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	/**
	 * 鎻忚堪锛氬鍔犱竴涓唴瀹逛笌tab.
	 *
	 * @param tabText the tab text
	 * @param fragment the fragment
	 * @param drawableNormal the drawable normal
	 * @param drawablePressed the drawable pressed
	 */
	public void addItemView(String tabText,Fragment fragment,Drawable drawableNormal,Drawable drawablePressed){
		tabItemTextList.add(tabText);
		pagerItemList.add(fragment);
		tabItemDrawableList.add(drawableNormal);
		tabItemDrawableList.add(drawablePressed);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	
	/**
	 * 鎻忚堪锛氬垹闄ゆ煇涓�涓�.
	 *
	 * @param index the index
	 */
	public void removeItemView(int index){
		
		mTabLayout.removeViewAt(index);
		pagerItemList.remove(index);
		tabItemList.remove(index);
		tabItemDrawableList.remove(index);
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	/**
	 * 鎻忚堪锛氬垹闄ゆ墍鏈�.
	 */
	public void removeAllItemViews(){
		mTabLayout.removeAllViews();
		pagerItemList.clear();
		tabItemList.clear();
		tabItemDrawableList.clear();
		mFragmentPagerAdapter.notifyDataSetChanged();
		notifyTabDataSetChanged();
	}
	
	/**
	 * 鎻忚堪锛氳幏鍙栬繖涓猇iew鐨刅iewPager.
	 *
	 * @return the view pager
	 */
	public ViewPager getViewPager() {
		return mViewPager;
	}
	
	/**
	 * 鎻忚堪锛氳缃瘡涓猼ab鐨勮竟璺�.
	 *
	 * @param left the left
	 * @param top the top
	 * @param right the right
	 * @param bottom the bottom
	 */
	public void setTabPadding(int left, int top, int right, int bottom) {
		for(int i = 0;i<tabItemList.size();i++){
			TextView tv = tabItemList.get(i);
			tv.setPadding(left, top, right, bottom);
		}
	}
	
}
