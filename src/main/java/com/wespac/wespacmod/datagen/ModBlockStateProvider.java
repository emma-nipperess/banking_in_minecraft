package com.wespac.wespacmod.datagen;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.ModBlocks;
import net.minecraft.client.model.Model;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WespacMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.GEM_POLISHING_STATION.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gem_polishing_station")));

        simpleBlockWithItem(ModBlocks.TELLER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/teller")));

    }
}