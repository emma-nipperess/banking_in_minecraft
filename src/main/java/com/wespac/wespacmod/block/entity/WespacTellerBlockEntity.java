package com.wespac.wespacmod.block.entity;

import com.wespac.wespacmod.WespacMod;
import com.wespac.wespacmod.chat.GeminiAPI;
import com.wespac.wespacmod.item.ModItems;
import com.wespac.wespacmod.screen.WespacTellerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WespacTellerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public WespacTellerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WESPAC_TELLER_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> WespacTellerBlockEntity.this.progress;
                    case 1 -> WespacTellerBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> WespacTellerBlockEntity.this.progress = pValue;
                    case 1 -> WespacTellerBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.wespacmod.wespac_teller");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WespacTellerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("wespac_teller.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("wespac_teller.progress");
    }

    private Player findNearbyPlayer(Level level, BlockPos pos) {
        // Example implementation: find the nearest player within a radius of 10 blocks
        double radius = 10.0;
        return level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), radius, false);
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                Player player = findNearbyPlayer(pLevel, pPos);

                craftItem(player);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }
    

    private void craftItem(Player player) {
        ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        Item inputItem = inputStack.getItem();

        if (inputItem instanceof WritableBookItem || inputItem instanceof WrittenBookItem) {
            String bookText = extractTextFromBook(inputStack);

            WespacMod.LOGGER.info("RAW BOOK TEXT: " + bookText);
            RequestBody body = new FormBody.Builder()
                    .add("name", bookText)
                    .add("password", bookText)
                    .build();

            Request request = new Request.Builder()
                    .url(GeminiAPI.API_URL + "/open-account")
                    .post(body)
                    .build();

            GeminiAPI.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    //player.sendSystemMessage(Component.translatable("Error opening credit card"));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Drop credit card if API call is successful
                        ItemStack creditCard = new ItemStack(ModItems.MONEY_ITEMS.get("credit_card").get());
                        player.drop(creditCard, false);
                    } else {
                        //player.sendSystemMessage(Component.translatable("Failed to open credit card"));
                    }
                    response.close();
                }
            });

            // Remove one writable book from the input slot
            this.itemHandler.extractItem(INPUT_SLOT, 1, false);

            // Optionally, set a result item in the output slot
            ItemStack result = new ItemStack(ModItems.MONEY_ITEMS.get("credit_card").get(), 1);
            this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                    this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
        } else {
            player.sendSystemMessage(Component.translatable("Invalid item in input slot"));
        }
    }

    private String extractTextFromBook(ItemStack bookStack) {
        if (bookStack.getItem() instanceof WritableBookItem || bookStack.getItem() instanceof WrittenBookItem) {
            CompoundTag tag = bookStack.getTag();
            if (tag != null && tag.contains("pages", 9)) { // 9 is the ID for a list of strings
                ListTag pages = tag.getList("pages", 8); // 8 is the ID for a string
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < pages.size(); i++) {
                    text.append(pages.getString(i)).append("\n");
                }
                return text.toString();
            }
        }
        return "";
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemHandler.getStackInSlot(INPUT_SLOT).getItem() == Items.WRITABLE_BOOK;
        ItemStack result = new ItemStack(ModItems.MONEY_ITEMS.get("credit_card").get());

        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }
}