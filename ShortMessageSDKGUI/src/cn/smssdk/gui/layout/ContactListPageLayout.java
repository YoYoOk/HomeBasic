/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，
 * 也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package cn.smssdk.gui.layout;

import android.content.Context;
import android.widget.LinearLayout;

import com.mob.tools.utils.ResHelper;

import cn.smssdk.gui.ContactsListView;
import cn.smssdk.gui.PersonalInfoView;
import cn.smssdk.gui.SearchView;

/**联系人列表页面布局*/
public class ContactListPageLayout extends BasePageLayout {

	public ContactListPageLayout(Context c) {
		super(c, null);
	}

	protected void onCreateContent(LinearLayout parent) {
		// 搜索框
		SearchView searchView = new SearchView(context, true);
		parent.addView(searchView);

//		LayoutInflater inflater = LayoutInflater.from(context);
//		View personalInfoView = inflater.inflate(R.layout.smssdk_personal_info, null);
//		parent.addView(personalInfoView);

		// 我的资料
		parent.addView(PersonalInfoView.create(context));

		//通讯录列表（包括我的资料）rootView
		ContactsListView contactsList = new ContactsListView(context);
		contactsList.setId(ResHelper.getIdRes(context, "clContact"));
		LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);
		contactsList.setLayoutParams(listParams);

		parent.addView(contactsList);
	}

}
