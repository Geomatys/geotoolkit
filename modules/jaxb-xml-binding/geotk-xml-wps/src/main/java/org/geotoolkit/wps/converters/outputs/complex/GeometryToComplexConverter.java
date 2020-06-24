/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wps.converters.outputs.complex;

import com.fasterxml.jackson.core.JsonEncoding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.apache.sis.internal.feature.Geometries;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.referencing.factory.IdentifiedObjectFinder;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.storage.geojson.GeoJSONStreamWriter;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a JTS Geometry into a {@link Data}.
 *
 * @author Quentin Boileau
 * @author Theo Zozime
 */
public final class GeometryToComplexConverter extends AbstractComplexOutputConverter<Geometry> {

    private static GeometryToComplexConverter INSTANCE;

    private GeometryToComplexConverter() {
    }

    public static synchronized GeometryToComplexConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GeometryToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry> getSourceClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data convert(final Geometry source, final Map<String, Object> params) throws UnconvertibleObjectException {

        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        } else if (params == null) {
            throw new UnconvertibleObjectException("Not enough information about data format");
        }

        final Object tmpMime = params.get(MIME);
        final String mime;
        if (tmpMime instanceof String) {
            mime = (String) tmpMime;
        } else {
            throw new UnconvertibleObjectException("No valid mime type given. We cannot determine output image format");
        }


        final Data complex = new Data();
        complex.setMimeType(mime);

        final Object tmpEncoding = params.get(ENCODING);
        if (tmpEncoding instanceof String) {
            complex.setEncoding((String) tmpEncoding);
        }

        final Object tmpSchema = params.get(SCHEMA);
        if (tmpSchema instanceof String) {
            complex.setSchema((String) tmpSchema);
        }

        Object tmpGmlVersion = params.get(GMLVERSION);
        final String gmlVersion;
        if (tmpGmlVersion instanceof String) {
            gmlVersion = (String) tmpGmlVersion;
        } else {
            gmlVersion = "3.2.1";
        }

        final String mimeType = complex.getMimeType();
        if (WPSMimeType.APP_GML.val().equalsIgnoreCase(mimeType)||
            WPSMimeType.TEXT_XML.val().equalsIgnoreCase(mimeType) ||
            WPSMimeType.TEXT_GML.val().equalsIgnoreCase(mimeType)) {
            try {
                final AbstractGeometry gmlGeom = JTStoGeometry.toGML(gmlVersion, source);
                complex.getContent().add(gmlGeom);

            } catch (NoSuchAuthorityCodeException ex) {
                throw new UnconvertibleObjectException(ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        } else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(mimeType)) {
            GeoJSONGeometry jsonGeometry = GeoJSONGeometry.toGeoJSONGeometry(source);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                GeoJSONStreamWriter.writeSingleGeometry(baos, WPSConvertersUtils.convertGeoJSONGeometryToGeometry(jsonGeometry), JsonEncoding.UTF8, WPSConvertersUtils.FRACTION_DIGITS, true);
                WPSConvertersUtils.addCDATAToComplex(baos.toString("UTF-8"), complex);
            }  catch (UnsupportedEncodingException e) {
                throw new UnconvertibleObjectException("Can't convert output stream into String.", e);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException(ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("Couldn't decode a CRS.", ex);
            }
        } else if (WPSMimeType.APP_EWKT.val().equalsIgnoreCase(mimeType)) {

            Geometry geom = source;
            int srid = 0;
            try {
                CoordinateReferenceSystem crs = Geometries.wrap(geom).get().getCoordinateReferenceSystem();
                if (crs != null) {
                    final IdentifiedObjectFinder finder = IdentifiedObjects.newFinder("EPSG");
                    finder.setIgnoringAxes(true);
                    final CoordinateReferenceSystem epsgcrs = (CoordinateReferenceSystem) finder.findSingleton(crs);
                    if (epsgcrs != null) {
                        srid = IdentifiedObjects.lookupEPSG(epsgcrs);

                        //force geometry in longitude first
                        final CoordinateReferenceSystem crs2 = ((AbstractCRS)crs).forConvention(AxesConvention.RIGHT_HANDED);
                        if (crs2 != crs) {
                            geom = JTS.transform(geom, crs2);
                        }
                    }
                }
            } catch (FactoryException | MismatchedDimensionException | TransformException ex) {
                throw new UnconvertibleObjectException(ex.getMessage(), ex);
            }

            String wkt = geom.toText();
            if (srid > 0) {
                wkt = "SRID="+srid+";"+wkt;
            }
            complex.getContent().add(wkt);

        } else {
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + complex.getMimeType());
        }

        return complex;
    }
}
