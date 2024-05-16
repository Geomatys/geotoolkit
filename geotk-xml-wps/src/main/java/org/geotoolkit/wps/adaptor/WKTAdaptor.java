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

import org.apache.sis.geometry.wrapper.Geometries;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.wps.xml.ReferenceProxy;
import org.geotoolkit.wps.xml.v200.ComplexData;
import org.geotoolkit.wps.xml.v200.Data;
import org.geotoolkit.wps.xml.v200.DataInput;
import org.geotoolkit.wps.xml.v200.Format;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.coordinate.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WKTAdaptor extends ComplexAdaptor {

    static final String ENC_UTF8 = "UTF-8";
    static final String ENC_BASE64 = "base64";

    static final String MIME_TYPE = "application/ewkt";

    private final String mimeType;
    private final String encoding;

    public WKTAdaptor(String mimeType, String encoding) {
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String getSchema() {
        return null;
    }

    @Override
    public Class getValueClass() {
        return Geometry.class;
    }

    @Override
    public DataInput toWPS2Input(Object candidate) throws UnconvertibleObjectException {
        if (candidate instanceof ReferenceProxy) return super.toWPS2Input(candidate);

        final ComplexData cdt = new ComplexData();
        cdt.getContent().add(new org.geotoolkit.wps.xml.v200.Format(encoding, mimeType, null, null));

        Geometry geom = (Geometry) candidate;

        int srid = 0;
        int dimension = 2;
        try {
            CoordinateReferenceSystem crs = Geometries.wrap(geom).get().getCoordinateReferenceSystem();
            if (crs != null) {
                dimension = crs.getCoordinateSystem().getDimension();
                final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder("EPSG");
                // TODO: Ensure no project strongly rely on that, then remove. It's pure non-sense/madness.
                // Note: If you read this after march 2020: do not ask : delete.
                finder.setIgnoringAxes(true);
                final CoordinateReferenceSystem epsgcrs = (CoordinateReferenceSystem) finder.findSingleton(crs);
                if (epsgcrs != null) {
                    srid = IdentifiedObjects.lookupEPSG(epsgcrs);

                    //force geometry in longitude first
                    final CoordinateReferenceSystem crs2 = ((AbstractCRS)crs).forConvention(AxesConvention.RIGHT_HANDED);
                    if (crs2 != crs) {
                        geom = org.apache.sis.geometry.wrapper.jts.JTS.transform(geom, crs2);
                    }
                    if (crs2 != null)
                        dimension = crs2.getCoordinateSystem().getDimension();
                }
            }
        } catch (FactoryException | MismatchedDimensionException | TransformException ex) {
            throw new UnconvertibleObjectException(ex.getMessage(), ex);
        }

//        String wkt = geom.toText();

        WKTWriter writer = new WKTWriter(dimension);
        String wkt = writer.write(geom);
        if (srid > 0) {
            wkt = "SRID="+srid+";"+wkt;
        }
        cdt.getContent().add(wkt);

        final Data data = new Data();
        data.getContent().add(cdt);

        final DataInput dit = new DataInput();
        dit.setData(data);
        return dit;
    }

    public static class Spi implements ComplexAdaptor.Spi {

        @Override
        public ComplexAdaptor create(Format format) {
            final String encoding = format.getEncoding();
            final String mimeType = format.getMimeType();

            if (!MIME_TYPE.equalsIgnoreCase(mimeType)) return null;
            if (encoding != null && !(ENC_UTF8.equalsIgnoreCase(encoding) || ENC_BASE64.equalsIgnoreCase(encoding))) return null;

            return new WKTAdaptor(mimeType, encoding);
        }

    }

}
