package com.lothrazar.cyclic.item.elemental;

import java.util.List;
import java.util.Locale;
import com.lothrazar.cyclic.item.ItemBaseCyclic;
import com.lothrazar.cyclic.registry.SoundRegistry;
import com.lothrazar.cyclic.util.ItemStackUtil;
import com.lothrazar.cyclic.util.LevelWorldUtil;
import com.lothrazar.cyclic.util.ShapeUtil;
import com.lothrazar.cyclic.util.SoundUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AntimatterEvaporatorWandItem extends ItemBaseCyclic {

  private static final int SIZE = 4;
  private static final String NBT_MODE = "mode";
  public static final int COOLDOWN = 15;

  public enum EvaporateMode implements StringRepresentable {

    WATER, LAVA, GENERIC;

    @Override
    public String getSerializedName() {
      return this.name().toLowerCase(Locale.ENGLISH);
    }

    public EvaporateMode getNext() {
      switch (this) {
        case WATER:
          return LAVA;
        case LAVA:
          return GENERIC;
        case GENERIC:
          return WATER;
      }
      return WATER;
    }
  }

  public AntimatterEvaporatorWandItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    BlockPos pos = context.getClickedPos();
    Level world = context.getLevel();
    Direction face = context.getClickedFace();
    ItemStack itemstack = context.getItemInHand();
    EvaporateMode fluidMode = EvaporateMode.values()[itemstack.getOrCreateTag().getInt(NBT_MODE)];
    List<BlockPos> area = ShapeUtil.cubeSquareBase(pos.relative(face), SIZE, 1);
    //    AtomicBoolean removed = new AtomicBoolean(false);
    switch (fluidMode) {
      case GENERIC:
      break;
      case LAVA:
      break;
      case WATER:
      break;
      default:
      break;
    }
    int countSuccess = 0;
    boolean tryHere = false;
    for (BlockPos posTarget : area) {
      BlockState blockHere = world.getBlockState(posTarget);
      FluidState fluidHere = blockHere.getFluidState();
      if (fluidHere == null) {
        continue;
      }
      tryHere = false;
      if (fluidMode == EvaporateMode.GENERIC && fluidHere.getType() != null
          && fluidHere.getType() != Fluids.EMPTY) {
        tryHere = true;
      }
      else if (fluidMode == EvaporateMode.WATER && fluidHere.is(FluidTags.WATER)) {
        tryHere = true;
      }
      else if (fluidMode == EvaporateMode.LAVA && fluidHere.is(FluidTags.LAVA)) {
        tryHere = true;
      }
      if (tryHere && LevelWorldUtil.removeFlowingLiquid(world, posTarget, true)) {
        countSuccess++;
      }
    }
    if (countSuccess > 0) {
      Player player = context.getPlayer();
      player.swing(context.getHand());
      ItemStackUtil.damageItem(player, itemstack);
      if (world.isClientSide) {
        SoundUtil.playSound(world, pos, SoundRegistry.PSCHEW_FIRE.get());
      }
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    super.appendHoverText(stack, worldIn, tooltip, flagIn);
    tooltip.add(getModeTooltip(stack).withStyle(ChatFormatting.AQUA));
  }

  @Override
  public void onCraftedBy(ItemStack stack, Level worldIn, Player playerIn) {
    stack.getOrCreateTag().putInt(NBT_MODE, EvaporateMode.WATER.ordinal());
    super.onCraftedBy(stack, worldIn, playerIn);
  }

  private static TranslatableComponent getModeTooltip(ItemStack stack) {
    EvaporateMode mode = EvaporateMode.values()[stack.getOrCreateTag().getInt(NBT_MODE)];
    return new TranslatableComponent("item.cyclic.antimatter_wand.tooltip0",
        new TranslatableComponent(String.format("item.cyclic.antimatter_wand.mode.%s",
            mode.getSerializedName())));
  }

  public static void toggleMode(Player player, ItemStack stack) {
    if (player.getCooldowns().isOnCooldown(stack.getItem())) {
      return;
    }
    EvaporateMode mode = EvaporateMode.values()[stack.getOrCreateTag().getInt(NBT_MODE)];
    stack.getOrCreateTag().putInt(NBT_MODE, mode.getNext().ordinal());
    player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN);
    if (player.level.isClientSide) {
      player.displayClientMessage(getModeTooltip(stack), true);
      SoundUtil.playSound(player, SoundRegistry.TOOL_MODE.get());
    }
  }
}
