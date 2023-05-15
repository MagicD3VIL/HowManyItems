package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;

public class mod_HowManyItems extends BaseMod
{
	public static void addModTab(Tab tab) {
    	if(tab != null) {
    		modTabs.add(tab);
    	}
    }

	public String getName()
	{
		return "How Many Items";
	}
	
	public String Version()
    {
        return "v4.2.1 Unofficial, MagicD3VIL backport for b1.5_01";
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
	
	public mod_HowManyItems()
    {
		thisMod = this;
		HMIConfig.init();
		ModLoader.RegisterKey(this, HMIConfig.toggleOverlay, false);
		
		ModLoader.SetInGUIHook(this, true, false);
		ModLoader.SetInGameHook(this, true, false);
    }
	
	private GuiOverlay overlay;
	public static mod_HowManyItems thisMod;
	
	public static void onSettingChanged() {
		if(thisMod.overlay != null) thisMod.overlay.initGui();
		HMIConfig.writeConfig();
	}
	
	public void OnTickInGUI(Minecraft mc, GuiScreen guiscreen) {
		if(guiscreen instanceof GuiContainer) {
			GuiContainer screen = (GuiContainer)guiscreen;
			if(HMIConfig.overlayEnabled) {
				if(GuiOverlay.screen != screen || screen.width != overlay.width || screen.height != overlay.height
		    			|| screen.xSize != overlay.xSize || screen.ySize != overlay.ySize) {
					overlay = new GuiOverlay(screen);
		        }
				overlay.onTick();
			}
			Utils.drawStoredToolTip();
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
						newFilter = Utils.hoveredItem((GuiContainer)guiscreen, posX, posY);
						if (newFilter == null) {
							newFilter = GuiOverlay.hoverItem;
						}
						if(newFilter == null) {
							if(guiscreen instanceof GuiRecipeViewer)
								newFilter = ((GuiRecipeViewer)guiscreen).getHoverItem();
						}
						if(newFilter != null) {
							pushRecipe(guiscreen, newFilter, getUses);
						}
						else {
							if(HMIConfig.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && HMIConfig.fastSearch) {
								GuiOverlay.focusSearchBox();
							}
						}
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.prevRecipe.keyCode)) {
				if(!keyHeldLastTick) {
					if ((guiscreen instanceof GuiRecipeViewer || guiscreen instanceof GuiOverlay) && !GuiOverlay.searchBoxFocused()) {
						if(guiscreen instanceof GuiOverlay && GuiOverlay.screen instanceof GuiRecipeViewer) guiscreen = GuiOverlay.screen;
						if(guiscreen instanceof GuiRecipeViewer) ((GuiRecipeViewer) guiscreen).pop();
					}
					else {
						if(HMIConfig.overlayEnabled && guiscreen == GuiOverlay.screen && !GuiOverlay.searchBoxFocused() && HMIConfig.fastSearch)
							if(!GuiOverlay.emptySearchBox()) GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(HMIConfig.clearSearchBox.keyCode == HMIConfig.focusSearchBox.keyCode
					&& Keyboard.isKeyDown(HMIConfig.clearSearchBox.keyCode)) {
				
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						if(!GuiOverlay.searchBoxFocused())
						GuiOverlay.clearSearchBox();
						GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.clearSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					GuiOverlay.clearSearchBox();
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.focusSearchBox.keyCode)) {
				if (guiscreen instanceof GuiContainer
						|| guiscreen instanceof GuiOverlay) {
					if(System.currentTimeMillis() > focusCooldown) {
						focusCooldown = System.currentTimeMillis() + 800L;
						GuiOverlay.focusSearchBox();
					}
				}
			}
			else if(Keyboard.isKeyDown(HMIConfig.allRecipes.keyCode)) {
				if (guiscreen instanceof GuiOverlay) {
					guiscreen = GuiOverlay.screen;
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
			if (ModLoader.isGUIOpen(GuiContainer.class) && !GuiOverlay.searchBoxFocused()) {
				HMIConfig.overlayEnabled = !HMIConfig.overlayEnabled;
				HMIConfig.writeConfig();
				if(overlay != null) overlay.toggle();
			}
		}
    }
	
	public static void pushRecipe(GuiScreen gui, ItemStack item, boolean getUses) {
		if(Utils.mc.thePlayer.inventory.getItemStack() == null) {
			if (gui instanceof GuiRecipeViewer) {
				((GuiRecipeViewer) gui).push(item, getUses);
			}
			else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
				Utils.mc.setIngameNotInFocus();
				GuiRecipeViewer newgui = new GuiRecipeViewer(item, getUses, gui);
				Utils.mc.currentScreen = newgui;
				ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);
				int i = scaledresolution.getScaledWidth();
				int j = scaledresolution.getScaledHeight();
	            newgui.setWorldAndResolution(Utils.mc, i, j);
			}
		}
	}
	
	public static void pushTabBlock(GuiScreen gui, ItemStack item) {
		if (gui instanceof GuiRecipeViewer) {
			((GuiRecipeViewer) gui).pushTabBlock(item);
		}
		else if (!GuiOverlay.searchBoxFocused() && getTabs().size() > 0){
			Utils.mc.setIngameNotInFocus();
			GuiRecipeViewer newgui = new GuiRecipeViewer(item, gui);
			Utils.mc.currentScreen = newgui;
			ScaledResolution scaledresolution = new ScaledResolution(Utils.mc.gameSettings, Utils.mc.displayWidth, Utils.mc.displayHeight);
			int i = scaledresolution.getScaledWidth();
			int j = scaledresolution.getScaledHeight();
	        newgui.setWorldAndResolution(Utils.mc, i, j);
		}
	}
	
	//Used to avoid reflection
	public static void drawRect(int i, int j, int k, int l, int i1) {
		Utils.gui.drawRect(i, j, k, l, i1);
	}
	
    public static ArrayList<Tab> getTabs() {
    	if(tabs == null) {
    		allTabs = new ArrayList<Tab>();
    		
    		TabUtils.loadTabs(allTabs, thisMod);
    		
    		for(Tab tab : modTabs) {
    			allTabs.add(tab);
    		}
    		HMIConfig.readConfig();
    		tabs = HMIConfig.orderTabs();
    	}
        return tabs;
	}
    
    public static void tabOrderChanged(boolean[] tabEnabled, Tab[] tabOrder) {
    	HMIConfig.tabOrderChanged(tabEnabled, tabOrder);
		tabs = HMIConfig.orderTabs();
    }
    
    private static ArrayList<Tab> tabs;
    public static ArrayList<Tab> allTabs;
    private static ArrayList<Tab> modTabs = new ArrayList<Tab>();

	
}
