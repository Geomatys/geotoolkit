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

import org.junit.Assert;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FileSetTest {

    public void build() {
        final Path noExt = Paths.get("/virtual/file");
        final FileSet set = new FileSet(noExt);
        Assert.assertEquals("File set directory should be equal to prepared path parent.", noExt.getParent(), set.parent);
        Assert.assertEquals("File set base name should be equal to prepared path name.", noExt.getFileName().toString(), set.baseName);

        Assert.assertFalse("We should not be able to add a file already registered in the set.", set.add(noExt));

        final Path secondFile = noExt.getParent().resolve("file.tmp");
        Assert.assertTrue("We should be able to add siblings with same base name", set.add(secondFile));

        final Path other = noExt.getParent().resolve("i.am.outside.set.scope");
        Assert.assertFalse("Path with different base name should be ignored.", set.add(other));

        final Set<Path> expectedFiles = new HashSet<>();
        expectedFiles.add(noExt);
        expectedFiles.add(secondFile);

        final Set<Path> content = StreamSupport.stream(set.spliterator(), false)
                .collect(Collectors.toSet());
        Assert.assertEquals("file set content is not equals to prepared paths", expectedFiles, content);
    }
}
