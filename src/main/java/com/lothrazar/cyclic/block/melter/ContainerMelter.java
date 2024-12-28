package com.lothrazar.cyclic.block.melter;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMelter extends ContainerBase {

  TileMelter tile;

  public ContainerMelter(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
    super(MenuTypeRegistry.MELTER.get(), windowId);
    tile = (TileMelter) world.getBlockEntity(pos);
    this.playerEntity = player;
    this.playerInventory = playerInventory;
    tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
      this.endInv = h.getSlots();
      addSlot(new SlotItemHandler(h, 0, 17, 31) {

        @Override
        public void setChanged() {
          tile.setChanged();
        }
      });
      addSlot(new SlotItemHandler(h, 1, 35, 31) {

        @Override
        public void setChanged() {
          tile.setChanged();
        }
      });
    });
    layoutPlayerInventorySlots(8, 84);
    this.trackAllIntFields(tile, TileMelter.Fields.values().length);
    trackEnergy(tile);
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, BlockRegistry.MELTER.get());
  }
}
