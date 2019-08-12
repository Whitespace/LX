/**
  * Copyright 2013- Mark C. Slee, Heron Arts LLC
  *
  * This file is part of the LX Studio software library. By using
  * LX, you agree to the terms of the LX Studio Software License
  * and Distribution Agreement, available at: http://lx.studio/license
  *
  * Please note that the LX license is not open-source. The license
  * allows for free, non-commercial use.
  *
  * HERON ARTS MAKES NO WARRANTY, EXPRESS, IMPLIED, STATUTORY, OR
  * OTHERWISE, AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
  * MERCHANTABILITY, NON-INFRINGEMENT, OR FITNESS FOR A PARTICULAR
  * PURPOSE, WITH RESPECT TO THE SOFTWARE.
  *
  * @author Mark C. Slee <mark@heronarts.com>
  */

package glome.lx.headless;

import glome.lx.model.GlomeModel;
import java.io.File;
import heronarts.lx.LX;
import heronarts.lx.LXChannel;
import heronarts.lx.LXPattern;
import heronarts.lx.*;
import heronarts.lx.model.LXModel;
import heronarts.lx.output.ArtNetDatagram;
import heronarts.lx.output.LXDatagramOutput;

public class GlomeHeadless {

  public static LXModel buildModel() {
    return new GlomeModel();
  }

  public static void addArtNetOutput(LX lx) throws Exception {
    final String ARTNET_IP = "192.168.1.50";

    // Construct a new DatagramOutput object
    LXDatagramOutput output = new LXDatagramOutput(lx);

    // manually send each panel to the pixlite
    //   each panel is either 468 or 390 pixels
    //   DMX takes 512 bytes per universe
    //   512/3 = RGB 170 pixels
    // so split each panel into groups of 170 or fewer pixels, and send their colors on a single universe
    // we do this by computing which pixels to send on each universe and creating a Datagram object, which will pull the colors for us
    //
    // we can just precompute everything and inline it
    int currentPixelIndex = 0;
    final int[] counts = {170,170,128,170,170,128,170,170,128,170,170,128,170,170,128,170,170,128,170,170,50,170,170,50,170,170,50,170,170,50};
    for (int universeIndex = 0; universeIndex < counts.length; ++universeIndex) {
      int count = counts[universeIndex];

      // generate an array of length count with the pixel indices to send on this universe
      int[] pixelIndices = new int[count];
      for (int i = 0; i < count; ++i) {
        pixelIndices[i] = i + currentPixelIndex;
      }
      ArtNetDatagram strip = new ArtNetDatagram(pixelIndices, universeIndex);
      strip.setAddress(ARTNET_IP);
      output.addDatagram(strip);

      currentPixelIndex += count;
    }

    // Add the datagram output to the LX engine
    lx.engine.addOutput(output);
  }

  public static void main(String[] args) {
    try {
      LXModel glome = buildModel();
      LX lx = new LX(glome);

      addArtNetOutput(lx);

      // Patterns
      lx.registerPattern(PulsePattern.class);
      lx.registerPattern(PatternTumbler.class);
      lx.registerPattern(PatternVortex.class);
      lx.registerPattern(PatternBorealis.class);

      // Colors
      lx.registerPattern(Plasma.class);

      // read from lxp file
      File projectFile = null;
      for (String arg : args) {
        if (arg.endsWith(".lxp")) {
          projectFile = new File(arg);
          if (!projectFile.exists()) {
            System.err.println("Project file not found: " + projectFile);
            projectFile = null;
          }
        }
      }

      lx.openProject(projectFile);

      for (LXChannelBus channelBus : lx.engine.getChannels()) {
        LXChannel channel = (LXChannel)channelBus;
        System.out.println("Channel " + channel.getIndex());
        System.out.println("- "+channel.getPatterns().size()+" patterns");
        System.out.println("- enabled: "+channel.enabled.isOn());
        System.out.println("- selected: "+channel.selected.isOn());
        // System.out.println("- blendMode: "+channel.blendMode.getLabel());
        // System.out.println("- blendMode: "+channel.blendMode.getName());
        // System.out.println("- blendMode: "+channel.blendMode.toString());
      }

      lx.engine.start();
    } catch (Exception x) {
      System.err.println(x.getLocalizedMessage());
    }
  }
}
