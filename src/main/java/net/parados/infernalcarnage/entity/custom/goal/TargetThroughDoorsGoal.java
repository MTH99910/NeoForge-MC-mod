package net.parados.infernalcarnage.entity.custom.goal;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.parados.infernalcarnage.entity.custom.BuffZombieEntity;

public class TargetThroughDoorsGoal extends NearestAttackableTargetGoal<Player>
{
    private final double doorDetectionRange;

    public TargetThroughDoorsGoal(Mob mob, double doorDetectionRange)
    {
        super(mob, Player.class, true);
        this.doorDetectionRange = doorDetectionRange;
    }

    @Override
    public boolean canUse()
    {
        if (super.canUse()) return true;

        return findPlayerBehindDoor() != null;
    }

    @Override
    public boolean canContinueToUse()
    {
        if (super.canContinueToUse()) return true;

        return findPlayerBehindDoor() != null;
    }

    private boolean hasDoorBetween(Vec3 from, Vec3 to)
    {
        Vec3 dir = to.subtract(from);
        double length = dir.length();
        Vec3 step = dir.normalize().scale(0.5);

        int steps = (int)(length / 0.5);
        Vec3 current = from;

        for (int i = 0; i < steps; i++)
        {
            current = current.add(step);
            net.minecraft.core.BlockPos pos = net.minecraft.core.BlockPos.containing(current);

            if (mob.level().getBlockState(pos).getBlock() instanceof DoorBlock)
            {
                if (mob instanceof BuffZombieEntity buffZombie)
                    buffZombie.doorPos = pos;

                return true;
            }
        }

        return false;
    }

    private Player findPlayerBehindDoor()
    {
        AABB searchBox = mob.getBoundingBox().inflate(doorDetectionRange);

        Player found = mob.level().getEntitiesOfClass(Player.class, searchBox, player ->
                {
                    if (player.isCreative() || player.isSpectator() || !player.isAlive()) return false;
                    return hasDoorBetween(mob.getEyePosition(), player.getEyePosition());
                }).stream()
                .min((a, b) -> Double.compare(mob.distanceTo(a), mob.distanceTo(b)))
                .orElse(null);

        if (found == null && mob instanceof BuffZombieEntity buffZombie)
            buffZombie.doorPos = null;

        return found;
    }
}