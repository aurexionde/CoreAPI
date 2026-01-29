package de.aurexion.CoreAPI;

/**
 * Core plugin interface providing services to modules
 */
public interface CorePlugin {

    /**
     * Get variables service instance
     * @return Variables service for data persistence
     */
    Variables getVariables();
}
