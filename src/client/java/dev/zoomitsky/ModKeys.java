package dev.zoomitsky;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ModKeys {

    public static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of("zoomitsky", "category"));

    public static KeyBinding zoom;
    public static KeyBinding cinematicZoom;
    public static KeyBinding toggleMode;
    public static KeyBinding resetZoom;

    public static void register() {
        zoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY));

        cinematicZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.cinematic_zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY));

        toggleMode = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.toggle_mode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY));

        resetZoom = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.zoomitsky.reset_zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CATEGORY));
    }
}