package com.lothrazar.cyclic.block.generatorfood;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.registry.ContainerScreenRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerGeneratorFood extends ContainerBase {

  TileGeneratorFood tile;

  public ContainerGeneratorFood(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
    super(ContainerScreenRegistry.GENERATOR_FOOD, windowId);
    tile = (TileGeneratorFood) world.getBlockEntity(pos);
    this.playerEntity = player;
    this.playerInventory = playerInventory;
    addSlot(new SlotItemHandler(tile.inputSlots, 0, 75, 35) {

      @Override
      public void setChanged() {
        tile.setChanged();
      }
    });
    //    addSlot(new SlotItemHandler(tile.outputSlots, 0, 109, 35));
    this.endInv = tile.inputSlots.getSlots();
    layoutPlayerInventorySlots(8, 84);
    this.trackAllIntFields(tile, TileGeneratorFood.Fields.values().length);
    trackEnergy(tile);
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, BlockRegistry.GENERATOR_FOOD.get());
  }
}
