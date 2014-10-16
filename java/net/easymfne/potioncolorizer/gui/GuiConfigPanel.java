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
package net.easymfne.potioncolorizer.gui;

import java.util.ArrayList;
import java.util.List;

import net.easymfne.potioncolorizer.LiteModPotionColorizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.modconfig.ConfigPanelHost;

/**
 * In-game configuration panel with buttons for independently enabling and disabling functionality
 * and changing settings.
 */
public class GuiConfigPanel extends Gui implements ConfigPanel {

  /**
   * Class representing a configuration line for a potion effect's color.
   */
  private class ColorConfigLine {

    /** Width of the checkbox, in points, for offsetting color button. */
    private static final int BOX_WIDTH = 18;

    private int x; // X position of top-left corner.
    private int y; // Y position of top-left corner.
    private String name; // Internal potion name.
    private GuiCheckbox checkbox; // Enable/disable checkbox.
    private GuiColorButton colorButton; // Color chooser button.

    /**
     * @param idStart Gui element id number to start with.
     * @param potionName Internal potion effect name.
     * @param xPos Top-left x-position of the line.
     * @param yPos Top-left y-position of the line.
     */
    public ColorConfigLine(int idStart, String potionName, int xPos, int yPos) {
      x = xPos;
      y = yPos;
      name = potionName;
      checkbox = new GuiCheckbox(idStart++, xPos, yPos, null);
      checkbox.enabled = customColorBox.checked;
      checkbox.checked = LiteModPotionColorizer.instance.customPotionColors.containsKey(potionName);
      if (checkbox.checked) {
        colorButton =
            new GuiColorButton(mc, idStart++, xPos + BOX_WIDTH, yPos, 24, 12,
                LiteModPotionColorizer.instance.customPotionColors.get(potionName), name);
      } else {
        colorButton =
            new GuiColorButton(mc, idStart++, xPos + BOX_WIDTH, yPos, 24, 12,
                LiteModPotionColorizer.defaultColors.get(potionName), name);
      }
      colorButton.enabled = checkbox.enabled && checkbox.checked;
    }

    /** Draw the configuration line to Gui screen (stage 1). */
    public void draw(Gui gui, int mouseX, int mouseY) {
      checkbox.drawButton(mc, mouseX, mouseY);
      colorButton.drawButton(mc, mouseX, mouseY);
    }

    /** Draw the configuration line to Gui screen (stage 2). */
    public void draw2(Gui gui, int mouseX, int mouseY) {
      colorButton.drawPicker(mc, mouseX, mouseY);
    }

    /** Pass keypress along to the color hex field. */
    public void keyPressed(char keyChar, int keyCode) {
      colorButton.keyTyped(keyChar, keyCode);
    }

    /** Handle mouse clicks, toggling checked/enabled status if needed. */
    public void mousePressed(int mouseX, int mouseY, int mouseButton) {
      if (checkbox.mousePressed(mc, mouseX, mouseY)) {
        activeButton = checkbox;
        checkbox.checked = !checkbox.checked;
        colorButton.enabled = checkbox.enabled && checkbox.checked;
      } else if (colorButton.mousePressed(mc, mouseX, mouseY)) {
        activeButton = colorButton;
      }
    }

    /** Refresh the checkbox and hex field status to reflect any changes. */
    public void refresh() {
      checkbox.enabled = customColorBox.checked;
      checkbox.checked =
          checkbox.enabled && LiteModPotionColorizer.instance.customPotionColors.containsKey(name);
      colorButton.enabled = checkbox.enabled && checkbox.checked;
    }

    /** Write the changes back to the mod instance. */
    public void save() {
      if (checkbox.checked) {
        LiteModPotionColorizer.instance.customPotionColors.put(name, colorButton.getColor());
      } else {
        LiteModPotionColorizer.instance.customPotionColors.remove(name);
      }
    }

  }

  /** Line spacing, in points. */
  private final static int SPACING = 16;

  /** Handy references. */
  private LiteModPotionColorizer mod;
  private Minecraft mc;

  /* Gui components. */
  private GuiCheckbox deglintBox;
  private GuiCheckbox recolorBox;
  private GuiCheckbox customColorBox;
  private List<ColorConfigLine> colorLines;

  /* Active component tracker/reference. */
  private GuiButton activeButton;

  /** Construct a new configuration panel. */
  public GuiConfigPanel() {
    mc = Minecraft.getMinecraft();
  }

  /** Draw the configuration panel's elements every refresh. */
  @Override
  public void drawPanel(ConfigPanelHost host, int mouseX, int mouseY, float partialTicks) {
    deglintBox.drawButton(mc, mouseX, mouseY);
    recolorBox.drawButton(mc, mouseX, mouseY);
    customColorBox.drawButton(mc, mouseX, mouseY);
    for (ColorConfigLine line : colorLines) {
      line.draw(this, mouseX, mouseY);
    }
    for (ColorConfigLine line : colorLines) {
      line.draw2(this, mouseX, mouseY);
    }
  }

  /** Get the height of the panel in points. */
  @Override
  public int getContentHeight() {
    return SPACING * (3 + colorLines.size());
  }

  /** Get the title to display for the panel. */
  @Override
  public String getPanelTitle() {
    return I18n.format("config.panel.title", new Object[] {LiteModPotionColorizer.MOD_NAME});
  }

  /** On keypresses, pass the keypress to the text fields. */
  @Override
  public void keyPressed(ConfigPanelHost host, char keyChar, int keyCode) {
    for (ColorConfigLine line : colorLines) {
      line.keyPressed(keyChar, keyCode);
    }
  }

  /** On mouse movement, nothing needs to be done. */
  @Override
  public void mouseMoved(ConfigPanelHost host, int mouseX, int mouseY) {}

  /** On click, activate button under cursor if one exists. */
  @Override
  public void mousePressed(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
    if (deglintBox.mousePressed(mc, mouseX, mouseY)) {
      activeButton = deglintBox;
      LiteModPotionColorizer.instance.deglintPotions =
          !LiteModPotionColorizer.instance.deglintPotions;
      deglintBox.checked = LiteModPotionColorizer.instance.deglintPotions;
    } else if (recolorBox.mousePressed(mc, mouseX, mouseY)) {
      activeButton = recolorBox;
      LiteModPotionColorizer.instance.recolorPotions =
          !LiteModPotionColorizer.instance.recolorPotions;
      recolorBox.checked = LiteModPotionColorizer.instance.recolorPotions;
    } else if (customColorBox.mousePressed(mc, mouseX, mouseY)) {
      activeButton = customColorBox;
      LiteModPotionColorizer.instance.customColors = !LiteModPotionColorizer.instance.customColors;
      customColorBox.checked = LiteModPotionColorizer.instance.customColors;
      for (ColorConfigLine line : colorLines) {
        line.refresh();
      }
    } else {
      for (ColorConfigLine line : colorLines) {
        line.mousePressed(mouseX, mouseY, mouseButton);
      }
    }
  }

  /** On release of click, deactivate the selected button (if any). */
  @Override
  public void mouseReleased(ConfigPanelHost host, int mouseX, int mouseY, int mouseButton) {
    if (activeButton != null) {
      activeButton.func_146111_b(mouseX, mouseY);
      if (activeButton instanceof GuiControl) {
        ((GuiControl) activeButton).mouseReleased(mouseX, mouseY);
      }
      activeButton = null;
    }
  }

  /** On closing of panel, save current configuration to disk. */
  @Override
  public void onPanelHidden() {
    for (ColorConfigLine line : colorLines) {
      line.save();
    }
    mod.writeConfig();
  }

  /** On resizing of panel, adjust scaling. */
  @Override
  public void onPanelResize(ConfigPanelHost host) {
    GuiControl.setScreenSizeAndScale(host.getWidth(), host.getHeight(), new ScaledResolution(
        this.mc, this.mc.displayWidth, this.mc.displayHeight).getScaleFactor());
  }

  /** On opening of panel, instantiate the user interface components. */
  @Override
  public void onPanelShown(ConfigPanelHost host) {
    mod = (LiteModPotionColorizer) host.getMod();
    GuiControl.setScreenSizeAndScale(host.getWidth(), host.getHeight(), new ScaledResolution(
        this.mc, this.mc.displayWidth, this.mc.displayHeight).getScaleFactor());
    int id = 0;
    int line = 0;
    deglintBox =
        new GuiCheckbox(id++, 10, SPACING * line++, I18n.format("config.deglint.text",
            new Object[0]));
    deglintBox.checked = LiteModPotionColorizer.instance.deglintPotions;
    recolorBox =
        new GuiCheckbox(id++, 10, SPACING * line++, I18n.format("config.recolor.text",
            new Object[0]));
    recolorBox.checked = LiteModPotionColorizer.instance.recolorPotions;
    customColorBox =
        new GuiCheckbox(id++, 10, SPACING * line++,
            I18n.format("config.custom.text", new Object[0]));
    customColorBox.checked = LiteModPotionColorizer.instance.customColors;
    colorLines = new ArrayList<ColorConfigLine>();
    for (Potion potion : Potion.potionTypes) {
      if (potion != null) {
        colorLines.add(new ColorConfigLine(id, potion.getName(), 20, SPACING * line++));
        id += 2;
      }
    }
  }

  /** On each tick, nothing needs to be done. */
  @Override
  public void onTick(ConfigPanelHost host) {}

}
