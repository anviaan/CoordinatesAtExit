package net.anvian.coordinatesatexit.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Final
    @Shadow
    private MinecraftServer server;
    @Shadow
    public ServerPlayer player;

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    public void onDisconnect(Component component, CallbackInfo ci) {
        var dimensionLocation = player.level.dimension().location();
        var dimensionName = dimensionLocation.toString();
        var position = player.blockPosition();
        String message = getString(dimensionName, position);
        System.out.println(message);
        server.getPlayerList().broadcastSystemMessage(Component.literal(message).withStyle(ChatFormatting.YELLOW), ChatType.SYSTEM);
    }

    @Unique
    private @NotNull String getString(String dimensionName, BlockPos position) {
        String dim;
        int sepIndex = dimensionName.indexOf(":");
        if (sepIndex != -1 && sepIndex + 1 < dimensionName.length()) {
            dim = dimensionName.substring(sepIndex + 1);
        } else {
            dim = dimensionName;
        }
        return String.format("%s / %s / %d %d %d",
                player.getName().getString(),
                dim,
                position.getX(),
                position.getY(),
                position.getZ());
    }
}
