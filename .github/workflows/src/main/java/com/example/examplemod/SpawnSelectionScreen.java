package com.example.examplemod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnSelectionScreen extends Screen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private static final int SCREEN_WIDTH = 400;
    private static final int SCREEN_HEIGHT = 300;
    private static final int CIRCLE_RADIUS = 80;
    private static final int BUTTON_SIZE = 40;
    
    private List<SpawnLocation> spawnLocations;
    private SpawnLocation selectedLocation;
    private int centerX, centerY;
    private Random random;
    
    public SpawnSelectionScreen() {
        super(Component.literal("เลือกสถานที่เกิด"));
        this.random = new Random();
        generateSpawnLocations();
    }
    
    private void generateSpawnLocations() {
        spawnLocations = new ArrayList<>();
        
        // สร้างสถานที่สุ่ม 6 จุด
        String[] locationNames = {
            "หมู่บ้าน", "ป่า", "ภูเขา", "ทะเลทราย", "ทะเล", "ถ้ำ"
        };
        
        // ใช้ Minecraft block textures
        ResourceLocation[] icons = {
            new ResourceLocation("textures/block/oak_planks.png"),
            new ResourceLocation("textures/block/oak_leaves.png"),
            new ResourceLocation("textures/block/stone.png"),
            new ResourceLocation("textures/block/sand.png"),
            new ResourceLocation("textures/block/water_still.png"),
            new ResourceLocation("textures/block/stone.png")
        };
        
        for (int i = 0; i < 6; i++) {
            int x = random.nextInt(2000) - 1000; // สุ่มพิกัด X ระหว่าง -1000 ถึง 1000
            int z = random.nextInt(2000) - 1000; // สุ่มพิกัด Z ระหว่าง -1000 ถึง 1000
            spawnLocations.add(new SpawnLocation(locationNames[i], icons[i], x, z));
        }
    }
    
    @Override
    protected void init() {
        super.init();
        
        centerX = width / 2;
        centerY = height / 2;
        
        // สร้างปุ่มสถานที่ในรูปแบบวงกลม
        for (int i = 0; i < spawnLocations.size(); i++) {
            SpawnLocation location = spawnLocations.get(i);
            double angle = (2 * Math.PI * i) / spawnLocations.size();
            int buttonX = centerX + (int)(CIRCLE_RADIUS * Math.cos(angle)) - BUTTON_SIZE / 2;
            int buttonY = centerY + (int)(CIRCLE_RADIUS * Math.sin(angle)) - BUTTON_SIZE / 2;
            
            // สร้างปุ่มแบบง่ายแทน ImageButton
            Button locationButton = Button.builder(location.getDisplayName(), button -> selectLocation(location))
                .pos(buttonX, buttonY)
                .size(BUTTON_SIZE, BUTTON_SIZE)
                .build();
            
            addRenderableWidget(locationButton);
        }
        
        // ปุ่มปิด
        addRenderableWidget(Button.builder(Component.literal("ปิด"), button -> onClose())
            .pos(centerX - 50, centerY + CIRCLE_RADIUS + 20)
            .size(100, 20)
            .build());
    }
    
    private void selectLocation(SpawnLocation location) {
        selectedLocation = location;
        Minecraft.getInstance().setScreen(new SpawnTypeSelectionScreen(location));
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        
        // วาดพื้นหลัง
        guiGraphics.fill(0, 0, width, height, 0x88000000);
        
        // วาดวงกลมหลัก
        drawCircle(guiGraphics, centerX, centerY, CIRCLE_RADIUS, 0x44FFFFFF);
        
        // วาดชื่อหน้าจอ
        guiGraphics.drawCenteredString(font, title, centerX, 20, 0xFFFFFFFF);
        
        // วาดคำแนะนำ
        guiGraphics.drawCenteredString(font, Component.literal("คลิกที่สถานที่เพื่อเลือก"), centerX, 40, 0xFFFFFFFF);
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    private void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x * x + y * y <= radius * radius) {
                    guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
                }
            }
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
} 