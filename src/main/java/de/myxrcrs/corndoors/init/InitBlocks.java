package de.myxrcrs.corndoors.init;

import de.myxrcrs.corndoors.CornDoors;
import de.myxrcrs.corndoors.blocks.DNaiveDoor;
import de.myxrcrs.corndoors.blocks.GlassDoor;
import de.myxrcrs.corndoors.blocks.NaiveDoor;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CornDoors.MOD_ID);
    public static final RegistryObject<Block> NAIVE_DOOR = BLOCKS.register("naive_door",NaiveDoor::new);
    public static final RegistryObject<Block> GLASS_DOOR = BLOCKS.register("glass_door",GlassDoor::new);
    public static final RegistryObject<Block> D_NAIVE_DOOR = BLOCKS.register("d_naive_door",DNaiveDoor::new);
}