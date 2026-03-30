package net.parados.infernalcarnage.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.parados.infernalcarnage.entity.custom.goal.CustomBreakDoorGoal;
import net.parados.infernalcarnage.entity.custom.goal.LeapAttackGoal;
import net.parados.infernalcarnage.entity.custom.goal.TargetThroughDoorsGoal;
import org.jetbrains.annotations.NotNull;

public class BuffZombieEntity extends Zombie
{
    public boolean isLeaping = false;
    public BlockPos doorPos = null;

    public BuffZombieEntity(EntityType<? extends Zombie> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0F)
                .add(Attributes.MOVEMENT_SPEED, 0.5F)
                .add(Attributes.ATTACK_DAMAGE, 9.0F)
                .add(Attributes.ARMOR, 4.0F)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.MAX_HEALTH, 60.0F)
                .add(Attributes.ATTACK_KNOCKBACK, 3.0F);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.addBehaviourGoals();

        // Custom Goals
        this.goalSelector.addGoal(1, new CustomBreakDoorGoal(this, 15));
        this.goalSelector.addGoal(2, new LeapAttackGoal(
                this,
                10.0,
                30.0,
                2.0f,
                1.0f,
                5
        ));

        this.targetSelector.addGoal(2, new TargetThroughDoorsGoal(this, 6));
    }

    protected void addBehaviourGoals()
    {
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0F, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source)
    {
        if (isLeaping)
        {
            isLeaping = false;
            return false;
        }

        return super.causeFallDamage(fallDistance, multiplier, source);
    }
}
