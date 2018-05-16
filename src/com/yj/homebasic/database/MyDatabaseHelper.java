package com.yj.homebasic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author liaoyao
 * 数据库创建，本应封装，但是不想弄了 就这样吧
 */
public class MyDatabaseHelper extends SQLiteOpenHelper{
	public static final String CREATE_USER = "create table user("
			+ "id integer primary key autoincrement,"
			+ "username text,"//手机号即为用户名
			+ "phone text,"
			+ "password text,"
			+ "gender integer,"
			+ "age integer,"
			+ "height integer,"
			+ "weight integer)";
	
	public static final String CREATE_DRUG ="create table drug("
			+ "id integer primary key autoincrement,"
			+ "name text,"
			+ "descri text,"
			+ "dosage integer)";
	
	public static final String CREATE_MEDICINE ="create table medicine("
			+ "id integer primary key autoincrement,"
			+ "name text,"
			+ "record text,"
			+ "createTime datetime)";
	
	public static final String CREATE_RECORD ="create table record("
			+ "id integer primary key autoincrement,"
			+ "name text,"
			+ "value text,"
			+ "result text"
			+ "createTime datetime)";
	//创建一个表的语句
	private Context mContext;
	public MyDatabaseHelper(Context context,String name,CursorFactory factory,int version){
		super(context,name,factory,version);
		mContext = context;
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		/*
		 * 1、在第一次打开数据库的时候               会执行
		 * 2、在清除数据之后再次运行  -->打开数据库   会执行
		 * 3、没有清除数据，         不会执行
		 * 4、数据库升级的时候    不会执行
		 */
		db.execSQL(CREATE_USER);//执行创建用户表的语句
		db.execSQL(CREATE_DRUG);//执行创建患者信息表的语句
		db.execSQL(CREATE_MEDICINE);//执行创建样本信息的语句
		db.execSQL(CREATE_RECORD);//执行创建检测记录表的语句
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * 1、第一次创建数据库的时候  不执行
		 * 2、清除数据后再次运行（相当于第一次创建）  不执行
		 * 3、数据库已经存在，而且版本升高的时候，    调用
		 */
		//清除数据库 重新创建
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists drug");
		db.execSQL("drop table if exists medicine");
		db.execSQL("drop table if exists record");
		onCreate(db);
		//解决数据库更新问题 只需加一个switch判断  oldVsersion
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onDowngrade(db, oldVersion, newVersion);
		/**
		* 执行数据库的降级操作
		* 1、只有新版本比旧版本低的时候才会执行
		* 2、如果不执行降级操作，会抛出异常
		*/
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists drug");
		db.execSQL("drop table if exists medicine");
		db.execSQL("drop table if exists record");//降级
		onCreate(db);
	}
}
