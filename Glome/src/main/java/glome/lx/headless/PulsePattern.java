package glome.lx.headless;

import heronarts.lx.color.LXColor;
import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.model.LXPoint;
import heronarts.lx.LXUtils;

public class PulsePattern extends LXPattern {
  // public final CompoundParameter duration = new CompoundParameter("duration", 1000, 100, 5000)
  //   .setDescription("duration (ms)");

  // public final CompoundParameter thickness = new CompoundParameter("thickness", 10, 1, 80)
  //   .setDescription("thickness");

  public PulsePattern(LX lx) {
    super(lx);
    // addParameter("duration", this.duration);
    // addParameter("thickness", this.thickness);
  }

  private final int black = LXColor.hsb(0, 0, 0);
  private double previous_t = 0;
  private LXPoint center = model.points[(int)LXUtils.random(0, model.points.length - 1)];
  private double jitter = LXUtils.random(0, 500);

  public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
    return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
  }

  public void run(double deltaMs) {
    // constants
    double radiusFudgeFactor = 0.05; // radius is in global units, so we need to scale it so a pulse travels the whole glome in 1000ms at 1 speed by default

    // parameters
    // double pulseDuration = this.duration.getValuef(); // + jitter;
    // double pulseThickness = this.thickness.getValuef();
    double pulseDuration = 5000.0; // + jitter;
    double pulseThickness = 10.0;

    // computed values
    double t = java.lang.System.currentTimeMillis() % pulseDuration;
    double radius = t * radiusFudgeFactor;
    double pulseBrightness = t / pulseDuration;
    double easedPulseBrightness = 1 - Math.pow(pulseBrightness, 3);

    // new pulse detected
    if (t < previous_t) {
      center = model.points[(int)LXUtils.random(0, model.points.length - 1)];
      jitter = LXUtils.random(0, 500);
    }

    for (LXPoint p : model.points) {
      double distanceToCenter = distance(center.x, center.y, center.z, p.x, p.y, p.z);
      double distanceToRadius = Math.abs(distanceToCenter - radius + pulseThickness); // pulse is eased on both sides
      // double distanceToRadius = distanceToCenter - radius + pulseThickness; // pulse is eased on back side only; front is solid

      if (distanceToRadius < pulseThickness) {
        double proximityToPulseCenter = distanceToRadius / pulseThickness;
        double easedPulseThickness = 1 - Math.pow(proximityToPulseCenter, 3);

        colors[p.index] = LXColor.hsb(0, 0, 100 * easedPulseThickness * easedPulseBrightness);
      } else {
        colors[p.index] = black;
      }
    }

    previous_t = t;
  }
}
