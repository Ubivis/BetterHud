package kr.toxicity.hud.api.manager;

import kr.toxicity.hud.api.trigger.HudTrigger;
import kr.toxicity.hud.api.yaml.YamlObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.function.Function;

/**
 * Represents trigger manager.
 */
public interface TriggerManager {
    /**
     * Adds trigger by builder.
     * @param name trigger name
     * @param trigger trigger builder
     */
    void addTrigger(@NotNull String name, @NotNull Function<YamlObject, HudTrigger<?>> trigger);

    @NotNull
    @Unmodifiable
    Set<String> getAllTriggerKeys();
}