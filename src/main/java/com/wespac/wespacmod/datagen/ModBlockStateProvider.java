package com.wespac.wespacmod.datagen;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, WespacMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.GEM_POLISHING_STATION.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gem_polishing_station")));

        simpleBlockWithItem(ModBlocks.BANK_COUNTER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/bank_counter")));

        simpleBlockWithItem(ModBlocks.WESPAC_TELLER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/wespac_teller")));


        simpleBlockWithItem(ModBlocks.FORM_DISP.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/form_disp")));


    }
}