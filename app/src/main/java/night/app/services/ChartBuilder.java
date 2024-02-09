package night.app.services;

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
import java.util.List;

import night.app.data.Theme;


public class ChartBuilder {
    Theme theme;

    private void initStyle(BarLineChartBase<?> chart) {
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(theme.onSurface);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setGranularityEnabled(true);
        yAxis.setTextColor(theme.onSurface);
    }

    private void setData(BarChart chart, Integer[] data) {
        ArrayList<BarEntry> dataEntries = new ArrayList<>();

        for (int i=0; i < data.length; i++) {
            if (data[i] == null) continue;
            dataEntries.add(new BarEntry(i, data[i]));
        }

        BarDataSet dataSet = new BarDataSet(dataEntries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(theme.accent);

        chart.getAxisLeft().setLabelCount(4, true);

        chart.setData(new BarData(dataSet));
    }

    private void setData(LineChart chart, List<Integer> data) {
        ArrayList<Entry> dataEntries = new ArrayList<>();

        for (int i=0; i < data.size(); i++) {
            if (data.get(i) == null) continue;
            dataEntries.add(new Entry(i, data.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(dataEntries, "");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(theme.accent);
        dataSet.setValueTextColor(theme.onPrimaryVariant);
        dataSet.setHighlightEnabled(false);
        chart.setData(new LineData(dataSet));
    }

    public ChartBuilder(BarChart chart, Theme theme, List<String> xLabel, Integer[] data) {
        this.theme = theme;

        initStyle(chart);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabel));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(3);
        yAxis.setAxisMaximum(12);

        yAxis.setLabelCount(4, true);
        yAxis.setTextColor(theme.onSurface);

        setData(chart, data);
        chart.invalidate();
    }

    public ChartBuilder(LineChart chart, Theme theme, List<String> xLabel, List<Integer> data) {
        this.theme = theme;

        initStyle(chart);

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
                if (value == 100.0f) return "Deep sleep";

                return "";
            }
        });

        setData(chart, data);
        chart.invalidate();
    }
}

