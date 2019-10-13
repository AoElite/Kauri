package dev.brighten.anticheat.check.impl.packets.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.CheckType;
import dev.brighten.anticheat.check.api.Packet;

@CheckInfo(name = "BadPackets (B)", description = "Checks for block place packets before flying is sent.",
        checkType = CheckType.BADPACKETS, punishVL = 20)
public class BadPacketsB extends Check {

    private long lastFlying;
    @Packet
    public void onPlace(WrappedInBlockPlacePacket place, long timeStamp) {
        long delta = timeStamp - lastFlying;
        if(delta < 5 && !data.lagInfo.lagging) {
            if(vl++ > 4) flag("sent place before flying packet.");
        } else vl-= vl > 0 ? 2 : 0;
        debug("delta=" + delta + "ms");
    }

    @Packet
    public void onFlying(WrappedInFlyingPacket packet, long timeStamp) {
        lastFlying = timeStamp;
    }
}
