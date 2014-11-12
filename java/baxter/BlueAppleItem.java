package baxter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlueAppleItem extends Item {
	
    private static final String __OBFID = "CL_10006662";
    
	public BlueAppleItem() {
	    setMaxStackSize(64);
	    setUnlocalizedName("blueApple");
	    setTextureName("baxter:blueApple");
	    bFull3D = true;
	    
	}
	
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
		System.out.println("C A L V I N");
		player.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
        return itemStack;
    }
	
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.eat;
	}
}
