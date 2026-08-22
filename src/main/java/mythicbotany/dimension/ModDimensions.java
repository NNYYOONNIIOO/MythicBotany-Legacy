package mythicbotany.dimension;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public final class ModDimensions {
    public static final int ALFHEIM_DIMENSION_ID = -105;
    public static DimensionType ALFHEIM;

    private ModDimensions() {
    }

    public static void register() {
        if (ALFHEIM != null) {
            return;
        }
        ALFHEIM = DimensionType.register("Alfheim", "_alfheim", ALFHEIM_DIMENSION_ID,
                WorldProviderAlfheim.class, false);
        if (!DimensionManager.isDimensionRegistered(ALFHEIM_DIMENSION_ID)) {
            DimensionManager.registerDimension(ALFHEIM_DIMENSION_ID, ALFHEIM);
        }
    }
}
