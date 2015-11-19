package org.geotoolkit.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Quentin Boileau (Geomatys)
 */
public class UnixPathMatcherTest {

    @Test
    public void testMatcher() throws IOException {

        PathMatcher phgMatcher = new UnixPathMatcher("*.png");

        assertTrue(phgMatcher.matches(Paths.get("test.png")));
        assertFalse(phgMatcher.matches(Paths.get("test.jpeg")));

        assertTrue(phgMatcher.matches(Paths.get("/tmp/path/test.png")));
        assertFalse(phgMatcher.matches(Paths.get("/tmp/path/test.jpeg")));

    }

    @Test
    public void testMatcherCaseUnsensitive() throws IOException {

        PathMatcher phgMatcher = new UnixPathMatcher("*.pNg", true);

        assertTrue(phgMatcher.matches(Paths.get("test.png")));
        assertFalse(phgMatcher.matches(Paths.get("test.jpeg")));

        assertTrue(phgMatcher.matches(Paths.get("/tmp/path/test.PNG")));
        assertFalse(phgMatcher.matches(Paths.get("/tmp/path/test.jpeg")));

    }
}
