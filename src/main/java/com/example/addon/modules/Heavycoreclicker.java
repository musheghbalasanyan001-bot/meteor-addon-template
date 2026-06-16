package com.example.addon.modules;

import com.example.addon.AddonTemplate;
import net.meteorclient.api.systems.modules.Module;
import net.meteorclient.api.events.world.TickEvent;
import net.meteorclient.api.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class Heavycoreclicker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public enum Mode {
        Any,
        Wind_Burst_1,
        Wind_Burst_2,
        Wind_Burst_3
    }

    private final Setting<Mode> clickMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Zachar Level")
            .description("Ընտրիր թե որ մակարդակի վրա ավտոմատ աջ քլիք անի:")
            .defaultValue(Mode.Any)
            .build()
    );

    public Heavycoreclicker() {
        super(AddonTemplate.CATEGORY, "trial-clicker", "Automatically clicks Heavy Core based on Wind Burst level.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.crosshairTarget;
            BlockPos pos = blockHit.getBlockPos();

            if (mc.world.getBlockState(pos).getBlock() == Blocks.HEAVY_CORE) {
                if (mc.inGameHud != null && mc.inGameHud.getTitle() != null) {
                    String titleText = mc.inGameHud.getTitle().getString().toLowerCase();
                    
                    if (titleText.contains("wind burst") || titleText.contains("windburst")) {
                        boolean shouldClick = false;
                        
                        if (clickMode.get() == Mode.Any) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_1 && (titleText.contains(" i ") || titleText.endsWith(" i") || titleText.contains("1"))) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_2 && (titleText.contains(" ii ") || titleText.endsWith(" ii") || titleText.contains("2"))) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_3 && (titleText.contains(" iii ") || titleText.endsWith(" iii") || titleText.contains("3"))) {
                            shouldClick = true;
                        }

                        if (shouldClick) {
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);
                        }
                    }
                }
            }
        }
    }
}
