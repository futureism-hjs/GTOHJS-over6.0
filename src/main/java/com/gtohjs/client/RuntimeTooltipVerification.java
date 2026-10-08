package com.gtohjs.client;

import com.gtohjs.GTOHJS;
import com.gtohjs.coremod.ProofLog;
import com.gtohjs.methods.FullRegistrationMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Opt-in development evidence using actual Minecraft tooltip generation. */
@Mod.EventBusSubscriber(modid=GTOHJS.MOD_ID,bus=Mod.EventBusSubscriber.Bus.FORGE,value=Dist.CLIENT)
public final class RuntimeTooltipVerification {
    private static final boolean ENABLED=Boolean.getBoolean("gtohjs.verify.tooltips");
    private static boolean done;
    private RuntimeTooltipVerification() {}
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if(!ENABLED || done || event.phase!=TickEvent.Phase.END) return;
        var player=Minecraft.getInstance().player;
        if(player==null) return;
        done=true;
        int count=0;
        for(var id:ForgeRegistries.ITEMS.getKeys()) {
            if(!GTOHJS.MOD_ID.equals(id.getNamespace()) && !FullRegistrationMethods.ownsMachine(id)) continue;
            var item=ForgeRegistries.ITEMS.getValue(id);
            var lines=new ItemStack(item).getTooltipLines(player,TooltipFlag.Default.NORMAL);
            int matches=0;
            for(var line:lines) if(line.getContents() instanceof TranslatableContents text &&
                    "gtohjs.tooltip.added_by".equals(text.getKey())) {
                if(line.getStyle().getColor()==null || line.getStyle().getColor().getValue()!=0x800080 ||
                        text.getArgs().length!=1 ||
                        !(text.getArgs()[0] instanceof Component logo))
                    throw new IllegalStateException("HJS attribution segmentation mismatch: "+id);
                if(!"GTO HJS".equals(logo.getString()) || logo.getStyle().getColor()==null ||
                        logo.getStyle().getColor().getValue()!=0xFFA500)
                    throw new IllegalStateException("HJS orange attribution name missing: "+id);
                matches++;
            }
            if(matches!=1) throw new IllegalStateException("HJS attribution tooltip missing/duplicated: "+id);
            count++;
        }
        if(count!=46) throw new IllegalStateException("Expected 46 owned-item tooltips, found "+count);
        ProofLog.record("actual Minecraft tooltip generation: 46/46 purple attribution and orange name PASS");
    }
}
