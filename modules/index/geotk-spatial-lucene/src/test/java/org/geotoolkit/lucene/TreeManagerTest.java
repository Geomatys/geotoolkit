/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Geomatys
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

package org.geotoolkit.lucene;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.geotoolkit.index.tree.Tree;
import org.geotoolkit.index.tree.manager.SQLRtreeManager;
import org.geotoolkit.index.tree.manager.postgres.LucenePostgresSQLTreeEltMapper;
import org.geotoolkit.util.FileUtilities;

import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class TreeManagerTest extends org.geotoolkit.test.TestBase {

    public static Path directory = Paths.get("TreeManagerTest");
    
    @BeforeClass
    public static void setUpMethod() throws Exception {
        if (Files.isDirectory(directory)) {
            FileUtilities.deleteDirectory(directory.toFile());
        }
        Files.createDirectory(directory);
    }

    @AfterClass
    public static void tearDownMethod() throws Exception {
        // postgres
        if (System.getProperty(SQLRtreeManager.JDBC_TYPE_KEY) != null) {
            if (System.getProperty(SQLRtreeManager.JDBC_TYPE_KEY).equals("postgres")) {
                if (Files.isDirectory(directory) && Files.newDirectoryStream(directory).iterator().hasNext()) {
                    LucenePostgresSQLTreeEltMapper.resetDB(Files.newDirectoryStream(directory).iterator().next());
                }
            }
        }

        FileUtilities.deleteDirectory(directory.toFile());
    }

    @Test
    public void openEmptyTestTest() throws Exception {

        Tree tree = SQLRtreeManager.get(directory, this);
        assertNotNull(tree);

        SQLRtreeManager.close(directory, tree, this);

        tree = SQLRtreeManager.get(directory, this);

        assertNotNull(tree);

    }
}
