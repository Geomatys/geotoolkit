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
import org.locationtech.jts.geom.Geometry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.geojson.GeoJSONStreamWriter;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.utils.GeometryUtils;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import static org.geotoolkit.wps.converters.WPSObjectConverter.ENCODING;
import static org.geotoolkit.wps.converters.WPSObjectConverter.MIME;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;


/**
 * Implementation of ObjectConverter to convert a JTS Geometry array into a {@link Data}.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class GeometryArrayToComplexConverter extends AbstractComplexOutputConverter<Geometry[]> {

    private static GeometryArrayToComplexConverter INSTANCE;

    private GeometryArrayToComplexConverter(){
    }

    public static synchronized GeometryArrayToComplexConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new GeometryArrayToComplexConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry[]> getSourceClass() {
        return Geometry[].class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Data convert(final Geometry[] source, final Map<String, Object> params) throws UnconvertibleObjectException {
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

        Object tmpGmlVersion = params.get(GMLVERSION);
        final String gmlVersion;
        if (tmpGmlVersion instanceof String) {
            gmlVersion = (String) tmpGmlVersion;
        } else {
            gmlVersion = "3.2.1";
        }

        if (WPSMimeType.APP_GML.val().equalsIgnoreCase(complex.getMimeType())||
            WPSMimeType.TEXT_XML.val().equalsIgnoreCase(complex.getMimeType()) ||
            WPSMimeType.TEXT_GML.val().equalsIgnoreCase(complex.getMimeType())) {
            try {
                for(final Geometry jtsGeom : source){
                    final AbstractGeometry gmlGeom = JTStoGeometry.toGML(gmlVersion, jtsGeom);
                    complex.getContent().add(gmlGeom);
                }
            } catch (NoSuchAuthorityCodeException ex) {
               throw new UnconvertibleObjectException(ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException(ex);
            }
        }
        else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(complex.getMimeType())) {
            GeoJSONGeometry.GeoJSONGeometryCollection geometryCollection = new GeoJSONGeometry.GeoJSONGeometryCollection();
            for (Geometry geometry : source)
                geometryCollection.getGeometries().add(GeometryUtils.toGeoJSONGeometry(geometry));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                GeoJSONStreamWriter.writeSingleGeometry(baos, WPSConvertersUtils.convertGeoJSONGeometryToGeometry(geometryCollection), JsonEncoding.UTF8, WPSConvertersUtils.FRACTION_DIGITS, true);
                WPSConvertersUtils.addCDATAToComplex(baos.toString("UTF-8"), complex);
            }  catch (UnsupportedEncodingException e) {
                throw new UnconvertibleObjectException("Can't convert output stream into String.", e);
            } catch (IOException ex) {
                throw new UnconvertibleObjectException(ex);
            } catch (FactoryException ex) {
                throw new UnconvertibleObjectException("Couldn't decode CRS." , ex);
            }
        }
        else
            throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + complex.getMimeType());

        return complex;
    }
}

