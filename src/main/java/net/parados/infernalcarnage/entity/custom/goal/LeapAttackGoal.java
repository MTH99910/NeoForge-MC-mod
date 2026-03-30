package net.parados.infernalcarnage.entity.custom.goal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.parados.infernalcarnage.entity.custom.BuffZombieEntity;
import net.parados.infernalcarnage.entity.custom.goal.vfx.ParticlesHandler;

import java.util.EnumSet;

public class LeapAttackGoal extends Goal
{
    private enum Phase {WINDUP, LEAPING, LANDED}

    private final Mob mob;
    private final double minRange;
    private final double maxRange;
    private final float arcBonus;
    private final float damage;
    private final int windupTicks;

    private Player target;
    private Phase phase;
    private int phaseTick;

    public LeapAttackGoal(Mob mob, double minRange, double maxRange, float arcBonus, float damage, int windupTicks)
    {
        this.mob = mob;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.arcBonus = arcBonus;
        this.damage = damage;
        this.windupTicks = windupTicks;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse()
    {
        Player nearest = mob.level().getNearestPlayer(mob, maxRange);
        if (nearest == null || nearest.isCreative() || nearest.isSpectator()) return false;

        double dist = mob.distanceTo(nearest);
        if (dist < minRange || dist > Math.min(maxRange, 18.0)) return false;

        if (!mob.onGround()) return false;

        if (!mob.hasLineOfSight(nearest)) return false;

        target = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (target == null || !target.isAlive()) return false;

        if (target.isCreative() || target.isSpectator()) return false;

        if (phase == Phase.LEAPING) return true;

        double dist = mob.distanceTo(target);

        if (!mob.hasLineOfSight(target)) return false;

        return dist >= minRange && dist <= maxRange * 1.5;
    }

    @Override
    public void start()
    {
        phase = Phase.WINDUP;
        phaseTick = 0;
        mob.getNavigation().stop();
    }

    @Override
    public void stop()
    {
        target = null;
    }

    @Override
    public void tick()
    {
        if (target == null) return;

        if (mob.distanceTo(target) >= minRange && mob.distanceTo(target) <= maxRange)
        {
            // Keep staring at the player
            mob.getLookControl().setLookAt(target, 30f, 30f);
        }

        switch (phase)
        {
            case WINDUP -> tickWindup();
            case LEAPING -> tickLeaping();
            case LANDED -> {}
        }
    }

    private void tickWindup()
    {
        phaseTick++;

        Vec3 diff = target.position().subtract(mob.position());
        float yaw = (float)(Math.toDegrees(Math.atan2(-diff.x, diff.z)));

        mob.setYRot(yaw);
        mob.yRotO = yaw;
        mob.setYBodyRot(yaw);
        mob.setYHeadRot(yaw);

        if (phaseTick >= windupTicks)
        {
            launch();
        }
    }

    private void launch()
    {
        Vec3 mobPos = mob.position();
        Vec3 targetPos = target.position();

        // Horizontal distance only
        double dx = targetPos.x - mobPos.x;
        double dz = targetPos.z - mobPos.z;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        // Simulate how long a given Y boost keeps us airborne,
        // then use that hang time to derive XZ speed needed.
        float yBoost = PhysicsHelpers.calculateYBoost(targetPos.y - mobPos.y, arcBonus);
        int hangTicks = PhysicsHelpers.simulateHangTime(yBoost, targetPos.y - mobPos.y);
        float xzSpeed = PhysicsHelpers.calculateXZSpeed(horizontalDist, hangTicks);

        Vec3 dir = new Vec3(dx, 0, dz).normalize();

        mob.setDeltaMovement(
                dir.x * xzSpeed,
                yBoost,
                dir.z * xzSpeed
        );
        mob.hasImpulse = true;

        phase = Phase.LEAPING;
        phaseTick = 0;

        if (mob instanceof BuffZombieEntity buffZombieEntity)
        {
            buffZombieEntity.isLeaping = true;
        }

        mob.swing(InteractionHand.MAIN_HAND);
        mob.swing(InteractionHand.OFF_HAND);
    }

    private void tickLeaping()
    {
        phaseTick++;

        if (mob.onGround() && phaseTick > 2)
        {
            tryLandingAttack();
            phase = Phase.LANDED;
        }
    }

    private void tryLandingAttack()
    {
        if (mob.distanceTo(target) <= mob.getBbWidth() + 3.5f)
        {
            target.hurt(mob.damageSources().mobAttack(mob), damage);
        }

        mob.swing(InteractionHand.MAIN_HAND);
        mob.swing(InteractionHand.OFF_HAND);
        ParticlesHandler.SpawnLandingParticles(mob);
    }
}

class PhysicsHelpers
{
    private static final float AIR_DRAG   = 0.91f;  // Minecraft's per-tick horizontal drag
    private static final float GRAVITY    = 0.08f;  // Minecraft's per-tick gravity
    private static final float VERT_DRAG  = 0.98f;  // per-tick vertical drag

    /**
     * Picks a Y boost strong enough to clear the height difference
     * plus a small arc bonus so the jump looks natural.
     */
    public static float calculateYBoost(double heightDiff, float arcBonus)
    {
        // Base arc height — always jump at least 1.2 blocks above the highest point
        double minApex = Math.max(0, heightDiff) + arcBonus;

        // Simulate upward: find the Y boost where peak height >= minApex
        // Peak is reached when vertical velocity crosses zero.
        // vy each tick: vy_new = (vy - GRAVITY) * VERT_DRAG
        // Solve iteratively — cheap and exact for our drag model.
        float vy = 0.3f;
        while (true) {
            double peak = simulatePeakHeight(vy);
            if (peak >= minApex) break;
            vy += 0.02f;
            if (vy > 3.0f) break; // safety cap
        }
        return vy;
    }

    /**
     * Returns the maximum Y reached starting from vy=boost at y=0.
     */
    public static  double simulatePeakHeight(float initialVY)
    {
        double y = 0;
        double vy = initialVY;
        for (int t = 0; t < 200; t++) {
            vy = (vy - GRAVITY) * VERT_DRAG;
            y += vy;
            if (vy <= 0) break; // past the peak
        }
        return y;
    }

    /**
     * Simulates the full arc and returns the tick count until
     * the mob's Y position is at or below targetYOffset.
     */
    public static  int simulateHangTime(float initialVY, double targetYOffset)
    {
        double y = 0;
        double vy = initialVY;
        for (int t = 1; t <= 200; t++) {
            vy = (vy - GRAVITY) * VERT_DRAG;
            y += vy;
            // Land when we've descended back to (or past) the target height
            if (y <= targetYOffset) {
                return t;
            }
        }
        return 40; // fallback — ~2 s
    }

    /**
     * Given horizontal distance and available ticks, find the XZ speed
     * accounting for per-tick drag (speed *= AIR_DRAG each tick).
     * Sum of a geometric series: dist = v0 * (1 - drag^n) / (1 - drag)
     * Solve for v0: v0 = dist * (1 - AIR_DRAG) / (1 - AIR_DRAG^hangTicks)
     */
    public static float calculateXZSpeed(double horizontalDist, int hangTicks)
    {
        double dragFactor = (1.0 - Math.pow(AIR_DRAG, hangTicks)) / (1.0 - AIR_DRAG);
        double speed = horizontalDist / dragFactor;

        // Clamp to reasonable bounds
        return (float) Math.min(Math.max(speed, 0.3), 3.5);
    }
}
