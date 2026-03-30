package net.parados.infernalcarnage.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.parados.infernalcarnage.InfernalCarnage;
import net.parados.infernalcarnage.entity.ModEntities;

public class ModItems
{

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(InfernalCarnage.MOD_ID);

    public static final DeferredItem<Item> BUFF_SPAWN_EGG =
            ITEMS.register("buffzombiespawnegg", () -> new DeferredSpawnEggItem(ModEntities.BUFF_ZOMBIE, 0x31afaf, 0xffac00,
                    new Item.Properties()));

    public static void register(IEventBus modEventBus)
    {
        ITEMS.register(modEventBus);
    }
}
