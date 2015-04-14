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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.geotoolkit.gml.GeometrytoJTS;
import org.geotoolkit.gml.xml.v311.AbstractGeometryType;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry;
import org.geotoolkit.data.geojson.binding.GeoJSONGeometry.GeoJSONGeometryCollection;
import org.geotoolkit.data.geojson.binding.GeoJSONObject;
import org.geotoolkit.wps.converters.WPSConvertersUtils;
import org.geotoolkit.wps.io.WPSMimeType;
import org.geotoolkit.wps.xml.v100.ComplexDataType;
import org.opengis.util.FactoryException;

/**
 * Implementation of ObjectConverter to convert a complex input into a JTS Geometry array.
 *
 * @author Quentin Boileau (Geomatys).
 * @author Theo Zozime
 */
public final class ComplexToGeometryArrayConverter extends AbstractComplexInputConverter<Geometry[]> {

    private static ComplexToGeometryArrayConverter INSTANCE;

    private ComplexToGeometryArrayConverter(){
    }

    public static synchronized ComplexToGeometryArrayConverter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ComplexToGeometryArrayConverter();
        }
        return INSTANCE;
    }

    @Override
    public Class<Geometry[]> getTargetClass() {
        return Geometry[].class;
    }

    /**
     * {@inheritDoc}
     * @return Geometry array.
     */
    @Override
    public Geometry[] convert(final ComplexDataType source, final Map<String, Object> params) throws UnconvertibleObjectException {

        String dataMimeTypeIdentifier = null;
        try {
            final List<Object> data = source.getContent();

            if(!data.isEmpty()){
                if (WPSMimeType.APP_GML.val().equalsIgnoreCase(source.getMimeType())) {
                    dataMimeTypeIdentifier = "GML";
                    final List<Geometry> geoms = new ArrayList<Geometry>();
                    for(int i = 0; i<data.size(); i++){
                        geoms.add(GeometrytoJTS.toJTS((AbstractGeometryType) data.get(i)));
                    }
                    return geoms.toArray(new Geometry[geoms.size()]);
                }
                // When reading GeoJSON the converter assumes that the Geometry
                // array is passed as a GeoJSON FeatureCollection
                else if (WPSMimeType.APP_GEOJSON.val().equalsIgnoreCase(source.getMimeType())) {
                    dataMimeTypeIdentifier = "GeoJSON";

                    final String content = WPSConvertersUtils.extractGeoJSONContentAsStringFromComplex(source);
                    final GeoJSONObject jsonObject = WPSConvertersUtils.readGeoJSONObjectsFromString(content);

                    // Check that we have a FeatureCollection
                    if (!(jsonObject instanceof GeoJSONGeometryCollection))
                        throw new UnconvertibleObjectException("Expected a feature collection. Found : " + jsonObject.getClass().getName());

                    // Check collection size, if it's empty raise an exception
                    final GeoJSONGeometryCollection geometryCollection = (GeoJSONGeometryCollection) jsonObject;
                    int collectionSize = geometryCollection.getGeometries().size();
                    if (collectionSize == 0)
                        throw new UnconvertibleObjectException("Empty feature collection.");

                    // Fill the Geometry array
                    final Geometry[] geometryArray = new Geometry[collectionSize];
                    Iterator<GeoJSONGeometry> iterator = geometryCollection.getGeometries().iterator();
                    int index = 0;
                    while (iterator.hasNext()) {
                        final GeoJSONGeometry jsonGeometry = iterator.next();
                        geometryArray[index] = WPSConvertersUtils.convertGeoJSONGeometryToGeometry(jsonGeometry);//GeometryUtils.toJTS(jsonGeometry, crs);
                        index++;
                    }

                    return geometryArray;
                }
                else
                    throw new UnconvertibleObjectException("Unsupported mime-type for " + this.getClass().getName() +  " : " + source.getMimeType());
            }else{
                throw new UnconvertibleObjectException("Invalid data input : Empty geometry list.");
            }
        }catch(ClassCastException ex){
            throw new UnconvertibleObjectException("Invalid data input : empty " + dataMimeTypeIdentifier + " geometry.",ex);
        }catch (FactoryException ex) {
            throw new UnconvertibleObjectException("Invalid data input : Cannot convert " + dataMimeTypeIdentifier + " geometry.",ex);
        } catch (MalformedURLException ex) {
            throw new UnconvertibleObjectException("Unknown CRS code or unable to read the CRS from a geometry");
        } catch (IOException ex) {
            throw new UnconvertibleObjectException(ex);
        }
    }
}