package baxter;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelSheep1;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Baxter.MODID, version = Baxter.VERSION)
public class Baxter
{
	public static final String MODID = "baxter";
    public static final String VERSION = "1.0";

    @Instance(value=MODID)
    public static Baxter instance;
    
    static Item blueApple;
    static Block blue;
    
    static Item sheepCreatorGun;
    static Item sheepCreatorBullet;
    static PurpleSheepEntity sheep;
    
    public static CreativeTabs baxterTab = new CreativeTabs("baxterTab") {
        @Override
        public Item getTabIconItem() {
            return Items.apple;
        }
        
        public String getTabLabel() {
            return "Baxter";
        };
        
        public String getTranslatedTabLabel() {
            return "Baxter";
        };        
    };
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        blueApple = new BlueAppleItem().setCreativeTab(baxterTab);
        GameRegistry.registerItem(blueApple, blueApple.getUnlocalizedName());

        blue = new BlueAppleBlock().setCreativeTab(baxterTab);
        GameRegistry.registerBlock(blue, "blue");
        
        sheepCreatorGun = new SheepCreatorGun().setCreativeTab(baxterTab);
        GameRegistry.registerItem(sheepCreatorGun, "sheepCreatorGun");
        
        sheepCreatorBullet = new Item().setTextureName("baxter:calvinStar");
        
        int bulletId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(SheepCreatorBullet.class, "SheepCreatorBullet", bulletId);
        EntityRegistry.registerModEntity(SheepCreatorBullet.class, "SheepCreatorBullet", bulletId, this, 128, 1, true);
        RenderingRegistry.registerEntityRenderingHandler(SheepCreatorBullet.class, new RenderSnowball(sheepCreatorBullet));

        int sheepId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(PurpleSheepEntity.class, "PurpleSheepEntity", sheepId);
        EntityRegistry.registerModEntity(PurpleSheepEntity.class, "PurpleSheepEntity", sheepId, this, 129, 1, true);
        RenderingRegistry.registerEntityRenderingHandler(PurpleSheepEntity.class, new RenderPurpleSheep(new ModelPurpleSheep2(), new ModelPurpleSheep1(), 0.6F));
        
        GameRegistry.registerItem(sheepCreatorBullet, "sheepCreatorBullet");
    }
    
}
