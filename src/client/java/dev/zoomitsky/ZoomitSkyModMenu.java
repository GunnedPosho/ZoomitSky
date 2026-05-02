package dev.zoomitsky;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

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
            ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.zoomitsky.category.zoom"));

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

            category.addEntry(entryBuilder
                    .startFloatField(Text.translatable("config.zoomitsky.transitionSpeed"), config.transitionSpeed)
                    .setDefaultValue(0.24f)
                    .setMin(0.01f).setMax(2.0f)
                    .setSaveConsumer(val -> config.transitionSpeed = val)
                    .build());

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