package rama.spigot.itemrarityglow;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import rama.spigot.itemrarityglow.util.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlowManager implements Listener {

    private final List<Color> colors;
    private final ItemRarityGlow main;
    private final Scoreboard scoreboard;

    private List<Entity> flashyItems;
    private List<Entity> rainbowItems;
    private HashMap<Integer, Color> rainbowColors;

    public GlowManager(ItemRarityGlow main, Scoreboard scoreboard){
        this.main = main;
        this.scoreboard = scoreboard;
        colors = new ArrayList<>();
        flashyItems = new ArrayList<>();
        rainbowColors = new HashMap<>();
        rainbowItems = new ArrayList<>();
        initFlashyLooper();
        initRainbowLooper();
    }

    public void addColor(Color c){
        colors.add(c);
    }

    public void buildRainbowHashMap(){
        String colorsString = main.getConfig().getString("Effects.Rainbow.colors");
        String[] colors = colorsString.split("-");
        for(int i = 0; i < colors.length; i++){
            rainbowColors.put(i, getColor(colors[i]));
        }

        if(main.debug()){
            main.log("&e[&6DEBUG&e] &eAdded &f" + rainbowColors.size() + "&e colors to the rainbow effect.", true, null);
        }

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

            if(color.getType().equals("STATIC")) {

                color.addItem(entity);
                if (main.debug()) {
                    main.log("&e[&6DEBUG&e] &eAdding color (&f" + color.getIdentifier() + "&e) to material &f" + material.toString(), true, null);
                }

            }

            if(color.getType().equals("FLASHY")){
                color.addItem(entity);
                flashyItems.add(entity);
                if (main.debug()) {
                    main.log("&e[&6DEBUG&e] &eAdding Flashy color (&f" + color.getIdentifier() + "&e) to material &f" + material.toString(), true, null);
                }
            }

            if(color.getType().equals("RAINBOW")){
                rainbowItems.add(entity);
                if (main.debug()) {
                    main.log("&e[&6DEBUG&e] &eAdding Rainbow color (&f" + color.getIdentifier() + "&e) to material &f" + material.toString(), true, null);
                }
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

    //Initializes the flashy looper
    private void initFlashyLooper(){
        int interval = main.getConfig().getInt("Effects.Flashy.interval");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, () -> switchFlashyItems(), 0, interval);
    }

    //Initializes the rainbow looper
    private void initRainbowLooper(){
        int interval = main.getConfig().getInt("Effects.Rainbow.interval");

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, () -> {

            for(Entity item : rainbowItems){
                if(!item.isGlowing()){
                    rainbowColors.get(0).addItem(item); //Si no está brillando el añado el primero
                }

                int itemCurrentColor = getRainbowColor(item);
                rainbowColors.get(itemCurrentColor).removeEntity(item); //Remuevo el color

                if(itemCurrentColor + 1 >= rainbowColors.size()){ //Si es el último le asigno -1 para que la próxima instrucción le asigne el primer color
                    itemCurrentColor = -1;
                }

                rainbowColors.get(itemCurrentColor + 1).addItem(item);

            }

        },0, interval);

    }

    private int getRainbowColor(Entity item){
        for(int i = 0; i < rainbowColors.size(); i++){
            if(rainbowColors.get(i).isItemGlowing(item)){
                return i;
            }
        }
        return 0;
    }

    //Enables or disables glowing in flashy items
    private void switchFlashyItems(){

        for(Entity item : flashyItems){

            if(item == null){
                continue;
            }

            item.setGlowing(!item.isGlowing());

        }

    }

    public List<Color> getColors(){
        return colors;
    }

}
