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
	//����¼�������ʾ��ض���
	private List<String> xTextLabel_result;
	private List<Double> xList_result, yList_result;
	private XYSeries xyseries_result, xyseries_up, xyseries_down;// ����ƽ����
	private XYMultipleSeriesDataset dataset_result;
	private GraphicalView chartview_result;
	private XYMultipleSeriesRenderer renderer_result;
	private XYSeriesRenderer datarenderer_result, datarenderer_up, datarenderer_down;
	//��ҩ��¼����ʾ��ض���
	private List<Double> yList_drug;
	private XYSeries xyseries_drug;
	private XYMultipleSeriesDataset dataset_drug;
	private GraphicalView chartview_drug;
	private XYMultipleSeriesRenderer renderer_drug;
	private XYSeriesRenderer datarenderer_drug;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);// ���ò˵���Ϊtrue
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
		xTextLabel_result.add("2018-05-16��");
		xTextLabel_result.add("2018-05-16��");
		xTextLabel_result.add("2018-05-16��");
		xTextLabel_result.add("2018-05-17��");
		xTextLabel_result.add("2018-05-17��");
		xTextLabel_result.add("2018-05-17��");
		xTextLabel_result.add("2018-05-18��");
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
//		renderer_result.setChartTitle("�����¼");
		renderer_result.setXTitle("ʱ��");// X�����
		renderer_result.setYTitle("Rֵ(min)");// Y�����
		renderer_result.setXAxisMin(0.8);// X��Сֵ
//		renderer_result.setXAxisMax(xMax);// X���ֵ
		renderer_result.setYAxisMin(0d);// Y��Сֵ
		renderer_result.setYAxisMax(15d);// Y��Сֵ
		renderer_result.setAxesColor(Color.BLACK);// X����ɫ
		renderer_result.setLabelsColor(Color.BLACK);// Y����ɫ
//		renderer_result.setAxisTitleTextSize(XTitleTextSize); // ��������������С��16
//		renderer_result.setChartTitleTextSize(ChartTitleTextSize); // ͼ����������С��20
		renderer_result.setLabelsTextSize(20); // ���ǩ�����С��10
		renderer_result.setLegendTextSize(15); // ͼ�������С��15
		renderer_result.setXLabels(0);// ����X����ʾ�Ŀ̶ȱ�ǩ�ĸ��� ���ó�0 ѭ�����
//		renderer_result.addTextLabel(x, text);
		renderer_result.setYLabels(10);// ����Y����ʾ�Ŀ̶ȱ�ǩ�ĸ���
		renderer_result.setXLabelsColor(Color.WHITE);
		renderer_result.setYLabelsColor(0, Color.WHITE);
		
		renderer_result.setShowGrid(true); // ����������ʾ
		renderer_result.setPointSize(12f);//���õ�Ĵ�С
		renderer_result.setMarginsColor(Color.argb(0, 0xff, 0, 0));//����4������͸��
		dataset_result = new XYMultipleSeriesDataset();
		datarenderer_result = new XYSeriesRenderer();
		datarenderer_result.setDisplayChartValues(true);
		xyseries_result = new XYSeries("���");
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
		// ����������� �ֱ����µ�XYSeriesRendererҪ��Ȼ�ᱨ��
		xyseries_up = new XYSeries("����");
		xyseries_down = new XYSeries("����");
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
//		renderer_drug.setChartTitle("�����¼");
		renderer_drug.setXTitle("ʱ��");// X�����
		renderer_drug.setYTitle("��ҩ��");// Y�����
		renderer_drug.setXAxisMin(0.5);// X��Сֵ
//		renderer_drug.setXAxisMax(xMax);// X���ֵ
		renderer_drug.setYAxisMin(0d);// Y��Сֵ
		renderer_drug.setYAxisMax(3d);// Y��Сֵ
		renderer_drug.setAxesColor(Color.BLACK);// X����ɫ
		renderer_drug.setLabelsColor(Color.BLACK);// Y����ɫ
//		renderer_drug.setAxisTitleTextSize(XTitleTextSize); // ��������������С��16
//		renderer_drug.setChartTitleTextSize(ChartTitleTextSize); // ͼ����������С��20
		renderer_drug.setLabelsTextSize(20); // ���ǩ�����С��10
		renderer_drug.setLegendTextSize(15); // ͼ�������С��15
		renderer_drug.setXLabels(0);// ����X����ʾ�Ŀ̶ȱ�ǩ�ĸ��� ���ó�0 ѭ�����
//		renderer_drug.addTextLabel(x, text);
		renderer_drug.setYLabels(10);// ����Y����ʾ�Ŀ̶ȱ�ǩ�ĸ���
		renderer_drug.setXLabelsColor(Color.WHITE);
		renderer_drug.setYLabelsColor(0, Color.WHITE);
		renderer_drug.setBarSpacing(0.2);
		renderer_drug.setBarWidth(30f);
		renderer_drug.setShowGrid(true); // ����������ʾ
		renderer_drug.setPointSize(12f);//���õ�Ĵ�С
		renderer_drug.setMarginsColor(Color.argb(0, 0xff, 0, 0));//����4������͸��
		dataset_drug = new XYMultipleSeriesDataset();
		datarenderer_drug = new XYSeriesRenderer();
		datarenderer_drug.setDisplayChartValues(true);
		xyseries_drug= new XYSeries("��������Ƭ");
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
		// ����������� �ֱ����µ�XYSeriesRendererҪ��Ȼ�ᱨ��
	}
	private void addDataSeries(){
		XYSeries tempSeries = new XYSeries("Ѫ˨������Ƭ");
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
