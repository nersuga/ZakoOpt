package zako.opt.mixin.entity;

import zako.opt.feature.Features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import zako.opt.entity.PartListIterator;

import java.util.Iterator;
import java.util.List;

// every model resets every part before posing it, every frame: the part list is immutable and its for-each made an iterator
// per model per frame (5% of the render thread's garbage on a farm). The model keeps one iterator and rewinds it. A wrap, not
// an overwrite: Polydium wraps the same iterator() call (MixinModelPoseReset), and an overwrite took its target away (#7)
@Mixin(Model.class)
public class ModelResetPoseMixin {
	@Unique
	private PartListIterator zakoopt$parts;

	@WrapOperation(method = "resetPose", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
	private Iterator<ModelPart> zakoopt$reusedIterator(List<ModelPart> parts, Operation<Iterator<ModelPart>> original) {
		if (!Features.FEWER_ALLOCATIONS.on || !RenderSystem.isOnRenderThread()) {
			return original.call(parts);
		}
		PartListIterator it = zakoopt$parts;
		if (it == null) {
			zakoopt$parts = it = new PartListIterator();
		}
		return it.reset(parts);
	}
}
