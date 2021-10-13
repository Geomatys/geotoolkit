package org.geotoolkit.image.io.large;

import org.junit.Test;

import java.util.Properties;
import static org.junit.Assert.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class ImageCacheConfigurationTest extends org.geotoolkit.test.TestBase {

    @Test
    public void readMemorySize() {
        System.getProperties().remove(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE);
        assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512m");
        assertEquals(536870912l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512M");
        assertEquals(536870912l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1g");
        assertEquals(1073741824l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1G");
        assertEquals(1073741824l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700k");
        assertEquals(716800l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700K");
        assertEquals(716800l, ImageCacheConfiguration.getCacheMemorySize());

        //invalid formats
        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700Ko");
        assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "invalid");
        assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512");
        assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1t");
        assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());
    }

    @Test
    public void writeMemorySize() {
        Properties properties = System.getProperties();

        ImageCacheConfiguration.setCacheMemorySize("1g");
        assertEquals("1g", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        ImageCacheConfiguration.setCacheMemorySize("512m");
        assertEquals("512m", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        ImageCacheConfiguration.setCacheMemorySize("900k");
        assertEquals("900k", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        //invalid formats
        try {
            ImageCacheConfiguration.setCacheMemorySize("512");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("700Ko");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("1t");
            fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("invalid");
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
