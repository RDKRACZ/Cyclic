/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclic.item.animal;

import java.util.UUID;
import com.lothrazar.cyclic.api.IEntityInteractable;
import com.lothrazar.cyclic.item.ItemBaseCyclic;
import com.lothrazar.cyclic.util.ChatUtil;
import com.lothrazar.cyclic.util.EntityUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class ItemHorseEmeraldJump extends ItemBaseCyclic implements IEntityInteractable {

  private static final int JUMP_MAX = 10;
  private static final double JUMP_AMT = 0.08;
  public static final UUID MODIFIER_ID = UUID.fromString("abc30aa2-eff2-4a81-b92b-a1cb95f115c6");

  public ItemHorseEmeraldJump(Properties prop) {
    super(prop);
  }

  @Override
  public void interactWith(EntityInteract event) {
    if (event.getItemStack().getItem() == this
        && event.getTarget() instanceof Horse) {
      // lets go 
      Horse ahorse = (Horse) event.getTarget();
      Attribute attr = EntityUtil.getAttributeJump(ahorse);
      //got the attribute instance
      AttributeInstance mainAttribute = ahorse.getAttribute(attr);
      //now create a modifier 
      if (mainAttribute.getValue() < JUMP_MAX) {
        //ok good 
        AttributeModifier oldModifier = mainAttribute.getModifier(MODIFIER_ID);
        //what was the previous value
        double newAdded = (oldModifier == null) ? JUMP_AMT : oldModifier.getAmount() + JUMP_AMT;
        //got it      //replace the modifier on the main attribute
        mainAttribute.removeModifier(MODIFIER_ID);
        AttributeModifier newModifier = new AttributeModifier(MODIFIER_ID, "Cyclic Carrot Jump", newAdded, AttributeModifier.Operation.ADDITION);
        mainAttribute.addPermanentModifier(newModifier);
        //finish up
        //
        //success doesnt work, its broken. player still does the mounting lol
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        event.getItemStack().shrink(1);
        EntityUtil.eatingHorse(ahorse);
        ChatUtil.sendStatusMessage(event.getPlayer(), "" + (mainAttribute.getValue() + newAdded));
      }
    }
  }
}
