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

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.isoonjts.GeometryUtils;
import org.geotoolkit.ows.xml.BoundingBox;
import org.geotoolkit.ows.xml.v200.BoundingBoxType;
import org.geotoolkit.wps.xml.v100.InputType;
import org.geotoolkit.wps.xml.v100.OutputDataType;
import org.geotoolkit.wps.xml.v200.BoundingBoxData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInputType;
import org.geotoolkit.wps.xml.v200.DataOutputType;
import org.geotoolkit.wps.xml.v200.SupportedCRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BboxAdaptor implements DataAdaptor<Envelope> {

    private final List<CoordinateReferenceSystem> crss;

    private BboxAdaptor(List<CoordinateReferenceSystem> crss){
        this.crss = crss;

    }

    @Override
    public Class getValueClass() {
        return Envelope.class;
    }

    @Override
    public InputType toWPS1Input(Envelope candidate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DataInputType toWPS2Input(Envelope candidate) {

        //change envelope crs if not supported
        if (crss.contains(candidate.getCoordinateReferenceSystem())) {
            try {
                candidate = Envelopes.transform(candidate, crss.get(0));
            } catch (TransformException ex) {
                throw new UnconvertibleObjectException(ex.getMessage(), ex);
            }
        }

        final BoundingBoxType litValue = new BoundingBoxType(candidate);
        final Data data = new Data();
        data.getContent().add(litValue);

        final DataInputType dit = new DataInputType();
        dit.setData(data);

        return dit;
    }

    @Override
    public Envelope fromWPS1Input(OutputDataType candidate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope fromWPS2Input(DataOutputType candidate) throws UnconvertibleObjectException {
        final BoundingBox bbox = candidate.getData().getBoundingBoxData();

        final List<Double> lower = bbox.getLowerCorner();
        final List<Double> upper = bbox.getUpperCorner();
        final String crsName = bbox.getCrs();
        final int dimension = bbox.getDimensions();

        //Check if it's a 2D boundingbox
        if (dimension != 2 || lower.size() != 2 || upper.size() != 2) {
            throw new UnconvertibleObjectException("Invalid data input : Only 2 dimension boundingbox supported.");
        }

        final CoordinateReferenceSystem crs;
        try {
            crs = CRS.forCode(crsName);
        } catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Invalid data input : CRS not supported. "+ex.getMessage(), ex);
        }

        final Envelope cenv = GeometryUtils.createCRSEnvelope(crs, lower.get(0), lower.get(1), upper.get(0), upper.get(1));
        return cenv;
    }

    public static BboxAdaptor create(BoundingBoxData data) {

        final List<CoordinateReferenceSystem> crss = new ArrayList<>();
        for(SupportedCRS scrs : data.getSupportedCRS()) {
            try {
                CoordinateReferenceSystem crs = CRS.forCode(scrs.getValue());
                if (Boolean.TRUE.equals(scrs.isDefault())) {
                    crss.add(0, crs);
                } else {
                    crss.add(crs);
                }
            } catch (FactoryException ex) {
                //do nothing, skip this crs
            }
        }

        return new BboxAdaptor(crss);
    }

}
