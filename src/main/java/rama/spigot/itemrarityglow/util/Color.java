package rama.spigot.itemrarityglow.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Color {

    private Scoreboard scoreboard;
    private String identifier;
    private int weight;
    private String type; //FLASHY - STATIC - RAINBOW
    private Team team;
    private ChatColor color;
    private Plugin main;
    private List<Material> materials;


    //Requires valid color enum
    public Color(Plugin main, String identifier, int weight, String type, String color, Scoreboard scoreboard){ //Creates new scoreboard and registers a new team with the desired color
        this.identifier = identifier;
        this.weight = weight;
        this.type = type;
        this.main = main;
        this.scoreboard = scoreboard;
        this.team = scoreboard.registerNewTeam("t_" + identifier);
        this.color = ChatColor.valueOf(color);
        team.setColor(this.color);
        materials = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getWeight() {
        return weight;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public Team getTeam() {
        return team;
    }

    public boolean containsMaterial(Material material){
        return materials.contains(material);
    }

    public void addMaterial(Material material){
        materials.add(material);
    }

    public String toString(){
        return materials.toString();
    }

    public void addItem(Entity item){
        team.addEntry(item.getUniqueId().toString());
        item.setGlowing(true);
    }

    public boolean isItemGlowing(Entity item){
        return team.hasEntry(item.getUniqueId().toString());
    }

    public void removeEntity(Entity item){
        team.removeEntry(item.getUniqueId().toString());
    }

}
