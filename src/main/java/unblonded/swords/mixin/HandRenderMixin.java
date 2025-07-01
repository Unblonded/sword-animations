package unblonded.swords.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;
import unblonded.swords.SwordBlocking;

import static unblonded.swords.Config.config;

@Mixin(HeldItemRenderer.class)
public class HandRenderMixin {
	@Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
	private void scale(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci) {
		if (!(entity instanceof ClientPlayerEntity) || !((ClientPlayerEntity) entity).isMainPlayer()) return;
		if (config.oldHead && stack.isOf(Items.PLAYER_HEAD)) {
			matrices.translate(0,0.03f,-0.1f);
			matrices.scale(0.5f,0.5f,0.5f);
			matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(2)));
			matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(-135)));
		}
		if (config.customHandRender) {
			float s = config.handRenderTransform[0];
			float x = config.handRenderTransform[1];
			float y = config.handRenderTransform[2];
			float z = config.handRenderTransform[3];
			matrices.scale(s,s,s);
			matrices.translate(x,y,z);
		}
		if (config.spinItems && !SwordBlocking.mc.options.attackKey.isPressed() && !SwordBlocking.isEntityBlocking((ClientPlayerEntity) entity)) {
			long currentTimeMillis = System.currentTimeMillis();
			float timeInSeconds = (currentTimeMillis % 100_000L) / 1000f;
			float angle = (timeInSeconds * config.spinSpeed) % 360f;

			matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(angle)));
		}
		if (config.swordBlocking && SwordBlocking.isEntityBlocking((ClientPlayerEntity) entity) && SwordBlocking.isSword(stack.getItem())) {
			matrices.translate(0,0.05,0);
			matrices.multiply(new Quaternionf().rotateX((float) Math.toRadians(75)));
			matrices.multiply(new Quaternionf().rotateY((float) Math.toRadians(165)));
			matrices.multiply(new Quaternionf().rotateZ((float) Math.toRadians(-95)));
		}
	}
}