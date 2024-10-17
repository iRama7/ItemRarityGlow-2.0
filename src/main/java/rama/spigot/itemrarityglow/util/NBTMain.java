package rama.spigot.itemrarityglow.util;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NBTMain {

    private static final String key = "Glow";

    public void addNBT(String value, ItemStack item){
        NBT.modify(item, nbt -> {
            nbt.setString(key, value);
        });
    }

    public String getNBT(ItemStack item){

        if (item == null || item.getType().equals(Material.AIR)) {
            return null;
        }

        return NBT.get(item, nbt -> (String) nbt.getString(key));
    }

    public void removeNBT(ItemStack item){
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.removeKey(key);
        item.setItemMeta(nbtItem.getItem().getItemMeta());
    }

}
