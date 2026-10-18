package com.he.cheat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CheatClient implements ClientModInitializer {
    public static final String MOD_ID = "hecheat";

    public static KeyBinding espKey;
    public static KeyBinding fullbrightKey;
    public static KeyBinding flyKey;

    public static boolean esp = false;
    public static boolean fullbright = false;
    public static boolean fly = false;

    private static boolean wasFullbright = false;
    private static double oldGamma = 0.5;

    @Override
    public void onInitializeClient() {
        espKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hecheat.esp", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.hecheat"));
        fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hecheat.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.hecheat"));
        flyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hecheat.fly", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.hecheat"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (espKey.wasPressed()) esp = !esp;
            while (fullbrightKey.wasPressed()) fullbright = !fullbright;
            while (flyKey.wasPressed()) fly = !fly;

            handleFullbright(client);
            handleFly(client);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(EspRenderer::render);
    }

    private void handleFullbright(MinecraftClient client) {
        if (client.options == null) return;
        if (fullbright && !wasFullbright) {
            oldGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(10.0);
        } else if (!fullbright && wasFullbright) {
            client.options.getGamma().setValue(oldGamma);
        }
        wasFullbright = fullbright;
    }

    private void handleFly(MinecraftClient client) {
        if (!fly || client.player == null) return;
        client.player.getAbilities().allowFlying = true;
        client.player.getAbilities().flying = true;
    }
}