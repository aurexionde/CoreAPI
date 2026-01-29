package de.aurexion.CoreAPI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Variables service interface for modules
 * Provides access to persistent data storage
 */
public interface Variables {

    /**
     * Get a stored value
     * @param key Data key
     * @return Value or null
     */
    @Nullable
    Object get(@Nonnull String key);

    /**
     * Get a stored value with type casting
     * @param clazz Expected type
     * @param key Data key
     * @return Typed value or null
     */
    @Nullable
    <T> T get(@Nonnull Class<T> clazz, @Nonnull String key);

    /**
     * Compute if absent pattern
     * @param key Data key
     * @param supplier Value supplier if absent
     * @param clazz Expected type
     * @return Value (existing or computed)
     */
    @Nonnull
    <T> T computeIfAbsent(@Nonnull String key, @Nonnull Supplier<T> supplier, @Nonnull Class<T> clazz);

    /**
     * Set a value
     * @param key Data key
     * @param value Value to store (null = delete)
     */
    void set(@Nonnull String key, @Nullable Object value);

    /**
     * Update existing value (persist immediately)
     * @param key Data key
     */
    void update(@Nonnull String key);

    /**
     * Delete a value
     * @param key Data key
     */
    void delete(@Nonnull String key);

    /**
     * Check if key exists
     * @param key Data key
     * @return true if set
     */
    boolean isSet(@Nonnull String key);

    /**
     * Get a list/map
     * @param listName List name
     * @return Map of values
     */
    @Nonnull
    ConcurrentHashMap<String, Object> getList(@Nonnull String listName);

    /**
     * Get a typed list/map
     * @param clazz Value type
     * @param listName List name
     * @return Typed map
     */
    @Nonnull
    <T> Map<String, T> getList(@Nonnull Class<T> clazz, @Nonnull String listName);
}
