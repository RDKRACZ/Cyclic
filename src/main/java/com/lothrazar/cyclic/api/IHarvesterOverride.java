package com.lothrazar.cyclic.api;

import java.util.function.Consumer;
import com.lothrazar.cyclic.util.HarvestUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IHarvesterOverride {

  public static void registerHarvestOverrider(IHarvesterOverride override) {
    HarvestUtil.HARVEST_OVERRIDES.add(override);
  }

  /**
   * Checks if this harvesting logic applies to a certain block
   *
   * @param state
   *          the state of the block
   * @param world
   *          the world
   * @param pos
   *          the position
   * @return true if this logic handles the given block
   */
  boolean appliesTo(BlockState state, Level world, BlockPos pos);

  /**
   * Attempts to harvest and run custom harvest logic for a block at the given position.
   *
   * @param state
   *          the state of the block
   * @param world
   *          the world
   * @param pos
   *          the position
   * @param dropConsumer
   *          pass any drops resulting from the harvest operation
   * @return true if the harvesting operation was successfully handled
   */
  boolean attemptHarvest(BlockState state, Level world, BlockPos pos, Consumer<ItemStack> dropConsumer);
}
