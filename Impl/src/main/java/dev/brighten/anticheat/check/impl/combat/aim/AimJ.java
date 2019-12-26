package dev.brighten.anticheat.check.impl.combat.aim;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MathUtils;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.anticheat.processing.MovementProcessor;
import dev.brighten.api.check.CheckType;
import lombok.val;

@CheckInfo(name = "Aim (J)", description = "Checks for strange mouse movements",
        checkType = CheckType.AIM, developer = true)
public class AimJ extends Check {

    private int verbose;

    @Packet
    public void onFlying(WrappedInFlyingPacket packet) {
        if(packet.isLook() && data.moveProcessor.deltaX > 0) {
            val sensX = MovementProcessor.sensToPercent(data.moveProcessor.sensitivityX);
            val sensY = MovementProcessor.sensToPercent(data.moveProcessor.sensitivityY);
            val deltaX = Math.abs(data.moveProcessor.deltaX - data.moveProcessor.lastDeltaX);
            /*
            double deltaY = Math.abs(data.moveProcessor.deltaY - data.moveProcessor.lastDeltaY);
            if (data.moveProcessor.deltaX >= 10 && data.moveProcessor.deltaX <= 20 && data.moveProcessor.deltaY == 2
                    && deltaX >= 2.0 && deltaX <= 5.0 && deltaY == 0.0) {
                flag("dx=" + data.moveProcessor.deltaX + ", dy=" + data.moveProcessor.deltaY + ", mDx=" + deltaX + ", mDy=" + deltaY);
            }

            debug("yaw=" + data.moveProcessor.deltaX
                    + "pitch= " + data.moveProcessor.deltaY
                    + " sens=" + MovementProcessor.sensToPercent(data.moveProcessor.sensitivityX)
                    + " mYaw= " + deltaX
                    + " mPitch= " + deltaY
                    + " dYaw=" + data.playerInfo.deltaYaw
                    + " dPitch=" + data.playerInfo.deltaPitch
                    + " vl=" + vl + " pos=" + data.playerInfo.serverPos);*/

            if(data.moveProcessor.deltaY <= 1
                    && (data.moveProcessor.deltaX > 50 || deltaX < 10)
                    && data.moveProcessor.deltaX > 20) {
                verbose++;
                if(verbose > 9) {
                    vl++;
                    flag("sens=" + sensX + " x=" + data.moveProcessor.deltaX
                            + " y=" + data.moveProcessor.deltaY + " vb=" + verbose + " deltaX=" + deltaX);
                }
            } else verbose = 0;

            debug("sx=" + sensX + " sy=" + sensY
                    + " deltaX=" + deltaX
                    + " x=" + data.moveProcessor.deltaX + " y=" + data.moveProcessor.deltaY
                    + " verbose=" + (verbose > 0 ? Color.Green : "") + verbose);
        }
    }
}