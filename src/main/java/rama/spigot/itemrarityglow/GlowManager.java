package rama.spigot.itemrarityglow;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import rama.spigot.itemrarityglow.util.Color;

import java.util.ArrayList;
import java.util.List;

public class GlowManager implements Listener {

    private final List<Color> colors;
    private final ItemRarityGlow main;
    private final Scoreboard scoreboard;

    public GlowManager(ItemRarityGlow main, Scoreboard scoreboard){
        this.main = main;
        this.scoreboard = scoreboard;
        colors = new ArrayList<>();
    }

    public void addColor(Color c){
        colors.add(c);
    }

    public boolean containsColor(String identifier){
        for(Color c : colors){
            if(c.getIdentifier().equals(identifier)){
                return true;
            }
        }
        return false;
    }

    public Color getColor(String identifier){
        for(Color c : colors){
            if(c.getIdentifier().equals(identifier)){
                return c;
            }
        }
        return null;
    }

    public void debugColors(){
        for(Color c : colors){
            Bukkit.getConsoleSender().sendMessage(c.toString());
        }
    }


    //Adding glow to dropped items
    @EventHandler
    public void itemDropEvent(ItemSpawnEvent e){
        Item entity = e.getEntity();
        ItemStack item = entity.getItemStack();
        Material material = item.getType();
        Color color = getMostWeightColor(material);

        if(color != null){
            color.addItem(entity);
            if(main.debug()) {
                main.log("&e[&6DEBUG&e] &eAdding color (&f" + color.getIdentifier() + "&e) to material &f" + material.toString(), true, null);
            }
        }

    }


    //Updating player scoreboard when joining
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e){
        e.getPlayer().setScoreboard(scoreboard);
    }

    //Returns most weight color that contains material or null if not found
    private Color getMostWeightColor(Material material){
        Color mwc = null;
        for(Color color : colors){
            if(color.containsMaterial(material)){
                if(mwc == null){
                    mwc = color;
                }
                if(color.getWeight() > mwc.getWeight()){
                    mwc = color;
                }
            }
        }
        return mwc;
    }

}
