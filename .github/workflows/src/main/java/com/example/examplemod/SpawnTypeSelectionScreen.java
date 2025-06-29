package com.example.examplemod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class SpawnTypeSelectionScreen extends Screen {
    private final SpawnLocation selectedLocation;
    private Button richButton;
    private Button poorButton;
    
    public SpawnTypeSelectionScreen(SpawnLocation location) {
        super(Component.literal("เลือกประเภทการเกิด"));
        this.selectedLocation = location;
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        // ปุ่มคนรวย (หักเพชร 5)
        richButton = Button.builder(Component.literal("คนรวย (เพชร 5)"), button -> selectRich())
            .pos(centerX - 100, centerY - 20)
            .size(200, 20)
            .build();
        
        // ปุ่มคนจน (หักเพชร 1)
        poorButton = Button.builder(Component.literal("คนจน (เพชร 1)"), button -> selectPoor())
            .pos(centerX - 100, centerY + 20)
            .size(200, 20)
            .build();
        
        addRenderableWidget(richButton);
        addRenderableWidget(poorButton);
        
        // ปุ่มย้อนกลับ
        addRenderableWidget(Button.builder(Component.literal("ย้อนกลับ"), button -> onBack())
            .pos(centerX - 50, centerY + 60)
            .size(100, 20)
            .build());
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            int diamonds = countDiamonds(player);
            
            // ตรวจสอบเพชรสำหรับคนรวย (ต้องการ 5 เพชร)
            if (diamonds >= 5) {
                richButton.active = true;
                richButton.setMessage(Component.literal("คนรวย (เพชร 5)"));
            } else {
                richButton.active = false;
                richButton.setMessage(Component.literal("คนรวย (เพชรไม่พอ)"));
            }
            
            // ตรวจสอบเพชรสำหรับคนจน (ต้องการ 1 เพชร)
            if (diamonds >= 1) {
                poorButton.active = true;
                poorButton.setMessage(Component.literal("คนจน (เพชร 1)"));
            } else {
                poorButton.active = false;
                poorButton.setMessage(Component.literal("คนจน (เพชรไม่พอ)"));
            }
        }
    }
    
    private int countDiamonds(Player player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == Items.DIAMOND) {
                count += stack.getCount();
            }
        }
        return count;
    }
    
    private void selectRich() {
        Player player = Minecraft.getInstance().player;
        if (player != null && countDiamonds(player) >= 5) {
            // หักเพชร 5
            removeDiamonds(player, 5);
            
            // ตั้งค่าสถานะคนรวย
            setRichStatus(player);
            
            // เปลี่ยนตำแหน่ง
            teleportPlayer(player);
            
            onClose();
        }
    }
    
    private void selectPoor() {
        Player player = Minecraft.getInstance().player;
        if (player != null && countDiamonds(player) >= 1) {
            // หักเพชร 1
            removeDiamonds(player, 1);
            
            // ตั้งค่าสถานะคนจน
            setPoorStatus(player);
            
            // เปลี่ยนตำแหน่ง
            teleportPlayer(player);
            
            onClose();
        }
    }
    
    private void removeDiamonds(Player player, int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() == Items.DIAMOND) {
                int toRemove = Math.min(remaining, stack.getCount());
                stack.shrink(toRemove);
                remaining -= toRemove;
            }
        }
    }
    
    private void setRichStatus(Player player) {
        // เลือดครึ่งหนึ่ง (10 หัวใจ)
        float maxHealth = player.getMaxHealth();
        player.setHealth(maxHealth / 2.0f);
        
        // อาหารเต็ม
        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(20);
        foodData.setSaturation(20.0f);
    }
    
    private void setPoorStatus(Player player) {
        // เลือด 1 หัวใจ (2 หน่วย)
        player.setHealth(2.0f);
        
        // อาหารไม่มี
        FoodData foodData = player.getFoodData();
        foodData.setFoodLevel(0);
        foodData.setSaturation(0.0f);
    }
    
    private void teleportPlayer(Player player) {
        // เปลี่ยนตำแหน่งไปยังสถานที่ที่เลือก
        double y = player.level().getHeight();
        player.setPos(selectedLocation.getX(), y, selectedLocation.getZ());
        
        // หาตำแหน่งที่ปลอดภัย (ไม่ตกจากที่สูง)
        while (player.level().getBlockState(player.blockPosition()).isAir() && player.getY() > 0) {
            player.setPos(player.getX(), player.getY() - 1, player.getZ());
        }
        player.setPos(player.getX(), player.getY() + 1, player.getZ());
        
        // ตั้งค่า spawn point ใหม่
        player.setRespawnPosition(player.level().dimension(), player.blockPosition(), 0.0f, true, false);
    }
    
    private void onBack() {
        Minecraft.getInstance().setScreen(new SpawnSelectionScreen());
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics);
        
        // วาดพื้นหลัง
        guiGraphics.fill(0, 0, width, height, 0x88000000);
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        // วาดชื่อหน้าจอ
        guiGraphics.drawCenteredString(font, title, centerX, 20, 0xFFFFFFFF);
        
        // วาดชื่อสถานที่ที่เลือก
        guiGraphics.drawCenteredString(font, 
            Component.literal("สถานที่: " + selectedLocation.getName()), 
            centerX, 50, 0xFFFFFFFF);
        
        // แสดงจำนวนเพชรในตัว
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            int diamonds = countDiamonds(player);
            guiGraphics.drawCenteredString(font, 
                Component.literal("เพชรในตัว: " + diamonds), 
                centerX, 80, 0xFFFFFFFF);
        }
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
} 