package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class mod_HowManyItems extends BaseMod
{
	public static void addModTab(HMITab tab) {
    	if(tab != null) {
    		modTabs.add(tab);
    	}
    }

	public String getName()
	{
		return "How Many Items v4.2.2";
	}
	
	public String Version()
    {
        return getName() + ", MagicD3VIL backport to b1.5_01 [v"+ changelog.length+"]";
    }

	public String Name() {
		return "How Many Items";
	}
	public String Description() {
		return "TMI but cooler.";
	}
	public String Icon() {
		return "/hmi/modmenu.png";
	}
	public static final String[] changelog = {
			"Changed the way how the item names are read from game data and how the search bar code works. Now sub-items like colored wool, stairs, and other items and blocks based on the damage index (meta) can be searched by the name of the sub-item directly like color, material, etc. instead of searching for the name of the root item. This also fixes displayed names for blocks and items if in the code their class is based on extending another block's or item's class. Fixed how background rectangle behind item names is rendered in inventory. Refactoring.",

			"Ported the unofficial version of the original mod (v4.2.1 by Logan#9337) from b1.8.1 to b1.5_01. The Creative mode option has been removed since it's missing from b1.5_01."
	};
	
	public mod_HowManyItems()
    {
		thisMod = this;
		HMIConfig.init();
		ModLoader.RegisterKey(this, HMIConfig.toggleOverlay, false);
		
		ModLoader.SetInGUIHook(this, true, false);
		ModLoader.SetInGameHook(this, true, false);
    }
	
	private HMIGuiOverlay overlay;
	public static mod_HowManyItems thisMod;
	
	public static void onSettingChanged() {
		if(thisMod.overlay != null) thisMod.overlay.initGui();
		HMIConfig.writeConfig();
	}
	
	public void OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		if(guiscreen instanceof GuiContainer) {
			GuiContainer screen = (GuiContainer)guiscreen;
			if(HMIConfig.overlayEnabled) {
				if(HMIGuiOverlay.screen != screen || screen.width != overlay.width || screen.height != overlay.height
		    			|| screen.xSize != overlay.xSize || screen.ySize != overlay.ySize) {
					overlay = new HMIGuiOverlay(screen);
		        }
				overlay.onTick();
			}
			HMIUtils.drawStoredToolTip();
			if(Keyboard.isKeyDown(HMIConfig.pushRecipe.keyCode) || Keyboard.isKeyDown(HMIConfig.pushUses.keyCode)) {
				if(!keyHeldLastTick) {
					boolean getUses = Keyboard.isKeyDown(HMIConfig.pushUses.keyCode);
					if (guiscreen instanceof GuiContainer) {
						ItemStack newFilter = null;

						ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
						int i = scaledresolution.getScaledWidth();
						int j = scaledresolution.getScaledHeight();
						int posX = (Mouse.getEventX() * i) / mc.displayWidth;
						int posY = j - (Mouse.getEventY() * j) / mc.displayHeight - 1;
						newFilter = HMIUtils.hoveredItem((GuiContainer)guiscreen, posX, posY);
						if (newFilter == null) {
							newFilter = HMIGuiOverlay.hoverItem;
						}
						if(newFilter == null) {
							if(guiscreen instanceof HMIGuiRecipeViewer)
								newFilter = ((HMIGuiRecipeViewer)guiscreen).getHoverItem();
						}
						if(newFilter != null) {
							pushRecipe(guiscreen, newFilter, getUses);
						}
						else {
							if(HMIConfig.overlayEnabled && guiscreen == HMIGuiOverlay.screen && !HMIGuiOverlay.searchBoxFocused() && HMIConfig.fastSearch) {
								HMIGuiOverlay.focusSearchBox();
							}
						}
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.prevRecipe.keyCode)) {
				if(!keyHeldLastTick) {
					if ((guiscreen instanceof HMIGuiRecipeViewer || guiscreen instanceof HMIGuiOverlay) && !HMIGuiOverlay.searchBoxFocused()) {
						if(guiscreen instanceof HMIGuiOverlay && HMIGuiOverlay.screen instanceof HMIGuiRecipeViewer) guiscreen = HMIGuiOverlay.screen;
						if(guiscreen instanceof HMIGuiRecipeViewer) ((HMIGuiRecipeViewer) guiscreen).pop();
					}
					else {
						if(HMIConfig.overlayEnabled && guiscreen == HMIGuiOverlay.screen && !HMIGuiOverlay.searchBoxFocused() && HMIConfig.fastSearch)
							if(!HMIGuiOverlay.emptySearchBox()) HMIGuiOverlay.focusSearchBox();
					}
				}
			}
			else if(HMIConfig.clearSearchBox.keyCode == HMIConfig.focusSearchBox.keyCode
					&& Keyboard.isKeyDown(HMIConfig.clearSearchBox.keyCode)) {
				
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof HMIGuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						if(!HMIGuiOverlay.searchBoxFocused())
						HMIGuiOverlay.clearSearchBox();
						HMIGuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.clearSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof HMIGuiOverlay) {
					HMIGuiOverlay.clearSearchBox();
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.focusSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof HMIGuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						HMIGuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.allRecipes.keyCode)) {
				if (guiscreen instanceof HMIGuiOverlay) {
					guiscreen = HMIGuiOverlay.screen;
				}
				pushRecipe(guiscreen, null, false);
			}
			else {
				keyHeldLastTick = false;
			}
			if(Keyboard.isKeyDown(HMIConfig.pushRecipe.keyCode) || Keyboard.isKeyDown(HMIConfig.pushUses.keyCode) || Keyboard.isKeyDown(HMIConfig.prevRecipe.keyCode)) {
				keyHeldLastTick = true;
			}
			
		}
	}
	
	public void OnTickInGame(Minecraft minecraft)
    {
		if(minecraft.currentScreen == null && Keyboard.isKeyDown(HMIConfig.allRecipes.keyCode) && !keyHeldLastTick) {
			keyHeldLastTick = true;
			pushRecipe(null, null, false);
		}
    }
	
	public static boolean keyHeldLastTick = false;
	private static long focusCooldown = 0L;
	
	public void KeyboardEvent(KeyBinding event)
    {
		if (event == HMIConfig.toggleOverlay) {
			if (ModLoader.isGUIOpen(GuiContainer.class) && !HMIGuiOverlay.searchBoxFocused()) {
				HMIConfig.overlayEnabled = !HMIConfig.overlayEnabled;
				HMIConfig.writeConfig();
				if(overlay != null) overlay.toggle();
			}
		}
    }
	
	public static void pushRecipe(GuiScreen gui, ItemStack item, boolean getUses) {
		if(HMIUtils.mc.thePlayer.inventory.getItemStack() == null) {
			if (gui instanceof HMIGuiRecipeViewer) {
				((HMIGuiRecipeViewer) gui).push(item, getUses);
			}
			else if (!HMIGuiOverlay.searchBoxFocused() && getTabs().size() > 0){
				HMIUtils.mc.setIngameNotInFocus();
				HMIGuiRecipeViewer newgui = new HMIGuiRecipeViewer(item, getUses, gui);
				HMIUtils.mc.currentScreen = newgui;
				ScaledResolution scaledresolution = new ScaledResolution(HMIUtils.mc.gameSettings, HMIUtils.mc.displayWidth, HMIUtils.mc.displayHeight);
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
	            newgui.setWorldAndResolution(HMIUtils.mc, i, j);
			}
		}
	}
	
	public static void pushTabBlock(GuiScreen gui, ItemStack item) {
		if (gui instanceof HMIGuiRecipeViewer) {
			((HMIGuiRecipeViewer) gui).pushTabBlock(item);
		}
		else if (!HMIGuiOverlay.searchBoxFocused() && getTabs().size() > 0){
			HMIUtils.mc.setIngameNotInFocus();
			HMIGuiRecipeViewer newgui = new HMIGuiRecipeViewer(item, gui);
			HMIUtils.mc.currentScreen = newgui;
			ScaledResolution scaledresolution = new ScaledResolution(HMIUtils.mc.gameSettings, HMIUtils.mc.displayWidth, HMIUtils.mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
	        newgui.setWorldAndResolution(HMIUtils.mc, i, j);
		}
	}
	
	//Used to avoid reflection
	public static void drawRect(int i, int j, int k, int l, int i1) {
		HMIUtils.gui.drawRect(i, j, k, l, i1);
	}
	
    public static ArrayList<HMITab> getTabs() {
    	if(tabs == null) {
    		allTabs = new ArrayList<HMITab>();
    		
    		HMITabUtils.loadTabs(allTabs, thisMod);
    		
    		for(HMITab tab : modTabs) {
    			allTabs.add(tab);
    		}
    		HMIConfig.readConfig();
    		tabs = HMIConfig.orderTabs();
    	}
        return tabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, HMITab[] tabOrder) {
    	HMIConfig.tabOrderChanged(tabEnabled, tabOrder);
		tabs = HMIConfig.orderTabs();
    }
    
    private static ArrayList<HMITab> tabs;
    public static ArrayList<HMITab> allTabs;
    private static ArrayList<HMITab> modTabs = new ArrayList<HMITab>();

	
}
