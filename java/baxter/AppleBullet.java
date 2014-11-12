package baxter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AppleBullet extends Entity implements IProjectile {
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
    private double damage = 2.0D;
    /** The amount of knockback an arrow applies when it hits a mob. */
    private int knockbackStrength;
    private static final AtomicInteger counter = new AtomicInteger(0);

    private int myCount = counter.incrementAndGet();
    private static final String __OBFID = "CL_00070002";

    public AppleBullet(World world) {
        super(world);
        renderDistanceWeight = 10.0D;
        setSize(0.5F, 0.5F);
    }

    public AppleBullet(World world, double posX, double posY, double posZ) {
        super(world);
        renderDistanceWeight = 10.0D;
        setSize(0.5F, 0.5F);
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
    }

    public AppleBullet(World world, EntityLivingBase player, EntityLivingBase living, float yaw_maybe, float pitch_maybe) {
        super(world);
        renderDistanceWeight = 10.0D;
        shootingEntity = player;

        if (player instanceof EntityPlayer) {
            canBePickedUp = 1;
        }

        posY = player.posY + (double) player.getEyeHeight() - 0.1;
        double d0 = living.posX - player.posX;
        double d1 = living.boundingBox.minY + (double) (living.height / 3.0F) - posY;
        double d2 = living.posZ - player.posZ;
        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D) {
            float f2 = (float) (Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float) (-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            setLocationAndAngles(player.posX + d4, posY, player.posZ + d5, f2, f3);
            yOffset = 0.0F;
            float f4 = (float) d3 * 0.2F;
            setThrowableHeading(d0, d1 + (double) f4, d2, yaw_maybe, pitch_maybe);
        }
    }

    public AppleBullet(World world, EntityLivingBase player, float heading) {
        super(world);
        renderDistanceWeight = 10.0D;
        shootingEntity = player;

        if (player instanceof EntityPlayer) {
            canBePickedUp = 1;
        }

        setSize(0.5F, 0.5F);
        setLocationAndAngles(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        posX -= (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        posY -= 0.10000000149011612D;
        posZ -= (double) (MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        setPosition(posX, posY, posZ);
        yOffset = 0.0F;
        motionX = (double) (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
        motionZ = (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
        motionY = (double) (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));
        setThrowableHeading(motionX, motionY, motionZ, heading * 1.5F, 1.0F);
    }

    protected void entityInit() {
        dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z
     * direction.
     */
    public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
        float f2 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
        p_70186_1_ /= (double) f2;
        p_70186_3_ /= (double) f2;
        p_70186_5_ /= (double) f2;
        p_70186_1_ += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) p_70186_8_;
        p_70186_3_ += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) p_70186_8_;
        p_70186_5_ += rand.nextGaussian() * (double) (rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) p_70186_8_;
        p_70186_1_ *= (double) p_70186_7_;
        p_70186_3_ *= (double) p_70186_7_;
        p_70186_5_ *= (double) p_70186_7_;
        motionX = p_70186_1_;
        motionY = p_70186_3_;
        motionZ = p_70186_5_;
        float f3 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
        prevRotationYaw = rotationYaw = (float) (Math.atan2(p_70186_1_, p_70186_5_) * 180.0D / Math.PI);
        prevRotationPitch = rotationPitch = (float) (Math.atan2(p_70186_3_, (double) f3) * 180.0D / Math.PI);
        ticksInGround = 0;
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no
     * bounding on the rotation. Args: posX, posY, posZ, yaw, pitch
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_) {
        setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        setRotation(p_70056_7_, p_70056_8_);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        motionX = p_70016_1_;
        motionY = p_70016_3_;
        motionZ = p_70016_5_;

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(p_70016_3_, (double) f) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch;
            prevRotationYaw = rotationYaw;
            setLocationAndAngles(posX, posY, posZ, rotationYaw, rotationPitch);
            ticksInGround = 0;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    /*
     * (non-Javadoc)
     * 
     * @see net.minecraft.entity.Entity#onUpdate()
     */
    public void onUpdate() {
        super.onUpdate();

        if (prevRotationPitch == 0.0F && prevRotationYaw == 0.0F) {
            double f = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);
            prevRotationPitch = rotationPitch = (float) (Math.atan2(motionY, f) * 180.0D / Math.PI);
        }

        Block block = worldObj.getBlock(x, y, z);
        inGround = hitGround(block);

        arrowShake = Math.max(0, arrowShake - 1);

        if (inGround) {
            int blockData = worldObj.getBlockMetadata(x, y, z);
            if (block == lodgedInBlock && blockData == inData) {
                if (++ticksInGround == 1200) {
                    setDead();
                }
            } else {
                inGround = false;
                motionX *= (double) (rand.nextFloat() * 0.2F);
                motionY *= (double) (rand.nextFloat() * 0.2F);
                motionZ *= (double) (rand.nextFloat() * 0.2F);
                ticksInGround = 0;
                ticksInAir = 0;
            }
        } else {
            ++ticksInAir;
            Vec3 current = Vec3.createVectorHelper(posX, posY, posZ);
            Vec3 next = Vec3.createVectorHelper(posX + motionX, posY + motionY, posZ + motionZ);
            MovingObjectPosition movingPos = worldObj.func_147447_a(current, next, false, true, false);

            if (movingPos != null) {
                next = Vec3.createVectorHelper(movingPos.hitVec.xCoord, movingPos.hitVec.yCoord, movingPos.hitVec.zCoord);
            }

            Entity hit = hitAnything(current, next);

            if (hit != null) {
                movingPos = new MovingObjectPosition(hit);
            }

            if (hit != null) {
                float f2 = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
                if (hit instanceof EntityLivingBase && !isDead && !worldObj.isRemote) {
                    EntityLivingBase livingHit = (EntityLivingBase) hit;
                    livingHit.setDead();
                    PurpleSheepEntity crazy = new PurpleSheepEntity(worldObj);
                    crazy.setGrowingAge(24000);
                    crazy.setAngles(0f, 0f);
                    crazy.setPosition(posX, posY, posZ);
                    crazy.setJumping(true);
                    worldObj.spawnEntityInWorld(crazy);
                }
                playSound("random.fizz", 1f, (float) (1.2 / (rand.nextFloat() * 0.2 + 0.9)));
                if (!(hit instanceof EntityEnderman)) {
                    setDead();
                }
            }

            if (getIsCritical()) {
                for (double i = 0; i < 4; ++i) {
                    worldObj.spawnParticle("crit", posX + motionX * i / 4d, posY + motionY * i / 4d, posZ + motionZ * i / 4d, -motionX, -motionY + 0.2D, -motionZ);
                }
            }

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            float f2 = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            rotationYaw = (float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI);

            for (rotationPitch = (float) (Math.atan2(motionY, f2) * 180 / Math.PI); rotationPitch - prevRotationPitch < -180; prevRotationPitch -= 360) {
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

    private Entity hitAnything(Vec3 vec31, Vec3 vec3) {
        Entity entity = null;
        List list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
        double d0 = 0.0D;
        int i;
        float f1;

        for (i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (entity1.canBeCollidedWith() && (entity1 != shootingEntity || ticksInAir >= 5)) {
                f1 = 0.3F;
                AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
                MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec31, vec3);

                if (movingobjectposition1 != null) {
                    double d1 = vec31.distanceTo(movingobjectposition1.hitVec);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }
        return entity;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setShort("xTile", (short) x);
        p_70014_1_.setShort("yTile", (short) y);
        p_70014_1_.setShort("zTile", (short) z);
        p_70014_1_.setShort("life", (short) ticksInGround);
        p_70014_1_.setByte("inTile", (byte) Block.getIdFromBlock(lodgedInBlock));
        p_70014_1_.setByte("inData", (byte) inData);
        p_70014_1_.setByte("shake", (byte) arrowShake);
        p_70014_1_.setByte("inGround", (byte) (inGround ? 1 : 0));
        p_70014_1_.setByte("pickup", (byte) canBePickedUp);
        p_70014_1_.setDouble("damage", damage);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        x = p_70037_1_.getShort("xTile");
        y = p_70037_1_.getShort("yTile");
        z = p_70037_1_.getShort("zTile");
        ticksInGround = p_70037_1_.getShort("life");
        lodgedInBlock = Block.getBlockById(p_70037_1_.getByte("inTile") & 255);
        inData = p_70037_1_.getByte("inData") & 255;
        arrowShake = p_70037_1_.getByte("shake") & 255;
        inGround = p_70037_1_.getByte("inGround") == 1;

        if (p_70037_1_.hasKey("damage", 99)) {
            damage = p_70037_1_.getDouble("damage");
        }

        if (p_70037_1_.hasKey("pickup", 99)) {
            canBePickedUp = p_70037_1_.getByte("pickup");
        } else if (p_70037_1_.hasKey("player", 99)) {
            canBePickedUp = p_70037_1_.getBoolean("player") ? 1 : 0;
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
        if (!worldObj.isRemote && inGround && arrowShake <= 0) {
            boolean flag = canBePickedUp == 1 || canBePickedUp == 2 && p_70100_1_.capabilities.isCreativeMode;

            if (canBePickedUp == 1 && !p_70100_1_.inventory.addItemStackToInventory(new ItemStack(Baxter.appleBullet, 1))) {
                flag = false;
            }

            if (flag) {
                playSound("random.pop", 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                p_70100_1_.onItemPickup(this, 1);
                setDead();
            }
        }
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0F;
    }

    public void setDamage(double p_70239_1_) {
        damage = p_70239_1_;
    }

    public double getDamage() {
        return damage;
    }

    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockbackStrength(int p_70240_1_) {
        knockbackStrength = p_70240_1_;
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem() {
        return false;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public void setIsCritical(boolean p_70243_1_) {
        byte b0 = dataWatcher.getWatchableObjectByte(16);

        if (p_70243_1_) {
            dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 | 1)));
        } else {
            dataWatcher.updateObject(16, Byte.valueOf((byte) (b0 & -2)));
        }
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind
     * it.
     */
    public boolean getIsCritical() {
        byte b0 = dataWatcher.getWatchableObjectByte(16);
        return (b0 & 1) != 0;
    }
}