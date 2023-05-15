# HowManyItems 
v4.2.2 Unofficial backported from b1.8.1 to beta 1.5_01  

**v2**  
  
Thanks to [Rek](https://github.com/rekadoodle) for making the [original mod](https://github.com/rekadoodle/HowManyItems/releases/tag/v4.2) 
Thanks to Logan for [porting the mod to b1.8.1](https://www.mediafire.com/file/bd1e8w8nh5l1pg5)  
Backported by MagicD3VIL  
  
### INSTALL  
* Requires [ModLoader B1.5_01v4](https://archive.org/details/modloader-b1.5_01v4)
1. Backup your minecraft.jar (b1.5_01.jar)  
2. Delete META-INF  
3. Install ModLoader B1.5_01v4  
4. Copy this .zip into /.minecraft/mods/ or your local mod folder  
5. Start up Minecraft Beta 1.5_01 and play with HMI! 

### CHANGELOG
* v2 - Changed the way how the item names are read from game data and how the search bar code works.  
       Now sub-items like colored wool, stairs, and other items and blocks based on the damage index (meta) can be searched by the name of the sub-item directly like color, material, etc. instead of searching for the name of the root item.  
       This also fixes displayed names for blocks and items if in the code their class is based on extending another block's or item's class.
       Fixed how background rectangle behind item names is rendered in inventory.
       Refactoring.
* v1 - Ported the unofficial version of the original mod (v4.2.1 by Logan#9337) from b1.8.1 to b1.5_01. The Creative mode option has been removed since it's missing from b1.5_01.  
  
* Downloaded from https://archive.org/details/hmi_backport_b1.5_01 *  
