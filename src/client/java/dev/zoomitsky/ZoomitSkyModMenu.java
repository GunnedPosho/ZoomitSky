package dev.zoomitsky;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class ZoomitSkyModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = ModConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.zoomitsky.title"))
                    .setSavingRunnable(ModConfig::save);

            ConfigEntryBuilder e = builder.entryBuilder();

            // ─── General ──────────────────────────────────────────────────────────
            ConfigCategory general = builder.getOrCreateCategory(
                    Text.translatable("config.zoomitsky.category.general"));

            // Keybindings
            SubCategoryBuilder keybinds = e.startSubCategory(
                    Text.translatable("config.zoomitsky.category.keybinds"));

            keybinds.add(e.fillKeybindingField(
                    Text.translatable("key.zoomitsky.zoom"), ModKeys.zoom).build());
            keybinds.add(e.fillKeybindingField(
                    Text.translatable("key.zoomitsky.cinematic_zoom"), ModKeys.cinematicZoom).build());
            keybinds.add(e.fillKeybindingField(
                    Text.translatable("key.zoomitsky.toggle_mode"), ModKeys.toggleMode).build());
            keybinds.add(e.fillKeybindingField(
                    Text.translatable("key.zoomitsky.reset_zoom"), ModKeys.resetZoom).build());

            general.addEntry(keybinds.build());

            // Cinematic Bars Speed
            general.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.cinematicBarsSpeed"),
                            config.cinematicBarsSpeed)
                    .setDefaultValue(0.10f)
                    .setMin(0.01f).setMax(1.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.cinematicBarsSpeed.tooltip"))
                    .setSaveConsumer(val -> config.cinematicBarsSpeed = val)
                    .build());

            // ─── Primera Persona ──────────────────────────────────────────────────
            ConfigCategory fp = builder.getOrCreateCategory(
                    Text.translatable("config.zoomitsky.category.zoom"));

            fp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.defaultZoom"), config.defaultZoom)
                    .setDefaultValue(0.28f)
                    .setMin(0.005f).setMax(1.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.defaultZoom.tooltip"))
                    .setSaveConsumer(val -> config.defaultZoom = val)
                    .build());

            fp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.minZoom"), config.minZoom)
                    .setDefaultValue(0.05f)
                    .setMin(0.005f).setMax(0.4f)
                    .setTooltip(Text.translatable("config.zoomitsky.minZoom.tooltip"))
                    .setSaveConsumer(val -> config.minZoom = val)
                    .build());

            fp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.maxZoom"), config.maxZoom)
                    .setDefaultValue(0.6f)
                    .setMin(0.5f).setMax(1.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.maxZoom.tooltip"))
                    .setSaveConsumer(val -> config.maxZoom = val)
                    .build());

            fp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.zoomStep"), config.zoomStep)
                    .setDefaultValue(0.08f)
                    .setMin(0.01f).setMax(0.5f)
                    .setTooltip(Text.translatable("config.zoomitsky.zoomStep.tooltip"))
                    .setSaveConsumer(val -> config.zoomStep = val)
                    .build());

            fp.addEntry(e.startIntSlider(
                            Text.translatable("config.zoomitsky.transitionDuration"),
                            Math.round(config.transitionDuration * 100), 0, 200)
                    .setDefaultValue(32)
                    .setTextGetter(val -> Text.literal(String.format("%.2fs", val / 100f)))
                    .setTooltip(Text.translatable("config.zoomitsky.transitionDuration.tooltip"))
                    .setSaveConsumer(val -> config.transitionDuration = val / 100f)
                    .build());

            List<ZoomEasing.EasingType> easingOptions = Arrays.asList(ZoomEasing.EasingType.values());
            fp.addEntry(e.startSelector(
                            Text.translatable("config.zoomitsky.easingType"),
                            easingOptions.toArray(), config.easingType)
                    .setDefaultValue(ZoomEasing.EasingType.EASE_ZOOMITSKY)
                    .setNameProvider(val ->
                            Text.translatable(((ZoomEasing.EasingType) val).getTranslationKey()))
                    .setTooltip(Text.translatable("config.zoomitsky.easingType.tooltip"))
                    .setSaveConsumer(val -> config.easingType = (ZoomEasing.EasingType) val)
                    .build());

            // ─── Tercera Persona ──────────────────────────────────────────────────
            ConfigCategory tp = builder.getOrCreateCategory(
                    Text.translatable("config.zoomitsky.category.thirdperson"));

            tp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.tp.defaultDistance"),
                            config.tpDefaultDistance)
                    .setDefaultValue(4.0f)
                    .setMin(1.5f).setMax(20.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.tp.defaultDistance.tooltip"))
                    .setSaveConsumer(val -> config.tpDefaultDistance = val)
                    .build());

            tp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.tp.minDistance"),
                            config.tpMinDistance)
                    .setDefaultValue(1.5f)
                    .setMin(0.5f).setMax(10.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.tp.minDistance.tooltip"))
                    .setSaveConsumer(val -> config.tpMinDistance = val)
                    .build());

            tp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.tp.maxDistance"),
                            config.tpMaxDistance)
                    .setDefaultValue(20.0f)
                    .setMin(2.0f).setMax(50.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.tp.maxDistance.tooltip"))
                    .setSaveConsumer(val -> config.tpMaxDistance = val)
                    .build());

            tp.addEntry(e.startFloatField(
                            Text.translatable("config.zoomitsky.tp.distanceStep"),
                            config.tpDistanceStep)
                    .setDefaultValue(0.5f)
                    .setMin(0.1f).setMax(3.0f)
                    .setTooltip(Text.translatable("config.zoomitsky.tp.distanceStep.tooltip"))
                    .setSaveConsumer(val -> config.tpDistanceStep = val)
                    .build());

            tp.addEntry(e.startIntSlider(
                            Text.translatable("config.zoomitsky.tp.transitionDuration"),
                            Math.round(config.tpTransitionDuration * 100), 0, 200)
                    .setDefaultValue(32)
                    .setTextGetter(val -> Text.literal(String.format("%.2fs", val / 100f)))
                    .setTooltip(Text.translatable("config.zoomitsky.tp.transitionDuration.tooltip"))
                    .setSaveConsumer(val -> config.tpTransitionDuration = val / 100f)
                    .build());

            List<ZoomEasing.EasingType> tpEasingOptions = Arrays.asList(ZoomEasing.EasingType.values());
            tp.addEntry(e.startSelector(
                            Text.translatable("config.zoomitsky.tp.easingType"),
                            tpEasingOptions.toArray(), config.tpEasingType)
                    .setDefaultValue(ZoomEasing.EasingType.EASE_ZOOMITSKY)
                    .setNameProvider(val ->
                            Text.translatable(((ZoomEasing.EasingType) val).getTranslationKey()))
                    .setTooltip(Text.translatable("config.zoomitsky.tp.easingType.tooltip"))
                    .setSaveConsumer(val -> config.tpEasingType = (ZoomEasing.EasingType) val)
                    .build());

            return builder.build();
        };
    }
}