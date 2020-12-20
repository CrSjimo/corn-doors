package de.myxrcrs.corndoors.init;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.TestDoor;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CornDoors.MOD_ID);
    public static final RegistryObject<Block> TEST_DOOR = BLOCKS.register("test_door",TestDoor::new);
}