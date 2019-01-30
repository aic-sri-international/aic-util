package com.sri.ai.util.graph2d.core;

import java.io.File;

import com.sri.ai.util.graph2d.api.GraphPlot;
import java.util.Map;

public class DefaultGraphPlot implements GraphPlot {
  private File imageFile;
  private Map<String, Double> mapRegionNameToValue;

  @Override
  public Map<String, Double> getRegionToValue() {
    return mapRegionNameToValue;
  }

  @Override
  public GraphPlot setMapRegionNameToValue(Map<String, Double> mapRegionNameToValue) {
    this.mapRegionNameToValue = mapRegionNameToValue;
    return this;
  }

  @Override
  public File getImageFile() {
    return imageFile;
  }

  @Override
  public GraphPlot setImageFile(File imageFile) {
    this.imageFile = imageFile;
    return this;
  }

  @Override
  public String toString() {
    return "DefaultGraphPlot{" +
        "imageFile=" + imageFile +
        '}';
  }
}
