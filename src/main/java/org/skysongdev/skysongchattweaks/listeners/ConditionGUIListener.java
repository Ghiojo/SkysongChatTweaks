package org.skysongdev.skysongchattweaks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.skysongdev.skysongchattweaks.SkysongChatTweaks;
import org.skysongdev.skysongchattweaks.gui.ConditionsGUI;

public class ConditionGUIListener implements Listener {
    @EventHandler
    public void onGUIClick(InventoryClickEvent event){
        if(event.getInventory().getHolder() instanceof ConditionsGUI){
            event.setCancelled(true);
            if(event.getCurrentItem() == null){
                return;
            }
            if(event.getCurrentItem().getItemMeta().getPersistentDataContainer().has(SkysongChatTweaks.optionKey)){
                PersistentDataContainer container = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
                ConditionsGUI gui = (ConditionsGUI) event.getInventory().getHolder();
                if(container.has(SkysongChatTweaks.optionKey, PersistentDataType.STRING)){
                    switch(container.get(SkysongChatTweaks.optionKey, PersistentDataType.STRING)){
                        case "next":
                            gui.currentPage++;
                            gui.setupInventory();
                            break;
                        case "back":
                            gui.currentPage--;
                            gui.setupInventory();
                            break;
                    }
                }
            }
        }
    }
}
