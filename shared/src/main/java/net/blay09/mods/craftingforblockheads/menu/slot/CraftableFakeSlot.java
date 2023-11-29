package net.blay09.mods.craftingforblockheads.menu.slot;

import net.blay09.mods.craftingforblockheads.crafting.RecipeWithStatus;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CraftableFakeSlot extends AbstractFakeSlot {

    private RecipeWithStatus craftable;

    public CraftableFakeSlot(Container container, int slotId, int x, int y) {
        super(container, slotId, x, y);
    }

    @Override
    public ItemStack getItem() {
        return craftable != null ? craftable.resultItem() : ItemStack.EMPTY;
    }

    @Override
    public boolean hasItem() {
        return craftable != null;
    }

    @Override
    public boolean isActive() {
        return craftable != null;
    }

    public void setCraftable(@Nullable RecipeWithStatus craftable) {
        this.craftable = craftable;
    }

    @Nullable
    public RecipeWithStatus getCraftable() {
        return craftable;
    }

}
