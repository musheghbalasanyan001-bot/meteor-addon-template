package com.example.addon;

import net.meteorclient.api.addons.MeteorAddon;
import net.meteorclient.api.systems.modules.Modules;
import net.meteorclient.api.systems.modules.Module;
import net.meteorclient.api.events.world.TickEvent;
import net.meteorclient.api.systems.modules.Categories;
import net.meteorclient.api.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

// 1. ՍԱ ԳԼԽԱՎՈՐ ԱԴՈՆՆ Է, ՈՐԸ ՄԻԱՑՆՈՒՄ Է ՄՈԴԸ
public class Addon extends MeteorAddon {
    @Override
    public void onInitialize() {
        // Ավտոմատ գրանցում ենք մեր սարքած Trial Clicker-ը
        Modules.get().add(new TrialClicker());
    }

    @Override
    public void onRegisterCategories() {
        // Թողնում ենք դատարկ
    }
}

// 2. ՍԱ ՔՈ ՈՒԶԱԾ ՏՐԻԱԼ ՔԼԻՔԵՐՆ Է՝ ԻՐ ԲՈԼՈՐ ԿԱՐԳԱՎՈՐՈՒՄՆԵՐՈՎ
class TrialClicker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public enum Mode {
        Any,          // Ցանկացած Wind Burst
        Wind_Burst_1, // Միայն Wind Burst I (1)
        Wind_Burst_2, // Միայն Wind Burst II (2)
        Wind_Burst_3  // Միայն Wind Burst III (3)
    }

    private final Setting<Mode> clickMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("Zachar Level")
            .description("Ընտրիր թե որ մակարդակի վրա ավտոմատ աջ քլիք անի:")
            .defaultValue(Mode.Any)
            .build()
    );

    public TrialClicker() {
        // Անունը դրված է Trial Clicker, կհայտնվի Combat բաժնում
        super(Categories.Combat, "trial-clicker", "Automatically clicks Heavy Core based on Wind Burst level.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        // Ստուգում է՝ արդյոք նայում ես Heavy Core բլոկին
        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.crosshairTarget;
            BlockPos pos = blockHit.getBlockPos();

            if (mc.world.getBlockState(pos).getBlock() == Blocks.HEAVY_CORE) {
                
                // Ստուգում է էկրանի տեքստը (Title)
                if (mc.inGameHud != null && mc.inGameHud.getTitle() != null) {
                    String titleText = mc.inGameHud.getTitle().getString().toLowerCase();
                    
                    if (titleText.contains("wind burst") || titleText.contains("windburst")) {
                        boolean shouldClick = false;
                        
                        // Ստուգում ենք քո ընտրած մակարդակը մենյուից
                        if (clickMode.get() == Mode.Any) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_1 && (titleText.contains(" i ") || titleText.endsWith(" i") || titleText.contains("1"))) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_2 && (titleText.contains(" ii ") || titleText.endsWith(" ii") || titleText.contains("2"))) {
                            shouldClick = true;
                        } else if (clickMode.get() == Mode.Wind_Burst_3 && (titleText.contains(" iii ") || titleText.endsWith(" iii") || titleText.contains("3"))) {
                            shouldClick = true;
                        }

                        // Եթե տեքստը համընկավ ընտրածիդ հետ՝ ԱԿՆԹԱՐԹԱՅԻՆ ԱՋ ՔԼԻՔ
                        if (shouldClick) {
                            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);
                        }
                    }
                }
            }
        }
    }
}
