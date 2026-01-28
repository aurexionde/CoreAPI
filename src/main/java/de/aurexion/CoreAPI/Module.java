package de.aurexion.CoreAPI;

public interface Module {

    /**
     * Called when module is being loaded (before enable)
     * @param corePlugin Reference to CorePlugin for service access
     */
    default void onLoad(CorePlugin corePlugin) {}

    /**
     * Called when module is enabled
     */
    default void onEnable() {}

    /**
     * Called when module is disabled
     */
    default void onDisable() {}

    /**
     * @return Module name
     */
    String getName();

    /**
     * @return Module author
     */
    String getAuthor();

    /**
     * @return Module version
     */
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * @return Module description
     */
    default String getDescription() {
        return "";
    }
}
