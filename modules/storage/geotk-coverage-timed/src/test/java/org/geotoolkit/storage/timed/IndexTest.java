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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import org.geotoolkit.index.tree.StoreIndexException;
import org.geotoolkit.test.Assert;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class IndexTest extends DirectoryBasedTest {

    private static DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd")
            .parseLenient()
            .parseDefaulting(ChronoField.MILLI_OF_DAY, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();

    @Test
    public void index() throws IOException, StoreIndexException {
        final Path tmpImage = dir.resolve(LocalDate.now().toString()+".tif");
        try (final InputStream stream = IndexTest.class.getResourceAsStream("testImage.tif")) {
            Files.copy(stream, tmpImage);
        }

        final Index idx = new Index(dir.resolve("index"), dir, IndexTest::getDate);
        idx.tryIndex(tmpImage);

        Assert.assertEquals("Index should contain exactly one element.", 1, idx.size());
        final Path result = idx.getObject(1)
                .orElse(null);
        Assert.assertNotNull("No object found for given identifier", result);
        Assert.assertEquals("Indexed object is not valid", tmpImage, result);
    }

    private static TemporalAccessor getDate(final Path p) {
        String fileName = p.getFileName().toString();
        final int lastPoint = fileName.lastIndexOf(".");
        if (lastPoint > 0) {
            fileName = fileName.substring(0, lastPoint);
        }
        return FORMATTER.parse(fileName);
    }
}
