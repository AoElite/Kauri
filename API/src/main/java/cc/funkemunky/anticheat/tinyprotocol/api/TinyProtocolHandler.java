package cc.funkemunky.anticheat.tinyprotocol.api;

import cc.funkemunky.anticheat.Kauri;
import cc.funkemunky.anticheat.checks.Check;
import lombok.Getter;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
    @Getter
    private static AbstractTinyProtocol instance;

    public static boolean enabled = true;

    public TinyProtocolHandler() {
        TinyProtocolHandler self = this;
        // 1.8+ and 1.7 NMS have different class paths for their libraries used. This is why we have to separate the two.
        // These feed the packets asynchronously, before Minecraft processes it, into our own methods to process and be used as an API.
        instance = ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8) ? new TinyProtocol1_7(Kauri.INSTANCE) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                if(enabled) {
                    return self.onPacketOutAsync(receiver, packet);
                } else {
                    return packet;
                }
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                if(enabled) {
                    return self.onPacketInAsync(sender, packet);
                } else {
                    return packet;
                }
            }
        } : new TinyProtocol1_8(Kauri.INSTANCE) {
            @Override
            public Object onPacketOutAsync(Player receiver, Object packet) {
                if(enabled) {
                    return self.onPacketOutAsync(receiver, packet);
                } else {
                    return packet;
                }
            }

            @Override
            public Object onPacketInAsync(Player sender, Object packet) {
                if(enabled) {
                    return self.onPacketInAsync(sender, packet);
                } else {
                    return packet;
                }
            }
        };
    }

    // Purely for making the code cleaner
    public static void sendPacket(Player player, Object packet) {
        instance.sendPacket(player, packet);
    }

    public static int getProtocolVersion(Player player) {
        return instance.getProtocolVersion(player);
    }

    private boolean didPosition = false;

    public Object onPacketOutAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1);

        return packet;
    }

    public Object onPacketInAsync(Player sender, Object packet) {
        String name = packet.getClass().getName();
        int index = name.lastIndexOf(".");
        String packetName = name.substring(index + 1).replace("PacketPlayInUseItem", "PacketPlayInBlockPlace")
                .replace(Packet.Client.LEGACY_LOOK, Packet.Client.LOOK)
                .replace(Packet.Client.LEGACY_POSITION, Packet.Client.POSITION)
                .replace(Packet.Client.LEGACY_POSITION_LOOK, Packet.Client.POSITION_LOOK);

        switch(packetName) {
            case Packet.Client.POSITION:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.LOOK:
            case Packet.Client.FLYING: {
                Check check = new Check() {
                    @Override
                    public void run(Object object) {

                    }
                };
                break;
            }
        }

        return packet;
    }
}

