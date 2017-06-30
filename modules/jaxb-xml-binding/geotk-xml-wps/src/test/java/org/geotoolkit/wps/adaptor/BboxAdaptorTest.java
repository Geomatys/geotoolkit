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

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataOutputType;
import org.geotoolkit.wps.xml.v200.SupportedCRS;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BboxAdaptorTest {
    
    @Test
    public void supportedCrsWPS2() throws FactoryException {
                
        final BoundingBoxData bboxDataType = new BoundingBoxData();
        final SupportedCRS scrs = new SupportedCRS("ESPG:4326");
        bboxDataType.getSupportedCRS().add(scrs);
        
        final BboxAdaptor adaptor = BboxAdaptor.create(bboxDataType);
        assertEquals(Envelope.class, adaptor.getValueClass());
        
        final BoundingBoxType litValue = new BoundingBoxType("EPSG:4326",-180,-90,+180,+90);
        final DataOutputType output = new DataOutputType();
        final Data data = new Data(litValue);
        output.setData(data);
        
        final Envelope result = adaptor.fromWPS2Input(output);
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.forCode("EPSG:4326"));
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        
        assertEquals(env, new GeneralEnvelope(result));
        
        
    }
}
