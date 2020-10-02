package io.github.faecraft.enderboys.client.model;// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class EnderboyEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public boolean carryingBlock;
	public boolean angry;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart headwear;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart right_leg;
	private final ModelPart left_leg;

	public EnderboyEntityModel(float f) {
		super(0.0F, -14.0F, 64, 64);
		body = new ModelPart(this);
		body.setPivot(0.0F, 0.0F, 0.0F);
		body.setTextureOffset(1, 17).addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

		head = new ModelPart(this);
		head.setPivot(0.0F, 0.0F, 0.0F);
		head.setTextureOffset(0, 0).addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

		headwear = new ModelPart(this);
		headwear.setPivot(0.0F, 0.0F, 0.0F);
		headwear.setTextureOffset(32, 0).addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F, false);

		right_arm = new ModelPart(this);
		right_arm.setPivot(5.0F, 2.0F, 0.0F);
		right_arm.setTextureOffset(11, 34).addCuboid(-11.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

		left_arm = new ModelPart(this);
		left_arm.setPivot(-5.0F, 2.0F, 0.0F);
		left_arm.setTextureOffset(1, 34).addCuboid(9.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

		right_leg = new ModelPart(this);
		right_leg.setPivot(2.0F, 12.0F, 0.0F);
		right_leg.setTextureOffset(27, 19).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);

		left_leg = new ModelPart(this);
		left_leg.setPivot(-2.0F, 12.0F, 0.0F);
		left_leg.setTextureOffset(37, 19).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, 0.0F, false);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.head.visible = true;
		float k = -14.0F;
		this.torso.pitch = 0.0F;
		this.torso.pivotY = -14.0F;
		this.torso.pivotZ = -0.0F;
		ModelPart var10000 = this.rightLeg;
		var10000.pitch -= 0.0F;
		var10000 = this.leftLeg;
		var10000.pitch -= 0.0F;
		var10000 = this.rightArm;
		var10000.pitch = (float)((double)var10000.pitch * 0.5D);
		var10000 = this.leftArm;
		var10000.pitch = (float)((double)var10000.pitch * 0.5D);
		var10000 = this.rightLeg;
		var10000.pitch = (float)((double)var10000.pitch * 0.5D);
		var10000 = this.leftLeg;
		var10000.pitch = (float)((double)var10000.pitch * 0.5D);
		float l = 0.4F;
		if (this.rightArm.pitch > 0.4F) {
			this.rightArm.pitch = 0.4F;
		}

		if (this.leftArm.pitch > 0.4F) {
			this.leftArm.pitch = 0.4F;
		}

		if (this.rightArm.pitch < -0.4F) {
			this.rightArm.pitch = -0.4F;
		}

		if (this.leftArm.pitch < -0.4F) {
			this.leftArm.pitch = -0.4F;
		}

		if (this.rightLeg.pitch > 0.4F) {
			this.rightLeg.pitch = 0.4F;
		}

		if (this.leftLeg.pitch > 0.4F) {
			this.leftLeg.pitch = 0.4F;
		}

		if (this.rightLeg.pitch < -0.4F) {
			this.rightLeg.pitch = -0.4F;
		}

		if (this.leftLeg.pitch < -0.4F) {
			this.leftLeg.pitch = -0.4F;
		}

		if (this.carryingBlock) {
			this.rightArm.pitch = -0.5F;
			this.leftArm.pitch = -0.5F;
			this.rightArm.roll = 0.05F;
			this.leftArm.roll = -0.05F;
		}

		this.rightArm.pivotZ = 0.0F;
		this.leftArm.pivotZ = 0.0F;
		this.rightLeg.pivotZ = 0.0F;
		this.leftLeg.pivotZ = 0.0F;
		this.rightLeg.pivotY = -5.0F;
		this.leftLeg.pivotY = -5.0F;
		this.head.pivotZ = -0.0F;
		this.head.pivotY = -13.0F;
		this.helmet.pivotX = this.head.pivotX;
		this.helmet.pivotY = this.head.pivotY;
		this.helmet.pivotZ = this.head.pivotZ;
		this.helmet.pitch = this.head.pitch;
		this.helmet.yaw = this.head.yaw;
		this.helmet.roll = this.head.roll;
		float n;
		if (this.angry) {
			n = 1.0F;
			var10000 = this.head;
			var10000.pivotY -= 5.0F;
		}

		n = -14.0F;
		this.rightArm.setPivot(-5.0F, -12.0F, 0.0F);
		this.leftArm.setPivot(5.0F, -12.0F, 0.0F);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices,
					   int light,
					   int overlay,
					   float red,
					   float green,
					   float blue,
					   float alpha) {
		body.render(matrices, vertices, light, overlay);
		head.render(matrices, vertices, light, overlay);
		headwear.render(matrices, vertices, light, overlay);
		right_arm.render(matrices, vertices, light, overlay);
		left_arm.render(matrices, vertices, light, overlay);
		right_leg.render(matrices, vertices, light, overlay);
		left_leg.render(matrices, vertices, light, overlay);

	}

	public void setAngles(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.pitch = x;
		modelRenderer.yaw = y;
		modelRenderer.roll = z;
	}



}