package com.lothrazar.cyclic.block.wireless.energy;

import com.lothrazar.cyclic.block.BlockCyclic;
import com.lothrazar.cyclic.registry.MenuTypeRegistry;
import com.lothrazar.cyclic.registry.TileRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockWirelessEnergy extends BlockCyclic {

  private static final double BOUNDS = 4;
  public static final VoxelShape AABB = Block.box(BOUNDS, BOUNDS, BOUNDS, 16 - BOUNDS, 16 - BOUNDS, 16 - BOUNDS);

  public BlockWirelessEnergy(Properties properties) {
    super(properties.strength(1.2F).noOcclusion());
    this.setHasGui();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(LIT);
  }

  @Override
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return AABB;
  }

  @Override
  public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
    return true;
  }

  @Override
  public void registerClient() {
    MenuScreens.register(MenuTypeRegistry.WIRELESS_ENERGY.get(), ScreenWirelessEnergy::new);
    ItemBlockRenderTypes.setRenderLayer(this, RenderType.cutoutMipped());
    //    ClientRegistry.bindTileEntityRenderer(TileRegistry.wireless_transmitter, RenderTransmit::new);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TileWirelessEnergy(pos, state);
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, TileRegistry.WIRELESS_ENERGY.get(), world.isClientSide ? TileWirelessEnergy::clientTick : TileWirelessEnergy::serverTick);
  }

  @Override
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileWirelessEnergy tileentity = (TileWirelessEnergy) worldIn.getBlockEntity(pos);
      if (tileentity != null && tileentity.gpsSlots != null) {
        for (int s = 0; s < tileentity.gpsSlots.getSlots(); s++) {
          Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), tileentity.gpsSlots.getStackInSlot(s));
        }
      }
      worldIn.updateNeighbourForOutputSignal(pos, this);
    }
    super.onRemove(state, worldIn, pos, newState, isMoving);
  }
}
