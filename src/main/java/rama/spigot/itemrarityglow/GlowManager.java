package rama.spigot.itemrarityglow;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import rama.spigot.itemrarityglow.util.Color;

import java.util.ArrayList;
import java.util.List;

public class GlowManager {

    private final List<Color> colors;
    private final Plugin main;

    public GlowManager(Plugin main){
        this.main = main;
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

}
