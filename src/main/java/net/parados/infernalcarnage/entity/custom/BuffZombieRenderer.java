package net.parados.infernalcarnage.entity.custom;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.parados.infernalcarnage.InfernalCarnage;
import org.jetbrains.annotations.NotNull;

public class BuffZombieRenderer extends HumanoidMobRenderer<BuffZombieEntity, BuffZombieModel<BuffZombieEntity>>
{
    private static final ResourceLocation BUFF_ZOMBIE_LOCATION = ResourceLocation.fromNamespaceAndPath(InfernalCarnage.MOD_ID, "textures/entity/buffzombie/buffzombie.png");

    public BuffZombieRenderer(EntityRendererProvider.Context context)
    {
        super(context, new BuffZombieModel<>(context.bakeLayer(BuffZombieModel.BUFF_ZOMBIE_LAYER_LOCATION)), 0.5f );
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BuffZombieEntity entity)
    {
        return BUFF_ZOMBIE_LOCATION;
    }
}
