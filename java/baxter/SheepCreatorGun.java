package baxter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SheepCreatorGun extends ItemBow {
    public static final String[] bowPullIconNameArray = new String[] { "pulling_2", "pulling_1", "pulling_0" };
    @SideOnly(Side.CLIENT)
    private IIcon[] iconArray;
    private static final String __OBFID = "CL_10006670";

    public SheepCreatorGun() {
        this.maxStackSize = 1;
        setUnlocalizedName("calvinStar");
        setTextureName("baxter:calvinStar");
    }

    /**
     * called when the player releases the use item button. Args: itemstack,
     * world, entityplayer, itemInUseCount
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemUseInCount) {
        int remaining = this.getMaxItemUseDuration(stack) - itemUseInCount;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, remaining);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }

        if (!world.isRemote) {
            float f = event.charge / 20f;
            f = (f * f + f * 2) / 3;
            
            if (f < 0.1) {
                return;
            }
            
            if (f > 1) {
                f = 1;
            }
            SheepCreatorBullet bullet = new SheepCreatorBullet(world, player, f * 2);
            world.playSoundAtEntity(player, "mob.pig.say", 1.0F, (float)(1 / (itemRand.nextFloat() * 0.4 + 1.2) + f * 0.5));
            bullet.canBePickedUp = 2;
            world.spawnEntityInWorld(bullet);
        }

    }

    @Override
    public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items
     * is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.bow;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is
     * pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        ArrowNockEvent event = new ArrowNockEvent(par3EntityPlayer, par1ItemStack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return event.result;
        }

        if (par3EntityPlayer.capabilities.isCreativeMode || par3EntityPlayer.inventory.hasItem(Baxter.sheepCreatorBullet)) {
            par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        }

        return par1ItemStack;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based
     * on material.
     */
    public int getItemEnchantability() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(this.getIconString() + "_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i) {
            this.iconArray[i] = par1IconRegister.registerIcon(this.getIconString() + "_" + bowPullIconNameArray[i]);
        }
    }

    /**
     * used to cycle through icons based on their used duration, i.e. for the
     * bow
     */
    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int par1) {
        return this.iconArray[par1];
    }
}