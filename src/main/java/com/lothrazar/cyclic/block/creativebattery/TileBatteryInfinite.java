package com.lothrazar.cyclic.block.creativebattery;

import java.util.HashMap;
import java.util.Map;
import com.lothrazar.cyclic.block.TileBlockEntityCyclic;
import com.lothrazar.cyclic.capabilities.block.CustomEnergyStorage;
import com.lothrazar.cyclic.registry.TileRegistry;
import com.lothrazar.cyclic.util.UtilDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileBatteryInfinite extends TileBlockEntityCyclic {

  static final int MAX = Integer.MAX_VALUE;

  static enum Fields {
    N, E, S, W, U, D;
  }

  CustomEnergyStorage energy = new CustomEnergyStorage(MAX, MAX);
  private Map<Direction, Boolean> poweredSides;
  private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);

  public TileBatteryInfinite(BlockPos pos, BlockState state) {
    super(TileRegistry.BATTERY_INFINITE.get(), pos, state);
    poweredSides = new HashMap<Direction, Boolean>();
    for (Direction f : Direction.values()) {
      poweredSides.put(f, true);
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
  public void invalidateCaps() {
    energyCap.invalidate();
    super.invalidateCaps();
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityEnergy.ENERGY) {
      return energyCap.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void load(CompoundTag tag) {
    for (Direction f : Direction.values()) {
      poweredSides.put(f, tag.getBoolean("flow_" + f.getName()));
    }
    energy.deserializeNBT(tag.getCompound(NBTENERGY));
    super.load(tag);
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    for (Direction f : Direction.values()) {
      tag.putBoolean("flow_" + f.getName(), poweredSides.get(f));
    }
    tag.put(NBTENERGY, energy.serializeNBT());
    super.saveAdditional(tag);
  }

  //  @Override
  public Component getDisplayName() {
    return new TextComponent(getType().getRegistryName().getPath());
  }

  public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, TileBatteryInfinite e) {
    e.tick();
  }

  public static <E extends BlockEntity> void clientTick(Level level, BlockPos blockPos, BlockState blockState, TileBatteryInfinite e) {
    e.tick();
  }

  public void tick() {
    energy.receiveEnergy(MAX, false);
    //now go
    this.tickCableFlow();
  }

  private void tickCableFlow() {
    for (final Direction exportToSide : UtilDirection.getAllInDifferentOrder()) {
      if (this.poweredSides.get(exportToSide)) {
        moveEnergy(exportToSide, MAX / 4);
      }
    }
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
    }
    return -1;
  }

  @Override
  public void setField(int field, int value) {
    switch (Fields.values()[field]) {
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
}
