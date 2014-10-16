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

import net.minecraft.item.ItemStack;

import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.EventInjectionTransformer;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * Event injection to allow modification of the return value of the getColorFromItemStack method in
 * ItemPotion.
 */
public class PotionColorizerEventTransformer extends EventInjectionTransformer {

  /**
   * Add event for catching calls to ItemPotion.hasEffect() for modification.
   */
  private void addDeglintEvent() {
    addEvent(
        Event.getOrCreate("PotionColorizer_ItemPotion_hasEffect", true),
        new MethodInfo(PotionObf.itemPotion, PotionObf.itemPotion_hasEffect, Boolean.TYPE,
            new Object[] {ItemStack.class}), new MethodHead()).addListener(
        new MethodInfo("net.easymfne.potioncolorizer.LiteModPotionColorizer", "hasEffect"));
  }

  /** Add necessary events for catching and modifying method calls. */
  @Override
  protected void addEvents() {
    addRecolorEvent();
    addDeglintEvent();
  }

  /**
   * Add event for catching calls to ItemPotion.getColorFromItemStack(ItemStack, int) for
   * modification.
   */
  private void addRecolorEvent() {
    addEvent(
        Event.getOrCreate("PotionColorizer_ItemPotion_getColorFromItemStack", true),
        new MethodInfo(PotionObf.itemPotion, PotionObf.itemPotion_getColorFromItemStack,
            Integer.TYPE, new Object[] {ItemStack.class, Integer.TYPE}), new MethodHead())
        .addListener(
            new MethodInfo("net.easymfne.potioncolorizer.LiteModPotionColorizer", "recolorPotion"));

  }

}
