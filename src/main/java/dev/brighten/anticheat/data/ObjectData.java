package dev.brighten.anticheat.data;

import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.TickTimer;
import dev.brighten.anticheat.data.classes.BlockInformation;
import dev.brighten.anticheat.data.classes.CheckManager;
import dev.brighten.anticheat.data.classes.PlayerInformation;
import dev.brighten.anticheat.data.classes.PredictionService;
import dev.brighten.anticheat.utils.PastLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObjectData {

    public UUID uuid;
    private Player player;
    public boolean alerts;

    //Debugging
    public String debugging;
    public UUID debugged;

    public TickTimer creation;
    public PastLocation pastLocation,
            targetPastLocation;
    public LivingEntity target;
    public BoundingBox box, targetBounds;
    public ObjectData INSTANCE;
    public CheckManager checkManager;
    public PlayerInformation playerInfo;
    public BlockInformation blockInfo;
    public LagInformation lagInfo;
    public PredictionService predictionService;
    public List<LivingEntity> entitiesNearPlayer = new ArrayList<>();

    public ObjectData(UUID uuid) {
        this.uuid = uuid;
        INSTANCE = this;
        creation = new TickTimer(10);
        alerts = getPlayer().hasPermission("kauri.alerts");
        creation.reset();
        playerInfo = new PlayerInformation();
        blockInfo = new BlockInformation(this);
        lagInfo = new LagInformation();
        pastLocation = new PastLocation();
        targetPastLocation = new PastLocation();
        checkManager = new CheckManager(this);
        checkManager.addChecks();
        predictionService = new PredictionService(this);
        predictionService.posX = getPlayer().getLocation().getX();
        predictionService.posY = getPlayer().getLocation().getY();
        predictionService.posZ = getPlayer().getLocation().getZ();
    }

    public Player getPlayer() {
        if(player == null) {
            this.player = Bukkit.getPlayer(uuid);
        }
        return this.player;
    }

    public class LagInformation {
        public long lastKeepAlive, lastTrans;
        public long ping, transPing, lastPing, lastTransPing;
        public boolean lagging;
        public TickTimer lastPacketDrop = new TickTimer(10), lastPingDrop = new TickTimer(40);
        public long lastFlying;
    }
}