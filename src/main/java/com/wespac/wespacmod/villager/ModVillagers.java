package com.wespac.wespacmod.villager;


import com.google.common.collect.ImmutableSet;
import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.ModBlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, WespacMod.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS,  WespacMod.MODID);

    public static final RegistryObject<PoiType> SOUND_POI = POI_TYPES.register("sound_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.GEM_POLISHING_STATION.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    public static final RegistryObject<VillagerProfession> SOUND_MASTER =
            VILLAGER_PROFESSIONS.register("sound_master", () -> new VillagerProfession("sound_master",
                    holder -> holder.get() == SOUND_POI.get(), holder -> holder.get() == SOUND_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));


    // Define the POI type for the bank teller
    public static final RegistryObject<PoiType> BANK_TELLER_POI = POI_TYPES.register("bank_teller_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.BANK_COUNTER.get().getStateDefinition().getPossibleStates()),
                    1, 1));

    // Define the bank teller profession
    public static final RegistryObject<VillagerProfession> BANK_TELLER =
            VILLAGER_PROFESSIONS.register("bank_teller", () -> new VillagerProfession("bank_teller",
                    holder -> holder.get() == BANK_TELLER_POI.get(), holder -> holder.get() == BANK_TELLER_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC));

    // Define the POI type for the bank telle

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }
}
