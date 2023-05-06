package com.spleefleague.spleef.game.battle.power.ability.abilities.utility;

import com.google.common.collect.Lists;
import com.spleefleague.core.game.battle.BattlePlayer;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.variable.BlockRaycastResult;
import com.spleefleague.core.world.projectile.FakeEntitySnowball;
import com.spleefleague.core.world.projectile.ProjectileStats;
import com.spleefleague.core.world.projectile.ProjectileWorld;
import com.spleefleague.core.world.projectile.game.GameUtils;
import com.spleefleague.spleef.game.battle.power.ability.AbilityStats;
import com.spleefleague.spleef.game.battle.power.ability.abilities.AbilityUtility;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * @author NickM13
 * @since 5/19/2020
 */
public class UtilitySmokeBomb extends AbilityUtility {

    public static AbilityStats init() {
        return init(UtilitySmokeBomb.class)
                .setCustomModelData(8)
                .setName("Smoke Bomb")
                .setDescription("Throw a smoke bomb onto the ground lasting %DURATION% seconds, blinding players in a small radius around the bomb.")
                .setUsage(15);
    }

    private static final double DURATION = 5D;
    private static final double RANGE = 4D;

    public static class SmokeBombProjectile extends FakeEntitySnowball {

        private boolean activated = false;

        public SmokeBombProjectile(ProjectileWorld projectileWorld, CorePlayer shooter, Location location, ProjectileStats projectileStats, Double charge) {
            super(projectileWorld, shooter, location, projectileStats, charge);
        }

        @Override
        public void ao() {
            super.ao();
            if (activated) {
                Location loc = new Location(projectileWorld.getWorld(),
                        getBukkitEntity().getVelocity().getX(),
                        getBukkitEntity().getVelocity().getY() + 0.5,
                        getBukkitEntity().getVelocity().getZ());
                for (BattlePlayer bp : cpShooter.getBattle().getBattlers()) {
                    if (!bp.getCorePlayer().equals(cpShooter) &&
                            bp.getPlayer().getLocation().distance(loc) <= RANGE) {
                        bp.getPlayer().addPotionEffect(PotionEffectType.BLINDNESS.createEffect(30, 0));
                    }
                }
                GameUtils.spawnRingParticles(projectileWorld, loc.toVector(), Type.UTILITY.getDustMedium(), RANGE, 10);
                projectileWorld.playSound(loc, Sound.ENTITY_CAT_PURR, 0.25f, 2);
            }
        }

        @Override
        protected boolean onBlockHit(BlockRaycastResult blockRaycastResult, Vector intersection) {
            super.blockBounce(blockRaycastResult, intersection);
            if (!activated) {
                activated = true;
                this.lifeTicks = getBukkitEntity().getTicksLived() + (int) (DURATION * 20);
            }
            return true;
        }

    }

    private static ProjectileStats projectileStats = new ProjectileStats();

    static {
        projectileStats.entityClass = SmokeBombProjectile.class;
        projectileStats.customModelDatas = Lists.newArrayList(10);
        projectileStats.breakRadius = 0D;
        projectileStats.gravity = true;
        projectileStats.lifeTicks = 200;
        projectileStats.fireRange = 3.5D;
        projectileStats.collidable = false;
        projectileStats.noClip = true;
        projectileStats.bounces = 1;
        projectileStats.bounciness = 0.15;
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
