package com.lothrazar.cyclic.item;

import com.lothrazar.cyclic.registry.SoundRegistry;
import com.lothrazar.cyclic.util.EntityUtil;
import com.lothrazar.cyclic.util.ItemStackUtil;
import com.lothrazar.cyclic.util.ParticleUtil;
import com.lothrazar.cyclic.util.SoundUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class TeleporterWandItem extends ItemBaseCyclic {

  private static final int TICK_REPAIR = 4;
  private static final int TICKS_USING = 93000;
  public static IntValue RANGE;

  public TeleporterWandItem(Properties properties) {
    super(properties);
  }

  @Override
  public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.BOW;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return TICKS_USING; //bow has 72000
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
    ItemStack itemstack = playerIn.getItemInHand(handIn);
    playerIn.startUsingItem(handIn);
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
  }

  @Override
  public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    if (entityIn instanceof Player && stack.isDamaged() && stack.getDamageValue() >= TICK_REPAIR) {
      this.tryRepairWith(stack, (Player) entityIn, Items.ENDER_PEARL);
    }
  }

  /**
   * pearls repair 4 durability per item, unlike default 1 per item like Torches
   */
  @Override
  public int getRepairPerItem() {
    return 4;
  }

  @Override
  public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int chargeTimer) {
    if (!(entity instanceof Player)) {
      return;
    }
    float percentageCharged = getChargedPercent(stack, chargeTimer);
    if (percentageCharged >= 0.98) { //full charge with a bit of buffer room
      Player player = (Player) entity;
      HitResult trace = player.pick(RANGE.get(), 0, true);
      if (trace.getType() == HitResult.Type.BLOCK) {
        BlockHitResult blockRayTraceResult = (BlockHitResult) trace;
        Direction face = blockRayTraceResult.getDirection();
        BlockPos newPos = blockRayTraceResult.getBlockPos().relative(face);
        BlockPos oldPos = player.blockPosition();
        if (EntityUtil.enderTeleportEvent(player, world, newPos)) { // && player.getPosition() != currentPlayerPos    
          ItemStackUtil.damageItem(player, stack);
          if (world.isClientSide) {
            ParticleUtil.spawnParticleBeam(world, ParticleTypes.PORTAL, oldPos, newPos, RANGE.get());
            SoundUtil.playSound(player, SoundRegistry.WARP_ECHO.get());
          }
        }
      }
    }
  }
}
