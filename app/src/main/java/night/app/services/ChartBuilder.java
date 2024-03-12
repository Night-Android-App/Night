package night.app.services;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import night.app.data.Theme;


public class ChartBuilder <T extends  BarLineChartBase<?>> {
    T chart;
    DataSet<?> dataSet;

    private void initStyle() {
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(true);
        chart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(new String[] {}));
        chart.getAxisRight().setDrawGridLines(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setGranularityEnabled(true);
    }

    private void setBarData(Integer[] data) {
        ArrayList<BarEntry> dataEntries = new ArrayList<>();

        for (int i=0; i < 7; i++) {
            if (i >= data.length) {
                dataEntries.add(new BarEntry(i, 0));
                continue;
            }

            if (data[i] == null) {
                dataEntries.add(new BarEntry(i, 0));
                continue;
            }

            dataEntries.add(new BarEntry(i, data[i]));
        }

        BarDataSet dataSet = new BarDataSet(dataEntries, "");
        dataSet.setDrawValues(false);

        chart.getAxisLeft().setLabelCount(4, true);

        this.dataSet = dataSet;
    }

    private void setLineData(Integer[] data) {
        ArrayList<Entry> dataEntries = new ArrayList<>();

        for (int i=0; i < data.length; i++) {
            if (data[i] == null) dataEntries.add(new Entry(i, 0));
            dataEntries.add(new Entry(i, data[i]));
        }

        LineDataSet dataSet = new LineDataSet(dataEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        dataSet.setHighlightEnabled(false);

        this.dataSet = dataSet;
    }

    public ChartBuilder<T> setTheme(Theme theme) {
        dataSet.setValueTextColor(theme.getOnPrimaryVariant());
        dataSet.setColor(theme.getAccent());

        chart.getAxisLeft().setTextColor(theme.getOnSurface());
        chart.getAxisLeft().setTextColor(theme.getOnSurface());

        chart.getXAxis().setTextColor(theme.getOnSurface());
        return this;
    }


    public ChartBuilder(T chart, Integer[] data) {
        this.chart = chart;

        initStyle();
        String[] xLabel = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(12);

        yAxis.setLabelCount(4, true);

        setBarData(data);
    }

    public ChartBuilder(T chart, String[] xLabel, Integer[] data) {
        this.chart = chart;

        initStyle();

        if (xLabel == null) xLabel = new String[] {};
        if (data == null) data = new Integer[] {};

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(100);

        yAxis.setLabelCount(3, true);
        yAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0.0f) return "Awake";
                if (value == 50.0f) return "Light sleep";
                return "Deep sleep";
            }
        });

        setLineData(data);
    }

    public void invalidate() {
        if (chart instanceof LineChart) {
            ((LineChart) chart).setData(new LineData((LineDataSet) dataSet));
        }
        else {
            ((BarChart) chart).setData(new BarData((BarDataSet) dataSet));
        }
        chart.invalidate();
    }
}

