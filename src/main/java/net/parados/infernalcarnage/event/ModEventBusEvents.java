package net.parados.infernalcarnage.event;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.parados.infernalcarnage.InfernalCarnage;
import net.parados.infernalcarnage.entity.ModEntities;
import net.parados.infernalcarnage.entity.custom.BuffZombieEntity;
import net.parados.infernalcarnage.entity.custom.BuffZombieModel;

@EventBusSubscriber(modid = InfernalCarnage.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents
{
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(
                BuffZombieModel.BUFF_ZOMBIE_LAYER_LOCATION,
                () -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f), 64, 64)
        );
    }


    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.BUFF_ZOMBIE.get(), BuffZombieEntity.createAttributes().build());
    }
}
