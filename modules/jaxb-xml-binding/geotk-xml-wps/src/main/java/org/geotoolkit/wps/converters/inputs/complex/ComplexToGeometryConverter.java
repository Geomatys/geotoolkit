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


import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import org.geotoolkit.gml.GeometrytoJTS;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.geojson.binding.GeoJSONFeature;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.ComplexDataType;
import org.opengis.util.FactoryException;


/**
 * Implementation of ObjectConverter to convert a complex input into a JTS Geometry.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToGeometryConverter extends AbstractComplexInputConverter<Geometry> {

    private static ComplexToGeometryConverter INSTANCE;

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
    public Geometry convert(final ComplexDataType source, final Map<String, Object> params) throws UnconvertibleObjectException {

        String dataMimeTypeIdentifier = null;
        try {
            final List<Object> data = source.getContent();

            if (data.size() != 1)
                throw new UnconvertibleObjectException("Invalid data input : Only one geometry expected.");

            if (WPSMimeType.APP_GML.val().equalsIgnoreCase(source.getMimeType()) ||
                WPSMimeType.TEXT_XML.val().equalsIgnoreCase(source.getMimeType()) ||
                WPSMimeType.TEXT_GML.val().equalsIgnoreCase(source.getMimeType()) ) {
                dataMimeTypeIdentifier = "GML";
                AbstractGeometry abstractGeo = (AbstractGeometry) data.get(0);
                return GeometrytoJTS.toJTS(abstractGeo);
            } else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(source.getMimeType())) {
                dataMimeTypeIdentifier = "GeoJSON";
                final String content = WPSConvertersUtils.extractGeoJSONContentAsStringFromComplex(source);
                final GeoJSONObject jsonObject = WPSConvertersUtils.readGeoJSONObjectsFromString(content);

                if (!(jsonObject instanceof GeoJSONFeature))
                    throw new UnconvertibleObjectException("Expected a GeoJSONGeometry and found a " + jsonObject.getClass().getName());

                return WPSConvertersUtils.convertGeoJSONGeometryToGeometry(((GeoJSONFeature) jsonObject).getGeometry());
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
