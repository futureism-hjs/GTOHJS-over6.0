package com.gtohjs;

import com.gtohjs.coremod.InjectionVerifier;
import com.gtohjs.coremod.ProofLog;
import com.gtohjs.data.GTOHJSBlocks;
import com.gtohjs.data.GTOHJSItems;
import com.gtohjs.methods.Fix2RegistrationMethods;
import com.gtohjs.config.MEPatternBufferConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(GTOHJS.MOD_ID)
public final class GTOHJS {
    public static final String MOD_ID = "gtohjs";

    public GTOHJS(FMLJavaModLoadingContext context) {
        var modBus = context.getModEventBus();
        MEPatternBufferConfig.HOLDER.getConfigInstance();
        GTOHJSBlocks.register(modBus);
        GTOHJSItems.register(modBus);
        modBus.addListener(this::loadComplete);
        MinecraftForge.EVENT_BUS.addListener(this::beforeServer);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        ProofLog.record("addon constructed; fix3 complete feature adaptation");
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        event.enqueueWork(InjectionVerifier::verifyLoading);
    }

    private void beforeServer(ServerAboutToStartEvent event) {
        InjectionVerifier.verifyExecuted();
    }

    private void serverStarted(ServerStartedEvent event) { Fix2RegistrationMethods.verifyServerRecipes(event.getServer()); }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(MOD_ID, path); }
}
