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
        InitBlocks[] blockRegs = InitBlocks.class.getEnumConstants();
        for(int i=0;i<blockRegs.length;i++){
            RenderTypeLookup.setRenderLayer(blockRegs[i].get(), RenderType.getTranslucent());
        }
    }
}
