package com.example.examplemod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class SpawnLocation {
    private final String name;
    private final ResourceLocation icon;
    private final int x;
    private final int z;
    
    public SpawnLocation(String name, ResourceLocation icon, int x, int z) {
        this.name = name;
        this.icon = icon;
        this.x = x;
        this.z = z;
    }
    
    public String getName() {
        return name;
    }
    
    public ResourceLocation getIcon() {
        return icon;
    }
    
    public int getX() {
        return x;
    }
    
    public int getZ() {
        return z;
    }
    
    public Component getDisplayName() {
        return Component.literal(name);
    }
} 