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

import static org.lwjgl.opengl.GL11.glColor4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * Color picker button control, spawns a color picker when clicked. Code originally by Adam
 * Mummery-Smith.
 */
public class GuiColorButton extends GuiControl {

  private int color = 0x000000;
  private String potionName;
  private GuiColorPicker picker;
  private boolean pickerClicked = false;

  public GuiColorButton(Minecraft minecraft, int id, int xPosition, int yPosition,
      int controlWidth, int controlHeight, int color, String name) {
    super(minecraft, id, xPosition, yPosition, controlWidth, controlHeight, I18n.format(name));
    this.color = color;
    this.potionName = name;
  }

  public String getName() {
    return this.potionName;
  }

  public int getColor() {
    return this.color;
  }

  @Override
  public void drawControl(Minecraft minecraft, int mouseX, int mouseY) {
    if (this.visible) {
      boolean mouseOver =
          mouseX >= this.xPosition && mouseY >= this.yPosition
              && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
      int borderColor = mouseOver || this.picker != null ? 0xFFFFFFFF : 0xFFA0A0A0;

      drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition
          + this.height, borderColor);

      int v =
          Math.min(Math.max((int) (((float) this.height / (float) this.width) * 1024F), 256), 1024);

      glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

      drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 1,
          this.yPosition + this.height - 1, 0xFF000000 | this.color);

      this.mouseDragged(minecraft, mouseX, mouseY);

      if (this.displayString != null && this.displayString.length() > 0) {
        this.drawString(minecraft.fontRendererObj, this.displayString, this.xPosition + this.width
            + 8, this.yPosition + (this.height - 8) / 2, this.enabled ? 0xFFFFFFFF : 0xFFA0A0A0);
      }
    }
  }

  public void drawPicker(Minecraft minecraft, int mouseX, int mouseY) {
    if (this.visible && this.picker != null) {
      this.picker.drawButton(minecraft, mouseX, mouseY);

      if (this.picker.getDialogResult() == DialogResult.OK) {
        this.closePicker(true);
      } else if (this.picker.getDialogResult() == DialogResult.Cancel) {
        this.closePicker(false);
      }
    }
  }

  public void closePicker(boolean getColor) {
    if (getColor)
      this.color = this.picker.getColor();
    this.picker = null;
    this.pickerClicked = false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {
    if (this.pickerClicked && this.picker != null) {
      this.picker.mouseReleased(mouseX, mouseY);
      this.pickerClicked = false;
    }
  }

  @Override
  public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
    boolean pressed = super.mousePressed(minecraft, mouseX, mouseY);

    if (this.picker == null) {
      if (pressed) {
        int xPos = Math.min(this.xPosition + this.width, GuiControl.lastScreenWidth - 233);
        int yPos = Math.min(this.yPosition, GuiControl.lastScreenHeight - 175);

        this.picker =
            new GuiColorPicker(minecraft, 1, xPos, yPos, 0xFFFFFF & this.color, "Choose color");
        this.pickerClicked = false;
      }

      return pressed;
    }

    this.pickerClicked = this.picker.mousePressed(minecraft, mouseX, mouseY);

    if (pressed && !this.pickerClicked) {
      this.closePicker(true);
    }

    return this.pickerClicked;
  }

  public boolean keyTyped(char keyChar, int keyCode) {
    return (this.picker != null) ? this.picker.textBoxKeyTyped(keyChar, keyCode) : false;
  }
}
