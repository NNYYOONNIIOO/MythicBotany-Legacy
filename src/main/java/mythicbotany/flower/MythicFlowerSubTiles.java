package mythicbotany.flower;

import vazkii.botania.api.subtile.SubTileFunctional;

/**
 * MythicBotany flowers use Botania 1.12's single specialflower block.  Their
 * subtype is selected by the type string stored in the ItemStack NBT.
 */
public final class MythicFlowerSubTiles {
    private MythicFlowerSubTiles() {
    }

    public static class Exoblaze extends SubTileFunctional { }
    public static class WitherAconite extends SubTileFunctional { }
    public static class Aquapanthus extends SubTileFunctional { }
    public static class Hellebore extends SubTileFunctional { }
    public static class Raindeletia extends SubTileFunctional { }
    public static class Feysythia extends SubTileFunctional { }
    public static class Petrunia extends SubTileFunctional { }
}
