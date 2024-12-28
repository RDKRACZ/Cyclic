package com.lothrazar.cyclic.block.cratemini;

import com.lothrazar.cyclic.data.Const;
import com.lothrazar.cyclic.gui.ScreenBase;
import com.lothrazar.cyclic.registry.TextureRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenCrateMini extends ScreenBase<ContainerCrateMini> {

  public ScreenCrateMini(ContainerCrateMini screenContainer, Inventory inv, Component titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(ms);
    super.render(ms, mouseX, mouseY, partialTicks);
    this.renderTooltip(ms, mouseX, mouseY);
  }

  @Override
  protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
    super.renderLabels(ms, mouseX, mouseY);
  }

  @Override
  protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(ms, TextureRegistry.INVENTORY);
    for (int colPos = 0; colPos < 5; colPos++) {
      for (int rowPos = 0; rowPos < 3; rowPos++) {
        this.drawSlot(ms,
            7 + (colPos + 2) * Const.SQ,
            15 + rowPos * Const.SQ);
      }
    }
  }
}
