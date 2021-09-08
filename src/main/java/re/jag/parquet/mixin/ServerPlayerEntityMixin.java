package re.jag.parquet.mixin;

import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
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
import re.jag.parquet.interfaces.CameraModeData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CameraModeData{
	
	public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	private double saved_x, saved_y, saved_z;
	private float saved_pitch, saved_yaw;
	private String saved_dimension = "";
	private boolean save_active = false;
	
	@Shadow
	public MinecraftServer server;

	@Shadow
	public void teleport(ServerWorld arg, double d, double e, double f, float g, float h) {}
	
	@Inject(method="writeCustomDataToNbt", at = @At("RETURN"))
	private void onWriteCustomDataToTag(NbtCompound arg, CallbackInfo ci) {
		arg.put("ParquetSavedPos", (NbtElement) toNbtList(new double[] { saved_x, saved_y, saved_z, saved_yaw, saved_pitch }));

		arg.putString("ParquetSavedDimension", this.saved_dimension);

		arg.putBoolean("ParquetSaveActive", save_active);
	}
	
	@Inject(method="readCustomDataFromNbt", at = @At("RETURN"))
	private void onReadCustomDataFromTag(NbtCompound arg, CallbackInfo ci) {
		NbtList lv = arg.getList("ParquetSavedPos", 6);

		this.saved_dimension = arg.getString("ParquetSavedDimension");
		
		this.save_active = arg.getBoolean("ParquetSaveActive");
		
		if(lv != null) {
			this.saved_x = lv.getDouble(0);
			this.saved_y = lv.getDouble(1);
			this.saved_z = lv.getDouble(2);
			this.saved_yaw   = (float) lv.getDouble(3);
			this.saved_pitch = (float) lv.getDouble(4);
		}
	}
	
	//INTERFACE CameraModeData
	public boolean saveCameraPosition() {
		if (!this.save_active) {
			this.saved_x = getX();
			this.saved_y = getY();
			this.saved_z = getZ();


			this.saved_pitch = getPitch();
			this.saved_yaw = getYaw();

			this.saved_dimension = this.world.getRegistryKey().getValue().toString();

			this.save_active = true;
			
			return true;
		}
		return false;
	}
	
	public boolean restoreCameraPosition() {
		if (this.save_active) {
			this.teleport(server.getWorld( RegistryKey.of(Registry.WORLD_KEY, new Identifier(this.saved_dimension))) , saved_x, saved_y, saved_z, saved_yaw, saved_pitch);

			this.save_active = false;
			
			return true;
		}
		return false;
	}
}
