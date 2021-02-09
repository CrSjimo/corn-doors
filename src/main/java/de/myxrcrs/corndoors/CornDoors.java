package de.myxrcrs.corndoors;

import com.mojang.brigadier.CommandDispatcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.myxrcrs.corndoors.blocks.INaiveDoor.DoorWindowType;
import de.myxrcrs.corndoors.command.ToggleAllDoorsWithinCommand;
import de.myxrcrs.corndoors.command.ToggleDoorNearCommand;
import de.myxrcrs.corndoors.init.InitBlocks;
import de.myxrcrs.corndoors.init.InitItems;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("corndoors")
public class CornDoors
{
    public static final String MOD_ID = "corndoors";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CornDoors.MOD_ID);

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
        public static void onCommandsRegister(RegisterCommandsEvent event) {
            LOGGER.debug("HELLO server starting.");
            CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
            ToggleDoorNearCommand.register(dispatcher);
            ToggleAllDoorsWithinCommand.register(dispatcher);

        }
    }


    public static final ItemGroup ITEM_GROUP = new ItemGroup("corndoors") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(InitItems.NAIVE_DOOR.get(DoorWindowType.GLASS));
        }
    };
}
