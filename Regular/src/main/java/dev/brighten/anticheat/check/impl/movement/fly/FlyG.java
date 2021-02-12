package dev.brighten.anticheat.check.impl.movement.fly;

import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInFlyingPacket;
import cc.funkemunky.api.utils.MathUtils;
import dev.brighten.anticheat.check.api.Cancellable;
import dev.brighten.anticheat.check.api.Check;
import dev.brighten.anticheat.check.api.CheckInfo;
import dev.brighten.anticheat.check.api.Packet;
import dev.brighten.anticheat.processing.TagsBuilder;
import dev.brighten.api.KauriVersion;
import dev.brighten.api.check.CheckType;
import org.bukkit.potion.PotionEffectType;

@CheckInfo(name = "Fly (G)", description = "Looks for impossible movements, commonly done by Step modules",
        developer = true, checkType = CheckType.FLIGHT,
        planVersion = KauriVersion.FULL)
@Cancellable
public class FlyG extends Check {

    @Packet
    public void onFlying(WrappedInFlyingPacket packet) {
        if(!packet.isPos()) return;

        boolean toGround = data.playerInfo.clientGround && data.playerInfo.serverGround;
        boolean fromGround = data.playerInfo.lClientGround && data.playerInfo.lServerGround;

        TagsBuilder tags = new TagsBuilder();

        double max = data.playerInfo.jumpHeight;
        if(toGround) {
            if(!fromGround) {
                if(data.playerInfo.lDeltaY > 0 && data.playerInfo.blockAboveTimer.isPassed(2)) {
                    tags.addTag("INVALID_LANDING");
                    max = 0;
                }
            } else {
                if(data.blockInfo.onSlab || data.blockInfo.onStairs)
                    max = 0.5;
                else if(data.blockInfo.onHalfBlock || data.blockInfo.miscNear)
                    max = 0.5625;

                tags.addTag("GROUND_STEP");
            }
        }

        if(data.playerInfo.deltaY > max && tags.getSize() > 0 && !data.playerInfo.flightCancel) {
            vl++;
            flag("t=" + tags.build());
        }
    }
}
