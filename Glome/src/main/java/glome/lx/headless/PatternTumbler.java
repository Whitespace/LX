package glome.lx.headless;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.modulator.DampedParameter;
import heronarts.lx.modulator.LXModulator;
import heronarts.lx.modulator.SinLFO;
import heronarts.lx.modulator.SawLFO;
import heronarts.lx.parameter.LXParameter;
import heronarts.lx.parameter.LXParameter.Polarity;
import heronarts.lx.parameter.CompoundParameter;

public class PatternTumbler extends LXPattern {
  public String getAuthor() {
    return "Mark C. Slee";
  }
  
  private LXModulator azimuthRotation = startModulator(new SawLFO(0, 1, 15000).randomBasis());
  private LXModulator thetaRotation = startModulator(new SawLFO(0, 1, 13000).randomBasis());
  
  public PatternTumbler(LX lx) {
    super(lx);
  }
    
  public void run(double deltaMs) {
    double azimuthRotation = this.azimuthRotation.getValue();
    double thetaRotation = this.thetaRotation.getValue();
    for (LXPoint point: model.points) {
      double tri1 = LXUtils.tri(azimuthRotation + point.azimuth / Math.PI);
      double tri2 = LXUtils.tri(thetaRotation + (Math.PI + point.theta) / Math.PI);
      double tri = Math.max(tri1, tri2);
      setColor(point.index, LXColor.gray(100 * tri * tri));
    }
  }
}
