package mythicbotany.mixin;

import java.util.Collections;
import java.util.List;
import zone.rong.mixinbooter.IEarlyMixinLoader;

public final class MythicBotanyMixinLoader implements IEarlyMixinLoader {
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.mythicbotany.json");
    }
}
