package net.parados.infernalcarnage.entity.custom.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class PullPlayerGoal extends Goal
{
    private final Mob mob;
    private final double maxActivationRange;   // how far away the player can be to trigger the pull
    private final double minActivationRange;
    private final double pullStrength;      // how hard the player is pulled per tick
    private final int pullInterval;         // ticks between each pull pulse
    private Player target;
    private int tickCount;

    public PullPlayerGoal(Mob mob, double minActivationRange, double maxactivationRange, double pullStrength, int pullInterval)
    {
        this.mob = mob;
        this.maxActivationRange = maxactivationRange;
        this.minActivationRange = minActivationRange;
        this.pullStrength = pullStrength;
        this.pullInterval = pullInterval;
        // This goal controls movement, so flag it appropriately
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        // Find the nearest player within range
        target = mob.level().getNearestPlayer(mob, maxActivationRange);

        if (target != null && !target.isCreative() && !target.isSpectator())
            return true;

        assert target != null;
        return mob.distanceTo(target) <= maxActivationRange && mob.distanceTo(target) > minActivationRange;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (target == null || !target.isAlive())
            return false;

        if (target.isCreative() || target.isSpectator())
            return false;

        return mob.distanceTo(target) <= maxActivationRange && mob.distanceTo(target) > minActivationRange;
    }

    @Override
    public void start()
    {
        tickCount = 0;
    }

    @Override
    public void stop()
    {
        target = null;
    }

    @Override
    public void tick()
    {
        if (target == null)
            return;

        // Always look at the player
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);

        tickCount++;

        if (tickCount % pullInterval != 0)  // only pull every N ticks
            return;

        // Calculate direction vector from player -> mob (the pull direction)
        Vec3 mobPos = mob.position();
        Vec3 playerPos = target.position();

        Vec3 pullDir = mobPos.subtract(playerPos).normalize().scale(pullStrength);

        // Add horizontal pull to target's current momentum
        Vec3 current = target.getDeltaMovement();
        target.setDeltaMovement(
                current.x + pullDir.x,
                current.y + pullDir.y * 2.0f,
                current.z + pullDir.z
        );

        // Tell the client about the velocity change
        target.hurtMarked = true;
    }
}
