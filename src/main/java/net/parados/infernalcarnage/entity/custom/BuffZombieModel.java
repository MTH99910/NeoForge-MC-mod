package net.parados.infernalcarnage.entity.custom;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.parados.infernalcarnage.InfernalCarnage;
import org.jetbrains.annotations.NotNull;

public class BuffZombieModel<T extends Zombie> extends HumanoidModel<T>
{
    public static final ModelLayerLocation BUFF_ZOMBIE_LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(InfernalCarnage.MOD_ID, "buffzombie"), "main");

    public BuffZombieModel(ModelPart root)
    {
        super(root);
        // ScaleBodyParts(1.0F, 1.0F, 1.25F, 1.25F);
    }

//    private void ScaleBodyParts(float headMulti, float legsMulti, float torsoMulti, float armsMulti)
//    {
//        rightArm.xScale *= armsMulti;
//        rightArm.yScale *= armsMulti;
//        rightArm.zScale *= armsMulti;
//
//        leftArm.xScale *= armsMulti;
//        leftArm.yScale *= armsMulti;
//        leftArm.zScale *= armsMulti;
//
//        body.xScale *= torsoMulti;
//        body.zScale *= torsoMulti;
//    }

    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, this.isAggressive(entity), this.attackTime, ageInTicks);
    }

    public boolean isAggressive(T entity)
    {
        return entity.isAggressive();
    }
}
