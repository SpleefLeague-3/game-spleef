package com.spleefleague.spleef.game.battle.power.ability.abilities.offensive;

import com.google.common.collect.Lists;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.EntityRaycastResult;
import com.spleefleague.core.world.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.ProjectileWorld;
import com.spleefleague.core.world.projectile.ProjectileWorldPlayer;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityOffensive;
import net.minecraft.world.phys.MovingObjectPosition;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class OffensiveYoink extends AbilityOffensive {

    public static AbilityStats init() {
        return init(OffensiveYoink.class)
                .setCustomModelData(29)
                .setName("Yoink")
                .setDescription("Fire a hook forward, if the projectile collides with another player they are quickly pulled to the casters locations.")
                .setUsage(10);
    }

    private static final double POWER = 0.35D;
    private static final double POWER_CAP = 2.5D;

    public static class YoinkProjectile extends FakeEntitySnowball {

        public YoinkProjectile(ProjectileWorld<? extends ProjectileWorldPlayer> projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        protected void a(MovingObjectPosition var0) {
            if (!projectileStats.noClip) {
                super.a(var0);
            }
        }

        @Override
        protected void onEntityHit(EntityRaycastResult entityRaycastResult) {
            getBukkitEntity().remove();
            Vector dir = cpShooter.getLocation().toVector().subtract(entityRaycastResult.getEntity().getLocation().toVector());
            dir.setY(0);
            double dist = dir.length();
            dir = dir.normalize();
            entityRaycastResult.getEntity().setVelocity(dir.multiply(Math.min(dist * POWER, POWER_CAP)).add(new Vector(0, 0.25, 0)));
        }

    }

    private static final ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = YoinkProjectile.class;
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = false;
        projectileStats.lifeTicks = 80;
        projectileStats.fireRange = 5D;
        projectileStats.collidable = true;
        projectileStats.noClip = true;
        projectileStats.size = 1.D;
        projectileStats.customModelDatas = Lists.newArrayList(31);
    }

    /**
     * This is called when a player uses an ability that isn't on cooldown.
     */
    @Override
    public boolean onUse() {
        getUser().getBattle().getGameWorld().shootProjectile(getUser().getCorePlayer(), projectileStats);
        return true;
    }

    /**
     * Called at the start of a round
     */
    @Override
    public void reset() {

    }

}
