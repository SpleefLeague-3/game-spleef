package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Lists;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.ProjectileWorld;
import com.spleefleague.core.world.projectile.ProjectileWorldPlayer;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveMeltingBurst extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveMeltingBurst.class)
                .setCustomModelData(7)
                .setName("Melting Burst")
                .setDescription("Throw a sticky bomb, latching onto the first terrain hit. After %BURST_DELAY% seconds, detonate in a large radius.")
                .setUsage(10);
    }
    
    private static final double BURST_DELAY = 1.5D;
    private static final double BURST_RADIUS = 4D;

    public static class MeltingProjectile extends FakeEntitySnowball {

        public MeltingProjectile(ProjectileWorld<? extends ProjectileWorldPlayer> projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        protected boolean onBlockHit(BlockRaycastResult blockRaycastResult, Vector intersection) {
            super.blockBounce(blockRaycastResult, intersection);
            return true;
        }

        @Override
        public void a(RemovalReason entity_removalreason) {
            Entity craftEntity = getBukkitEntity();
            BlockPosition pos = new BlockPosition(
                    craftEntity.getLocation().getBlockX(),
                    craftEntity.getLocation().getBlockY(),
                    craftEntity.getLocation().getBlockZ());
            projectileWorld.breakBlocks(pos, BURST_RADIUS, 1);
            projectileWorld.spawnParticles(Particle.REDSTONE,
                    pos.getX() - 1,
                    pos.getY() - 1,
                    pos.getZ() - 1,
                    150, 2, 2, 2, 0D, Type.OFFENSIVE.getDustMedium());
            super.a(entity_removalreason);
        }
    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = MeltingProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = true;
        projectileStats.bounciness = 0.D;
        projectileStats.bounces = 1;
        projectileStats.fireRange = 2.5D;
        projectileStats.lifeTicks = (int) (BURST_DELAY * 20);
        projectileStats.collidable = true;
        projectileStats.noClip = true;
        projectileStats.customModelDatas = Lists.newArrayList(11);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.8f);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
