/**
 * This file is part of PotionColorizer by Eric Hildebrand.
 * 
 * PotionColorizer is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * PotionColorizer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with PotionColorizer. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.potioncolorizer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.easymfne.potioncolorizer.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionHelper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigStrategy;
import com.mumfrey.liteloader.modconfig.ExposableOptions;
import com.mumfrey.liteloader.transformers.event.ReturnEventInfo;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * LiteLoader-loaded mod that modifies the process by which potion items are colorized. No longer
 * looks up colors using data values, instead looks up colors by the potion's effects. Will average
 * colors of effects in the situation of multiple, mirroring the way potion particle effects are
 * colored.
 * 
 * @author Eric Hildebrand
 */
@ExposableOptions(strategy = ConfigStrategy.Unversioned, filename = "potioncolorizer.config.json")
public class LiteModPotionColorizer implements LiteMod, InitCompleteListener, Configurable {

  /** Name/Version information. */
  public static final String MOD_NAME = "PotionColorizer";
  public static final String MOD_VERSION = "1.1.1";

  /** Modification instance. */
  public static LiteModPotionColorizer instance;
  public static Map<String, Integer> defaultColors;

  /**
   * Modify the return value (on the fly) of the method in ItemPotion that examines an ItemStack and
   * determines whether or not it has effects.
   * 
   * @param event Return event to be modified
   * @param itemStack The potion ItemStack
   */
  public static void hasEffect(ReturnEventInfo<ItemPotion, Boolean> event, ItemStack itemStack) {
    if (instance.deglintPotions) {
      event.setReturnValue(Boolean.FALSE);
    }
  }

  /**
   * Modify the return value (on the fly) of the method that fetches the color for a potion. Only
   * modifies the coloring of the necessary render pass.
   * 
   * @param event Return event to be modified
   * @param itemStack The potion's ItemStack
   * @param pass The render pass number
   */
  public static void recolorPotion(ReturnEventInfo<ItemPotion, Integer> event, ItemStack itemStack,
      int pass) {
    if (instance.recolorPotions && pass == 0) {
      event.setReturnValue(PotionHelper.calcPotionLiquidColor(((ItemPotion) itemStack.getItem())
          .getEffects(itemStack)));
    }
  }

  /** Whether or not to remove glint overlay from potions. */
  @Expose
  @SerializedName("deglint_potions")
  public boolean deglintPotions = false;

  /** Whether or not to recolor potions based on their effects. */
  @Expose
  @SerializedName("recolor_potions")
  public boolean recolorPotions = true;

  /** Whether or not to use customized colors for effects. */
  @Expose
  @SerializedName("custom_colors_enabled")
  public boolean customColors = false;

  /** Custom potion colors map. */
  @Expose
  @SerializedName("custom_potion_colors")
  public Map<String, Integer> customPotionColors = new HashMap<String, Integer>();

  /** Construct new instance of the mod and update static reference to it. */
  public LiteModPotionColorizer() {
    if (instance != null) {
      System.err.println("Error: Attempted to instantiate two instances of " + MOD_NAME);
    } else {
      instance = this;
    }
  }

  /** Get the class responsible for configuration panel functionality. */
  @Override
  public Class<? extends ConfigPanel> getConfigPanelClass() {
    return GuiConfigPanel.class;
  }

  /** Get the human-readable modification name. */
  @Override
  public String getName() {
    return MOD_NAME;
  }

  /** Get the human-readable modification version. */
  @Override
  public String getVersion() {
    return MOD_VERSION;
  }

  /** On initialization, prep default potion colors for later. */
  @Override
  public void init(File configPath) {
    if (defaultColors != null) {
      return;
    }
    defaultColors = new HashMap<String, Integer>();
  }

  /** Once initialization finishes, save default colors and modify current colors via reflection. */
  @Override
  public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
    for (Potion potion : Potion.potionTypes) {
      if (potion != null) {
        defaultColors.put(potion.getName(), Integer.valueOf(potion.getLiquidColor()));
      }
    }
    LiteLoaderLogger.info("Saved %d default potion liquid colors.", defaultColors.size());
    try {
      setPotionColors();
    } catch (Exception e) {
      LiteLoaderLogger.severe("Failed to set potion colors: " + e.getClass().getName());
    }
  }

  /** On each tick, nothing needs to be done. */
  @Override
  public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {}

  /**
   * Using reflection, modify potion colors to custom values. If a custom color is not defined,
   * reset its current color to the default if necessary.
   * 
   * @throws IllegalAccessException If a potion does not have an accessible liquid color field.
   * @throws NoSuchFieldException If the Potion class does not have the liquid color field.
   */
  private void setPotionColors() throws NoSuchFieldException, IllegalAccessException {
    for (int i = 0; i < PotionObf.potion_liquidColor.names.length; i++) {
      try {
        setPotionColors(PotionObf.potion_liquidColor.names[i]);
        return;
      } catch (NoSuchFieldException e) {
        if (i == PotionObf.potion_liquidColor.names.length - 1) {
          throw e;
        }
      } catch (IllegalAccessException e) {
        if (i == PotionObf.potion_liquidColor.names.length - 1) {
          throw e;
        }
      }
    }
  }

  /**
   * Using reflection, modify potion colors to custom values via the given field name. If a custom
   * color is not defined, reset its current color to the default if necessary.
   * 
   * @param fieldName The name of the liquid color field.
   * @throws IllegalAccessException If a potion does not have an accessible field of that name.
   * @throws NoSuchFieldException If the Potion class does not have a field of that name.
   */
  private void setPotionColors(String fieldName) throws NoSuchFieldException,
      IllegalAccessException {
    Field f = Potion.class.getDeclaredField(fieldName);
    boolean accessibility = f.isAccessible();
    f.setAccessible(true);
    for (Potion potion : Potion.potionTypes) {
      if (potion == null) {
        continue;
      } else if (customPotionColors.containsKey(potion.getName())) {
        f.setInt(potion, customPotionColors.get(potion.getName()));
      } else if (potion.getLiquidColor() != defaultColors.get(potion.getName())) {
        f.setInt(potion, defaultColors.get(potion.getName()));
      }
    }
    if (f.isAccessible() != accessibility) {
      f.setAccessible(accessibility);
    }
  }

  /** On upgrading from a previous version, nothing needs to be done. */
  @Override
  public void upgradeSettings(String version, File configPath, File oldConfigPath) {}

  /** Write current configuration values to disk. */
  public void writeConfig() {
    try {
      setPotionColors();
    } catch (Exception e) {
      LiteLoaderLogger.severe("Failed to set potion colors: " + e.getClass().getName());
    }
    LiteLoader.getInstance().writeConfig(this);
  }
}
