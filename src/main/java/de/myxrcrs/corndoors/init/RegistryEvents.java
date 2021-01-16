package de.myxrcrs.corndoors.init;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
    public static void onRenderTypeSetup(FMLClientSetupEvent event) {
        RenderTypeLookup.setRenderLayer(InitBlocks.NAIVE_DOOR.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(InitBlocks.GLASS_DOOR.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(InitBlocks.D_NAIVE_DOOR.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(InitBlocks.D_NAIVE_DOOR_EDGE.get(), RenderType.getTranslucent());
    }
}
