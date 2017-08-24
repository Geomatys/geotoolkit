/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.adaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.bind.JAXBException;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.Format;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLAdaptorTest {

    @Test
    public void kmlCdataWPS2() throws FactoryException, IOException, JAXBException {

        final Format format = new Format(null, "application/vnd.google-earth.kml+xml", null, null);

        final ComplexAdaptor adaptor = ComplexAdaptor.getAdaptor(format);
        assertEquals(Path.class, adaptor.getValueClass());

        Path path = Files.createTempFile("cdata", ".kml");
        Files.write(path, "some text".getBytes());

        DataInputType out = adaptor.toWPS2Input(path);
        assertNotNull(out);

    }
}
