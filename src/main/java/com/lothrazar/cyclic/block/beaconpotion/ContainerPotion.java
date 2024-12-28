package com.lothrazar.cyclic.block.beaconpotion;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPotion extends ContainerBase {

  TilePotionBeacon tile;

  public ContainerPotion(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
    super(MenuTypeRegistry.BEACON.get(), windowId);
    tile = (TilePotionBeacon) world.getBlockEntity(pos);
    this.playerEntity = player;
    this.playerInventory = playerInventory;
    this.endInv = tile.inventory.getSlots();
    addSlot(new SlotItemHandler(tile.inventory, 0, 9, 35) {

      @Override
      public void setChanged() {
        tile.setChanged();
      }
    });
    this.endInv++;
    addSlot(new SlotItemHandler(tile.filter, 0, 149, 9) {

      @Override
      public void setChanged() {
        tile.setChanged();
      }
    });
    layoutPlayerInventorySlots(8, 84);
    this.trackAllIntFields(tile, TilePotionBeacon.Fields.values().length);
    trackEnergy(tile);
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, BlockRegistry.BEACON.get());
  }
}
