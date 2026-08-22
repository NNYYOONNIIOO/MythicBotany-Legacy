package mythicbotany.client;

import mythicbotany.entity.EntityAlfPixie;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

/**
 * The 1.12.2 equivalent of MythicBotany's small body-and-four-wing pixie
 * model.  The high-version model uses the same simple cuboids and wing flap.
 */
public class ModelAlfPixie extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer leftWingTop;
    private final ModelRenderer leftWingBottom;
    private final ModelRenderer rightWingTop;
    private final ModelRenderer rightWingBottom;

    public ModelAlfPixie(float scale) {
        textureWidth = 64;
        textureHeight = 32;

        body = new ModelRenderer(this, 0, 0);
        body.addBox(-2.0F, -4.0F, -1.0F, 4, 8, 2, scale);

        leftWingTop = new ModelRenderer(this, 0, 10);
        leftWingTop.setRotationPoint(2.0F, -2.0F, 0.0F);
        leftWingTop.addBox(0.0F, -5.0F, 0.0F, 4, 5, 1, scale);

        leftWingBottom = new ModelRenderer(this, 10, 10);
        leftWingBottom.setRotationPoint(2.0F, 1.0F, 0.0F);
        leftWingBottom.addBox(0.0F, 0.0F, 0.0F, 4, 5, 1, scale);

        rightWingTop = new ModelRenderer(this, 0, 10);
        rightWingTop.setRotationPoint(-2.0F, -2.0F, 0.0F);
        rightWingTop.addBox(-4.0F, -5.0F, 0.0F, 4, 5, 1, scale);

        rightWingBottom = new ModelRenderer(this, 10, 10);
        rightWingBottom.setRotationPoint(-2.0F, 1.0F, 0.0F);
        rightWingBottom.addBox(-4.0F, 0.0F, 0.0F, 4, 5, 1, scale);
    }

    @Override
    public void render(net.minecraft.entity.Entity entity, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        body.render(scale);
        leftWingTop.render(scale);
        leftWingBottom.render(scale);
        rightWingTop.render(scale);
        rightWingBottom.render(scale);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor,
                                  net.minecraft.entity.Entity entity) {
        float flap = MathHelper.cos(ageInTicks * 1.7F);
        rightWingTop.rotateAngleY = -(flap * (float) Math.PI * 0.5F);
        leftWingTop.rotateAngleY = flap * (float) Math.PI * 0.5F;
        rightWingBottom.rotateAngleY = -(flap * (float) Math.PI * 0.25F);
        leftWingBottom.rotateAngleY = flap * (float) Math.PI * 0.25F;
    }

    public void setRotationAngles(EntityAlfPixie entity, float limbSwing, float limbSwingAmount,
                                  float ageInTicks, float netHeadYaw, float headPitch,
                                  float scaleFactor) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                scaleFactor, entity);
    }
}
