package night.app.services;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;


public class ChartBuilder {
    private void initStyle(BarLineChartBase<?> chart) {
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setGranularityEnabled(true);

    }

    private void setData(BarChart chart, Integer[] data) {
        ArrayList<BarEntry> dataEntries = new ArrayList<>();

        for (int i=0; i < data.length; i++) {
            if (data[i] == null) continue;
            dataEntries.add(new BarEntry(i, data[i]));
        }

        BarDataSet dataSet = new BarDataSet(dataEntries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.parseColor("#441E9F"));

        chart.getAxisLeft().setLabelCount(4, true);

        chart.setData(new BarData(dataSet));
    }

    private void setData(LineChart chart, Integer[] data) {
        ArrayList<Entry> dataEntries = new ArrayList<>();

        for (int i=0; i < data.length; i++) {
            if (data[i] == null) continue;
            dataEntries.add(new Entry(i, data[i]));
        }

        LineDataSet dataSet = new LineDataSet(dataEntries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.parseColor("#441E9F"));

        chart.setData(new LineData(dataSet, new LineDataSet(dataEntries, "")));
    }

    public ChartBuilder(BarChart chart, String[] xLabel, Integer[] yRange, Integer[] data) {
        initStyle(chart);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));
        chart.getAxisLeft().setAxisMinimum(yRange[0]);
        chart.getAxisLeft().setAxisMaximum(yRange[1]);
        chart.getAxisLeft().setLabelCount(4, true);

        setData(chart, data);

        chart.invalidate();
    }

    public ChartBuilder(LineChart chart, String[] xLabel, Integer[] yRange, Integer[] data) {
        initStyle(chart);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));

        chart.getAxisLeft().setAxisMinimum(yRange[0]);
        chart.getAxisLeft().setAxisMaximum(yRange[1]);
        chart.getAxisLeft().setLabelCount(3, true);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0.0f) return "Awake";

                if (value == 50.0f) return "Light sleep";

                if (value == 100.0f) return "Deep sleep";

                return "Null";
            }
        });

        setData(chart, data);

        chart.invalidate();
    }
}

