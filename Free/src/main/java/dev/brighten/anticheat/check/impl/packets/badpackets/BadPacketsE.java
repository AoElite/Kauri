package dev.brighten.anticheat.check.impl.packets.badpackets;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.api.check.CheckType;

@CheckInfo(name = "BadPackets (E)", description = "Checks for a player not swinging their arm.",
        checkType = CheckType.BADPACKETS, punishVL = 10, developer = true)
public class BadPacketsE extends Check {

    private int attackTicks;

    @Packet
    public void onFlying(WrappedInUseEntityPacket packet) {
        if(!packet.getAction().equals(WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK)) return;

        if(attackTicks++ > 3
                && !data.lagInfo.lagging) {
            vl++;
            flag("has not swung since %v ticks", attackTicks);
        }
    }

    @Packet
    public void onFlying(WrappedInArmAnimationPacket packet) {
        attackTicks = 0;
    }
}
