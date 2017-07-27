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

import org.geotoolkit.ows.xml.v200.DomainMetadataType;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataOutputType;
import org.geotoolkit.wps.xml.v200.LiteralDataType;
import org.geotoolkit.wps.xml.v200.LiteralValue;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class LiteralAdaptorTest {

    private static final double DELTA = 1e-7;


    @Test
    public void stringWPS2() {

        final DomainMetadataType metaType = new DomainMetadataType("String","http://www.w3.org/TR/xmlschema-2/#string");
        final LiteralDataType.LiteralDataDomain domain = new LiteralDataType.LiteralDataDomain();
        domain.setDataType(metaType);

        final LiteralAdaptor adaptor = LiteralAdaptor.create(domain);
        assertEquals(String.class, adaptor.getValueClass());

        final DataOutputType output = new DataOutputType();
        final LiteralValue lit = new LiteralValue();
        lit.setValue("hello world");
        final Data data = new Data(lit);
        output.setData(data);

        final Object result = adaptor.fromWPS2Input(output);

        assertEquals("hello world", result);


    }

    @Test
    public void doubleWPS2() {

        final DomainMetadataType metaType = new DomainMetadataType(null,"xs:double");
        final LiteralDataType.LiteralDataDomain domain = new LiteralDataType.LiteralDataDomain();
        domain.setDataType(metaType);

        final LiteralAdaptor adaptor = LiteralAdaptor.create(domain);
        assertEquals(Double.class, adaptor.getValueClass());

        final DataOutputType output = new DataOutputType();
        final LiteralValue lit = new LiteralValue();
        lit.setValue("3.14");
        final Data data = new Data(lit);
        output.setData(data);

        final Object result = adaptor.fromWPS2Input(output);

        assertEquals(3.14, (Double)result, DELTA);


    }

    @Test
    public void booleanWPS2() {

        final DomainMetadataType metaType = new DomainMetadataType(null,"xs:boolean");
        final LiteralDataType.LiteralDataDomain domain = new LiteralDataType.LiteralDataDomain();
        domain.setDataType(metaType);

        final LiteralAdaptor adaptor = LiteralAdaptor.create(domain);
        assertEquals(Boolean.class, adaptor.getValueClass());

        final DataOutputType output = new DataOutputType();
        final LiteralValue lit = new LiteralValue();
        lit.setValue("true");
        final Data data = new Data(lit);
        output.setData(data);

        final Object result = adaptor.fromWPS2Input(output);

        assertEquals(true, result);


    }
}
