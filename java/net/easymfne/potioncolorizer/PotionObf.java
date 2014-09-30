/**
 * This file is part of PotionColorizer by Eric Hildebrand.
 * 
 * PotionColorizer is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * PotionColorizer is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * PotionColorizer. If not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.potioncolorizer;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * Obfuscation helper class that keeps track of the necessary class, method, and
 * field names for the functioning of Event injection.
 */
public class PotionObf extends Obf {
    
    /** net.minecraft.item.ItemPotion class. */
    public static PotionObf itemPotion = new PotionObf(
            "net.minecraft.item.ItemPotion", "adp");
    
    /** getColorFromItemStack(ItemStack, int) method from ItemPotion. */
    public static PotionObf itemPotion_getColorFromItemStack = new PotionObf(
            "func_82790_a", "a", "getColorFromItemStack");
    
    /** hasEffect(ItemStack) method from ItemPotion. */
    public static PotionObf itemPotion_hasEffect = new PotionObf(
            "func_77636_d", "e", "hasEffect");
    
    /** liquidColor field in net.minecraft.potion.Potion class. */
    public static PotionObf potion_liquidColor = new PotionObf("liquidColor",
            "K");
    
    /**
     * Create a new obfuscation mapping.
     * 
     * @param seargeName
     *            Searge's name for it
     * @param obfName
     *            Obfuscated name for it
     */
    protected PotionObf(String seargeName, String obfName) {
        super(seargeName, obfName);
    }
    
    /**
     * Create a new obfuscation mapping.
     * 
     * @param seargeName
     *            Searge's name for it
     * @param obfName
     *            Obfuscated name for it
     * @param mcpName
     *            MCP name for it
     */
    protected PotionObf(String seargeName, String obfName, String mcpName) {
        super(seargeName, obfName, mcpName);
    }
    
}
