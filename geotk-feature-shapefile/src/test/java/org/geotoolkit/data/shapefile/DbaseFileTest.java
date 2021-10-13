/*
 *    GeotoolKit - An Open source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.ShapeTestData;
import org.geotoolkit.data.dbf.DbaseFieldFormatter;
import org.geotoolkit.data.dbf.DbaseFileHeader;
import org.geotoolkit.data.dbf.DbaseFileReader;
import org.geotoolkit.data.dbf.DbaseFileWriter;
import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.lock.ShpFiles;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @version $Id$
 * @author Ian Schneider
 * @author James Macgill
 * @module
 */
public class DbaseFileTest extends AbstractTestCaseSupport {

    private static final Logger LOGGER = org.apache.sis.util.logging.Logging
            .getLogger("org.geotoolkit.data.shapefile");

    static final String TEST_FILE = "shapes/statepop.dbf";

    private DbaseFileReader dbf = null;

    private ShpFiles shpFiles;

    @Before
    public void setUp() throws Exception {
        shpFiles = new ShpFiles(ShapeTestData.url(TEST_FILE));
        final AccessManager locker = shpFiles.createLocker();
        dbf = locker.getDBFReader(false, ShapefileFeatureStore.DEFAULT_STRING_CHARSET);
    }

    @Test
    public void testNumberofColsLoaded() {
        assertEquals("Number of attributes found incorect", 252, dbf
                .getHeader().getNumFields());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        dbf.close();
        super.tearDown();
    }

    @Test
    public void testNumberofRowsLoaded() {
        assertEquals("Number of rows", 49, dbf.getHeader().getNumRecords());
    }

    @Test
    public void testDataLoaded() throws Exception {
        Object[] attrs = new Object[dbf.getHeader().getNumFields()];
        dbf.next().readAll(attrs);
        assertEquals("Value of Column 0 is wrong", "Illinois", attrs[0]);
        assertEquals("Value of Column 4 is wrong", 143986.61,
                ((Double) attrs[4]).doubleValue(), 0.001);
    }

    @Test
    public void testRowVsEntry() throws Exception {
        Object[] attrs = new Object[dbf.getHeader().getNumFields()];
        final AccessManager locker = shpFiles.createLocker();
        DbaseFileReader dbf2 = locker.getDBFReader(false, ShapefileFeatureStore.DEFAULT_STRING_CHARSET);
        while (dbf.hasNext()) {
            final DbaseFileReader.Row r1 = dbf.next();
            final DbaseFileReader.Row r2 = dbf2.next();
            r1.readAll(attrs);

            for (int i = 0, ii = attrs.length; i < ii; i++) {
                assertNotNull(attrs[i]);
                assertNotNull(r2.read(i));
                assertEquals(attrs[i], r2.read(i));
            }
        }
        dbf2.close();
    }

    @Test
    public void testHeader() throws Exception {
        DbaseFileHeader header = new DbaseFileHeader();

        Level before = LOGGER.getLevel();
        try {
            LOGGER.setLevel(Level.INFO);

            header.addColumn("emptyString", 'C', 20, 0);
            header.addColumn("emptyInt", 'N', 20, 0);
            header.addColumn("emptyDouble", 'N', 20, 5);
            header.addColumn("emptyFloat", 'F', 20, 5);
            header.addColumn("emptyLogical", 'L', 1, 0);
            header.addColumn("emptyDate", 'D', 20, 0);
            int length = header.getRecordLength();
            header.removeColumn("emptyDate");
            assertTrue(length != header.getRecordLength());
            header.addColumn("emptyDate", 'D', 20, 0);
            assertTrue(length == header.getRecordLength());
            header.removeColumn("billy");
            assertTrue(length == header.getRecordLength());
        } finally {
            LOGGER.setLevel(before);
        }
    }

    @Test
    public void testAddColumn() throws Exception {
        DbaseFileHeader header = new DbaseFileHeader();

        Level before = LOGGER.getLevel();
        try {
            LOGGER.setLevel(Level.INFO);

            header.addColumn("emptyInt", 'N', 9, 0);
            assertSame(Integer.class, header.getFieldClass(0));
            assertEquals(9, header.getFieldLength(0));

            header.addColumn("emptyString", 'C', 20, 0);
            assertSame(String.class, header.getFieldClass(1));
            assertEquals(20, header.getFieldLength(1));
        } finally {
            LOGGER.setLevel(before);
        }
    }

    @Test
    public void testEmptyFields() throws Exception {
        DbaseFileHeader header = new DbaseFileHeader();
        header.addColumn("emptyString", 'C', 20, 0);
        header.addColumn("emptyInt", 'N', 20, 0);
        header.addColumn("emptyDouble", 'N', 20, 5);
        header.addColumn("emptyFloat", 'F', 20, 5);
        header.addColumn("emptyLogical", 'L', 1, 0);
        header.addColumn("emptyDate", 'D', 20, 0);
        header.setNumRecords(20);
        File f = new File(System.getProperty("java.io.tmpdir"),
                "scratchDBF.dbf");
        f.deleteOnExit();
        FileOutputStream fout = new FileOutputStream(f);
        DbaseFileWriter dbf = new DbaseFileWriter(header, fout.getChannel(), Charset.defaultCharset());
        for (int i = 0; i < header.getNumRecords(); i++) {
            dbf.write(new Object[]{String.valueOf(900+i),null,null,null,null,null});
        }
        dbf.close();
        final ShpFiles tempShpFiles = new ShpFiles(f);
        final AccessManager locker = tempShpFiles.createLocker();
        DbaseFileReader r = locker.getDBFReader(false, ShapefileFeatureStore.DEFAULT_STRING_CHARSET);
        int cnt = 0;
        while (r.hasNext()) {
            cnt++;
            Object[] o = r.next().readAll(null);
            assertTrue(o.length == r.getHeader().getNumFields());
        }
        assertEquals("Bad number of records", cnt, 20);
        r.close(); // make sure the channel is closed
        f.delete();
    }

    @Test
    public void testFieldFormatter() throws Exception {
        DbaseFieldFormatter formatter = new DbaseFieldFormatter(Charset.defaultCharset());

        String stringWithInternationChars = "hello " + '\u20ac';
        // if (verbose) {
        // System.out.println(stringWithInternationChars);
        // }
        String formattedString = formatter.getFieldString(10,
                stringWithInternationChars);

        assertEquals("          ".getBytes().length,
                formattedString.getBytes().length);

        // test when the string is too big.
        stringWithInternationChars = '\u20ac' + "1234567890";
        formattedString = formatter.getFieldString(10,
                stringWithInternationChars);

        assertEquals("          ".getBytes().length,
                formattedString.getBytes().length);

    }

}
