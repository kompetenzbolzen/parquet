package re.jag.parquet.mixin;

import re.jag.parquet.commands.CameraModeData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CameraModeData{
	
	public ServerPlayerEntityMixin(World world, GameProfile profile) {
		super(world, profile);
	}

	private double saved_x, saved_y, saved_z;
	private DimensionType saved_dimension;
	private boolean save_active = false;
	
	@Shadow
	public MinecraftServer server;
	
	@Shadow
	public void teleport(ServerWorld arg, double d, double e, double f, float g, float h) {}
	
	@Inject(method="writeCustomDataToTag", at = @At("RETURN"))
	private void onWriteCustomDataToTag(CompoundTag arg, CallbackInfo ci) {
		arg.put("ParquetSavedPos", (Tag)toListTag(new double[] { saved_x, saved_y, saved_z }));
		arg.putInt("ParquetSavedDimension", this.dimension.getRawId());
		arg.putBoolean("ParquetSaveActive", save_active);
	}
	
	@Inject(method="readCustomDataFromTag", at = @At("RETURN"))
	private void onReadCustomDataFromTag(CompoundTag arg, CallbackInfo ci) {
		ListTag lv = arg.getList("ParquetSavedPos", 6);
		
		int dim_raw_id = arg.getInt("ParquetSavedDimension");
		
		this.save_active = arg.getBoolean("ParquetSaveActive");
		
		if(lv != null) {
			this.saved_x = lv.getDouble(0);
			this.saved_y = lv.getDouble(1);
			this.saved_z = lv.getDouble(2);
		}
		
		this.saved_dimension = DimensionType.byRawId(dim_raw_id);
	}

	public boolean saveCameraPosition() {
		if (!this.save_active) {
			this.saved_x = getX();
			this.saved_y = getY();
			this.saved_z = getZ();
			
			this.saved_dimension = this.dimension;
			
			this.save_active = true;
			
			return true;
		}
		return false;
	}
	
	public boolean restoreCameraPosition() {
		if (this.save_active) {
			this.teleport(server.getWorld(saved_dimension), saved_x, saved_y, saved_z, 0,0);
			
			this.save_active = false;
			
			return true;
		}
		return false;
	}
}
