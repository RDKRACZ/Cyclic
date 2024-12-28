package com.lothrazar.cyclic.block.cable.item;

import com.lothrazar.cyclic.gui.ContainerBase;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCableItem extends ContainerBase {

  protected TileCableItem tile;

  public ContainerCableItem(int windowId, Level world, BlockPos pos, Inventory playerInventory, Player player) {
    super(MenuTypeRegistry.ITEM_PIPE.get(), windowId);
    tile = (TileCableItem) world.getBlockEntity(pos);
    this.playerEntity = player;
    this.playerInventory = playerInventory;
    this.endInv = tile.filter.getSlots();
    //dont show 0 thats the actual thing in the slot 
    addSlot(new SlotItemHandler(tile.filter, 0, 80, 29) {

      @Override
      public void setChanged() {
        tile.setChanged();
      }
    });
    addSlot(new SlotItemHandler(tile.filter, 0, 80, 29));
    layoutPlayerInventorySlots(8, 84);
    this.trackEnergy(tile);
  }

  @Override
  public boolean stillValid(Player playerIn) {
    return stillValid(ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()), playerEntity, BlockRegistry.ITEM_PIPE.get());
  }
}
