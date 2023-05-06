/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.game;

import com.destroystokyo.paper.Namespaced;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.menu.hotbars.main.HeldItemMenu;
import com.spleefleague.core.player.BattleState;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.CorePlayerCollectibles;
import com.spleefleague.core.player.collectible.Holdable;
import com.spleefleague.core.vendor.Vendorable;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.spleef.Spleef;

import com.spleefleague.spleef.util.SpleefUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import se.llbit.nbt.CompoundTag;
import se.llbit.nbt.ListTag;
import se.llbit.nbt.StringTag;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author NickM13
 */
public class Shovel extends Holdable {

    private static final Collection<Namespaced> destroyableKeys = new HashSet<>();
    
    public static void init() {
        for (Material material : SpleefUtils.breakableBlocks) {
            destroyableKeys.add(material.getKey());
        }
        
        Vendorable.registerParentType(Shovel.class);

        loadCollectibles(Shovel.class);

        if (!Vendorables.contains(Shovel.class, "default")) {
            Shovel.create(Shovel.class, "default", "Default Shovel");
        }
        
        InventoryMenuAPI.createItemHotbar(0, "shovelHotbarItem")
                .setName(cp -> cp.getCollectibles().getActiveName(Shovel.class))
                .setDisplayItem(cp -> {
                    if (cp.getBattle().getGameWorld().isEditable()) {
                        return getGameItem(cp.getCollectibles().getActiveIcon(Shovel.class));
                    } else {
                        return cp.getCollectibles().getActiveIcon(Shovel.class);
                    }
                })
                .setDescription(cp -> cp.getCollectibles().getActive(Shovel.class).getDescription())
                .setAvailability(cp -> cp.isInBattle() &&
                        cp.getBattleState() == BattleState.BATTLER &&
                        cp.getBattle().getPlugin() instanceof Spleef &&
                        !cp.getBattle().getBattler(cp).isFallen());

        InventoryMenuAPI.createItemHotbar(8, "shovelWorldItem")
                .setName(cp -> cp.getCollectibles().getActiveName(Shovel.class))
                .setDisplayItem(cp -> cp.getCollectibles().getActiveIcon(Shovel.class))
                .setDescription(cp -> cp.getCollectibles().getActive(Shovel.class).getDescription())
                .setAvailability(cp -> cp.isInGlobal() && cp.getCollectibles().hasActive(Shovel.class) && cp.getCollectibles().isEnabled(Shovel.class))
                .setAction(cp -> cp.getCollectibles().getActive(Shovel.class).activateEffect(cp));
    }

    public static void createMenu() {
        InventoryMenuItem shovelMenu = CorePlayerCollectibles.createCollectibleContainer(Shovel.class,
                InventoryMenuAPI.createItemDynamic()
                        .setName("Shovels")
                        .setDescription("Set your active shovel")
                        .setDisplayItem(Material.DIAMOND_SHOVEL, 10));

        HeldItemMenu.getItem().getLinkedChest().addMenuItem(shovelMenu, 0, 0);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createActiveMenuItem(Shovel.class), 0, 1);
        HeldItemMenu.getItem().getLinkedChest().addMenuItem(CorePlayerCollectibles.createToggleMenuItem(Shovel.class), 0, 2);
    }

    @DBField private ShovelEffect shovelEffect = new ShovelEffect();

    public Shovel() {
        super();
        this.material = Material.DIAMOND_SHOVEL;
    }
    
    public Shovel(String identifier, String displayName) {
        super();
        this.identifier = identifier;
        this.name = displayName;
        this.material = Material.DIAMOND_SHOVEL;
    }

    public void clearEffect() {
        shovelEffect.setType(ShovelEffect.ShovelEffectType.NONE);
        saveChanges();
    }

    public void setEffect(ShovelEffect.ShovelEffectType type) {
        shovelEffect.setType(type);
        saveChanges();
    }

    @Override
    public boolean isDefault(CorePlayer cp) {
        return unlockType.equals(UnlockType.DEFAULT) || super.isDefault(cp);
    }
    
    @Override
    public boolean isAvailableToPurchase(CorePlayer corePlayer) {
        return true;
    }

    /**
     * This is for the Held Item Collectible right click action, NOT ingame!
     *
     * @param corePlayer Core Player
     */
    @Override
    public void onRightClick(CorePlayer corePlayer) {

    }

    public void activateEffect(CorePlayer corePlayer) {
        shovelEffect.activate(corePlayer);
    }

    /**
     * Creates a shovel item that can only break snow while in adventure mode
     *
     * @return Shovel ItemStack
     */
    public static ItemStack getGameItem(ItemStack activeDisplayItem) {
        ItemStack itemStack = activeDisplayItem.clone();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.addEnchant(Enchantment.DIG_SPEED, 9, true);
            itemMeta.setDestroyableKeys(destroyableKeys);
            //itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
    
}
