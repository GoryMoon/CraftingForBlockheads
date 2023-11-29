package net.blay09.mods.craftingforblockheads.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractFakeSlot extends Slot {

    private ItemStack displayStack = ItemStack.EMPTY;

    public AbstractFakeSlot(Container container, int slotId, int x, int y) {
        super(container, slotId, x, y);
    }

    public void setDisplayStack(ItemStack itemStack) {
        this.displayStack = itemStack;
    }

    @Override
    public ItemStack getItem() {
        return displayStack;
    }

    @Override
    public boolean hasItem() {
        return !displayStack.isEmpty();
    }

    @Override
    public void set(ItemStack stack) {
        // NOP
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setChanged() {
        // NOP
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }
}
