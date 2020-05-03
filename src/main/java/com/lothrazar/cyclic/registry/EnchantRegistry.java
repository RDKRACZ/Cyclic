package com.lothrazar.cyclic.registry;

import com.lothrazar.cyclic.ConfigManager;
import com.lothrazar.cyclic.enchant.EnchantBeheading;
import com.lothrazar.cyclic.enchant.EnchantExcavation;
import com.lothrazar.cyclic.enchant.EnchantGrowth;
import com.lothrazar.cyclic.enchant.EnchantLaunch;
import com.lothrazar.cyclic.enchant.EnchantLifeLeech;
import com.lothrazar.cyclic.enchant.EnchantMagnet;
import com.lothrazar.cyclic.enchant.EnchantMultishot;
import com.lothrazar.cyclic.enchant.EnchantQuickdraw;
import com.lothrazar.cyclic.enchant.EnchantReach;
import com.lothrazar.cyclic.enchant.EnchantStep;
import com.lothrazar.cyclic.enchant.EnchantTraveller;
import com.lothrazar.cyclic.enchant.EnchantVenom;
import com.lothrazar.cyclic.enchant.EnchantXp;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnchantRegistry {

  @SubscribeEvent
  public static void onEnchantRegister(final RegistryEvent.Register<Enchantment> event) {
    IForgeRegistry<Enchantment> r = event.getRegistry();
    if (ConfigManager.ENCHANTMENTS.get()) {
      r.register(new EnchantBeheading(Enchantment.Rarity.COMMON, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND).setRegistryName("beheading"));
      r.register(new EnchantExcavation(Enchantment.Rarity.RARE, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND).setRegistryName("excavate"));
      r.register(new EnchantXp(Enchantment.Rarity.UNCOMMON, EnchantmentType.DIGGER, EquipmentSlotType.MAINHAND).setRegistryName("experience_boost"));
      r.register(new EnchantGrowth(Enchantment.Rarity.RARE, EnchantmentType.ALL, EquipmentSlotType.MAINHAND).setRegistryName("growth"));
      r.register(new EnchantLaunch(Enchantment.Rarity.RARE, EnchantmentType.ARMOR_FEET, EquipmentSlotType.FEET).setRegistryName("launch"));
      r.register(new EnchantLifeLeech(Enchantment.Rarity.UNCOMMON, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND).setRegistryName("life_leech"));
      r.register(new EnchantMagnet(Enchantment.Rarity.VERY_RARE, EnchantmentType.ALL, EquipmentSlotType.MAINHAND).setRegistryName("magnet"));
      r.register(new EnchantMultishot(Enchantment.Rarity.VERY_RARE, EnchantmentType.BOW, EquipmentSlotType.MAINHAND).setRegistryName("multishot"));
      r.register(new EnchantQuickdraw(Enchantment.Rarity.RARE, EnchantmentType.BOW, EquipmentSlotType.MAINHAND).setRegistryName("quickshot"));
      r.register(new EnchantReach(Enchantment.Rarity.RARE, EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST).setRegistryName("reach"));
      r.register(new EnchantStep(Enchantment.Rarity.RARE, EnchantmentType.ARMOR_LEGS, EquipmentSlotType.LEGS).setRegistryName("step"));
      r.register(new EnchantTraveller(Enchantment.Rarity.VERY_RARE, EnchantmentType.ARMOR_LEGS, EquipmentSlotType.LEGS).setRegistryName("traveler"));
      r.register(new EnchantVenom(Enchantment.Rarity.UNCOMMON, EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND).setRegistryName("venom"));
    }
  }
}