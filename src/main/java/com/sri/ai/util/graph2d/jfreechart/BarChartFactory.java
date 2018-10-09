package com.sri.ai.util.graph2d.jfreechart;

import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartFactory {
  private String title;
  private String xAxisLabel;
  private String yAxisLabel;
  private int graphWidth = Constants.GRAPH_WIDTH;
  private int graphHeight = Constants.GRAPH_HEIGHT;
  private DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

  public BarChartFactory() {
  }

  public String getTitle() {
    return title;
  }

  public BarChartFactory setTitle(String title) {
    this.title = title;
    return this;
  }

  public String getxAxisLabel() {
    return xAxisLabel;
  }

  public BarChartFactory setxAxisLabel(String xAxisLabel) {
    this.xAxisLabel = xAxisLabel;
    return this;
  }

  public String getyAxisLabel() {
    return yAxisLabel;
  }

  public BarChartFactory setyAxisLabel(String yAxisLabel) {
    this.yAxisLabel = yAxisLabel;
    return this;
  }

  public int getGraphWidth() {
    return graphWidth;
  }

  public BarChartFactory setGraphWidth(int graphWidth) {
    this.graphWidth = graphWidth;
    return this;
  }

  public int getGraphHeight() {
    return graphHeight;
  }

  public BarChartFactory setGraphHeight(int graphHeight) {
    this.graphHeight = graphHeight;
    return this;
  }

  public BarChartFactory addValue(Number value, Comparable rowKey, Comparable columnKey) {
    dataset.addValue(value, rowKey, columnKey);
    return this;
  }

  public void generate(File file) {
    JFreeChart barChart = ChartFactory.createBarChart(
        title,
        xAxisLabel, yAxisLabel,
        dataset, PlotOrientation.VERTICAL,
        true, false, false);

    try {
      ChartUtils.saveChartAsPNG( file , barChart , graphWidth , graphHeight);
    } catch (IOException e) {
      throw new RuntimeException("Cannot generate chart", e);
    }
  }

  public static void main( String[ ] args ) {
    final String income = "Income";
    final String expense = "Expense";

    final String europe = "Europe";
    final String northAmerica = "North America";
    final String africa = "Africa";

    new BarChartFactory()
        .setTitle("Average Income & Expense by Continent For Age 20")
        .setyAxisLabel("$")
        .addValue(21000, income, europe)
        .addValue(16000, expense, europe)
        .addValue(24000, income, northAmerica)
        .addValue(20000, expense, northAmerica)
        .addValue(5000, income, africa)
        .addValue(4500, expense, africa)
        .generate(new File("SampleBarChart.png"));

  }
}