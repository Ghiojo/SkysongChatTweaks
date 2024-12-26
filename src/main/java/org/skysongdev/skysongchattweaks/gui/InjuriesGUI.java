package org.skysongdev.skysongchattweaks.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.skysongdev.skysongchattweaks.utils.Injury;

import java.util.ArrayList;
import java.util.List;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class InjuriesGUI implements InventoryHolder {
    Inventory inventory;
    ArrayList<Injury> viewableInjuries;
    public int currentPage = 1;
    Player player;

    public InjuriesGUI(Player player) {
        this.inventory = getPlugin().getServer().createInventory(this, 45);
        this.player = player;
        fetchViewableInjuries();
        setupInventory();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setupInventory() {
        clearInventory();
        ItemStack injuryItem;
        ItemMeta injuryMeta;
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        String[] description;
        List<Component> desclore;
        String descbuffer;
        for(int i = 0; i < 9; i++){
            this.inventory.setItem(i, borderItem);
        }
        for(int i = 36; i < 45; i++){
            this.inventory.setItem(i, borderItem);
        }
        int index;
        for(index = 0; index+(27*(currentPage-1)) < viewableInjuries.size() && index < 27; index++){
            Material mat = Material.getMaterial(viewableInjuries.get(index+(27*(currentPage-1))).getItem());
            if(mat == null){
                mat = Material.BARRIER;
            }
            injuryItem = new ItemStack(mat);
            injuryMeta = injuryItem.getItemMeta();
            injuryMeta.displayName(getPlugin().getMiniMessage().deserialize(viewableInjuries.get(index+(27*(currentPage-1))).getName()));
            desclore = new ArrayList<>();
            desclore.add(getPlugin().getMiniMessage().deserialize(viewableInjuries.get(index+(27*(currentPage-1))).getDescription()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gold>ID: " + viewableInjuries.get(index+(27*(currentPage-1))).getId()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gray>Severity: " + viewableInjuries.get(index+(27*(currentPage-1))).getSeverity()));
            injuryMeta.lore(desclore);
            injuryItem.setItemMeta(injuryMeta);
            this.inventory.setItem(index + 9, injuryItem);
        }

        if(index+(27*(currentPage-1)) < viewableInjuries.size()){
            setRightButton();
        }
        if(currentPage > 1){
            setLeftButton();
        }
    }

    public void setupInventory(ArrayList<Injury> injuries){
        clearInventory();
        ItemStack injuryItem;
        ItemMeta injuryMeta;
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        String[] description;
        List<Component> desclore;
        String descbuffer;
        for(int i = 0; i < 9; i++){
            this.inventory.setItem(i, borderItem);
        }
        for(int i = 36; i < 45; i++){
            this.inventory.setItem(i, borderItem);
        }
        for(int i = 0; i < injuries.size() && i < 27; i++){
            if(!injuries.get(i).doesPlayerHavePermission(player)){
                continue;
            }
            Material mat = Material.getMaterial(injuries.get(i).getItem());
            if(mat == null){
                mat = Material.BARRIER;
            }
            injuryItem = new ItemStack(mat);
            injuryMeta = injuryItem.getItemMeta();
            injuryMeta.displayName(getPlugin().getMiniMessage().deserialize(injuries.get(i).getName()));
            desclore = new ArrayList<>();
            desclore.add(getPlugin().getMiniMessage().deserialize(injuries.get(i).getDescription()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gold>ID: " + injuries.get(i).getId()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gray>Severity: " + injuries.get(i).getSeverity()));
            injuryMeta.lore(desclore);
            injuryItem.setItemMeta(injuryMeta);
            this.inventory.setItem(i + 9, injuryItem);
        }
    }


    public void setLeftButton() {
        ItemStack leftButton = new ItemStack(Material.ARROW);
        ItemMeta leftMeta = leftButton.getItemMeta();
        PersistentDataContainer container = leftMeta.getPersistentDataContainer();
        leftMeta.displayName(getPlugin().getMiniMessage().deserialize("<gray>Previous Page"));
        container.set(getPlugin().optionKey, PersistentDataType.STRING, "back");
        leftButton.setItemMeta(leftMeta);
        this.inventory.setItem(39, leftButton);
    }

    public void setRightButton() {
        ItemStack rightButton = new ItemStack(Material.ARROW);
        ItemMeta rightMeta = rightButton.getItemMeta();
        PersistentDataContainer container = rightMeta.getPersistentDataContainer();
        rightMeta.displayName(getPlugin().getMiniMessage().deserialize("<gray>Next Page"));
        container.set(getPlugin().optionKey, PersistentDataType.STRING, "next");
        rightButton.setItemMeta(rightMeta);
        this.inventory.setItem(41, rightButton);
    }

    public void clearInventory() {
        for(int i = 9; i < 36; i++){
            this.inventory.setItem(i, null);
        }
        ItemStack borderItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for(int i = 0; i < 9; i++){
            this.inventory.setItem(i, borderItem);
        }
        for(int i = 36; i < 45; i++){
            this.inventory.setItem(i, borderItem);
        }
    }

    public void fetchViewableInjuries() {
        viewableInjuries = new ArrayList<>();
        for(Injury injury : getPlugin().injuries) {
            if(injury.doesPlayerHavePermission(player)) {
                viewableInjuries.add(injury);
            }
        }
    }
    public void fetchViewableInjuries(String severity){
        fetchViewableInjuries();
        ArrayList<Injury> filteredInjuries = new ArrayList<>();
        for (Injury injury : viewableInjuries) {
            if (injury.getSeverity().equalsIgnoreCase(severity)) {
                filteredInjuries.add(injury);
            }
        }
        viewableInjuries = filteredInjuries;
    }


}
