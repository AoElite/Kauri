package cc.funkemunky.anticheat.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import lombok.val;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockBox1_12_R1 implements BlockBox {
    @Override
    public List<BoundingBox> getCollidingBoxes(World world, BoundingBox box) {
        BoundingBox collisionBox = box;
        List<AxisAlignedBB> aabbs = new ArrayList<>();
        List<BoundingBox> boxes = new ArrayList<>();

        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);


        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (block != null && !block.getType().equals(Material.AIR)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            final int aX = x, aY = y, aZ = z;
                            net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld) world).getHandle();
                            BlockPosition pos = new BlockPosition(aX, aY, aZ);
                            IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                            Block nmsBlock = nmsiBlockData.getBlock();
                            List<AxisAlignedBB> preBoxes = new ArrayList<>();
                            nmsBlock.updateState(nmsiBlockData, nmsWorld, pos);
                            nmsBlock.a(nmsiBlockData, nmsWorld, pos, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null, true);

                            if (preBoxes.size() > 0) {
                                aabbs.addAll(preBoxes);
                            } else {
                                aabbs.add(nmsBlock.b(nmsiBlockData, nmsWorld, new BlockPosition(aX, aY, aZ)));
                            }

                            if (nmsBlock instanceof BlockShulkerBox) {
                                TileEntity tileentity = nmsWorld.getTileEntity(pos);
                                BlockShulkerBox shulker = (BlockShulkerBox) nmsBlock;

                                if (tileentity instanceof TileEntityShulkerBox) {
                                    TileEntityShulkerBox entity = (TileEntityShulkerBox) tileentity;
                                    //Bukkit.broadcastMessage("entity");
                                    aabbs.add(entity.a(nmsiBlockData));

                                    val loc = block.getLocation();
                                    if (entity.p().toString().contains("OPEN") || entity.p().toString().contains("CLOSING")) {
                                        aabbs.add(new AxisAlignedBB(loc.getX(), loc.getY(), loc.getZ(), loc.getX() + 1, loc.getY() + 1.5, loc.getZ() + 1));
                                    }
                                }
                            }
                        }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                    }
                }
            }
        }

        aabbs.stream().filter(Objects::nonNull).forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.clone().toVector(), loc.clone().toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_12_R1.World world = ((CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.areChunksLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()), 4);
    }

    @Override
    public boolean isUsingItem(Player player) {
        EntityLiving entity = ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity) player).getHandle();
        return entity.cJ() != null && entity.cJ().getItem().f(entity.cJ()) != EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.b().getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().cy();
    }
}
