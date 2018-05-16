package com.yj.homebasic.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.yj.homebasic.domain.MyPointF;

import android.os.Environment;
import android.util.Log;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/*
 * 数据导入导出excel  或者csv操作  csv操作比excel快速
 */
public class SaveActionUtils {
	// 获取Excel文件夹
	public static String getExcelDir() {
		// SD卡指定文件夹//得到的是/mnt/sdcard/ 即SD卡的根目录
		String sdcardPath = Environment.getExternalStorageDirectory().toString();
		// File.separator 与系统有关的默认分隔符
		File dir = new File(sdcardPath + File.separator + "Excel" + File.separator + "Scan");

		if (dir.exists()) {
			return dir.toString();
			
		} else {
			// 创建此抽象路径名指定的目录，包括所有必需但不存在的父目录。
			dir.mkdirs();
			Log.d("BAG", "保存路径不存在,");
			return dir.toString();
		}
	}

	public static void exportCSV(File file,List list){
		//现将list的数据转换成String的格式
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.size() - 1; i++){
			sb.append(list.get(i) + ",");
		}
		sb.append(list.get(list.size() - 1));
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file,true);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			bw.append(sb.toString()).append("\r");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
		}
	}
	public static void exportCSV(File file, MyPointF pointf){
		//现将list的数据转换成String的格式
		String str = pointf.x + "," + pointf.y;
		FileOutputStream out = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			out = new FileOutputStream(file,true);
			osw = new OutputStreamWriter(out);
			bw = new BufferedWriter(osw);
			bw.append(str).append("\r");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            }
		}
	}
	
	  /* 导入
     * 
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(File file){
        List<String> dataList=new ArrayList<String>();
        
        BufferedReader br=null;
        try { 
            br = new BufferedReader(new FileReader(file));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                dataList.add(line);
            }
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        return dataList;
    }
    
    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
    	File file = new File(filePath);
        if (file.isFile() && file.exists()) {
        return file.delete();
        }
        return false;
    }
}
