// Random sparkles
package glome.lx.headless;

import heronarts.lx.LX;
import heronarts.lx.LXPattern;
import heronarts.lx.LXUtils;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.parameter.CompoundParameter;

public class RandomNoisePattern extends LXPattern {
  public final CompoundParameter delay = new CompoundParameter("delay", 10, 0, 1000)
    .setDescription("Delay");

  public RandomNoisePattern(LX lx) {
    super(lx);
    addParameter("delay", this.delay);
  }
  
  public void run(double deltaMs) {
    // long delay = this.delay.getValuei();
    long delay = 20;
    try {
      java.lang.Thread.sleep(delay);
    } catch (InterruptedException e) {
      
    }

    for (LXPoint p : model.points) {
      if (LXUtils.random(0, 1) > 0.5) {
        colors[p.index] = LXColor.hsb(0,0,100);
      } else {
        colors[p.index] = LXColor.hsb(0,0,0);
      }
    }
  }
}
