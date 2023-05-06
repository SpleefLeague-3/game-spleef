package com.spleefleague.spleef.game.battle.power.ability.abilities.mobility;

import com.google.common.collect.Lists;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.projectiles.EnderPearlProjectile;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityMobility;

/**
 * @author NickM13
 * @since 9/21/2020
 */
public class MobilityEnderPearl extends AbilityMobility {

    public static AbilityStats init() {
        return init(MobilityEnderPearl.class)
                .setCustomModelData(3)
                .setName("Ender Pearl")
                .setDescription("Throw an ender pearl, teleporting to the location it lands.")
                .setUsage(10D);
    }

    private static final ProjectileStats pearlStats = new ProjectileStats();

    static {
        pearlStats.entityClass = EnderPearlProjectile.class;
        pearlStats.customModelDatas = Lists.newArrayList(13);
        pearlStats.fireRange = 6D;
    }

    private EnderPearlProjectile projectile = null;

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        projectile = (EnderPearlProjectile) getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), pearlStats).get(0);
        return false;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {
        if (projectile != null && !projectile.getBukkitEntity().isDead()) {
            projectile.getBukkitEntity().remove();
        }
    }

}
