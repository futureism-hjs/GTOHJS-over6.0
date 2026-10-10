package com.gtohjs.methods;

import com.gtocore.api.wireless.energy.EnergyAccount;
import com.gtocore.api.wireless.energy.GridNode;
import com.gtocore.api.wireless.energy.WirelessGrid;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.UUID;

/** Read-only native grid parameters. Query on the server thread for current values. */
public final class WirelessGridMethods {
    public record AccountParameters(boolean present, @Nullable UUID team, long rate,
                                    BigInteger stored, BigInteger capacity, double averageLossPercent) {}
    public record NodeParameters(boolean present, @Nullable ResourceKey<Level> dimension,
                                 int tier, int reachTier, BigInteger stored, BigInteger capacity) {}

    private WirelessGridMethods() {}

    public static long rate(@Nullable UUID owner) {
        return WirelessGrid.accountIfPresent(owner).rate();
    }

    public static AccountParameters accountParameters(@Nullable UUID owner) {
        EnergyAccount account = WirelessGrid.accountIfPresent(owner);
        return new AccountParameters(!account.isNone(), account.isNone() ? null : account.team,
                account.rate(), account.totalStorage(), account.totalCapacity(), account.averageLossPercent());
    }

    /** Native GridBody mapping also resolves an orbit dimension to its planet node. */
    public static NodeParameters nodeParameters(@Nullable UUID owner, @Nullable ResourceKey<Level> dimension) {
        GridNode node = WirelessGrid.accountIfPresent(owner).node(dimension);
        return new NodeParameters(node.dimension() != null, node.dimension(), node.tier(), node.reachTier(),
                node.storage(), node.capacity());
    }
}
