package dev.brighten.anticheat.check.impl.movement.fly;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.math.cond.MaxDouble;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.anticheat.utils.MovementUtils;
import dev.brighten.api.check.CheckType;

@CheckInfo(name = "Fly (C)", description = "Checks for invalid jump heights.",
        checkType = CheckType.FLIGHT, punishVL = 7, vlToFlag = 1)
@Cancellable
public class FlyC extends Check {

    @Packet
    public void onPacket(WrappedInFlyingPacket packet) {
        if (packet.isPos()) {
            float maxHeight = MovementUtils.getJumpHeight(data);
            if (!data.playerInfo.flightCancel
                    && data.playerInfo.jumped
                    && !data.playerInfo.serverPos
                    && !data.playerInfo.wasOnSlime
                    && data.playerInfo.lClientGround
                    && !data.blockInfo.miscNear
                    && data.playerInfo.blockAboveTimer.isPassed(6)
                    && data.playerInfo.lastBlockPlace.isPassed(20)
                    && data.playerInfo.lastHalfBlock.isPassed(4)
                    && data.playerInfo.lastVelocity.isPassed(4)
                    && MathUtils.getDelta(data.playerInfo.deltaY, maxHeight) > 0.01f) {
                vl++;
                flag("deltaY=%v maxHeight=%v", data.playerInfo.deltaY, maxHeight);
            } else vl-= 0.01f;

            debug("deltaY=%v above=%v", data.playerInfo.deltaY,
                    data.playerInfo.blockAboveTimer.getPassed());
        }
    }
}
