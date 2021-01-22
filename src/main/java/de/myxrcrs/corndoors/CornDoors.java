package de.myxrcrs.corndoors;

import de.myxrcrs.corndoors.command.ToggleAllDoorsWithinCommand;
import de.myxrcrs.corndoors.command.ToggleDoorNearCommand;
import de.myxrcrs.corndoors.init.InitBlocks;
import de.myxrcrs.corndoors.init.InitItems;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

import com.mojang.brigadier.CommandDispatcher;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("corndoors")
public class CornDoors
{
    public static final String MOD_ID = "corndoors";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, CornDoors.MOD_ID);

    public CornDoors() {

        InitBlocks.initialize();
        InitItems.initialize();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Mod.EventBusSubscriber
    public static class CommandEventHandler {
        @SubscribeEvent
        public static void onServerStaring(FMLServerStartingEvent event) {
            LOGGER.debug("HELLO server starting.");
            CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
            ToggleDoorNearCommand.register(dispatcher);
            ToggleAllDoorsWithinCommand.register(dispatcher);

        }
    }


    public static final ItemGroup ITEM_GROUP = new ItemGroup("corndoors") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(InitItems.NAIVE_DOOR_GLASS.get());
        }
    };
}
