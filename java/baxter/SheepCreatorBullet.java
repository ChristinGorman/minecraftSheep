package baxter;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SheepCreatorBullet extends Entity implements IProjectile {
    private int x = -1;
    private int y = -1;
    private int z = -1;
    private Block lodgedInBlock;
    private int inData;
    private boolean inGround;
    /** 1 if the player can pick up the arrow */
    public int canBePickedUp;
    /** Seems to be some sort of timer for animating an arrow. */
    public int arrowShake;
    /** The owner of this arrow. */
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private static final String __OBFID = "CL_00070002";

    public SheepCreatorBullet(World world) {
        super(world);
        renderDistanceWeight = 10;
        setSize(0.5f, 0.5f);
    }

    public SheepCreatorBullet(World world, double posX, double posY, double posZ) {
        super(world);
        renderDistanceWeight = 10;
        setSize(0.5F, 0.5F);
        setPosition(posX, posY, posZ);
        yOffset = 0;
    }

    public SheepCreatorBullet(World world, EntityLivingBase player, EntityLivingBase living, float yaw, float pitch) {
        super(world);
        renderDistanceWeight = 10;
        shootingEntity = player;

        if (player instanceof EntityPlayer) {
            canBePickedUp = 1;
        }

        posY = player.posY + (double) player.getEyeHeight() - 0.1;
        double distanceX = living.posX - player.posX;
        double distanceY = living.boundingBox.minY + (living.height / 3) - posY;
        double distanceZ = living.posZ - player.posZ;
        double distanceSq = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

        if (distanceSq >= 10000000) {
            float f2 = (float) (atan2(distanceZ, distanceX) * 180 / PI) - 90;
            float f3 = (float) (-(atan2(distanceY, distanceSq) * 180 / PI));
            setLocationAndAngles(player.posX + (distanceX / distanceSq), posY, player.posZ + (distanceZ / distanceSq), f2, f3);
            yOffset = 0;
            setThrowableHeading(distanceX, distanceY + (distanceSq * 0.2), distanceZ, yaw, pitch);
        }
    }

    public SheepCreatorBullet(World world, EntityLivingBase player, float heading) {
        super(world);
        renderDistanceWeight = 10;
        shootingEntity = player;

        if (player instanceof EntityPlayer) {
            canBePickedUp = 1;
        }

        setSize(0.5F, 0.5F);
        setLocationAndAngles(player.posX, player.posY + player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        posX -= cos(rotationYaw / 180 * PI) * 0.16;
        posY -= 0.1;
        posZ -= sin(rotationYaw / 180 * PI) * 0.16;
        setPosition(posX, posY, posZ);
        yOffset = 0;
        motionX = (-sin(rotationYaw / 180 * PI) * cos(rotationPitch / 180 * PI));
        motionZ = (cos(rotationYaw / 180 * PI) * cos(rotationPitch / 180 * PI));
        motionY = (-sin(rotationPitch / 180 * PI));
        setThrowableHeading(motionX, motionY, motionZ, heading * 1.5f, 1);
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(16, (byte)0);
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float yaw, float pitch) {
        double f2 = sqrt(x * x + y * y + z * z);
        x /= f2;
        y /= f2;
        z /= f2;
        x += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.0075 * pitch;
        y += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.0075 * pitch;
        z += rand.nextGaussian() * (rand.nextBoolean() ? -1 : 1) * 0.0075 * pitch;
        x *= yaw;
        y *= yaw;
        z *= yaw;
        motionX = x;
        motionY = y;
        motionZ = z;
        prevRotationYaw = rotationYaw = (float) (atan2(x, z) * 180 / PI);
        prevRotationPitch = rotationPitch = (float)(atan2(y, sqrt(x * x + z * z)) * 180 / PI);
        ticksInGround = 0;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no
     * bounding on the rotation. Args: posX, posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int something) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void setVelocity(double x, double y, double z) {
        motionX = x;
        motionY = y;
        motionZ = z;

        if (prevRotationPitch == 0 && prevRotationYaw == 0) {
            prevRotationYaw = rotationYaw = (float) (atan2(x, z) * 180 / PI);
            prevRotationPitch = rotationPitch = (float) (atan2(y, sqrt(x * x + z * z)) * 180 / PI);
            prevRotationPitch = rotationPitch;
            prevRotationYaw = rotationYaw;
            setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
            ticksInGround = 0;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (prevRotationPitch == 0f && prevRotationYaw == 0f) {
            double motionSqRoot = sqrt(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) (atan2(motionX, motionZ) * 180 / PI);
            prevRotationPitch = rotationPitch = (float) (atan2(motionY, motionSqRoot) * 180 / PI);
        }

        Block block = worldObj.getBlock(x, y, z);
        inGround = hitGround(block);

        arrowShake = Math.max(0, arrowShake - 1);

        if (inGround) {
            int blockData = worldObj.getBlockMetadata(x, y, z);
            if (block == lodgedInBlock && blockData == inData && ++ticksInGround == 1200) {
                setDead();
            } else {
                inGround = false;
                motionX *= rand.nextFloat() * 0.2F;
                motionY *= rand.nextFloat() * 0.2F;
                motionZ *= rand.nextFloat() * 0.2F;
                ticksInGround = 0;
                ticksInAir = 0;
            }
        } else {
            ++ticksInAir;
            Vec3 current = Vec3.createVectorHelper(posX, posY, posZ);
            Vec3 next = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition movingPos = worldObj.func_147447_a(current, next, false, true, false);

            if (movingPos != null) {
                next = movingPos.hitVec;
            }

            Entity hit = hitAnything(current, next);
            if (hit != null) {
                movingPos = new MovingObjectPosition(hit);
                if (hit instanceof EntityLivingBase && !isDead && !worldObj.isRemote) {
                    EntityLivingBase livingHit = (EntityLivingBase) hit;
                    livingHit.setDead();
                    PurpleSheepEntity crazy = new PurpleSheepEntity(worldObj);
                    crazy.setFleeceColor(20);
                    crazy.setGrowingAge(-30);
                    crazy.setAngles(0f, 0f);
                    crazy.setPosition(livingHit.posX, livingHit.posY, livingHit.posZ);
                    crazy.setJumping(true);
                    crazy.setAir(10);
                    crazy.setSprinting(true);
                    worldObj.spawnEntityInWorld(crazy);
                }
                playSound("random.fizz", 1f, (float) (1.2 / (rand.nextFloat() * 0.2 + 0.9)));
                setDead();
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            double f2 = sqrt(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float)(atan2(motionX, motionZ) * 180 / PI);

            for (rotationPitch = (float) (atan2(motionY, f2) * 180 / PI); rotationPitch - prevRotationPitch < -180; prevRotationPitch -= 360) {
                ;
            }

            while (rotationPitch - prevRotationPitch >= 180) {
                prevRotationPitch += 360;
            }

            while (rotationYaw - prevRotationYaw < -180) {
                prevRotationYaw -= 360;
            }

            while (rotationYaw - prevRotationYaw >= 180) {
                prevRotationYaw += 360;
            }

            rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2f;
            rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2f;
            double f3 = 0.99f;

            if (isInWater()) {
                for (int l = 0; l < 4; ++l) {
                    worldObj.spawnParticle("bubble", posX - motionX * 0.25, posY - motionY * 0.25, posZ - motionZ * 0.25, motionX, motionY, motionZ);
                }
                f3 = 0.8f;
            }

            if (isWet()) {
                extinguish();
            }

            motionX *= f3;
            motionY *= f3;
            motionZ *= f3;
            motionY -= 0.05;
            setPosition(posX, posY, posZ);
            func_145775_I();
        }
    }

    private boolean hitGround(Block block) {
        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(worldObj, x, y, z);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
            return axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(posX, posY, posZ));
        }
        return false;
    }

    private Entity hitAnything(Vec3 current, Vec3 next) {
        Entity entity = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1, 1, 1));
        double smallestDistance = 0;
        double expansion = 0.3;
        for (Object obj : list) {
            Entity entity1 = (Entity)obj;
            if (entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5)) {
                AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand(expansion, expansion, expansion);
                MovingObjectPosition pos = axisalignedbb1.calculateIntercept(current, next);
                if (pos != null) {
                    double distance = current.distanceTo(pos.hitVec);
                    if (distance < smallestDistance || smallestDistance == 0) {
                        entity = entity1;
                        smallestDistance = distance;
                    }
                }
            }
        }
        return entity;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound packet) {
        packet.setShort("xTile", (short) x);
        packet.setShort("yTile", (short) y);
        packet.setShort("zTile", (short) z);
        packet.setShort("life", (short) ticksInGround);
        packet.setByte("inTile", (byte) Block.getIdFromBlock(lodgedInBlock));
        packet.setByte("inData", (byte) inData);
        packet.setByte("shake", (byte) arrowShake);
        packet.setByte("inGround", (byte) (inGround ? 1 : 0));
        packet.setByte("pickup", (byte) canBePickedUp);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound packet) {
        x = packet.getShort("xTile");
        y = packet.getShort("yTile");
        z = packet.getShort("zTile");
        ticksInGround = packet.getShort("life");
        lodgedInBlock = Block.getBlockById(packet.getByte("inTile") & 255);
        inData = packet.getByte("inData") & 255;
        arrowShake = packet.getByte("shake") & 255;
        inGround = packet.getByte("inGround") == 1;

        if (packet.hasKey("pickup", 99)) {
            canBePickedUp = packet.getByte("pickup");
        } else if (packet.hasKey("player", 99)) {
            canBePickedUp = packet.getBoolean("player") ? 1 : 0;
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (worldObj.isRemote || !inGround || arrowShake > 0) return;
        
        boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && player.capabilities.isCreativeMode;
        if (flag || (canBePickedUp == 1 && player.inventory.addItemStackToInventory(new ItemStack(Baxter.sheepCreatorBullet, 1)))) {
            playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7f + 1) * 2);
            player.onItemPickup(this, 1);
            setDead();
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getShadowSize() {
        return 0;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    @Override
    public boolean canAttackWithItem() {
        return false;
    }

}