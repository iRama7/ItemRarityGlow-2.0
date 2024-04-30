package rama.spigot.itemrarityglow.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Color {

    private Scoreboard scoreboard;
    private String identifier;
    private int weight;
    private String type;
    private Team team;
    private ChatColor color;
    private Plugin main;
    private List<Material> materials;


    //Requires valid color enum
    public Color(Plugin main, String identifier, int weight, String type, String color){ //Creates new scoreboard and registers a new team with the desired color
        this.identifier = identifier;
        this.weight = weight;
        this.type = type;
        this.main = main;
        this.scoreboard = main.getServer().getScoreboardManager().getNewScoreboard();
        this.team = scoreboard.registerNewTeam("team_" + identifier);
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

}
