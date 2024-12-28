package com.lothrazar.cyclic.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.lothrazar.cyclic.registry.BlockRegistry;
import com.lothrazar.cyclic.util.ShapeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class PeatBlock extends BlockCyclic {

  public static DoubleValue PEATCHANCE;
  public static final int FUEL_WEAK = 256;
  public static final int FUEL_STRONG = 4096;

  public PeatBlock(Properties properties) {
    super(properties.randomTicks().strength(0.9F));
  }

  @SuppressWarnings("deprecation")
  @Override
  public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
    super.randomTick(state, world, pos, random);
    List<BlockPos> around = ShapeUtil.squareHorizontalHollow(pos, 1);
    int sidesWet = 0;
    List<BlockPos> waters = new ArrayList<>();
    for (BlockPos p : around) {
      //try to bake if SOURCE water is nearby
      //using FluidState instead of Block
      //backport fix from this PR by PocketSizedWeeb https://github.com/Lothrazar/Cyclic/pull/2404/files#diff-75c5d8aa746dbf1c0a18dfd3f48a80408e4191eb536ea0566c243038eaf05269
      FluidState fluid = world.getFluidState(p);
      if (fluid.is(Fluids.WATER)) {
        sidesWet++;
        waters.add(p);
      }
    }
    if (sidesWet >= 0) {
      tryBake(world, pos, waters.size());
    }
  }

  /**
   * Percent chance to bake based on how much water. If touching 1 water source its just the PCT. Touching all 6 water sources means 6 * PCT
   */
  private void tryBake(Level world, BlockPos pos, int waters) {
    if (world.random.nextDouble() < PEATCHANCE.get().doubleValue() * waters) {
      world.setBlockAndUpdate(pos, BlockRegistry.PEAT_BAKED.get().defaultBlockState());
    }
  }
}
