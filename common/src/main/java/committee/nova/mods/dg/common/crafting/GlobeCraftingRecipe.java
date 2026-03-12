package committee.nova.mods.dg.common.crafting;

import committee.nova.mods.dg.CommonClass;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class GlobeCraftingRecipe extends CustomRecipe {

	private int[] glassSlots = new int[]{0, 1, 2, 3, 5};
	private int[] blockSlots = new int[]{6, 7, 8};

	// 1.21.1: CustomRecipe constructor no longer takes ResourceLocation id.
	public GlobeCraftingRecipe(CraftingBookCategory category) {
		super(category);
	}

	// 1.21.1: matches/assemble use CraftingInput instead of CraftingContainer.
	@Override
	public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
		return !assemble(inv, null).isEmpty();
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull CraftingInput inv, HolderLookup.Provider provider) {
		for (int glassSlot : glassSlots) {
			if (inv.getItem(glassSlot).getItem() != Items.GLASS) return ItemStack.EMPTY;
		}
		if (!inv.getItem(4).isEmpty()) return ItemStack.EMPTY;

		ItemStack blockStack = ItemStack.EMPTY;
		for (int blockSlot : blockSlots) {
			if (!blockStack.isEmpty()) {
				if (blockStack.getItem() != inv.getItem(blockSlot).getItem()) return ItemStack.EMPTY;
			}
			blockStack = inv.getItem(blockSlot);
			if (blockStack.isEmpty()) return ItemStack.EMPTY;
			if (blockStack.getItem() instanceof BlockItem bi) {
				Block block = bi.getBlock();
				if (!block.defaultBlockState().is(CommonClass.BASE_BLOCK_TAG)) return ItemStack.EMPTY;
			} else {
				return ItemStack.EMPTY;
			}
		}
		Block block = ((BlockItem) blockStack.getItem()).getBlock();
		return CommonClass.globeBlockItem.getWithBase(block);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return CommonClass.globeCrafting;
	}
}
