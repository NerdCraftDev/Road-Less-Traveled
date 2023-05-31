package com.nerdcraftmc.roadlesstraveled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

@Mod(RoadLessTraveled.MODID)
@Mod.EventBusSubscriber(modid = RoadLessTraveled.MODID)
public class RoadLessTraveled {
    static BlockPos oldPos;
    static Block block;
    static Block newBlock;
    static Level level;
    static Player player;
    static HashMap<Block, Block> nextBlock = new HashMap<>();
    static HashMap<Block, Double> chanceNum = new HashMap<>();
    static final String MODID = "roadlesstraveled";

    public RoadLessTraveled() {
        // get Event Bus
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::ClientSetup);
    }

    // Client Setup
    private void ClientSetup(final FMLClientSetupEvent event) {
        nextBlock.put(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH);
        nextBlock.put(Blocks.DIRT_PATH, Blocks.COARSE_DIRT);
        nextBlock.put(Blocks.COARSE_DIRT, Blocks.GRAVEL);
        nextBlock.put(Blocks.GRAVEL, Blocks.COBBLESTONE);
        nextBlock.put(Blocks.COBBLESTONE, Blocks.STONE);
        nextBlock.put(Blocks.STONE, Blocks.SMOOTH_STONE);
        nextBlock.put(Blocks.DIORITE, Blocks.POLISHED_DIORITE);
        nextBlock.put(Blocks.POLISHED_DIORITE, Blocks.SMOOTH_STONE);
        nextBlock.put(Blocks.ANDESITE, Blocks.POLISHED_ANDESITE);
        nextBlock.put(Blocks.POLISHED_ANDESITE, Blocks.SMOOTH_STONE);
        nextBlock.put(Blocks.GRANITE, Blocks.POLISHED_GRANITE);
        nextBlock.put(Blocks.POLISHED_GRANITE, Blocks.SMOOTH_STONE);
        nextBlock.put(Blocks.SMOOTH_STONE, Blocks.STONE_BRICKS);

        nextBlock.put(Blocks.SAND, Blocks.SANDSTONE);
        nextBlock.put(Blocks.SANDSTONE, Blocks.SMOOTH_SANDSTONE);
        nextBlock.put(Blocks.SMOOTH_SANDSTONE, Blocks.CUT_SANDSTONE);
        nextBlock.put(Blocks.CUT_SANDSTONE, Blocks.TERRACOTTA);

        chanceNum.put(Blocks.GRASS_BLOCK, 5.0);
        chanceNum.put(Blocks.DIRT_PATH, 2.5);
        chanceNum.put(Blocks.COARSE_DIRT, 1.0);
        chanceNum.put(Blocks.GRAVEL, 0.5);
        chanceNum.put(Blocks.COBBLESTONE, 0.25);
        chanceNum.put(Blocks.DIORITE, 0.25);
        chanceNum.put(Blocks.GRANITE, 0.25);
        chanceNum.put(Blocks.ANDESITE, 0.25);
        chanceNum.put(Blocks.STONE, 0.1);
        chanceNum.put(Blocks.POLISHED_DIORITE, 0.1);
        chanceNum.put(Blocks.POLISHED_GRANITE, 0.1);
        chanceNum.put(Blocks.POLISHED_ANDESITE, 0.1);
        chanceNum.put(Blocks.SMOOTH_STONE, 0.05);

        chanceNum.put(Blocks.SAND, 5.0);
        chanceNum.put(Blocks.SANDSTONE, 2.5);
        chanceNum.put(Blocks.SMOOTH_SANDSTONE, 1.0);
        chanceNum.put(Blocks.CUT_SANDSTONE, 0.5);
        System.out.println("Client Started, HashMaps Filled");
    }


    @SubscribeEvent
    public static void PlayerTick(TickEvent.PlayerTickEvent event /* On player tick */) {
        player = event.player;
        level = player.getLevel();
        // If level is Server Side
        if (!level.isClientSide()) {
            // Location at player's feet
            BlockPos newPos = player.blockPosition();
            BlockPos pos = newPos;

            if (oldPos != newPos) {
                // If the block at 'newPos' is air, move down one block
                if (level.getBlockState(newPos).getBlock() == Blocks.AIR) {
                    pos = newPos.below();
                }
                block = level.getBlockState(pos).getBlock();
                // If 'block' is a key in 'nextBlock'
                Block blockAbove = level.getBlockState(pos.above()).getBlock();
                if (nextBlock.containsKey(block) && blockAbove == Blocks.AIR) {
                    int featherLevel = player.getInventory().getArmor(0).getEnchantmentLevel(Enchantments.FALL_PROTECTION);
                    double rand = Math.ceil(Math.random() * (100.0 / ((chanceNum.get(block) * (1 - 0.25 * featherLevel)))));
                    System.out.println(rand);
                    if (rand == 1.0) {
                        if (nextBlock.get(block) == Blocks.COBBLESTONE) {
                            int x = 1;
                            newBlock = level.getBlockState(newPos.below()).getBlock();
                            while (newBlock != Blocks.STONE && newBlock != Blocks.ANDESITE && newBlock != Blocks.GRANITE && newBlock != Blocks.DIORITE && newBlock != Blocks.BEDROCK) {
                                x++;
                                newBlock = level.getBlockState(newPos.below(x)).getBlock();
                            }
                            if (newBlock == Blocks.STONE) {
                                newBlock = Blocks.COBBLESTONE;
                            }
                        } else {
                            newBlock = nextBlock.get(block);
                        }
                        level.setBlock(pos, newBlock.defaultBlockState(), 2);
                        if (block == Blocks.DIRT_PATH) {
                            player.teleportTo(player.getX(), player.getY() + 0.0625, player.getZ());
                        }
                    }
                }
            }
            oldPos = newPos;
        }
    }
}