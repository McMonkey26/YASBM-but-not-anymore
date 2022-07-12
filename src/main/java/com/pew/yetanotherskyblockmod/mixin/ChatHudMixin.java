package com.pew.yetanotherskyblockmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.pew.yetanotherskyblockmod.YASBM;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatHud.class)
public class ChatHudMixin {
    @ModifyArg(
        method = "addMessage(Lnet/minecraft/text/Text;)V",
        at = @At(value = "INVOKE", target = "addMessage(Lnet/minecraft/text/Text;I)V"),
        index = 0
    )
    private Text addMessage(Text text) {
        return YASBM.getInstance().onMessageReccieved(text);
    }
}