/**
 * This file is part of PotionColorizer by Eric Hildebrand.
 * 
 * PotionColorizer is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * PotionColorizer is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * PotionColorizer. If not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.potioncolorizer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

/**
 * Simple extension of net.minecraft.client.gui.GuiTextField that only allows
 * hexadecimal characters to be written to the field.
 */
public class GuiHexColorField extends GuiTextField {
    
    public GuiHexColorField(FontRenderer f, int x, int y, int w, int h) {
        super(f, x, y, w, h);
    }
    
    /** Remove non-hexadecimal characters before writing. */
    @Override
    public void writeText(String text) {
        super.writeText(text.replaceAll("[^0-9a-fA-F]", ""));
    }
    
}
