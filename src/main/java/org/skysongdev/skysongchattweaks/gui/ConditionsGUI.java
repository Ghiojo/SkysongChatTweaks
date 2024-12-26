package org.skysongdev.skysongchattweaks.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.skysongdev.skysongchattweaks.utils.Condition;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.skysongdev.skysongchattweaks.SkysongChatTweaks.getPlugin;

public class ConditionsGUI implements InventoryHolder {
    Inventory inventory;
    public int currentPage = 1;
    Pattern pattern = Pattern.compile("<[A-Za-z0-9\\#\\:\\']*>");

    public ConditionsGUI() {
        this.inventory = getPlugin().getServer().createInventory(this, 45);
        setupInventory();
    }

    public void setupInventory(ArrayList<Condition> conditions){
        clearInventory();
        ItemStack conditionItem;
        ItemMeta conditionMeta;
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
        for(int i = 0; i < conditions.size() && i < 27; i++){
            Material mat = Material.getMaterial(conditions.get(i).getItem());
            if(mat == null){
                mat = Material.BARRIER;
            }
            conditionItem = new ItemStack(mat);
            conditionMeta = conditionItem.getItemMeta();
            conditionMeta.displayName(getPlugin().getMiniMessage().deserialize(conditions.get(i).getName()));
            desclore = new ArrayList<>();
            desclore.add(getPlugin().getMiniMessage().deserialize(conditions.get(i).getDescription()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gold>ID: " + conditions.get(i).getId()));
            conditionMeta.lore(desclore);
            conditionItem.setItemMeta(conditionMeta);
            this.inventory.setItem(i + 9, conditionItem);
        }
    }

    public void setupInventory() {
        clearInventory();
        ItemStack conditionItem;
        ItemMeta conditionMeta;
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
        for(index = 0; index+(27*(currentPage-1)) < getPlugin().conditions.size() && index < 27; index++){
            Material mat = Material.getMaterial(getPlugin().conditions.get(index+(27*(currentPage-1))).getItem());
            if(mat == null){
                mat = Material.BARRIER;
            }
            conditionItem = new ItemStack(mat);
            conditionMeta = conditionItem.getItemMeta();
            conditionMeta.displayName(getPlugin().getMiniMessage().deserialize(getPlugin().conditions.get(index+(27*(currentPage-1))).getName()));
            desclore = new ArrayList<>();
            desclore.add(getPlugin().getMiniMessage().deserialize(getPlugin().conditions.get(index+(27*(currentPage-1))).getDescription()));
            desclore.add(getPlugin().getMiniMessage().deserialize("<gold>ID: " + getPlugin().conditions.get(index+(27*(currentPage-1))).getId()));
            conditionMeta.lore(desclore);
            conditionItem.setItemMeta(conditionMeta);
            this.inventory.setItem(index + 9, conditionItem);
        }

        if(index+(27*(currentPage-1)) < getPlugin().conditions.size()){
            setRightButton();
        }
        if(currentPage > 1){
            setLeftButton();
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

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
