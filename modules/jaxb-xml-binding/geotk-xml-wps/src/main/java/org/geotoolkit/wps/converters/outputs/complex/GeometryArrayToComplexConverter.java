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
import com.vividsolutions.jts.geom.Geometry;
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
import static org.geotoolkit.wps.converters.WPSObjectConverter.WPSVERSION;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.geotoolkit.wps.xml.WPSXmlFactory;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;


/**
 * Implementation of ObjectConverter to convert a JTS Geometry array into a {@link ComplexDataType}.
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
    public ComplexDataType convert(final Geometry[] source, final Map<String, Object> params) throws UnconvertibleObjectException {


        if (source == null) {
            throw new UnconvertibleObjectException("The output data should be defined.");
        }
        if (!(source instanceof Geometry[])) {
            throw new UnconvertibleObjectException("The requested output data is not an instance of Geometry array.");
        }

        String wpsVersion  = (String) params.get(WPSVERSION);
        if (wpsVersion == null) {
            LOGGER.warning("No WPS version set using default 1.0.0");
            wpsVersion = "1.0.0";
        }
        final ComplexDataType complex = WPSXmlFactory.buildComplexDataType(wpsVersion, (String) params.get(ENCODING),(String) params.get(MIME), (String) params.get(SCHEMA));

        String gmlVersion = (String) params.get(GMLVERSION);
        if (gmlVersion == null) {
            gmlVersion = "3.1.1";
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

