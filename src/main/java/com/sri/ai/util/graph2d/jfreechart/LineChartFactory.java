package com.sri.ai.util.graph2d.jfreechart;

import com.sri.ai.util.graph2d.jfreechart.LineChartFactory.SeriesEntry.LineColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Factory class to generate a line chart using JFreeChart.
 */
public class LineChartFactory {
  private static Map<LineColor, Color> colorMap = new HashMap<>();

  static {
    colorMap.put(LineColor.RED, Color.RED);
    colorMap.put(LineColor.GREEN, Color.GREEN);
    colorMap.put(LineColor.BLUE, Color.BLUE);
    colorMap.put(LineColor.MAGENTA, Color.MAGENTA);
    colorMap.put(LineColor.CYAN, Color.CYAN);
    colorMap.put(LineColor.ORANGE, Color.ORANGE);
    colorMap.put(LineColor.BLACK, Color.BLACK);
  }

  private String title;
  private String xAxisLabel;
  private String yAxisLabel;
  private List<SeriesEntry> seriesEntries = new ArrayList<>();

  public static void main(String[] args) {
    Number[][] expenses = {
      {18, 530 * 20},
      {20, 580 * 20},
      {25, 740 * 20},
      {30, 901 * 20},
      {40, 1300 * 20},
      {50, 2219 * 20},
      {99, 2000 * 20},
    };
    Number[][] income = {
      {18, 550 * 20},
      {20, 630 * 20},
      {25, 800 * 20},
      {30, 1000 * 20},
      {40, 1500 * 20},
      {50, 3000 * 20},
      {99, 3000 * 20},
    };
    Number[][] taxes = {
      {18, 550},
      {20, 630},
      {10, 800},
      {11, 1000},
      {40, 1500},
      {50, 3000},
      {99, 3000},
    };
    new LineChartFactory()
        .setTitle("North America: Average Income & Expenses Per Month")
        .setxAxisLabel("Age (yrs.)")
        .setyAxisLabel("Income ($)")
        .addSeries(new SeriesEntry().setKey("Income").setDataPoints(income))
        .addSeries(new SeriesEntry().setKey("Expenses").setDataPoints(expenses))
        .addSeries(new SeriesEntry().setKey("Taxes").setDataPoints(taxes))
        .generate(new File("SampleLineChart.png"));
  }

  public String getTitle() {
    return title;
  }

  public LineChartFactory setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getxAxisLabel() {
    return xAxisLabel;
  }

  public LineChartFactory setxAxisLabel(String xAxisLabel) {
    this.xAxisLabel = xAxisLabel;
    return this;
  }

  public String getyAxisLabel() {
    return yAxisLabel;
  }

  public LineChartFactory setyAxisLabel(String yAxisLabel) {
    this.yAxisLabel = yAxisLabel;
    return this;
  }

  public LineChartFactory addSeries(SeriesEntry seriesEntry) {
    seriesEntries.add(seriesEntry);
    return this;
  }

  public void generate(File file) {
    XYSeriesCollection dataset = new XYSeriesCollection();
    seriesEntries.forEach(se -> dataset.addSeries(toXYSeries(se)));

    JFreeChart chart = createChart(dataset);
    try {
      ChartUtils.saveChartAsPNG(file, chart, 450, 400);
    } catch (IOException e) {
      throw new RuntimeException("Cannot generate chart", e);
    }
  }

  private XYSeries toXYSeries(SeriesEntry seriesEntry) {
    XYSeries series = new XYSeries(seriesEntry.getKey());
    Arrays.stream(seriesEntry.getDataPoints()).forEach(xy -> series.add(xy[0], xy[1]));
    return series;
  }

  private JFreeChart createChart(XYDataset dataset) {
    Set<LineColor> assignedColors =
        seriesEntries
            .stream()
            .filter((e) -> e.lineColor != null)
            .map((e) -> e.lineColor)
            .collect(Collectors.toSet());

    JFreeChart chart =
        ChartFactory.createXYLineChart(
            null, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

    XYPlot plot = chart.getXYPlot();

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

    for (int i = 0; i < seriesEntries.size(); ++i) {
      SeriesEntry se = seriesEntries.get(i);
      LineColor lc = se.getLineColor();
      if (lc == null) {
        lc = getUnassignedColor(assignedColors);
      }
      assignedColors.add(lc);

      Color color = colorMap.get(lc);
      renderer.setSeriesPaint(i, color);
      renderer.setSeriesStroke(i, new BasicStroke(2.0f));
    }

    plot.setRenderer(renderer);
    plot.setBackgroundPaint(Color.white);

    plot.setRangeGridlinesVisible(true);
    plot.setRangeGridlinePaint(Color.BLACK);

    plot.setDomainGridlinesVisible(true);
    plot.setDomainGridlinePaint(Color.BLACK);

    chart.getLegend().setFrame(BlockBorder.NONE);

    chart.setTitle(new TextTitle(title, new Font("Serif", Font.BOLD, 18)));

    return chart;
  }

  private LineColor getUnassignedColor(Set<LineColor> assignedColors) {
    for (LineColor lc : LineColor.values()) {
      if (!assignedColors.contains(lc)) {
        return lc;
      }
    }
    throw new IllegalArgumentException(
        "Colors exceeded Need to create more colors for LineColor enum");
  }

  public static class SeriesEntry {
    private String key;
    private Number[][] dataPoints;
    private LineColor lineColor;

    public String getKey() {
      return key;
    }

    public SeriesEntry setKey(String key) {
      this.key = key;
      return this;
    }

    public Number[][] getDataPoints() {
      return dataPoints;
    }

    public SeriesEntry setDataPoints(Number[][] dataPoints) {
      this.dataPoints = dataPoints;
      return this;
    }

    public LineColor getLineColor() {
      return lineColor;
    }

    public SeriesEntry setLineColor(LineColor lineColor) {
      this.lineColor = lineColor;
      return this;
    }

    public enum LineColor {
      RED,
      GREEN,
      BLUE,
      CYAN,
      MAGENTA,
      ORANGE,
      BLACK
    }
  }
}
