package com.lothrazar.cyclic.block.generatorfuel;

import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import com.lothrazar.cyclic.registry.TileRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockGeneratorFuel extends BlockCyclic {

  public BlockGeneratorFuel(Properties properties) {
    super(properties.strength(1.8F));
    registerDefaultState(defaultBlockState().setValue(LIT, false));
    this.setHasGui();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(BlockStateProperties.FACING).add(LIT);
  }

  @Override
  public boolean hasAnalogOutputSignal(BlockState bs) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState st, Level level, BlockPos pos) {
    return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
  }

  @Override
  public void registerClient() {
    ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutoutMipped());
    MenuScreens.register(MenuTypeRegistry.GENERATOR_FUEL.get(), ScreenGeneratorFuel::new);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileGeneratorFuel(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, TileRegistry.GENERATOR_FUEL.get(), world.isClientSide ? TileGeneratorFuel::clientTick : TileGeneratorFuel::serverTick);
  }
}
