package net.parados.infernalcarnage.entity.custom.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.DoorBlock;
import net.parados.infernalcarnage.entity.custom.BuffZombieEntity;

import java.util.EnumSet;

public class CustomBreakDoorGoal extends Goal
{
    private final Mob mob;
    private final int breakTime;
    private int breakProgress;

    public CustomBreakDoorGoal(Mob mob, int breakTime)
    {
        this.mob = mob;
        this.breakTime = breakTime;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (!(mob instanceof BuffZombieEntity buffZombie)) return false;
        if (mob.getTarget() == null) return false;
        if (buffZombie.doorPos == null) return false;
        return isDoor(buffZombie.doorPos);
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!(mob instanceof BuffZombieEntity buffZombie)) return false;
        if (buffZombie.doorPos == null) return false;
        return isDoor(buffZombie.doorPos);
    }

    @Override
    public void start()
    {
        if (!(mob instanceof BuffZombieEntity buffZombie)) return;
        breakProgress = 0;
        mob.getNavigation().moveTo(buffZombie.doorPos.getX(), buffZombie.doorPos.getY(), buffZombie.doorPos.getZ(), 1.0);
    }

    @Override
    public void stop()
    {
        if (mob instanceof BuffZombieEntity buffZombie && buffZombie.doorPos != null)
            mob.level().destroyBlockProgress(mob.getId(), buffZombie.doorPos, -1);
    }

    @Override
    public void tick()
    {
        if (!(mob instanceof BuffZombieEntity buffZombie)) return;
        BlockPos doorPos = buffZombie.doorPos;
        if (doorPos == null) return;

        double distToDoor = mob.distanceToSqr(doorPos.getX() + 0.5, doorPos.getY() + 0.5, doorPos.getZ() + 0.5);

        if (distToDoor > 4.0)
        {
            if (mob.getNavigation().isDone())
                mob.getNavigation().moveTo(doorPos.getX(), doorPos.getY(), doorPos.getZ(), 1.0);

            return;
        }

        breakProgress++;

        if (mob.getRandom().nextInt(10) == 0)
        {
            mob.swing(mob.getUsedItemHand());
            mob.level().levelEvent(1019, doorPos, 0);
        }

        int stage = (int)((float) breakProgress / breakTime * 10.0f);
        mob.level().destroyBlockProgress(mob.getId(), doorPos, stage);

        if (breakProgress >= breakTime)
        {
            mob.level().removeBlock(doorPos, false);
            mob.level().levelEvent(1021, doorPos, 0);
            mob.level().destroyBlockProgress(mob.getId(), doorPos, -1);
            buffZombie.doorPos = null;
        }
    }

    private boolean isDoor(BlockPos pos)
    {
        return mob.level().getBlockState(pos).getBlock() instanceof DoorBlock;
    }
}
