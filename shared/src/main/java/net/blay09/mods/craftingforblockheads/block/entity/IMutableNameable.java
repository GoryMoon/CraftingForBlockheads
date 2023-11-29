package net.blay09.mods.craftingforblockheads.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

public interface IMutableNameable extends Nameable {
    void setCustomName(Component customName);
    Component getDefaultName();
}
