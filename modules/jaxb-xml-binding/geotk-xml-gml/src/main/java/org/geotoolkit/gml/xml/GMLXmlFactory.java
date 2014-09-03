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

package org.geotoolkit.gml.xml;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.Position;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GMLXmlFactory {
 
    public static Point buildPoint(final String version, final String id, final org.opengis.geometry.DirectPosition pos) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.PointType(id, pos);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.PointType(id, pos);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static org.opengis.geometry.DirectPosition buildDirectPosition(final String version, final String srsName, final Integer srsDimension, final List<Double> value) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.DirectPositionType(srsName, srsDimension, value);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.DirectPositionType(srsName, srsDimension, value);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static MultiPoint buildMultiPoint(final String version, final List<Point> points, final String srsName) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.PointPropertyType> pointList = new ArrayList<org.geotoolkit.gml.xml.v321.PointPropertyType>();
            for (Point pt : points) {
                pointList.add(new org.geotoolkit.gml.xml.v321.PointPropertyType((org.geotoolkit.gml.xml.v321.PointType)pt));
            }
            return new org.geotoolkit.gml.xml.v321.MultiPointType(srsName, pointList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.PointPropertyType> pointList = new ArrayList<org.geotoolkit.gml.xml.v311.PointPropertyType>();
            for (Point pt : points) {
                pointList.add(new org.geotoolkit.gml.xml.v311.PointPropertyType((org.geotoolkit.gml.xml.v311.PointType)pt));
            }
            return new org.geotoolkit.gml.xml.v311.MultiPointType(srsName, pointList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static LineString buildLineString(final String version, final String id, final String srsName, final List<org.opengis.geometry.DirectPosition> pos) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.LineStringType(id, srsName, pos);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.LineStringType(id, srsName, pos);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static AbstractGeometricAggregate buildMultiLineString(final String version, final List<LineString> lines, final String srsName) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.CurvePropertyType> lineList = new ArrayList<org.geotoolkit.gml.xml.v321.CurvePropertyType>();
            for (LineString ls : lines) {
                lineList.add(new org.geotoolkit.gml.xml.v321.CurvePropertyType((org.geotoolkit.gml.xml.v321.LineStringType)ls));
            }
            return new org.geotoolkit.gml.xml.v321.MultiCurveType(srsName, lineList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.LineStringPropertyType> lineList = new ArrayList<org.geotoolkit.gml.xml.v311.LineStringPropertyType>();
            for (LineString ls : lines) {
                lineList.add(new org.geotoolkit.gml.xml.v311.LineStringPropertyType((org.geotoolkit.gml.xml.v311.LineStringType)ls));
            }
            return new org.geotoolkit.gml.xml.v311.MultiLineStringType(srsName, lineList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static AbstractGeometricAggregate buildMultiPolygon(final String version, final List<Polygon> polygons, final String srsName) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.SurfacePropertyType> polyList = new ArrayList<org.geotoolkit.gml.xml.v321.SurfacePropertyType>();
            for (Polygon p : polygons) {
                polyList.add(new org.geotoolkit.gml.xml.v321.SurfacePropertyType((org.geotoolkit.gml.xml.v321.PolygonType) p));
            }
            return new org.geotoolkit.gml.xml.v321.MultiSurfaceType(srsName, polyList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.PolygonPropertyType> polyList = new ArrayList<org.geotoolkit.gml.xml.v311.PolygonPropertyType>();
            for (Polygon p : polygons) {
                polyList.add(new org.geotoolkit.gml.xml.v311.PolygonPropertyType((org.geotoolkit.gml.xml.v311.PolygonType)p));
            }
            return new org.geotoolkit.gml.xml.v311.MultiPolygonType(srsName, polyList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static LinearRing buildLinearRing(final String version,  final List<Double> coordList, final String srsName) {
        if ("3.2.1".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v321.DirectPositionListType(coordList);
            return new org.geotoolkit.gml.xml.v321.LinearRingType(srsName, dpList);
        } else if ("3.1.1".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v311.DirectPositionListType(coordList);
            return new org.geotoolkit.gml.xml.v311.LinearRingType(srsName, dpList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Polygon buildPolygon(final String version, final AbstractRing gmlExterior, final List<AbstractRing> gmlInterior, final String srsName) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.AbstractRingType> interiors = new ArrayList<>();
            if (gmlInterior != null) {
                for (AbstractRing ar : gmlInterior) {
                    if (ar != null && !(ar instanceof org.geotoolkit.gml.xml.v321.AbstractRingType)) {
                        throw new IllegalArgumentException("unexpected gml version for interior ring.(" + ar.getClass().getName()+ ")");
                    } else if (ar != null) {
                        interiors.add((org.geotoolkit.gml.xml.v321.AbstractRingType) ar);
                    }
                }
            }
            return new org.geotoolkit.gml.xml.v321.PolygonType(srsName, (org.geotoolkit.gml.xml.v321.AbstractRingType) gmlExterior, interiors);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.AbstractRingType> interiors = new ArrayList<>();
            if (gmlInterior != null) {
                for (AbstractRing ar : gmlInterior) {
                    if (ar != null && !(ar instanceof org.geotoolkit.gml.xml.v311.AbstractRingType)) {
                        throw new IllegalArgumentException("unexpected gml version for interior ring.");
                    } else if (ar != null) {
                        interiors.add((org.geotoolkit.gml.xml.v311.AbstractRingType)ar);
                    }
                }
            }
            return new org.geotoolkit.gml.xml.v311.PolygonType(srsName, (org.geotoolkit.gml.xml.v311.AbstractRingType)gmlExterior, interiors);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Envelope buildEnvelope(final String version, final String id, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if ("3.2.1".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionType lowerCorner = new org.geotoolkit.gml.xml.v321.DirectPositionType(minx, miny);
            final org.geotoolkit.gml.xml.v321.DirectPositionType upperCorner = new org.geotoolkit.gml.xml.v321.DirectPositionType(maxx, maxy);
            return new org.geotoolkit.gml.xml.v321.EnvelopeType(lowerCorner, upperCorner, srs);
        } else if ("3.1.1".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionType lowerCorner = new org.geotoolkit.gml.xml.v311.DirectPositionType(minx, miny);
            final org.geotoolkit.gml.xml.v311.DirectPositionType upperCorner = new org.geotoolkit.gml.xml.v311.DirectPositionType(maxx, maxy);
            return new org.geotoolkit.gml.xml.v311.EnvelopeType(id, lowerCorner, upperCorner, srs);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Period createTimePeriod(final String version, final String id, final String dateBegin, final String dateEnd) {
        if ("3.2.1".equals(version)) {
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType(id, dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType(id, dateBegin, dateEnd);
            }
        } else if ("3.1.1".equals(version)) {
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType(id, dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType(id, dateBegin, dateEnd);
            }
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Period createTimePeriod(final String version, final Timestamp dateBegin, final Timestamp dateEnd) {
        if ("3.2.1".equals(version)) {
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType(dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType(dateBegin, dateEnd);
            }
        } else if ("3.1.1".equals(version)) {
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType(dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType(dateBegin, dateEnd);
            }
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Period createTimePeriod(final String version, final Position dateBegin, final Position dateEnd) {
        if ("3.2.1".equals(version)) {
            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date begin.");
            }
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType((org.geotoolkit.gml.xml.v321.TimePositionType)dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v321.TimePeriodType((org.geotoolkit.gml.xml.v321.TimePositionType)dateBegin, 
                                                                      (org.geotoolkit.gml.xml.v321.TimePositionType)dateEnd);
            }
        } else if ("3.1.1".equals(version)) {
            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date begin.");
            }
            if (dateEnd == null) {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType((org.geotoolkit.gml.xml.v311.TimePositionType)dateBegin);
            } else {
                return new org.geotoolkit.gml.xml.v311.TimePeriodType((org.geotoolkit.gml.xml.v311.TimePositionType)dateBegin, 
                                                                      (org.geotoolkit.gml.xml.v311.TimePositionType)dateEnd);
            }
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Instant createTimeInstant(final String version, final Position date) {
        if ("3.2.1".equals(version)) {
            if (date != null && !(date instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date position.");
            }
            return new org.geotoolkit.gml.xml.v321.TimeInstantType((org.geotoolkit.gml.xml.v321.TimePositionType)date);
            
        } else if ("3.1.1".equals(version)) {
            if (date != null && !(date instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            return new org.geotoolkit.gml.xml.v311.TimeInstantType((org.geotoolkit.gml.xml.v311.TimePositionType)date);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Instant createTimeInstant(final String version, final String id, final String date) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.TimeInstantType(id, date);
            
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.TimeInstantType(id, date);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Instant createTimeInstant(final String version, final Timestamp date) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.TimeInstantType(date);
            
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.TimeInstantType(date);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Period createTimePeriod(final String version, final TimeIndeterminateValueType dateBegin, final Position dateEnd) {
        if ("3.2.1".equals(version)) {
            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
                    throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            return new org.geotoolkit.gml.xml.v321.TimePeriodType(dateBegin, (org.geotoolkit.gml.xml.v321.TimePositionType)dateEnd);
            
        } else if ("3.1.1".equals(version)) {
            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
                    throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            return new org.geotoolkit.gml.xml.v311.TimePeriodType(dateBegin, (org.geotoolkit.gml.xml.v311.TimePositionType)dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static Period createTimePeriod(final String version, final Position dateBegin, final TimeIndeterminateValueType dateEnd) {
        if ("3.2.1".equals(version)) {
            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date begin.");
            }
            return new org.geotoolkit.gml.xml.v321.TimePeriodType((org.geotoolkit.gml.xml.v321.TimePositionType)dateBegin, dateEnd);
            
        } else if ("3.1.1".equals(version)) {
            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
                throw new IllegalArgumentException("unexpected gml version for date end.");
            }
            return new org.geotoolkit.gml.xml.v311.TimePeriodType((org.geotoolkit.gml.xml.v311.TimePositionType)dateBegin, dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
    
    public static FeatureCollection createFeatureCollection(final String version, final String id, final String name, final String description, 
            final List<FeatureProperty> features) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.FeaturePropertyType> features321 = new ArrayList<org.geotoolkit.gml.xml.v321.FeaturePropertyType>();
            if (features != null) {
                for (FeatureProperty fp : features) {
                    if (fp != null && !(fp instanceof org.geotoolkit.gml.xml.v321.FeaturePropertyType)) {
                        throw new IllegalArgumentException("unexpected gml version for feature property.");
                    } else if (fp != null) {
                        features321.add((org.geotoolkit.gml.xml.v321.FeaturePropertyType)fp);
                    }
                }
            }
            return new org.geotoolkit.gml.xml.v321.FeatureCollectionType(id, name, description, features321);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.FeaturePropertyType> features311 = new ArrayList<org.geotoolkit.gml.xml.v311.FeaturePropertyType>();
            if (features != null) {
                for (FeatureProperty fp : features) {
                    if (fp != null && !(fp instanceof org.geotoolkit.gml.xml.v311.FeaturePropertyType)) {
                        throw new IllegalArgumentException("unexpected gml version for feature property.");
                    } else if (fp != null) {
                        features311.add((org.geotoolkit.gml.xml.v311.FeaturePropertyType)fp);
                    }
                }
            }
            return new org.geotoolkit.gml.xml.v311.FeatureCollectionType(id, name, description, features311);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }
}
