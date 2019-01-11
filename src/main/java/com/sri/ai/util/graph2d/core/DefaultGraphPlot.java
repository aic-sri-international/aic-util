package com.sri.ai.util.graph2d.core;

import java.io.File;

import com.sri.ai.util.graph2d.api.GraphPlot;

public class DefaultGraphPlot implements GraphPlot {
  private File imageFile;

  @Override
  public File getImageFile() {
    return imageFile;
  }

  @Override
  public DefaultGraphPlot setImageFile(File imageFile) {
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
