package de.aurexion.CoreAPI.datastore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Internal interface for DataStore access
 * Implemented by CorePlugin's DataStoreManager
 */
public interface DataStoreAccess {

    @Nullable
    Object getData(@Nonnull String key);

    @Nullable
    ConcurrentHashMap<String, Object> getDataList(@Nonnull String key);

    void setVariable(@Nonnull String key, @Nullable Object value);

    void queuePersist(@Nonnull String key, @Nullable Object value);
}
