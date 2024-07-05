package com.wespac.wespacmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LoanClerkBlockEntity extends BlockEntity {
    public LoanClerkBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LOAN_CLERK_BE.get(), pPos, pBlockState);
    }

}