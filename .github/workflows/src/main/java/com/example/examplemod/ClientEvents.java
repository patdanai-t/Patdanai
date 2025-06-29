package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import java.util.Timer;
import java.util.TimerTask;

@Mod.EventBusSubscriber(modid = ExampleMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerDeathEvent event) {
        LOGGER.info("Player death detected on client side");
        
        // Schedule GUI opening after death animation
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getInstance().execute(() -> {
                    if (Minecraft.getInstance().screen == null) {
                        LOGGER.info("Opening spawn selection screen");
                        Minecraft.getInstance().setScreen(new SpawnSelectionScreen());
                    }
                });
            }
        }, 2000); // 2 seconds delay
    }
} 