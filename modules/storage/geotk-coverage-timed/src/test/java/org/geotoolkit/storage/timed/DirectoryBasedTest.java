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
import java.nio.file.Files;
import java.nio.file.Path;
import org.geotoolkit.nio.IOUtilities;
import org.junit.After;
import org.junit.Before;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public abstract class DirectoryBasedTest {

    protected Path dir;

    @Before
    public void prepare() throws IOException {
        dir = Files.createTempDirectory("test-dir");
    }

    @After
    public void destroy() throws IOException {
        if (dir != null)
            IOUtilities.deleteRecursively(dir);
    }
}
