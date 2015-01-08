package org.geotoolkit.image.io.large;

import org.apache.sis.util.Static;
import org.apache.sis.util.logging.Logging;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.logging.Logger;

/**
 * Class that control {@link org.geotoolkit.image.io.large.LargeCache LargeCache} configuration
 *
 * @author Quentin Boileau (Geomatys)
 */
public class ImageCacheConfiguration extends Static {

    private static final Logger LOGGER = Logging.getLogger(ImageCacheConfiguration.class);

    /**
     * List of supported memory units.
     */
    private static final char[] VALID_UNIT = new char[]{'G', 'K', 'M', 'g', 'k', 'm'};

    /**
     *  The {@linkplain System#getProperties() system properties} key which control
     *  the LargeCache memory size.
     *  Format <size>[g|G|m|M|k|K]
     */
    public static final String KEY_CACHE_MEMORY_SIZE = "geotk.image.cache.size";

    /**
     * Default memory size used if {@linkplain System#getProperties() system properties} {@linkplain #KEY_CACHE_MEMORY_SIZE}
     * property is not defined.
     *
     * This value is equivalent to 64MB. Decomposed version : 1024l * 1024 * 1 * 1 * 64
     * (tile width(px) * tile height(px) * band number * component type size (byte) * number of images.)
     */
    private static final long DEFAULT_MEMORY_SIZE = 67108864l;


    /**
     * Check in {@linkplain System#getProperties() system properties} for cache memory size configuration
     * or use {@link #DEFAULT_MEMORY_SIZE} value is not found or doesn't respect <size>[g|G|m|M|k|K] format.
     *
     * @return cache memory size in bytes.
     */
    public static long getCacheMemorySize() {
        final String memoryProp = System.getProperties().getProperty(KEY_CACHE_MEMORY_SIZE);

        if (memoryProp == null) {
            return DEFAULT_MEMORY_SIZE;
        }

        //TODO compare cache size to -xmx runtime configuration to inform possible memory error.
        return parseToByte(memoryProp);
    }

    /**
     * Set cache memory size in {@linkplain System#getProperties() system properties}.
     *
     * @param candidate memory property value String formatted as <size>[g|G|m|M|k|K]
     * @throws IllegalArgumentException if candidate String doesn't respect <size>[g|G|m|M|k|K] format.
     */
    public static synchronized void setCacheMemorySize(String candidate) throws IllegalArgumentException{

        try {
            Long.valueOf(candidate.substring(0, candidate.length() - 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid cache memory format : '"+candidate+"' should be formatted as <size>[g|G|m|M|k|K]");
        }

        //test unit
        char unit = candidate.charAt(candidate.length()-1);
        if (Arrays.binarySearch(VALID_UNIT, unit) < 0) {
            throw new IllegalArgumentException("Invalid cache memory format : '"+candidate+"' should be formatted as <size>[g|G|m|M|k|K]");
        }

        System.getProperties().setProperty(KEY_CACHE_MEMORY_SIZE, candidate);
    }

    /**
     * Convert memory property value String formatted as <size>[g|G|m|M|k|K] to byte.
     * e.g.
     * 1g -> 1073741824l
     * 256m -> 268435456l
     * 900k -> 921600l
     *
     * If input String format is invalid, {@linkplain #DEFAULT_MEMORY_SIZE} value will be returned.
     *
     * @param memoryProp String formatted as <size>[g|G|m|M|k|K]
     * @return string input converted in byte number.
     */
    private static long parseToByte(String memoryProp) throws IllegalFormatException{

        long value;
        try {
            value = Long.valueOf(memoryProp.substring(0, memoryProp.length() - 1));
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid property -Dgeotk.image.cache.size value "+memoryProp+". Default value (64m) will be used.");
            return DEFAULT_MEMORY_SIZE;
        }

        char unit = memoryProp.charAt(memoryProp.length()-1);
        switch (unit) {
            case 'g' :
            case 'G' : return (long) (value * Math.pow(1024, 3));
            case 'm' :
            case 'M' : return (long) (value * Math.pow(1024, 2));
            case 'k' :
            case 'K' : return value * 1024;
            default:
                LOGGER.warning("Invalid property -Dgeotk.image.cache.size value "+memoryProp+". Default value (64m) will be used.");
                return DEFAULT_MEMORY_SIZE;
        }
    }

}
