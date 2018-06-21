package com.yj.homebasic.utils;

public class JniCall {
	//Start    Java的JNI技术
  	static{
  		System.loadLibrary("CALLC");
  	}
  	public native double[] process_Data(double[] source);//本地方法 对数据滤波处理的算法
  	//End      Java的JNI技术
}
