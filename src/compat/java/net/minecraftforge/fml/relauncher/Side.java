package net.minecraftforge.fml.relauncher;

/**
 * Development-runtime compatibility copy of Forge 1.12.2's client/server
 * side enum. The mapped Forge jar in this workspace contains an extra BUKKIT
 * constant that is incompatible with its NetworkRegistry implementation.
 */
public enum Side {
    CLIENT,
    SERVER;

    public boolean isServer() {
        return !isClient();
    }

    public boolean isClient() {
        return this == CLIENT;
    }
}
