package baxter;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPurpleSheep extends RenderLiving
{
    private static final ResourceLocation purpleSheepTextures = new ResourceLocation("baxter:textures/entity/purpleSheep.png");
    private static final ResourceLocation shearedPurpleSheepTextures = new ResourceLocation("baxter:textures/entity/purpleSheep.png");
    private static final String __OBFID = "CL_00070001";

    public RenderPurpleSheep(ModelBase p_i1266_1_, ModelBase p_i1266_2_, float p_i1266_3_)
    {
        super(p_i1266_1_, p_i1266_3_);
        this.setRenderPassModel(p_i1266_2_);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(PurpleSheepEntity p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        if (p_77032_2_ == 0 && !p_77032_1_.getSheared())
        {
            this.bindTexture(purpleSheepTextures);

            if (p_77032_1_.hasCustomNameTag() && "jeb_".equals(p_77032_1_.getCustomNameTag()))
            {
                boolean flag = true;
                int k = p_77032_1_.ticksExisted / 25 + p_77032_1_.getEntityId();
                int l = k % PurpleSheepEntity.fleeceColorTable.length;
                int i1 = (k + 1) % PurpleSheepEntity.fleeceColorTable.length;
                float f1 = ((float)(p_77032_1_.ticksExisted % 25) + p_77032_3_) / 25.0F;
                GL11.glColor3f(PurpleSheepEntity.fleeceColorTable[l][0] * (1.0F - f1) + PurpleSheepEntity.fleeceColorTable[i1][0] * f1, PurpleSheepEntity.fleeceColorTable[l][1] * (1.0F - f1) + PurpleSheepEntity.fleeceColorTable[i1][1] * f1, PurpleSheepEntity.fleeceColorTable[l][2] * (1.0F - f1) + PurpleSheepEntity.fleeceColorTable[i1][2] * f1);
            }
            else
            {
                int j = p_77032_1_.getFleeceColor();
                GL11.glColor3f(PurpleSheepEntity.fleeceColorTable[j][0], PurpleSheepEntity.fleeceColorTable[j][1], PurpleSheepEntity.fleeceColorTable[j][2]);
            }

            return 1;
        }
        else
        {
            return -1;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(PurpleSheepEntity p_110775_1_)
    {
        return shearedPurpleSheepTextures;
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        return this.shouldRenderPass((PurpleSheepEntity)p_77032_1_, p_77032_2_, p_77032_3_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((PurpleSheepEntity)p_110775_1_);
    }

}