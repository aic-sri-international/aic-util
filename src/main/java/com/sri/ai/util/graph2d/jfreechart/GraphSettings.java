package com.sri.ai.util.graph2d.jfreechart;

public class GraphSettings {
  private float lineWidth = 2.0f;
  private float dotWidth = 4.0f;

  public float getLineWidth() {
    return lineWidth;
  }

  public GraphSettings setLineWidth(float lineWidth) {
    this.lineWidth = lineWidth;
    return this;
  }

  public float getDotWidth() {
    return dotWidth;
  }

  public GraphSettings setDotWidth(float dotWidth) {
    this.dotWidth = dotWidth;
    return this;
  }
}
