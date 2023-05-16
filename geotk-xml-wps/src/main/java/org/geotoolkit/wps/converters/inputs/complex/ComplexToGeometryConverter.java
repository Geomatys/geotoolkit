/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wps.converters.inputs.complex;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import jakarta.xml.bind.JAXBElement;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.AbstractCRS;
import org.apache.sis.referencing.cs.AxesConvention;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.internal.geojson.binding.GeoJSONFeature;
import org.geotoolkit.internal.geojson.binding.GeoJSONObject;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.converters.WPSObjectConverter;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v200.Data;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * Implementation of ObjectConverter to convert a complex input into a JTS Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToGeometryConverter extends AbstractComplexInputConverter<Geometry> {

    private static ComplexToGeometryConverter INSTANCE;

    private static final GeometryFactory GF = JTS.getFactory();

    private ComplexToGeometryConverter(){
    }

    public static synchronized ComplexToGeometryConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToGeometryConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry> getTargetClass() {
        return Geometry.class;
    }

    /**
     * {@inheritDoc}
     * @return Geometry.
     */
    @Override
    public Geometry convert(final Data source, final Map<String, Object> params) throws UnconvertibleObjectException {

        String dataMimeTypeIdentifier = null;
        try {
            final List<Object> data = source.getContent();

            final String mimeType = source.getMimeType();

            if (data.size() != 1)
                throw new UnconvertibleObjectException("Invalid data input : Only one geometry expected.");

            if (WPSMimeType.APP_GML.val().equalsIgnoreCase(mimeType) ||
                WPSMimeType.TEXT_XML.val().equalsIgnoreCase(mimeType) ||
                WPSMimeType.TEXT_GML.val().equalsIgnoreCase(mimeType) ) {
                dataMimeTypeIdentifier = "GML";
                Object value = data.get(0);
                if (value instanceof JAXBElement) {
                    value = ((JAXBElement) value).getValue();
                }
                AbstractGeometry abstractGeo;
                if (value instanceof AbstractGeometry) {
                    abstractGeo = (AbstractGeometry) value;
                } else if (value instanceof String) {
                    abstractGeo = WPSConvertersUtils.readGMLGeometryFromString((String) value);
                } else {
                    throw new UnconvertibleObjectException("Invalid data input content for " + dataMimeTypeIdentifier + " geometry.");
                }
                return GeometrytoJTS.toJTS(abstractGeo);
            } else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(mimeType)) {
                dataMimeTypeIdentifier = "GeoJSON";
                final String content = WPSConvertersUtils.geojsonContentAsString(source);
                final GeoJSONObject jsonObject = WPSConvertersUtils.readGeoJSONObjectsFromString(content);

                if (!(jsonObject instanceof GeoJSONFeature)) {
                    throw new UnconvertibleObjectException("Expected a GeoJSONGeometry and found a " + jsonObject.getClass().getName());
                }
                Geometry geom = WPSConvertersUtils.convertGeoJSONGeometryToGeometry(((GeoJSONFeature) jsonObject).getGeometry());
                Class expectedClass = (Class) params.get(WPSObjectConverter.TARGET_CLASS);

                /*
                 * Linear ring does not exist in GeoJson spec, so we transform polygon geometry during conversion
                 */
                if (geom instanceof Polygon && LinearRing.class.equals(expectedClass)) {
                    CoordinateReferenceSystem crs = JTS.findCoordinateReferenceSystem(geom);
                    Polygon poly = (Polygon) geom;
                    LinearRing lr = GF.createLinearRing(poly.getExteriorRing().getCoordinateSequence());
                    JTS.setCRS(lr, crs);
                    return lr;
                }

                return geom;
            } else if (WPSMimeType.APP_EWKT.val().equalsIgnoreCase(mimeType)) {
                Object value = data.get(0);
                if (value instanceof String) {
                    String wkt = (String) value;
                    int idx = wkt.indexOf(';');
                    CoordinateReferenceSystem crs = null;
                    if (idx > 0) {
                        try {
                            int srid = Integer.valueOf(wkt.substring(5, idx));
                            if (srid > 0) {
                                crs = CRS.forCode("EPSG:"+srid);
                                crs = AbstractCRS.castOrCopy(crs).forConvention(AxesConvention.RIGHT_HANDED);
                            }
                        } catch (IllegalArgumentException ex) {
                            throw new UnconvertibleObjectException("Incorrect SRID definition " + wkt);
                        }
                        wkt = wkt.substring(idx+1);
                    }
                    final WKTReader reader = new WKTReader();
                    final Geometry geom;
                    try {
                        geom = reader.read(wkt);
                    } catch (ParseException ex) {
                            throw new UnconvertibleObjectException("Incorrect WKT definition " + wkt);
                    }
                    geom.setUserData(crs);
                    return geom;
                } else {
                    throw new UnconvertibleObjectException("Expected a WKT String and found a " + value.getClass().getName());
                }
            } else {
                throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
            }
        }catch(ClassCastException ex){
            throw new UnconvertibleObjectException("Invalid data input : empty " + dataMimeTypeIdentifier + " geometry.",ex);
        }catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Invalid data input : Cannot convert " + dataMimeTypeIdentifier + " geometry.",ex);
        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("Unable to read the CRS from the GeoJSONGeometry.", ex);
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex);
        }
    }
}
