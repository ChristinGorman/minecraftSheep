package baxter;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlueAppleBlock extends Block {
	
    private static final String __OBFID = "CL_00006667";
    
	public BlueAppleBlock() {
	    super(Material.clay);
	    setBlockName("blue");
	    setBlockTextureName("baxter:blue");
	}
	
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
	    return Items.carrot;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,  int blockMeta) {
	    System.out.println(String.format("M I L L A %d %d %d %d %f %f %f %d ", x,y,z,side,hitX,hitY,hitZ, blockMeta));
	    return super.onBlockPlaced(world,  x,  y,  z, side, hitX,  hitY,  hitZ,  blockMeta);
	}
	
}
