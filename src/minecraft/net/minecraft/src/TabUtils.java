package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabUtils {

	public static void loadTabs(ArrayList<Tab> tabList, BaseMod mod) {
		TabCrafting workbenchTab = new TabCrafting(mod);
		tabList.add(workbenchTab);
		guiToBlock.put(GuiCrafting.class, new ItemStack(Block.workbench));
		
		Tab smeltingTab = new TabSmelting(mod);
		tabList.add(smeltingTab);
		smeltingTab.equivalentCraftingStations.add(new ItemStack(Block.stoneOvenActive));
		guiToBlock.put(GuiFurnace.class, new ItemStack(Block.stoneOvenIdle));
	}
	
	public static ItemStack getItemFromGui(GuiContainer screen) {
		return guiToBlock.get(screen.getClass());
	}
	
	private static Map<Class<? extends GuiContainer>, ItemStack> guiToBlock = new HashMap();
	
	private static Field recipeListField;
	private static Field outputItemStackField;
	private static Field inputItemStacksListField;
	private static Field buildcraftBlockField;
	
	private static Map BTWMap(Object recipeListInstance) {
		HashMap recipes = new HashMap();
		try {
			List recipeListBTW = (List)recipeListField.get(recipeListInstance);
			for(Object obj : recipeListBTW) {
				ItemStack outputItem = (ItemStack)outputItemStackField.get(obj);
				List inputItemStacksList = (List)inputItemStacksListField.get(obj);
				ItemStack[] inputItemStacks = new ItemStack[inputItemStacksList.size()];
				for(int i = 0; i < inputItemStacksList.size(); i++) {
					inputItemStacks[i] = (ItemStack)inputItemStacksList.get(i);
				}
				recipes.put(inputItemStacks, outputItem);
			}
		}
		catch (Exception e) { e.printStackTrace(); }
		return recipes;
	}
}
