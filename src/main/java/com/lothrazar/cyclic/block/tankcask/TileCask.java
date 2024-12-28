package com.lothrazar.cyclic.block.tankcask;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import com.lothrazar.cyclic.block.TileBlockEntityCyclic;
import com.lothrazar.cyclic.capabilities.block.FluidTankBase;
import com.lothrazar.cyclic.registry.TileRegistry;
import com.lothrazar.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileCask extends TileBlockEntityCyclic {

  private Map<Direction, Boolean> poweredSides;
  public static final int CAPACITY = 8 * FluidAttributes.BUCKET_VOLUME;
  public static final int TRANSFER_FLUID_PER_TICK = CAPACITY / 2;
  public FluidTankBase tank = new FluidTankBase(this, CAPACITY, isFluidValid());
  LazyOptional<FluidTankBase> fluidCap = LazyOptional.of(() -> tank);

  static enum Fields {
    FLOWING, N, E, S, W, U, D;
  }

  public TileCask(BlockPos pos, BlockState state) {
    super(TileRegistry.CASK.get(), pos, state);
    flowing = 0;
    poweredSides = new HashMap<Direction, Boolean>();
    for (Direction f : Direction.values()) {
      poweredSides.put(f, false);
    }
  }

  public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileCask e) {
    e.tick();
  }

  public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileCask e) {
    e.tick();
  }

  public Predicate<FluidStack> isFluidValid() {
    return p -> true;
  }

  @Override
  public void load(CompoundTag tag) {
    for (Direction f : Direction.values()) {
      poweredSides.put(f, tag.getBoolean("flow_" + f.getName()));
    }
    this.flowing = (tag.getInt("flowing"));
    tank.readFromNBT(tag.getCompound(NBTFLUID));
    super.load(tag);
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    for (Direction f : Direction.values()) {
      tag.putBoolean("flow_" + f.getName(), poweredSides.get(f));
    }
    tag.putInt("flowing", this.flowing);
    CompoundTag fluid = new CompoundTag();
    tank.writeToNBT(fluid);
    tag.put(NBTFLUID, fluid);
    super.saveAdditional(tag);
  }

  @Override
  public void invalidateCaps() {
    fluidCap.invalidate();
    super.invalidateCaps();
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return fluidCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case D:
        return this.getSideField(Direction.DOWN);
      case E:
        return this.getSideField(Direction.EAST);
      case N:
        return this.getSideField(Direction.NORTH);
      case S:
        return this.getSideField(Direction.SOUTH);
      case U:
        return this.getSideField(Direction.UP);
      case W:
        return this.getSideField(Direction.WEST);
      case FLOWING:
        return flowing;
    }
    return -1;
  }

  @Override
  public void setField(int field, int value) {
    switch (Fields.values()[field]) {
      case FLOWING:
        flowing = value;
      break;
      case D:
        this.setSideField(Direction.DOWN, value % 2);
      break;
      case E:
        this.setSideField(Direction.EAST, value % 2);
      break;
      case N:
        this.setSideField(Direction.NORTH, value % 2);
      break;
      case S:
        this.setSideField(Direction.SOUTH, value % 2);
      break;
      case U:
        this.setSideField(Direction.UP, value % 2);
      break;
      case W:
        this.setSideField(Direction.WEST, value % 2);
      break;
    }
  }

  public boolean getSideHasPower(Direction side) {
    return this.poweredSides.get(side);
  }

  public int getSideField(Direction side) {
    return this.getSideHasPower(side) ? 1 : 0;
  }

  public void setSideField(Direction side, int pow) {
    this.poweredSides.put(side, (pow == 1));
  }

  @Override
  public void setFluid(FluidStack fluid) {
    tank.setFluid(fluid);
  }

  //  @Override
  public void tick() {
    //drain below but only to one of myself
    if (this.flowing > 0) {
      tickCableFlow();
    }
  }

  private void tickCableFlow() {
    for (final Direction exportToSide : UtilDirection.getAllInDifferentOrder()) {
      if (this.poweredSides.get(exportToSide)) {
        this.moveFluids(exportToSide, worldPosition.relative(exportToSide), TRANSFER_FLUID_PER_TICK / 4, tank);
      }
    }
  }
}
