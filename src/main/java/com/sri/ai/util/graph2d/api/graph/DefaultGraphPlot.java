package com.sri.ai.util.graph2d.api.graph;

import java.io.File;

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
