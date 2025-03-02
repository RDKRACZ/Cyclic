package com.lothrazar.cyclic.item;

import java.util.List;
import com.lothrazar.cyclic.capabilities.CapabilityProviderEnergyStack;
import com.lothrazar.cyclic.registry.ItemRegistry;
import com.lothrazar.cyclic.util.UtilItemStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemBaseCyclic extends Item {

  public static final String ENERGYTTMAX = "energyttmax";
  public static final String ENERGYTT = "energytt";
  public static final float INACCURACY_DEFAULT = 1.0F;
  public static final float VELOCITY_MAX = 1.5F;
  private boolean hasEnergy;

  public ItemBaseCyclic(Properties properties) {
    super(properties);
    ItemRegistry.ITEMSFIXME.add(this);
  }

  public void setUsesEnergy() {
    this.hasEnergy = true;
  }

  protected void shootMe(Level world, Player shooter, Projectile ball, float pitch, float velocityFactor) {
    if (world.isClientSide) {
      return;
    }
    Vec3 vector3d1 = shooter.getUpVector(1.0F);
    // pitch is degrees so can be -10, +10, etc
    Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), pitch, true);
    Vec3 vector3d = shooter.getViewVector(1.0F);
    Vector3f vector3f = new Vector3f(vector3d);
    vector3f.transform(quaternion);
    ball.shoot(vector3f.x(), vector3f.y(), vector3f.z(), velocityFactor * VELOCITY_MAX, INACCURACY_DEFAULT);
    //    worldIn.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
    //        SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
    world.addFreshEntity(ball);
  }

  protected ItemStack findAmmo(Player player, Item item) {
    for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
      ItemStack itemstack = player.getInventory().getItem(i);
      if (itemstack.getItem() == item) {
        return itemstack;
      }
    }
    return ItemStack.EMPTY;
  }

  public void tryRepairWith(ItemStack stackToRepair, Player player, Item target) {
    if (stackToRepair.isDamaged()) {
      ItemStack torches = this.findAmmo(player, target);
      if (!torches.isEmpty()) {
        torches.shrink(1);
        UtilItemStack.repairItem(stackToRepair);
      }
    }
  }

  public float getChargedPercent(ItemStack stack, int chargeTimer) {
    return BowItem.getPowerForTime(this.getUseDuration(stack) - chargeTimer);
  }

  @Override
  public boolean isBarVisible(ItemStack stack) {
    if (hasEnergy) {
      IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
      return storage != null; // && storage.getEnergyStored() > 0;
    }
    return super.isBarVisible(stack);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    tooltip.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    if (this.hasEnergy) {
      int current = 0;
      int energyttmax = 0;
      IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
      if (storage != null) {
        current = storage.getEnergyStored();
        energyttmax = storage.getMaxEnergyStored();
        tooltip.add(new TranslatableComponent(current + "/" + energyttmax).withStyle(ChatFormatting.RED));
      }
    }
  }

  @Override
  public int getBarWidth(ItemStack stack) {
    if (hasEnergy) {
      float current = 0;
      float max = 0;
      IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
      if (storage != null) {
        current = storage.getEnergyStored();
        max = storage.getMaxEnergyStored();
      }
      return (max == 0) ? 0 : Math.round(13.0F * current / max);
    }
    return super.getBarWidth(stack);
  }

  @OnlyIn(Dist.CLIENT)
  public void registerClient() {}

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
    if (this.hasEnergy) {
      return new CapabilityProviderEnergyStack(16000);
    }
    return super.initCapabilities(stack, nbt);
  }

  // ShareTag for server->client capability data sync
  @Override
  public CompoundTag getShareTag(ItemStack stack) {
    if (hasEnergy) {
      CompoundTag nbt = stack.getOrCreateTag();
      IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null).orElse(null);
      //on server  this runs . also has correct values.
      //set data for sync to client
      if (storage != null) {
        nbt.putInt(ENERGYTT, storage.getEnergyStored());
        nbt.putInt(ENERGYTTMAX, storage.getMaxEnergyStored());
      }
      return nbt;
    }
    return super.getShareTag(stack);
  }

  //clientside read tt
  @Override
  public void readShareTag(ItemStack stack, CompoundTag nbt) {
    if (hasEnergy && nbt != null) {
      CompoundTag stackTag = stack.getOrCreateTag();
      stackTag.putInt(ENERGYTT, nbt.getInt(ENERGYTT));
      stackTag.putInt(ENERGYTTMAX, nbt.getInt(ENERGYTTMAX));
    }
    super.readShareTag(stack, nbt);
  }
}
