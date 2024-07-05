package com.wespac.wespacmod.block.entity;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WespacMod.MODID);

    public static final RegistryObject<BlockEntityType<GemPolishingStationBlockEntity>> GEM_POLISHING_BE =
            BLOCK_ENTITIES.register("gem_polishing_be", () ->
                    BlockEntityType.Builder.of(GemPolishingStationBlockEntity::new,
                            ModBlocks.GEM_POLISHING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<WespacTellerBlockEntity>> WESPAC_TELLER_BE =
            BLOCK_ENTITIES.register("wespac_teller_be", () ->
                    BlockEntityType.Builder.of(WespacTellerBlockEntity::new,
                            ModBlocks.WESPAC_TELLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<FormDispenserBlockEntity>> FORM_DISP_BE =
            BLOCK_ENTITIES.register("form_disp_be", () ->
                    BlockEntityType.Builder.of(FormDispenserBlockEntity::new,
                            ModBlocks.FORM_DISP.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}