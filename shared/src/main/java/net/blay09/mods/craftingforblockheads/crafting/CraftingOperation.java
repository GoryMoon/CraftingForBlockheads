package net.blay09.mods.craftingforblockheads.crafting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.blay09.mods.craftingforblockheads.registry.CraftingForBlockheadsRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingOperation {

    private final CraftingContext context;
    private final Recipe<?> recipe;

    private final Multimap<IntList, IngredientToken> tokensByIngredient = ArrayListMultimap.create();
    private final List<IngredientToken> ingredientTokens = new ArrayList<>();
    private final List<Ingredient> missingIngredients = new ArrayList<>();

    private NonNullList<ItemStack> lockedInputs;
    private int missingIngredientsMask;

    public CraftingOperation(final CraftingContext context, Recipe<?> recipe) {
        this.context = context;
        this.recipe = recipe;
    }

    public CraftingOperation withLockedInputs(@Nullable NonNullList<ItemStack> lockedInputs) {
        this.lockedInputs = lockedInputs;
        return this;
    }

    public CraftingOperation prepare() {
        tokensByIngredient.clear();
        ingredientTokens.clear();
        missingIngredients.clear();
        missingIngredientsMask = 0;

        final var ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            final var ingredient = ingredients.get(i);
            final var lockedInput = lockedInputs != null ? lockedInputs.get(i) : ItemStack.EMPTY;
            final var stackingIds = ingredient.getStackingIds();
            final var itemProviders = context.getItemProviders();
            for (int j = 0; j < itemProviders.size(); j++) {
                final var itemProvider = itemProviders.get(j);
                IngredientToken ingredientToken;
                if (lockedInput.isEmpty()) {
                    ingredientToken = itemProvider.findIngredient(ingredient, tokensByIngredient.get(stackingIds));
                } else {
                    ingredientToken = itemProvider.findIngredient(lockedInput, tokensByIngredient.get(stackingIds));
                }
                if (ingredientToken != null) {
                    tokensByIngredient.put(stackingIds, ingredientToken);
                    if (ingredient.getItems().length > 1) {
                        if (lockedInputs == null) {
                            lockedInputs = NonNullList.withSize(ingredients.size(), ItemStack.EMPTY);
                        }
                        lockedInputs.set(i, ingredientToken.peek());
                    }
                    ingredientTokens.add(ingredientToken);
                } else {
                    missingIngredients.add(ingredient);
                    missingIngredientsMask |= 1 << i;
                }
            }
        }

        return this;
    }

    public boolean canCraft() {
        return missingIngredients.isEmpty();
    }

    public ItemStack craft(AbstractContainerMenu menu, RegistryAccess registryAccess) {
        return craft(menu, registryAccess, recipe);
    }

    private <C extends Container, T extends Recipe<C>> ItemStack craft(AbstractContainerMenu menu, RegistryAccess registryAccess, T recipe) {
        final var craftingContainer = new TransientCraftingContainer(menu, 3, 3);
        final var recipeTypeHandler = CraftingForBlockheadsRegistry.getRecipeWorkshopHandler(recipe);
        if (recipeTypeHandler == null) {
            return ItemStack.EMPTY;
        }

        for (int i = 0; i < ingredientTokens.size(); i++) {
            final var ingredientToken = ingredientTokens.get(i);
            final var matrixSlot = recipeTypeHandler.mapToMatrixSlot(recipe, i);
            craftingContainer.setItem(matrixSlot, ingredientToken.consume());
        }

        return recipeTypeHandler.assemble(recipe, craftingContainer, registryAccess);
    }

    public NonNullList<ItemStack> getLockedInputs() {
        return lockedInputs;
    }

    public List<Ingredient> getMissingIngredients() {
        return missingIngredients;
    }

    public int getMissingIngredientsMask() {
        return missingIngredientsMask;
    }
}
