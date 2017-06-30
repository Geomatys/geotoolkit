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
package org.geotoolkit.wps.converters.inputs.reference;

import java.net.URLConnection;
import java.util.Collections;
import org.geotoolkit.wps.converters.AbstractWPSConverterTest;
import org.geotoolkit.wps.converters.WPSConverterRegistry;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSIO;
import org.geotoolkit.wps.xml.Reference;
import org.geotoolkit.wps.xml.v100.InputReferenceType;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class ReferenceToUrlConnectionConverterTest extends AbstractWPSConverterTest {

    @Test
    public void convert() throws Exception {
        final WPSObjectConverter<Reference, URLConnection> converter = WPSConverterRegistry.getInstance().getConverter(Reference.class, URLConnection.class);

        final InputReferenceType ref = new InputReferenceType();
        ref.setHref("https://my.domain/context");
        final InputReferenceType.Header header = new InputReferenceType.Header();
        header.setKey("myKey");
        header.setValue("myValue");
        ref.getHeader().add(header);

        final URLConnection con = converter.convert(ref, Collections.singletonMap(WPSObjectConverter.IOTYPE, (Object) WPSIO.IOType.INPUT.name()));
        Assert.assertEquals("HRef differs !", ref.getHref(), con.getURL().toExternalForm());
        Assert.assertEquals("No header has been copied !", header.getValue(), con.getRequestProperty(header.getKey()));
    }
}
