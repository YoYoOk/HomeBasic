package com.yj.homebasic.fragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.yj.homebasic.activity.R;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FragmentHistory extends Fragment {
	private LinearLayout ll_drug_record, ll_result_record;
	//检测记录结果的显示相关定义
	private List<String> xTextLabel_result;
	private List<Double> xList_result, yList_result;
	private XYSeries xyseries_result, xyseries_up, xyseries_down;// 上下平行线
	private XYMultipleSeriesDataset dataset_result;
	private GraphicalView chartview_result;
	private XYMultipleSeriesRenderer renderer_result;
	private XYSeriesRenderer datarenderer_result, datarenderer_up, datarenderer_down;
	//服药记录的显示相关定义
	private List<Double> yList_drug;
	private XYSeries xyseries_drug;
	private XYMultipleSeriesDataset dataset_drug;
	private GraphicalView chartview_drug;
	private XYMultipleSeriesRenderer renderer_drug;
	private XYSeriesRenderer datarenderer_drug;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);// 设置菜单栏为true
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, null);
		initWidget(view);
		prepareDataResult();
		initDrawResult();
		prepareDataDrug();
		initDrawDrug();
		addDataSeries();
		return view;
	}

	private void initWidget(View view) {
		ll_drug_record = (LinearLayout) view.findViewById(R.id.ll_drug_record);
		ll_result_record = (LinearLayout) view.findViewById(R.id.ll_result_record);
	}
	private void prepareDataResult(){
		xTextLabel_result = new ArrayList<String>();
		xList_result = new ArrayList<Double>();
		yList_result = new ArrayList<Double>();
//		SimpleDateFormat format = new SimpleDateFormat("");
		xList_result.add(1d);
		xList_result.add(2d);
		xList_result.add(3d);
		xList_result.add(4d);
		xList_result.add(5d);
		xList_result.add(6d);
		xList_result.add(7d);
		xTextLabel_result.add("2018-05-16上");
		xTextLabel_result.add("2018-05-16中");
		xTextLabel_result.add("2018-05-16晚");
		xTextLabel_result.add("2018-05-17上");
		xTextLabel_result.add("2018-05-17中");
		xTextLabel_result.add("2018-05-17晚");
		xTextLabel_result.add("2018-05-18上");
		yList_result.add(3.6);
		yList_result.add(5.5d);
		yList_result.add(6.7d);
		yList_result.add(9.8d);
		yList_result.add(5.9d);
		yList_result.add(4.2d);
		yList_result.add(11.7d);
	}
	private void initDrawResult(){
		renderer_result = new XYMultipleSeriesRenderer();
//		renderer_result.setChartTitle("结果记录");
		renderer_result.setXTitle("时间");// X轴标题
		renderer_result.setYTitle("R值(min)");// Y轴标题
		renderer_result.setXAxisMin(0.8);// X最小值
//		renderer_result.setXAxisMax(xMax);// X最大值
		renderer_result.setYAxisMin(0d);// Y最小值
		renderer_result.setYAxisMax(15d);// Y最小值
		renderer_result.setAxesColor(Color.BLACK);// X轴颜色
		renderer_result.setLabelsColor(Color.BLACK);// Y轴颜色
//		renderer_result.setAxisTitleTextSize(XTitleTextSize); // 坐标轴标题字体大小：16
//		renderer_result.setChartTitleTextSize(ChartTitleTextSize); // 图表标题字体大小：20
		renderer_result.setLabelsTextSize(20); // 轴标签字体大小：10
		renderer_result.setLegendTextSize(15); // 图例字体大小：15
		renderer_result.setXLabels(0);// 设置X轴显示的刻度标签的个数 设置成0 循环添加
//		renderer_result.addTextLabel(x, text);
		renderer_result.setYLabels(10);// 设置Y轴显示的刻度标签的个数
		renderer_result.setXLabelsColor(Color.WHITE);
		renderer_result.setYLabelsColor(0, Color.WHITE);
		
		renderer_result.setShowGrid(true); // 设置网格显示
		renderer_result.setPointSize(12f);//设置点的大小
		renderer_result.setMarginsColor(Color.argb(0, 0xff, 0, 0));//设置4边留白透明
		dataset_result = new XYMultipleSeriesDataset();
		datarenderer_result = new XYSeriesRenderer();
		datarenderer_result.setDisplayChartValues(true);
		xyseries_result = new XYSeries("结果");
		// 2
		for(int i = 0; i < xList_result.size(); i++){
			xyseries_result.add(xList_result.get(i), yList_result.get(i));
			renderer_result.addXTextLabel(xList_result.get(i), xTextLabel_result.get(i));
		}
		// 3
		dataset_result.addSeries(0, xyseries_result);
		datarenderer_result.setColor(Color.WHITE);
		datarenderer_result.setChartValuesTextSize(25);
		datarenderer_result.setLineWidth(3);
		datarenderer_result.setPointStyle(PointStyle.CIRCLE);
		datarenderer_result.setChartValuesSpacing(6f);
		datarenderer_result.setFillPoints(true);
		// 4
		renderer_result.addSeriesRenderer(datarenderer_result);
		// 5
		chartview_result = ChartFactory.getLineChartView(getActivity(), dataset_result, renderer_result);
		ll_result_record.addView(chartview_result, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// 绘出两条红线 分别用新的XYSeriesRenderer要不然会报错
		xyseries_up = new XYSeries("低凝");
		xyseries_down = new XYSeries("高凝");
		xyseries_up.add(xList_result.get(0), 10);
		xyseries_up.add(xList_result.get(xList_result.size() - 1), 10);
		xyseries_down.add(xList_result.get(0), 5);
		xyseries_down.add(xList_result.get(xList_result.size() - 1), 5);
		dataset_result.addSeries(1, xyseries_up);
		dataset_result.addSeries(2, xyseries_down);
		datarenderer_up = new XYSeriesRenderer();
		datarenderer_up.setLineWidth(4f);
		datarenderer_up.setPointStyle(PointStyle.POINT);
		datarenderer_up.setStroke(BasicStroke.DASHED);
		datarenderer_up.setChartValuesTextAlign(Align.CENTER);
		datarenderer_down = new XYSeriesRenderer();
		datarenderer_up.setColor(Color.parseColor("#79DEDB"));
		datarenderer_down = datarenderer_up;
		renderer_result.addSeriesRenderer(datarenderer_up);
		renderer_result.addSeriesRenderer(datarenderer_down);
		chartview_result.repaint();
	}
	private void prepareDataDrug(){
		yList_drug = new ArrayList<Double>();
		yList_drug.add(1d);
		yList_drug.add(1.5);
		yList_drug.add(1.5d);
		yList_drug.add(1.5d);
		yList_drug.add(1d);
		yList_drug.add(2d);
	}
	private void initDrawDrug(){
		renderer_drug = new XYMultipleSeriesRenderer();
//		renderer_drug.setChartTitle("结果记录");
		renderer_drug.setXTitle("时间");// X轴标题
		renderer_drug.setYTitle("服药量");// Y轴标题
		renderer_drug.setXAxisMin(0.5);// X最小值
//		renderer_drug.setXAxisMax(xMax);// X最大值
		renderer_drug.setYAxisMin(0d);// Y最小值
		renderer_drug.setYAxisMax(3d);// Y最小值
		renderer_drug.setAxesColor(Color.BLACK);// X轴颜色
		renderer_drug.setLabelsColor(Color.BLACK);// Y轴颜色
//		renderer_drug.setAxisTitleTextSize(XTitleTextSize); // 坐标轴标题字体大小：16
//		renderer_drug.setChartTitleTextSize(ChartTitleTextSize); // 图表标题字体大小：20
		renderer_drug.setLabelsTextSize(20); // 轴标签字体大小：10
		renderer_drug.setLegendTextSize(15); // 图例字体大小：15
		renderer_drug.setXLabels(0);// 设置X轴显示的刻度标签的个数 设置成0 循环添加
//		renderer_drug.addTextLabel(x, text);
		renderer_drug.setYLabels(10);// 设置Y轴显示的刻度标签的个数
		renderer_drug.setXLabelsColor(Color.WHITE);
		renderer_drug.setYLabelsColor(0, Color.WHITE);
		renderer_drug.setBarSpacing(0.2);
		renderer_drug.setBarWidth(30f);
		renderer_drug.setShowGrid(true); // 设置网格显示
		renderer_drug.setPointSize(12f);//设置点的大小
		renderer_drug.setMarginsColor(Color.argb(0, 0xff, 0, 0));//设置4边留白透明
		dataset_drug = new XYMultipleSeriesDataset();
		datarenderer_drug = new XYSeriesRenderer();
		datarenderer_drug.setDisplayChartValues(true);
		xyseries_drug= new XYSeries("华法林钠片");
		// 2
		for(int i = 0; i < yList_drug.size(); i++){
			xyseries_drug.add(xList_result.get(i), yList_drug.get(i));
			renderer_drug.addXTextLabel(xList_result.get(i), xTextLabel_result.get(i));
		}
		// 3
		dataset_drug.addSeries(0, xyseries_drug);
		datarenderer_drug.setColor(Color.parseColor("#8FB2F6"));
		// 4
		renderer_drug.addSeriesRenderer(datarenderer_drug);
		// 5
		chartview_drug = ChartFactory.getBarChartView(getActivity(), dataset_drug, renderer_drug, Type.DEFAULT);
		ll_drug_record.addView(chartview_drug, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// 绘出两条红线 分别用新的XYSeriesRenderer要不然会报错
	}
	private void addDataSeries(){
		XYSeries tempSeries = new XYSeries("血栓心脉宁片");
		tempSeries.add(1, 2);
		tempSeries.add(2, 2);
//		tempSeries.add(3, 2);
		tempSeries.add(4, 2);
		tempSeries.add(5, 1);
		tempSeries.add(6, 2);
		dataset_drug.addSeries(1, tempSeries);
		XYSeriesRenderer tempRender = new XYSeriesRenderer();
		tempRender.setDisplayChartValues(true);
		tempRender.setColor(Color.parseColor("#4690E6"));
		renderer_drug.addSeriesRenderer(tempRender);
		XYSeries tempSeries1 = new XYSeries("#6FC5F5");
		tempSeries1.add(1, 2);
		tempSeries1.add(3, 1);
		tempSeries1.add(5, 2);
		dataset_drug.addSeries(2, tempSeries1);
		XYSeriesRenderer tempRender1 = new XYSeriesRenderer();
		tempRender1.setDisplayChartValues(true);
		tempRender1.setColor(Color.parseColor("#79DEDB"));
		renderer_drug.addSeriesRenderer(tempRender1);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
