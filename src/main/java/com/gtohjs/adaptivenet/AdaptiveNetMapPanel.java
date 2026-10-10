package com.gtohjs.adaptivenet;

import com.gregtechceu.gtceu.uipro.LayoutStyle;
import com.gregtechceu.gtceu.uipro.UIElement;
import com.gregtechceu.gtceu.uipro.elements.StatusPanel;
import com.gtocore.common.wireless.energy.map.GridMapContext;
import com.hepdd.gtmthings.utils.TeamUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/** Adds saved frequencies to a native node card without replacing its grid data. */
public final class AdaptiveNetMapPanel {
    private AdaptiveNetMapPanel() {}

    public static void append(UIElement column, Object card) {
        var panel = new StatusPanel(LayoutStyle.AUTO);
        panel.addSentence(() -> line(card));
        column.addChild(panel);
    }

    private static Component line(Object card) {
        try {
            Class<?> type = card.getClass();
            Field contextField = type.getDeclaredField("ctx");
            Field dimensionField = type.getDeclaredField("dimension");
            Method resolve = type.getDeclaredMethod("resolve");
            contextField.setAccessible(true);
            dimensionField.setAccessible(true);
            resolve.setAccessible(true);
            resolve.invoke(card);
            if (!(contextField.get(card) instanceof GridMapContext ctx) ||
                    !(ctx.player() instanceof ServerPlayer player)) return Component.translatable("gtocore.adaptive_net.map", "-");
            @SuppressWarnings("unchecked")
            ResourceKey<Level> dimension = (ResourceKey<Level>) dimensionField.get(card);
            if (dimension == null) return Component.translatable("gtocore.adaptive_net.map", "-");
            var team = TeamUtil.getTeamUUID(player.getUUID());
            if (team == null) team = player.getUUID();
            List<Long> frequencies = FrequencyRegistry.get(player.getServer()).frequenciesOf(team, dimension);
            return Component.translatable("gtocore.adaptive_net.map", frequencies.isEmpty() ? "-" :
                    String.join(", ", frequencies.stream().map(String::valueOf).toList()));
        } catch (ReflectiveOperationException | ClassCastException error) {
            throw new IllegalStateException("Adaptive network node card ABI changed", error);
        }
    }
}
