package baxter;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.world.World;

public class PurpleSheepEntity extends EntitySheep {
	
    public PurpleSheepEntity(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

    @Override
    protected String getLivingSound() {
        return "mob.villager.haggle";
    }
    
    
	
}
