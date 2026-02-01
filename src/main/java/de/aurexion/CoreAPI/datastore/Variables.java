package de.aurexion.CoreAPI.datastore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Static Variables accessor for persistent data storage
 * Modules use this directly with Variables.set(), Variables.get(), etc.
 */
public class Variables {

    private static DataStoreAccess dataStoreAccess;

    /**
     * Internal - Called by CorePlugin to inject DataStore access
     */
    public static void init(DataStoreAccess access) {
        dataStoreAccess = access;
    }

    @Nullable
    public static Object get(@Nonnull String dataKey) {
        return dataStoreAccess.getData(dataKey.toLowerCase());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T get(@Nonnull Class<T> clazz, @Nonnull String dataKey) {
        Object value = get(dataKey);
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    @Nonnull
    public static <T> T computeIfAbsent(@Nonnull String dataKey, @Nonnull Supplier<T> supplier, @Nonnull Class<T> clazz) {
        T value = get(clazz, dataKey);
        if (value == null) {
            value = supplier.get();
            set(dataKey, value);
        }
        return value;
    }

    public static void set(@Nonnull String dataKey, @Nullable Object value) {
        dataKey = dataKey.toLowerCase();
        dataStoreAccess.setVariable(dataKey, value);
        dataStoreAccess.queuePersist(dataKey, value);
    }

    public static void update(@Nonnull String dataKey) {
        dataStoreAccess.queuePersist(dataKey.toLowerCase(), get(dataKey));
    }

    public static void delete(@Nonnull String dataKey) {
        set(dataKey, null);
    }

    @Nonnull
    public static ConcurrentHashMap<String, Object> getList(@Nonnull String listName) {
        ConcurrentHashMap<String, Object> listVar = dataStoreAccess.getDataList(listName.toLowerCase());
        return (listVar != null) ? listVar : new ConcurrentHashMap<>();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getList(@Nonnull Class<T> clazz, @Nonnull String listName) {
        ConcurrentHashMap<String, Object> listVar = dataStoreAccess.getDataList(listName.toLowerCase());
        if (listVar == null) {
            return new ConcurrentHashMap<>();
        }
        return (Map<String, T>) (Map<String, ?>) listVar;
    }

    public static boolean isSet(@Nonnull String dataKey) {
        return get(dataKey) != null;
    }
}
