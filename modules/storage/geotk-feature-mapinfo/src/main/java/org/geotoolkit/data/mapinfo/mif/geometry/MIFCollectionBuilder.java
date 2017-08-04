/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.mapinfo.mif.geometry;

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.MIFUtils;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.sis.feature.DefaultFeatureType;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.feature.FeatureExt;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.InvalidPropertyValueException;
import org.opengis.feature.PropertyNotFoundException;

/**
 * Build features representing MIF Collection geometries.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 27/02/13
 */
public class MIFCollectionBuilder extends MIFGeometryBuilder {
    public static final GenericName NAME = NamesExt.create("COLLECTION");
    public static final GenericName GEOM_NAME = NamesExt.create("STYLE_RULES").push(NAME);
    public static final FeatureType EMPTY_TYPE = new DefaultFeatureType(Collections.singletonMap("name", "abstract"), true, null);

    @Override
    public void buildGeometry(Scanner scanner, Feature toFill, MathTransform toApply) throws DataStoreException {
        int numGeom = 0;
        try {
            numGeom = scanner.nextInt();
        } catch (Exception e) {
            throw new DataStoreException("Number of geometries in Collection is not specified", e);
        }

        final List<Feature> features = new ArrayList<>();
        for(int geomCount=0 ; geomCount < numGeom ; geomCount++ ) {
            while(scanner.hasNextLine()) {
                final MIFUtils.GeometryType enumType = MIFUtils.getGeometryType(scanner.findInLine("\\w+"));
                if (enumType != null) {
                    final FeatureType type = enumType.getBinding(null, EMPTY_TYPE);
                    final Feature currentFeature = type.newInstance();
                    enumType.readGeometry(scanner, currentFeature, toApply);
                    features.add(currentFeature);
                    break;
                } else {
                    scanner.nextLine();
                }
            }
        }

        toFill.setPropertyValue(GEOM_NAME.toString(), features);

        final String geomName = FeatureExt.getDefaultGeometry(toFill.getType()).getName().toString();
        toFill.setPropertyValue(geomName, getGeometry(toFill));
    }

    @Override
    public FeatureType buildType(CoordinateReferenceSystem crs, FeatureType parent) {
        final FeatureType type = super.buildType(crs, parent);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder(type);
        ftb.addAssociation(EMPTY_TYPE).setName(GEOM_NAME).setMinimumOccurs(1).setMaximumOccurs(3);

        return ftb.build();
    }

    @Override
    public String toMIFSyntax(Feature feature) throws DataStoreException {
        super.toMIFSyntax(feature);

        StringBuilder builder = new StringBuilder(NAME.tip().toString()).append(' ');
        Object value = MIFUtils.getPropertySafe(feature, GEOM_NAME.toString());
        if (value instanceof Feature) {
            featureToMIFCollection((Feature) value, builder);
        } else if (value instanceof GeometryCollection) {
            jtsToMIFCollection((GeometryCollection) value, builder);
        } else if ((value = MIFUtils.getGeometryValue(feature)) instanceof GeometryCollection) {
            jtsToMIFCollection((GeometryCollection) value, builder);
        } else {
            throw new DataStoreException("Incompatible geometry type.");
        }

        return builder.toString();
    }


    private void jtsToMIFCollection(GeometryCollection source, StringBuilder builder) throws DataStoreException {
        final ArrayList<Polygon> polygons = new ArrayList<>();
        final ArrayList<LineString> lines = new ArrayList<>();
        final ArrayList<Point> points = new ArrayList<>();

        sortGeometries(source, polygons, lines, points);

        int count = 0;
        ArrayList<String> geomsStr = new ArrayList<>();
        if(polygons.size() > 0) {
            count++;
            geomsStr.add(convert(
                    GEOMETRY_FACTORY.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()])),
                    MIFUtils.GeometryType.REGION
            ));
        }
        if(lines.size() > 0) {
            count++;
            geomsStr.add(convert(
                    GEOMETRY_FACTORY.createMultiLineString(lines.toArray(new LineString[lines.size()])),
                    MIFUtils.GeometryType.PLINE
            ));
        }
        if(points.size() > 0) {
            count++;
            geomsStr.add(convert(
                    GEOMETRY_FACTORY.createMultiPoint(points.toArray(new Point[points.size()])),
                    MIFUtils.GeometryType.MULTIPOINT
            ));
        }

        builder.append(count).append('\n');
        for(String mifMulti : geomsStr) {
            builder.append(mifMulti);
        }
    }

    private String convert(final GeometryCollection col, MIFUtils.GeometryType mifType) throws DataStoreException {
        final FeatureType type = mifType.getBinding(null, null);
        final Feature feature = type.newInstance();
        feature.setPropertyValue(FeatureExt.getDefaultGeometry(type).getName().toString(), col);
        return mifType.toMIFSyntax(feature);
    }

    /**
     * MIF defines geometry collection as :
     * 0 or 1 region,
     * 0 or 1 polyline,
     * 0 or 1 multipoint.
     * So, we have to parse source geometries and group them per type.
     *
     * @param source The collection of JTS geometries to create MIF collection with.
     * @param polygons list to store region.
     * @param lines list to store polyline.
     * @param points list to store multipoint.
     */
    private void sortGeometries(GeometryCollection source, List<Polygon> polygons, List<LineString> lines, List<Point> points) {
        for(int i = 0 ; i < source.getNumGeometries() ; i++) {
            final Geometry geom = source.getGeometryN(i);
            if(geom instanceof Polygon) {
                polygons.add((Polygon) geom);
            } else if(geom instanceof LineString) {
                lines.add((LineString) geom);
            } else if(geom instanceof Point) {
                points.add((Point) geom);
            } else if(geom instanceof GeometryCollection) {
                sortGeometries((GeometryCollection) geom, polygons, lines, points);
            }
        }
    }

    private void featureToMIFCollection(Feature source, StringBuilder builder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Class getGeometryBinding() {
        return GeometryCollection.class;
    }

    @Override
    public Class[] getPossibleBindings() {
        return new Class[]{Feature.class, GeometryCollection.class};
    }

    @Override
    public GenericName getName() {
        return NAME;
    }

    @Override
    protected List<AttributeType> getAttributes() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public GeometryCollection getGeometry(Feature f) throws PropertyNotFoundException, InvalidPropertyValueException {
        final Object value = f.getPropertyValue(GEOM_NAME.toString());
        if (value == null)
            return null;
        else if (!(value instanceof Collection)) {
            throw new InvalidPropertyValueException("A collection of features was expected, but we've got "+value.getClass());
        }

        final Collection col = (Collection) value;
        if (col.isEmpty())
            return null;

        final List<Geometry> geoms = new ArrayList<>(col.size());
        for (final Object o : col) {
            if (o == null)
                continue;
            if (o instanceof Geometry) {
                geoms.add((Geometry) o);
            } else if (o instanceof Feature) {
                final Object geom = ((Feature) o).getPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString());
                if (geom instanceof Geometry) {
                    geoms.add((Geometry) geom);
                } else if (geom != null) {
                    throw new InvalidPropertyValueException("A non-geometric value has been found : " + geom.getClass());
                }
            } else {
                throw new InvalidPropertyValueException("A geometry or feature was expected, but we found " + o.getClass());
            }
        }

        return GEOMETRY_FACTORY.createGeometryCollection(geoms.toArray(new Geometry[geoms.size()]));
    }
}
