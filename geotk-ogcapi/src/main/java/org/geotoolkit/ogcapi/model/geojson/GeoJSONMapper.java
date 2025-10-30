/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.model.geojson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeoJSONMapper {

    private boolean bboxOnCollection = false;
    private boolean bboxOnFeature = false;
    private boolean bboxOnGeometry = false;
    private boolean includeFeatureId = true;
    private boolean includeTypeOnFeature = false;
    private boolean includeTypeOnCollection = false;
    private boolean includeCoordRefSysOnCollection = false;
    private boolean includeCoordRefSysOnFeature = false;
    private boolean includeCoordRefSysOnGeometry = false;

    public void setBboxOnCollection(boolean bboxOnCollection) {
        this.bboxOnCollection = bboxOnCollection;
    }

    public boolean isBboxOnCollection() {
        return bboxOnCollection;
    }

    public void setBboxOnFeature(boolean bboxOnFeature) {
        this.bboxOnFeature = bboxOnFeature;
    }

    public boolean isBboxOnFeature() {
        return bboxOnFeature;
    }

    public void setBboxOnGeometry(boolean bboxOnGeometry) {
        this.bboxOnGeometry = bboxOnGeometry;
    }

    public boolean isBboxOnGeometry() {
        return bboxOnGeometry;
    }

    public void setIncludeFeatureId(boolean includeFeatureId) {
        this.includeFeatureId = includeFeatureId;
    }

    public boolean isIncludeFeatureId() {
        return includeFeatureId;
    }

    public void setIncludeTypeOnFeature(boolean includeTypeOnFeature) {
        this.includeTypeOnFeature = includeTypeOnFeature;
    }

    public boolean isIncludeTypeOnFeature() {
        return includeTypeOnFeature;
    }

    public void setIncludeTypeOnCollection(boolean includeTypeOnCollection) {
        this.includeTypeOnCollection = includeTypeOnCollection;
    }

    public boolean isIncludeTypeOnCollection() {
        return includeTypeOnCollection;
    }

    public void setIncludeCoordRefSysOnCollection(boolean includeCoordRefSysOnCollection) {
        this.includeCoordRefSysOnCollection = includeCoordRefSysOnCollection;
    }

    public boolean isIncludeCoordRefSysOnCollection() {
        return includeCoordRefSysOnCollection;
    }

    public void setIncludeCoordRefSysOnFeature(boolean includeCoordRefSysOnFeature) {
        this.includeCoordRefSysOnFeature = includeCoordRefSysOnFeature;
    }

    public boolean isIncludeCoordRefSysOnFeature() {
        return includeCoordRefSysOnFeature;
    }

    public void setIncludeCoordRefSysOnGeometry(boolean includeCoordRefSysOnGeometry) {
        this.includeCoordRefSysOnGeometry = includeCoordRefSysOnGeometry;
    }

    public boolean isIncludeCoordRefSysOnGeometry() {
        return includeCoordRefSysOnGeometry;
    }

    public GeoJSONFeature transform(Feature feature) throws DataStoreException {
        final FeatureType type = feature.getType();

        final GeoJSONFeature gf = new GeoJSONFeature();

        if (includeTypeOnFeature) {
            gf.setFeatureType(getTypeNames(type));
        }

        Object id;
        try {
            id = feature.getPropertyValue(AttributeConvention.IDENTIFIER);
        } catch (PropertyNotFoundException e) {
            id = null;
        }
        if (includeFeatureId && id != null) {
            gf.setId(id);
        }
        if (includeCoordRefSysOnFeature) {
            gf.setCoordRefSys(getCoordRefSys(type));
        }

        Object geom;
        try {
            geom = feature.getPropertyValue(AttributeConvention.GEOMETRY);
        } catch (PropertyNotFoundException e) {
            geom = null;
        }
        if (geom instanceof Geometry g) {
            GeoJSONGeometry json = transform(g);
            gf.setGeometry(json);

            if (bboxOnFeature) {
                Envelope env = g.getEnvelopeInternal();
                if (env != null && !env.isNull()) {
                    final List<Double> bbox = new ArrayList<>();
                    bbox.add(env.getMinX());
                    bbox.add(env.getMinY());
                    bbox.add(env.getMaxX());
                    bbox.add(env.getMaxY());
                    gf.setBbox(bbox);
                }
            }
        }

        final Map<String,Object> properties = new LinkedHashMap();
        gf.setProperties(properties);
        for (PropertyType pt : type.getProperties(true)) {
            if (AttributeConvention.contains(pt.getName())) continue;
            if (pt instanceof AttributeType) {
                final String name = pt.getName().toString();
                final Object value = feature.getPropertyValue(name);
                properties.put(name, value);
            }
        }

        return gf;
    }

    public GeoJSONFeatureCollection transform(FeatureSet features) throws DataStoreException {
        final FeatureType type = features.getType();

        final GeoJSONFeatureCollection cl = new GeoJSONFeatureCollection();
        final List<GeoJSONFeature> lst = new ArrayList<>();
        try (Stream<Feature> stream = features.features(false)) {
            final Iterator<Feature> ite = stream.iterator();
            while (ite.hasNext()) {
                lst.add(transform(ite.next()));
            }
        }

        cl.setFeatures(lst);

        if (includeTypeOnCollection) {
            cl.setFeatureType(getTypeNames(type));
        }
        if (includeCoordRefSysOnCollection) {
            cl.setCoordRefSys(getCoordRefSys(type));
        }
        if (bboxOnCollection) {
            org.opengis.geometry.Envelope env = FeatureStoreUtilities.getEnvelope(features);
            if (env != null) {
                final List<Double> bbox = new ArrayList<>();
                bbox.add(env.getMinimum(0));
                bbox.add(env.getMinimum(1));
                bbox.add(env.getMaximum(0));
                bbox.add(env.getMaximum(1));
                cl.setBbox(bbox);
            }
        }

        return cl;
    }

    public GeoJSONGeometry transform(Geometry geom) throws DataStoreException {

        final GeoJSONGeometry res;
        if (geom instanceof Point cdt) {
            final GeoJSONPoint json = new GeoJSONPoint();
            CoordinateSequence cs = cdt.getCoordinateSequence();
            json.setCoordinates(toList(cs).get(0));
            res = json;
        } else if (geom instanceof LineString cdt) {
            final GeoJSONLineString json = new GeoJSONLineString();
            CoordinateSequence cs = cdt.getCoordinateSequence();
            json.setCoordinates(toList(cs));
            res = json;
        } else if (geom instanceof Polygon cdt) {
            final GeoJSONPolygon json = new GeoJSONPolygon();
            final List<List<List<Double>>> lst = new ArrayList<>();
            lst.add(toList(cdt.getExteriorRing().getCoordinateSequence()));
            for (int i = 0, n = cdt.getNumInteriorRing(); i < n; i++) {
                lst.add(toList(cdt.getInteriorRingN(i).getCoordinateSequence()));
            }
            json.setCoordinates(lst);
            res = json;
        } else if (geom instanceof MultiPoint cdt) {
            final GeoJSONMultiPoint json = new GeoJSONMultiPoint();
            final List<List<Double>> lst = new ArrayList<>();
            for (int i = 0, n = cdt.getNumGeometries(); i < n; i++) {
                lst.add(toList(((Point)cdt.getGeometryN(i)).getCoordinateSequence()).get(0));
            }
            json.setCoordinates(lst);
            res = json;
        } else if (geom instanceof MultiLineString cdt) {
            final GeoJSONMultiLineString json = new GeoJSONMultiLineString();
            final List<List<List<Double>>> lst = new ArrayList<>();
            for (int i = 0, n = cdt.getNumGeometries(); i < n; i++) {
                lst.add(toList(((LineString)cdt.getGeometryN(i)).getCoordinateSequence()));
            }
            json.setCoordinates(lst);
            res = json;
        } else if (geom instanceof MultiPolygon cdt) {
            final GeoJSONMultiPolygon json = new GeoJSONMultiPolygon();
            final List<List<List<List<Double>>>> lst = new ArrayList<>();
            for (int i = 0, n = cdt.getNumGeometries(); i < n; i++) {
                final Polygon pl = (Polygon) cdt.getGeometryN(i);
                final List<List<List<Double>>> sublst = new ArrayList<>();
                sublst.add(toList(pl.getExteriorRing().getCoordinateSequence()));
                for (int j = 0, k = pl.getNumInteriorRing(); j < k; j++) {
                    sublst.add(toList(pl.getInteriorRingN(j).getCoordinateSequence()));
                }
                lst.add(sublst);
            }
            json.setCoordinates(lst);
            res = json;
        } else if (geom instanceof GeometryCollection cdt) {
            final GeoJSONGeometryCollection json = new GeoJSONGeometryCollection();
            final List<GeoJSONGeometry> geometries = new ArrayList<>();
            for (int i = 0, n = cdt.getNumGeometries(); i < n; i++) {
                final Geometry pl = (Geometry) cdt.getGeometryN(i);
                geometries.add(transform(pl));
            }
            json.setGeometries(geometries);
            res = json;
        } else {
            throw new DataStoreException("Geometry not supported yet " + geom);
        }

        if (bboxOnGeometry) {
            Envelope env = geom.getEnvelopeInternal();
            if (env != null && !env.isNull()) {
                final List<Double> bbox = new ArrayList<>();
                bbox.add(env.getMinX());
                bbox.add(env.getMinY());
                bbox.add(env.getMaxX());
                bbox.add(env.getMaxY());
                res.setBbox(bbox);
            }
        }

        if (includeCoordRefSysOnGeometry) {
            Object userData = geom.getUserData();
            if (userData instanceof CoordinateReferenceSystem crs) {
                res.setCoordRefSys(getCoordRefSys(crs));
            }
        }

        return res;
    }

    private List<List<Double>> toList(CoordinateSequence cs) throws DataStoreException {
        final int dim = cs.getDimension();
        final int size = cs.size();
        final List<List<Double>> lst = new ArrayList<>(size);
        switch (dim) {
            case 2 : {
                for (int i = 0; i < size; i++) {
                    lst.add(List.of(cs.getOrdinate(i, 0), cs.getOrdinate(i, 1)));
                }
            } break;
            case 3 : {
                for (int i = 0; i < size; i++) {
                    lst.add(List.of(cs.getOrdinate(i, 0), cs.getOrdinate(i, 1), cs.getOrdinate(i, 2)));
                }
            } break;
            case 4 : {
                for (int i = 0; i < size; i++) {
                    lst.add(List.of(cs.getOrdinate(i, 0), cs.getOrdinate(i, 1), cs.getOrdinate(i, 2), cs.getOrdinate(i, 3)));
                }
            } break;
            default: throw new DataStoreException("Unexpected coordinate sequence dimension " + dim);
        }
        return lst;
    }

    private static List<String> getTypeNames(FeatureType type) {
        final String name = type.getName().toString();
        final Set<? extends FeatureType> superTypes = type.getSuperTypes();
        if (superTypes.isEmpty()) {
            return List.of(name);
        } else {
            final List<String> names = new ArrayList<>(1 + superTypes.size());
            names.add(name);
            for (FeatureType ft : superTypes) {
                names.add(ft.getName().toString());
            }
            return names;
        }
    }

    private static JSONFGCoordRefSys getCoordRefSys(FeatureType ft) {
        final CoordinateReferenceSystem crs = FeatureExt.getCRS(ft);
        return getCoordRefSys(crs);
    }

    private static JSONFGCoordRefSys getCoordRefSys(CoordinateReferenceSystem crs) {
        if (crs == null) return null;

        final JSONFGCoordRefSys cref = new JSONFGCoordRefSys();
        cref.setHref(IdentifiedObjects.getIdentifierOrName(crs));
        return cref;
    }
}
