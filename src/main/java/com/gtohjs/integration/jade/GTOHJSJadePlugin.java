package com.gtohjs.integration.jade;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;

/** Called from GTOCore's fixed Jade plugin list while Jade is present. */
public final class GTOHJSJadePlugin implements IWailaPlugin {
    private static final GTOHJSJadePlugin INSTANCE = new GTOHJSJadePlugin();

    public static void registerCommon(IWailaCommonRegistration registration) {
        INSTANCE.register(registration);
    }

    public static void registerClientComponents(IWailaClientRegistration registration) {
        INSTANCE.registerClient(registration);
    }

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(InfiniteEnergyJadeProvider.INSTANCE, MetaMachineBlockEntity.class);
        registration.registerBlockDataProvider(AdaptiveNetJadeProvider.INSTANCE, MetaMachineBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(InfiniteEnergyJadeProvider.INSTANCE, MetaMachineBlock.class);
        registration.registerBlockComponent(AdaptiveNetJadeProvider.INSTANCE, MetaMachineBlock.class);
    }
}
