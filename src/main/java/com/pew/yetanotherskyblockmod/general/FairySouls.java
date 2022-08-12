package com.pew.yetanotherskyblockmod.general;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.brigadier.context.CommandContext;
import com.pew.yetanotherskyblockmod.YASBM;
import com.pew.yetanotherskyblockmod.config.ModConfig;
import com.pew.yetanotherskyblockmod.mixin.BeaconBlockEntityRendererAccessor;
import com.pew.yetanotherskyblockmod.util.Constants;
import com.pew.yetanotherskyblockmod.util.Utils;

import me.shedaniel.math.Color;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FairySouls implements com.pew.yetanotherskyblockmod.Features.WorldFeature, com.pew.yetanotherskyblockmod.Features.ChatFeature {
    public static FairySouls instance = new FairySouls();
    private FairySouls() {}

    private static Set<String> soulsMsgs = Set.of(
        "You have already found that Fairy Soul!",
        "SOUL! You found a Fairy Soul!"
    );
    private static Color waypointColor = Color.ofOpaque(0x772991);
    private static boolean enabled = false;
    private static Set<BlockPos> current;

    public void init() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("fairysouls")
			.requires(source -> Utils.isOnSkyblock())
			.then(ClientCommandManager.literal("toggle").executes(Command::toggle))
            .then(ClientCommandManager.literal("clear").executes(Command::clear))
            .then(ClientCommandManager.literal("reset").executes(Command::unclear))
            .then(ClientCommandManager.literal("help").executes(Command::help))
		);
    }
    public void tick() {}
    public void onConfigUpdate() {}
    public void onWorldLoad(ClientWorld world) {
        if (enabled) loadSouls();
    }
    public void onDrawWorld(ClientWorld world, WorldRenderer renderer, MatrixStack matrices, VertexConsumerProvider vertices, float tickDelta) {
        if (!enabled || current == null || current.isEmpty()) return;
        long l = world.getTime();
        Camera cam = YASBM.client.gameRenderer.getCamera();
        if (cam == null) return;
        Vec3d vec3d = cam.getPos();
        for (BlockPos pos : current) {
            matrices.push();
            matrices.translate((double)pos.getX() - vec3d.getX(), (double)pos.getY() - vec3d.getY(), (double)pos.getZ() - vec3d.getZ());
            BeaconBlockEntityRendererAccessor.invokeRenderBeam(matrices, vertices, tickDelta, l, 0, 1024, new float[]{waypointColor.getRed()/255f, waypointColor.getGreen()/255f, waypointColor.getBlue()/255f});
            matrices.pop();
        }
    }
    public Text onIncomingChat(Text text) {
        if (soulsMsgs.contains(text.getString())) markFound();
        return text;
    }
    public String onOutgoingChat(String msg) {return msg;}
    public Text onHypixelMessage(String chattype, String rank, String username, String message, Text fullmsg) {return fullmsg;}

    private static boolean loadSouls() {
        Utils.InternalLocation l = Utils.getInternalLocation();
        if (l == null) return false;
        var souls = Constants.getFairySouls();
        if (souls == null || !souls.containsKey(l)) return false;
        current = souls.get(l);
        if (ModConfig.get().foundSouls.containsKey(l)) current.removeIf((BlockPos pos) -> ModConfig.get().foundSouls.get(l).contains(pos.toShortString()));
        return true;
    }

    private static boolean markFound() {
        if (YASBM.client.player == null) return false;
        Utils.InternalLocation l = Utils.getInternalLocation();
        if (l == null) return false;
        if (!ModConfig.get().foundSouls.containsKey(l)) ModConfig.get().foundSouls.put(l, new HashSet<>());
        var souls = Constants.getFairySouls();
        if (souls == null || !souls.containsKey(l)) return false;
        Set<BlockPos> all = souls.get(l);
        Vec3d playerloc = YASBM.client.player.getPos();
        List<BlockPos> sorted = all.stream().sorted((BlockPos a, BlockPos b) -> (int)Math.round(b.getSquaredDistance(playerloc) - a.getSquaredDistance(playerloc))).collect(Collectors.toList());
        BlockPos closest = sorted.get(0);
        ModConfig.get().foundSouls.get(l).add(closest.toShortString());
        current.remove(closest);
        return true;
    }
    private static boolean markAll(boolean found) {
        Utils.InternalLocation l = Utils.getInternalLocation();
        if (l == null) return false;
        if (!ModConfig.get().foundSouls.containsKey(l)) ModConfig.get().foundSouls.put(l, new HashSet<>());
        if (found) {
            Map<Utils.InternalLocation, Set<BlockPos>> souls = Constants.getFairySouls();
            if (souls == null || !souls.containsKey(l)) return false;
            Set<BlockPos> all = souls.get(l);
            Set<String> set = ModConfig.get().foundSouls.get(l);
            all.forEach((BlockPos pos) -> set.add(pos.toShortString()));
            if (current != null) current.clear();
        } else {
            ModConfig.get().foundSouls.put(l, new HashSet<>());
            loadSouls();
        }
        return true;
    }
    
    private static class Command {
        private static int toggle(CommandContext<FabricClientCommandSource> ctx) {
            FairySouls.enabled = !FairySouls.enabled;
            ctx.getSource().sendFeedback(new TranslatableText("commands.fairysouls.toggle."+(enabled ? "on" : "off")));
            return FairySouls.enabled ? (FairySouls.loadSouls() ? 1 : -1) : 0;
        }

        private static int clear(CommandContext<FabricClientCommandSource> ctx) {
            int ret = markAll(true) ? 1 : -1;
            ctx.getSource().sendFeedback(new TranslatableText("commands.fairysouls.clear"));
            return ret;
        }

        private static int unclear(CommandContext<FabricClientCommandSource> ctx) {
            int ret = markAll(false) ? (loadSouls() ? 1 : -1) : -1;
            ctx.getSource().sendFeedback(new TranslatableText("commands.fairysouls.unclear"));
            return ret;
        }

        private static int help(CommandContext<FabricClientCommandSource> ctx) {
            ctx.getSource().sendFeedback(new TranslatableText("commands.fairysouls.help"));
            return 1;
        }
    }
}
