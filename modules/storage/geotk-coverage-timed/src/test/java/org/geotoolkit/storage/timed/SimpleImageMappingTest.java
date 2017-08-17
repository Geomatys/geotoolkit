/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.index.tree.TreeElementMapper;
import org.geotoolkit.test.Assert;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class SimpleImageMappingTest extends DirectoryBasedTest {

    /**
     * Try inserting new elements, then retrieving them by id.
     * @throws IOException If we cannot create the mapping, or an error occurs
     * while reading from/writing on underlying support.
     */
    @Test
    public void insertThenSearch() throws IOException {
        final SimpleImageMapping mapper = create();
        final List<Path> mapped = bulkInsert(mapper, 1000);

        for (int i = 0; i < mapped.size() ; i++) {
            final Path expectedPath = mapped.get(i);
            final int expectedId = i + 1;
            final Path foundPath = mapper.getObjectFromTreeIdentifier(expectedId);
            Assert.assertEquals("unexpected path for identifier "+(expectedId), expectedPath, foundPath);
        }
    }

    /**
     * Try a search by path (inserted value).
     * @throws IOException If we cannot create the mapping, or an error occurs
     * while reading from/writing on underlying support.
     */
    @Test
    public void reverseSearch() throws IOException {
        final SimpleImageMapping mapper = create();
        final List<Path> mapped = bulkInsert(mapper, 1000);

        for (int i = 0; i < mapped.size(); i++) {
            // Try reverse search
            final Path expectedPath = mapped.get(i);
            final int expectedId = i + 1;
            final int id = mapper.getTreeIdentifier(expectedPath);
            Assert.assertEquals("Unexpected identifier for path " + expectedPath, expectedId, id);
        }
    }

    /**
     * Ensure that {@link TreeElementMapper#getFullMap() } is not implemented.
     * We do it because it's a very dangerous method if badly used/ coded.
     *
     * @throws IOException If we cannot create the mapping.
     */
    @Test
    public void fobidFullMap() throws IOException {
        final SimpleImageMapping mapper = create();
        try {
            mapper.getFullMap();
            org.junit.Assert.fail("Acquiring full mapping should be prohibited.");
        } catch (UnsupportedOperationException ex) {
            // Expected behavior. Full map would potentially be too big.
        }
    }

    private SimpleImageMapping create() throws IOException {
        return new SimpleImageMapping(dir.resolve("mapping"), dir, path -> new GeneralEnvelope(3));
    }

    /**
     * Insert a given number of paths into the mapper in parameter. We create
     * mock paths in the {@link #dir} folder.
     * @param target The mapper to put data into.
     * @param bulkSize Number of path to create/insert.
     * @return List of inserted path. The identifier for each path should be its
     * index in the list, plus 1.
     *
     * @throws IOException
     */
    private List<Path> bulkInsert(final SimpleImageMapping target, final int bulkSize) throws IOException {
        ArgumentChecks.ensureStrictlyPositive("Number of values to insert", bulkSize);
        final List<Path> mapped = new ArrayList<>();

        // Tree identifier auto-increment starts at 1.
        for (int i = 1; i <= bulkSize; i++) {
            final Path newPath = dir.resolve("I am n° "+i);
            target.setTreeIdentifier(newPath, i);
            mapped.add(newPath);
        }

        return mapped;
    }
}
