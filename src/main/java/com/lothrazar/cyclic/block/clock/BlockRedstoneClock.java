package com.lothrazar.cyclic.block.clock;

import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import com.lothrazar.cyclic.registry.TileRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class BlockRedstoneClock extends BlockCyclic {

  public BlockRedstoneClock(Properties properties) {
    super(properties.strength(1.8F));
    this.registerDefaultState(this.stateDefinition.any().setValue(LIT, Boolean.valueOf(false)));
    this.setHasGui();
  }

  @Override
  public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
    return blockState.getValue(LIT) ? getPower(blockAccess, pos, side.getOpposite()) : 0;
  }

  @Override
  public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
    return blockState.getValue(LIT) ? getPower(blockAccess, pos, side.getOpposite()) : 0;
  }

  private int getPower(BlockGetter world, BlockPos pos, Direction side) {
    TileRedstoneClock tile = (TileRedstoneClock) world.getBlockEntity(pos);
    return tile.getPower();
  }

  @Override
  public boolean isSignalSource(BlockState state) {
    return true;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileRedstoneClock(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, TileRegistry.CLOCK.get(), world.isClientSide ? TileRedstoneClock::clientTick : TileRedstoneClock::serverTick);
  }

  @Override
  public void registerClient() {
    MenuScreens.register(MenuTypeRegistry.CLOCK.get(), ScreenClock::new);
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
  }
}
