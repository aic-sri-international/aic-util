package com.sri.ai.util.graph2d.api.graph;

import java.io.File;

public class DefaultGraphPlot implements GraphPlot {
  private File imageFile;


  @Override
  public File getImageFile() {
    return imageFile;
  }

  @Override
  public void setImageFile(File imageFile) {
    this.imageFile = imageFile;
  }
}
