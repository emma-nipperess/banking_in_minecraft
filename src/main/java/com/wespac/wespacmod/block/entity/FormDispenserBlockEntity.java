package com.wespac.wespacmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FormDispenserBlockEntity extends BlockEntity {
    public FormDispenserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FORM_DISP_BE.get(), pPos, pBlockState);
    }

}