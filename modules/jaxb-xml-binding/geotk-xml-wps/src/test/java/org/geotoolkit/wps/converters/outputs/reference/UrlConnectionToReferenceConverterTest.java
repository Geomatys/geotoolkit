/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.converters.outputs.reference;

import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.converters.inputs.references.AbstractReferenceInputConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.Reference;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class UrlConnectionToReferenceConverterTest extends AbstractWPSConverterTest {

    @Test
    public void convert() throws Exception {
        final WPSObjectConverter<URLConnection, Reference> converter = WPSConverterRegistry.getInstance().getConverter(URLConnection.class, Reference.class);
        final URL url = new URL("https://toto.titi/tata");
        URLConnection conn = url.openConnection();

        Reference ref = converter.convert(conn, Collections.singletonMap(AbstractReferenceInputConverter.IOTYPE, (Object) WPSIO.IOType.OUTPUT));
        Assert.assertEquals("Href differs !", url.toExternalForm(), ref.getHref());
    }
}
