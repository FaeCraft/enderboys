package io.github.faecraft.enderboys.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EndermanEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.EndermiteEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnderboyEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {

	public boolean carryingBlock;
	public boolean angry;

	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public EnderboyEntityModel(float f) {
		super(0.0F, -14.0F, 64, 32);
		float k = -14.0F;
		body = new ModelPart(this);
		body.setPivot(0.0F, 0.0F, 0.0F);
		body.setTextureOffset(35, 1).addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

		head = new ModelPart(this);
		head.setPivot(0.0F, 0.0F, 0.0F);
		head.setTextureOffset(1, 1).addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		right_arm = new ModelPart(this);
		right_arm.setPivot(5.0F, 2.0F, 0.0F);
		right_arm.setTextureOffset(13, 18).addCuboid(-11.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, true);

		left_arm = new ModelPart(this);
		left_arm.setPivot(-5.0F, 2.0F, 0.0F);
		left_arm.setTextureOffset(13, 18).addCuboid(9.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

		right_leg = new ModelPart(this);
		right_leg.setPivot(2.0F, 12.0F, 0.0F);
		right_leg.setTextureOffset(3, 18).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

		left_leg = new ModelPart(this);
		left_leg.setPivot(-2.0F, 12.0F, 0.0F);
		left_leg.setTextureOffset(3, 18).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, true);
	}

	@Override
	public void setAngles(T livingEntity,
						  float limbSwing,
						  float limbSwingAmount,
						  float ageInTicks,
						  float netHeadYaw,
						  float headPitch) {
		super.setAngles(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

		this.torso.pitch = 0.0F;
		this.torso.pivotY = -14.0F;
		this.torso.pivotZ = -0.0F;

		this.left_arm.pitch = (float)((double)left_arm.pitch * 0.2D);
		this.right_arm.pitch = (float)((double)right_arm.pitch * 0.2D);

		left_leg.pitch = MathHelper.cos(limbSwing * 1f) * 0.7f * limbSwingAmount;
		right_leg.pitch = MathHelper.cos(limbSwing * 1f + (float) Math.PI) * 0.7f * limbSwingAmount;

		right_arm.pitch = MathHelper.cos(limbSwing * 1f) * 0.7f * limbSwingAmount;
		left_arm.pitch = MathHelper.cos(limbSwing * 1f + (float) Math.PI) * 0.7f * limbSwingAmount;

		if (this.carryingBlock) {
			this.right_arm.pitch = -1F;
			this.left_arm.pitch = -1F;
			this.right_arm.roll = 0.1F;
			this.left_arm.roll = -0.1F;
		}
	}


	@Override
	public void render(MatrixStack matrixStack, VertexConsumer	buffer,
					   int packedLight,
					   int packedOverlay,
					   float red,
					   float green,
					   float blue,
					   float alpha){

		body.render(matrixStack, buffer, packedLight, packedOverlay);
		head.render(matrixStack, buffer, packedLight, packedOverlay);
		right_arm.render(matrixStack, buffer, packedLight, packedOverlay);
		left_arm.render(matrixStack, buffer, packedLight, packedOverlay);
		right_leg.render(matrixStack, buffer, packedLight, packedOverlay);
		left_leg.render(matrixStack, buffer, packedLight, packedOverlay);

	}

	public void setRotationAngle(ModelPart bone, float x, float y, float z) {
		bone.pitch = x;
		bone.yaw = y;
		bone.roll = z;
	}



}