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

public class PatternVortex extends LXPattern {
  // Helpful global constants
  private final static double INCHES = 1;
  private final static double IN = INCHES;
  private final static double FEET = 12 * INCHES;
  private final static double FT = FEET;
  private final static double CM = IN / 2.54;
  private final static double MM = CM * .1;
  private final static double M = CM * 100;
  private final static double METER = M;

  public String getAuthor() {
    return "Mark C. Slee";
  }
  
  public final CompoundParameter speed = (CompoundParameter)
    new CompoundParameter("Speed", 2000, 9000, 300)
    .setExponent(.5)
    .setDescription("Speed of vortex motion");
  
  public final CompoundParameter size =
    new CompoundParameter("Size",  4*FEET, 1*FEET, 10*FEET)
    .setDescription("Size of vortex");
  
  public final CompoundParameter xPos = (CompoundParameter)
    new CompoundParameter("XPos", model.cx, model.xMin, model.xMax)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("X-position of vortex center");
    
  public final CompoundParameter yPos = (CompoundParameter)
    new CompoundParameter("YPos", model.cy, model.yMin, model.yMax)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("Y-position of vortex center");
    
  public final CompoundParameter xSlope = (CompoundParameter)
    new CompoundParameter("XSlp", .2, -1, 1)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("X-slope of vortex center");
    
  public final CompoundParameter ySlope = (CompoundParameter)
    new CompoundParameter("YSlp", .5, -1, 1)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("Y-slope of vortex center");
    
  public final CompoundParameter zSlope = (CompoundParameter)
    new CompoundParameter("ZSlp", .3, -1, 1)
    .setPolarity(LXParameter.Polarity.BIPOLAR)
    .setDescription("Z-slope of vortex center");
  
  private final LXModulator pos = startModulator(new SawLFO(1, 0, this.speed));
  
  private final LXModulator sizeDamped = startModulator(new DampedParameter(this.size, 5*FEET, 8*FEET));
  private final LXModulator xPosDamped = startModulator(new DampedParameter(this.xPos, model.xRange, 3*model.xRange));
  private final LXModulator yPosDamped = startModulator(new DampedParameter(this.yPos, model.yRange, 3*model.yRange));
  private final LXModulator xSlopeDamped = startModulator(new DampedParameter(this.xSlope, 3, 6));
  private final LXModulator ySlopeDamped = startModulator(new DampedParameter(this.ySlope, 3, 6));
  private final LXModulator zSlopeDamped = startModulator(new DampedParameter(this.zSlope, 3, 6));

  public PatternVortex(LX lx) {
    super(lx);
    addParameter("speed", this.speed);
    addParameter("size", this.size);
    addParameter("xPos", this.xPos);
    addParameter("yPos", this.yPos);
    addParameter("xSlope", this.xSlope);
    addParameter("ySlope", this.ySlope);
    addParameter("zSlope", this.zSlope);
  }

  public void run(double deltaMs) {
    final float xPos = this.xPosDamped.getValuef();
    final float yPos = this.yPosDamped.getValuef();
    final float size = this.sizeDamped.getValuef();
    final float pos = this.pos.getValuef();
    
    final float xSlope = this.xSlopeDamped.getValuef();
    final float ySlope = this.ySlopeDamped.getValuef();
    final float zSlope = this.zSlopeDamped.getValuef();

    float dMult = 2 / size;
    for (LXPoint point: model.points) {
      float radix = Math.abs((xSlope*Math.abs(point.x-model.cx) + ySlope*Math.abs(point.y-model.cy) + zSlope*Math.abs(point.z-model.cz)));
      double dist = LXUtils.distance(point.x, point.y, xPos, yPos); 
      //float falloff = 100 / max(20*INCHES, 2*size - .5*dist);
      //float b = 100 - falloff * LXUtils.wrapdistf(radix, pos * size, size);
      double b = Math.abs(((dist + radix + pos * size) % size) * dMult - 1);
      setColor(point.index, (b > 0) ? LXColor.gray(b*b*100) : LXColor.BLACK);
    }
  }
}