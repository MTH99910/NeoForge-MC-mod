package net.parados.infernalcarnage.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.parados.infernalcarnage.InfernalCarnage;
import net.parados.infernalcarnage.entity.custom.BuffZombieEntity;

import java.util.function.Supplier;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, InfernalCarnage.MOD_ID);

    public static final Supplier<EntityType<BuffZombieEntity>> BUFF_ZOMBIE =
            ENTITY_TYPES.register("buffzombie", () -> EntityType.Builder.of(BuffZombieEntity::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f)
                    .build("buffzombie"));

    public static void register(IEventBus modEventBus)
    {
        ENTITY_TYPES.register(modEventBus);
    }
}
