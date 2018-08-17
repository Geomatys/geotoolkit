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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.Format;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class KMLAdaptorTest {

    private static final String BRUT_KML_LOCATION = "/inputs/placemark.xml";

    @Test
    public void kmlCdataWPS2() throws Exception {

        final Format format = new Format(null, "application/vnd.google-earth.kml+xml", null, null);

        final ComplexAdaptor adaptor = ComplexAdaptor.getAdaptor(format);
        assertEquals(Path.class, adaptor.getValueClass());

        final URL kmlResource = KMLAdaptorTest.class.getResource(BRUT_KML_LOCATION);
        final Path kmlPath = Paths.get(kmlResource.toURI());
        final DataInput out = adaptor.toWPS2Input(kmlPath);

        assertNotNull("KML adaptor has not returned any result", out);

        final Data data = out.getData();
        assertNotNull("Generated data input does not contain any data markup", data);

        final List<Object> dc = data.getContent();
        assertTrue("Generated content should contain exactly one element", dc != null && dc.size() == 1);

        final byte[] brutFileContent = Files.readAllBytes(kmlPath);
        final String brutExpectedKml = new String(brutFileContent, StandardCharsets.UTF_8);
        assertEquals("Written content is unexpected", "<![CDATA["+brutExpectedKml+"]]>", dc.get(0));
    }
}
