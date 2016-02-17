package org.geotoolkit.image.io.large;

import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class ImageCacheConfigurationTest extends org.geotoolkit.test.TestBase {

    @Test
    public void readMemorySize() {
        System.getProperties().remove(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE);
        Assert.assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512m");
        Assert.assertEquals(536870912l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512M");
        Assert.assertEquals(536870912l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1g");
        Assert.assertEquals(1073741824l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1G");
        Assert.assertEquals(1073741824l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700k");
        Assert.assertEquals(716800l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700K");
        Assert.assertEquals(716800l, ImageCacheConfiguration.getCacheMemorySize());

        //invalid formats
        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "700Ko");
        Assert.assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "invalid");
        Assert.assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "512");
        Assert.assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());

        System.getProperties().setProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE, "1t");
        Assert.assertEquals(67108864l, ImageCacheConfiguration.getCacheMemorySize());
    }

    @Test
    public void writeMemorySize() {
        Properties properties = System.getProperties();

        ImageCacheConfiguration.setCacheMemorySize("1g");
        Assert.assertEquals("1g", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        ImageCacheConfiguration.setCacheMemorySize("512m");
        Assert.assertEquals("512m", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        ImageCacheConfiguration.setCacheMemorySize("900k");
        Assert.assertEquals("900k", properties.getProperty(ImageCacheConfiguration.KEY_CACHE_MEMORY_SIZE));

        //invalid formats
        try {
            ImageCacheConfiguration.setCacheMemorySize("512");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("700Ko");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("1t");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }

        try {
            ImageCacheConfiguration.setCacheMemorySize("invalid");
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
