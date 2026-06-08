package dev.zoomitsky;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class ZoomitSkyModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ZoomitSkyConfig config = ZoomitSkyConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("config.zoomitsky.title"))
                    .setSavingRunnable(ZoomitSkyConfig::save);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory category = builder.getOrCreateCategory(
                    Text.translatable("config.zoomitsky.category.zoom"));

            // --- Zoom values ---
            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.defaultZoom"), config.defaultZoom)
                    .setDefaultValue(0.28f)
                    .setMin(0.005f).setMax(1.0f)
                    .setSaveConsumer(val -> config.defaultZoom = val)
                    .build());

            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.minZoom"), config.minZoom)
                    .setDefaultValue(0.05f)
                    .setMin(0.005f).setMax(0.4f)
                    .setSaveConsumer(val -> config.minZoom = val)
                    .build());

            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.maxZoom"), config.maxZoom)
                    .setDefaultValue(0.6f)
                    .setMin(0.5f).setMax(1.0f)
                    .setSaveConsumer(val -> config.maxZoom = val)
                    .build());

            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.zoomStep"), config.zoomStep)
                    .setDefaultValue(0.08f)
                    .setMin(0.01f).setMax(0.5f)
                    .setSaveConsumer(val -> config.zoomStep = val)
                    .build());

            // --- Transition ---
            category.addEntry(entryBuilder
                    .startIntSlider(
                            Text.translatable("config.zoomitsky.transitionDuration"),
                            Math.round(config.transitionDuration * 100),
                            0, 200)
                    .setDefaultValue(18)
                    .setTextGetter(val -> Text.literal(String.format("%.2fs", val / 100f)))
                    .setSaveConsumer(val -> config.transitionDuration = val / 100f)
                    .build());

            // Dropdown de easing
            List<ZoomEasing.EasingType> easingOptions = Arrays.asList(ZoomEasing.EasingType.values());
            category.addEntry(entryBuilder
                    .startSelector(
                            Text.translatable("config.zoomitsky.easingType"),
                            easingOptions.toArray(),
                            config.easingType)
                    .setDefaultValue(ZoomEasing.EasingType.EASE_OUT)
                    .setNameProvider(val ->
                            Text.translatable(((ZoomEasing.EasingType) val).getTranslationKey()))
                    .setSaveConsumer(val -> config.easingType = (ZoomEasing.EasingType) val)
                    .build());

            // --- Cinematic ---
            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.cinematicBarsSpeed"), config.cinematicBarsSpeed)
                    .setDefaultValue(0.10f)
                    .setMin(0.01f).setMax(1.0f)
                    .setSaveConsumer(val -> config.cinematicBarsSpeed = val)
                    .build());

            return builder.build();
        };
    }
}