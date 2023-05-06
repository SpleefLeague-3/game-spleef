package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.world.FakeUtil;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.projectiles.HookshotProjectile;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.AbilityUtils;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class MobilityHookshot extends AbilityMobility {

    private static final double HOOKLIFE = 2D;

    public static AbilityStats init() {
        return init(MobilityHookshot.class)
                .setCustomModelData(6)
                .setName("Hookshot")
                .setDescription("Fire a grappling hook into a target block, attaching you to it for %HOOKLIFE% seconds. Reactivate while attached to a block to fire yourself in the direction you are facing, destroying blocks you pass.")
                .setUsage(10D);
    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = HookshotProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 4;
        projectileStats.fireRange = 10D;
        projectileStats.collidable = true;
        projectileStats.size = 1.2D;
        projectileStats.noClip = true;
        projectileStats.bounces = 2;
        projectileStats.customModelDatas = Lists.newArrayList(29);
    }

    private HookshotProjectile hookshot = null;

    /**
     * Called every 0.1 seconds (2 ticks)
     */
    @Override
    public void update() {
        if (hookshot != null && hookshot.isHooked()) {
            if (hookshot.bO()) {
                getPlayer().setGravity(false);
                Vector dir = hookshot.getHookPos().subtract(getPlayer().getLocation().toVector()).normalize();
                if (hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) < 0.2) {
                    getPlayer().setVelocity(new Vector(0, 0, 0));
                } else {
                    if (hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) > 1.25) {
                        getUser().getBattle().getGameWorld().breakBlocks(getPlayer().getBoundingBox().expand(0.15, 0., 0.15, 0.15, 0.15, 0.15));
                    }
                    getPlayer().setVelocity(dir.multiply(Math.min(1.1, hookshot.getHookPos().distance(getPlayer().getLocation().toVector()) / 5.)));
                }
            } else {
                hookshot = null;
                getPlayer().setGravity(true);
                applyCooldown();
            }
        }
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        if (hookshot == null || !hookshot.bO()) {
            net.minecraft.world.entity.Entity test = (HookshotProjectile) getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats).get(0);
            hookshot.setMaxHookLife(HOOKLIFE);
            getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1.4f);
            applyCooldown(projectileStats.lifeTicks / 20.);
            return false;
        } else {
            Location facing = getPlayer().getLocation().clone();
            facing.setPitch(Math.max(-60, Math.min(60, facing.getPitch())));
            AbilityUtils.startFling(getUser(), facing.getDirection(), 0.3);
            Entity hookedEntity = hookshot.getHookedEntity();
            if (hookedEntity != null) {
                if (hookedEntity instanceof Player) {
                    CorePlayer cp = Core.getInstance().getPlayers().get(hookedEntity.getUniqueId());
                    if (FakeUtil.isOnGround(cp)) {
                        hookedEntity.setVelocity(hookedEntity.getVelocity().clone().setY(0).add(facing.getDirection().clone().normalize().multiply(-0.4)));
                    } else {
                        hookedEntity.setVelocity(facing.getDirection().clone().normalize().multiply(-0.4));
                    }
                } else {
                    hookedEntity.setVelocity(facing.getDirection().clone().normalize().multiply(-0.4));
                }
            }
            hookshot.getBukkitEntity().remove();
            hookshot = null;
            return true;
        }
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        hookshot = null;
        getPlayer().setGravity(true);
        AbilityUtils.stopFling(getUser());
    }

}
