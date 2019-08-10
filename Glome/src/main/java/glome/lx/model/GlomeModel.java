package glome.lx.model;

import heronarts.lx.model.LXModel;
import heronarts.lx.model.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
// import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GlomeModel extends LXModel {
  
  public GlomeModel() {
    super(getPixels());
    // System.out.println("GlomeModel: " + this.getPoints().size() + " pixels loaded successfully");
  }

  public static List<LXPoint> getPixels() {
    List<LXPoint> points = new ArrayList<LXPoint>();

    try (FileReader fr = new FileReader("pixels2.json")) {
      // System.out.println("getPixels: read file");
      JsonObject obj = new Gson().fromJson(fr, JsonObject.class);
      JsonArray panels = obj.getAsJsonArray("Pixels");

      for (int i = 0; i < panels.size(); ++i) {
        JsonArray panel = panels.get(i).getAsJsonArray();

        for (int j = 0; j < panel.size(); ++j) {
          JsonObject pixel = panel.get(j).getAsJsonObject();
          float x = pixel.get("x").getAsFloat();
          float y = pixel.get("y").getAsFloat();
          float z = pixel.get("z").getAsFloat();

          points.add(new LXPoint(x, y, z));
        }
      }

      System.out.println(points.size() + " pixels loaded successfully");
    } catch (IOException iox) {
      // System.err.println("Could not load pixel file: " + iox.getLocalizedMessage());
    } catch (Exception x) {
      // System.err.println("Exception in buildModel: " + x.getLocalizedMessage());
      x.printStackTrace(System.err);
    }

    return points;
  }
}
