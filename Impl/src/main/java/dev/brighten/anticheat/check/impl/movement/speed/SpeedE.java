package dev.brighten.anticheat.check.impl.movement.speed;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.MathUtils;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.anticheat.utils.MovementUtils;
import lombok.var;

@CheckInfo(name = "Speed (E)", description = "Ensures a user doesn't go faster than the absolute maximum speed.",
        punishVL = 3)
@Cancellable
public class SpeedE extends Check {

    @Packet
    public void onFlying(WrappedInFlyingPacket packet) {
        if(packet.isPos() && !data.playerInfo.generalCancel
                && !data.playerInfo.riptiding) {
            var max = data.playerInfo.baseSpeed;

            if(data.playerInfo.blockAboveTimer.hasNotPassed(5)
                    || data.playerInfo.iceTimer.hasNotPassed(4)
                    || data.playerInfo.slimeTimer.hasNotPassed(5)
                    || data.playerInfo.lastHalfBlock.hasNotPassed(5)) {
                max+= 0.4;
            }

            if(max < 1) max = Math.sqrt(max) * 2;
            else max*= 4;

            if(data.playerInfo.lastVelocity.hasNotPassed(80))
                max = Math.max(max, MathUtils.hypot(data.playerInfo.velocityX, data.playerInfo.velocityZ) * 1.5);

            if(data.playerInfo.deltaXZ > max) {
                vl++;
                flag("deltaXZ=%1 max=%2",
                        MathUtils.round(data.playerInfo.deltaXZ, 3), MathUtils.round(max, 3));
            }
        }
    }
}
