package glome.lx.headless;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.parameter.CompoundParameter;

public class AxisPlanesPattern extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }
  
  public final CompoundParameter xSpeed = new CompoundParameter("XSpd", 19000, 31000, 5000).setDescription("Speed of motion on X-axis");
  public final CompoundParameter ySpeed = new CompoundParameter("YSpd", 13000, 31000, 5000).setDescription("Speed of motion on Y-axis");
  public final CompoundParameter zSpeed = new CompoundParameter("ZSpd", 17000, 31000, 5000).setDescription("Speed of motion on Z-axis");
  
  public final CompoundParameter xSize = new CompoundParameter("XSize", .1, .05, .3).setDescription("Size of X scanner");
  public final CompoundParameter ySize = new CompoundParameter("YSize", .1, .05, .3).setDescription("Size of Y scanner");
  public final CompoundParameter zSize = new CompoundParameter("ZSize", .1, .05, .3).setDescription("Size of Z scanner");
  
  private final LXModulator xPos = startModulator(new SinLFO(0, 1, this.xSpeed).randomBasis());
  private final LXModulator yPos = startModulator(new SinLFO(0, 1, this.ySpeed).randomBasis());
  private final LXModulator zPos = startModulator(new SinLFO(0, 1, this.zSpeed).randomBasis());
  
  public AxisPlanesPattern(LX lx) {
    super(lx);
    addParameter("xSpeed", this.xSpeed);
    addParameter("ySpeed", this.ySpeed);
    addParameter("zSpeed", this.zSpeed);
    addParameter("xSize", this.xSize);
    addParameter("ySize", this.ySize);
    addParameter("zSize", this.zSize);
  }
  
  public void run(double deltaMs) {
    float xPos = this.xPos.getValuef();
    float yPos = this.yPos.getValuef();
    float zPos = this.zPos.getValuef();
    float xFalloff = 100 / this.xSize.getValuef();
    float yFalloff = 100 / this.ySize.getValuef();
    float zFalloff = 100 / this.zSize.getValuef();
    
    for (LXPoint point : model.points) {
      float b = Math.max(Math.max(
        100 - xFalloff * Math.abs(point.xn - xPos),
        100 - yFalloff * Math.abs(point.yn - yPos)),
        100 - zFalloff * Math.abs(point.zn - zPos)
      );
      setColor(point.index, LXColor.gray(Math.max(0, b)));
    }
  }
}
