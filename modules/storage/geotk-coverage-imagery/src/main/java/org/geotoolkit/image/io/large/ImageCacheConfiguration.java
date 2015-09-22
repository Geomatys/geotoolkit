/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
public final class ImageCacheConfiguration extends Static {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.image.io.large");

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
     *  The {@linkplain System#getProperties() system properties} key which control
     *  the LargeCache swap on filesystem.
     *  Valid values : "true", "false"
     *  If true LargeCache will use a QuadTreeDirectory to write tiles on filesystem, otherwise
     *  cache will only use memory to store tiles.
     */
    public static final String KEY_CACHE_SWAP = "geotk.image.cache.swap";

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
        final String memoryProp = System.getProperty(KEY_CACHE_MEMORY_SIZE);

        if (memoryProp == null) {
            return DEFAULT_MEMORY_SIZE;
        }

        //TODO compare cache size to -xmx runtime configuration to inform possible memory error.
        return parseToByte(memoryProp);
    }

    /**
     * Set cache memory size in {@linkplain System#getProperties() system properties}.
     * It is not assured that LargeCache will use given value if it was already instantiated.
     * <b>This memory size should be set during application startup not during his life-cycle.</b>
     *
     * @param candidate memory property value String formatted as <size>[g|G|m|M|k|K]
     * @throws IllegalArgumentException if candidate String doesn't respect <size>[g|G|m|M|k|K] format.
     */
    public static void setCacheMemorySize(String candidate) throws IllegalArgumentException{

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

        System.setProperty(KEY_CACHE_MEMORY_SIZE, candidate);
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

    /**
     * Check in {@linkplain System#getProperties() system properties} for cache swap configuration.
     *
     * @return return property value or {@code true} if property not found.
     */
    public static boolean isCacheSwapEnable() {
        final String swap = System.getProperty(KEY_CACHE_SWAP);
        return swap == null || Boolean.parseBoolean(swap);
    }

    /**
     * Set cache swap in {@linkplain System#getProperties() system properties}.
     * It is not assured that LargeCache will use given value if it was already instantiated.
     * <b>This flag should be set during application startup not during his life-cycle.</b>
     *
     * @param allowSwap flag that enable memory swapping on filesystem.
     */
    public static void setCacheSwapEnable(boolean allowSwap) {
        System.setProperty(KEY_CACHE_SWAP, String.valueOf(allowSwap));
    }
}
