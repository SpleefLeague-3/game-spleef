package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.projectiles.BoomerangProjectile;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import org.bukkit.Sound;

/**
 * @author NickM13
 * @since 5/18/2020
 */
public class OffensiveBoomerang extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveBoomerang.class)
                .setCustomModelData(2)
                .setName("Boomerang")
                .setDescription("Throw a boomerang forward destroying all destructible blocks it passes, returning to the sender after %X1% second.")
                .setUsage(3);
    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = BoomerangProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 80;
        projectileStats.fireRange = 4D;
        projectileStats.collidable = false;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
        projectileStats.customModelDatas = Lists.newArrayList(12);
    }

    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats);
        getUser().getBattle().getGameWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1.4f);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
