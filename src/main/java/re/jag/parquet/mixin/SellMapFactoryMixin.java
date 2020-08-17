package re.jag.parquet.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

// No longer activated, but left here, just in case
// Update: Turns out Mojang sets bugs to fixed with dice

@Mixin(targets="net.minecraft.village.TradeOffers$SellMapFactory")
public class SellMapFactoryMixin implements TradeOffers.Factory {
    @Shadow
    private int price;
    @Shadow
    private MapIcon.Type iconType;
    @Shadow
    private int maxUses;
    @Shadow
    private int experience;

    @Overwrite
    public TradeOffer create(Entity entity, Random random) {
        if (!(entity.world instanceof ServerWorld)) {
            return null;
        } else {
            ServerWorld serverWorld = (ServerWorld) entity.world;
            BlockPos blockPos = new BlockPos(0,0,0);
            ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), (byte)2, true, true);
            FilledMapItem.fillExplorationMap(serverWorld, itemStack);
            MapState.addDecorationsTag(itemStack, blockPos, "+", this.iconType);
            itemStack.setCustomName(new LiteralText("Mojang-award for code quality"));
            return new TradeOffer(new ItemStack(Items.EMERALD, this.price), new ItemStack(Items.COMPASS), itemStack, this.maxUses, this.experience, 0.2F);
        }
    }
}

