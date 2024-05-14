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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.gml.xml.v311.CoordinatesType;
import org.opengis.temporal.Period;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GMLXmlFactory {

    public static Point buildPoint(final String version, final String id, final String crsName, final org.opengis.geometry.DirectPosition pos) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.PointType(id, crsName, pos);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.PointType(id, crsName, pos);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static Point buildPoint(final String version, final String id, final org.opengis.geometry.DirectPosition pos) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.PointType(id, pos, false);
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
            final List<org.geotoolkit.gml.xml.v321.PointPropertyType> pointList = new ArrayList<>();
            for (Point pt : points) {
                pointList.add(new org.geotoolkit.gml.xml.v321.PointPropertyType((org.geotoolkit.gml.xml.v321.PointType)pt));
            }
            return new org.geotoolkit.gml.xml.v321.MultiPointType(srsName, pointList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.PointPropertyType> pointList = new ArrayList<>();
            for (Point pt : points) {
                pointList.add(new org.geotoolkit.gml.xml.v311.PointPropertyType((org.geotoolkit.gml.xml.v311.PointType)pt));
            }
            return new org.geotoolkit.gml.xml.v311.MultiPointType(srsName, pointList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static LineString buildLineString(final String version, final List<Double> coordList, final String srsName, final Integer srsDimension) {
        if ("3.2.1".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v321.DirectPositionListType(coordList);
            org.geotoolkit.gml.xml.v321.LineStringType ls = new org.geotoolkit.gml.xml.v321.LineStringType((org.geotoolkit.gml.xml.v321.CoordinatesType) null);
            ls.setSrsName(srsName);
            ls.setSrsDimension(srsDimension);
            ls.setPosList(dpList);
            return ls;
        } else if ("3.1.1".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v311.DirectPositionListType(coordList);
            org.geotoolkit.gml.xml.v311.LineStringType ls = new org.geotoolkit.gml.xml.v311.LineStringType((CoordinatesType)null);
            ls.setSrsName(srsName);
            ls.setSrsDimension(srsDimension);
            ls.setPosList(dpList);
            return ls;
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
            final List<org.geotoolkit.gml.xml.v321.CurvePropertyType> lineList = new ArrayList<>();
            for (LineString ls : lines) {
                lineList.add(new org.geotoolkit.gml.xml.v321.CurvePropertyType((org.geotoolkit.gml.xml.v321.LineStringType)ls));
            }
            return new org.geotoolkit.gml.xml.v321.MultiCurveType(srsName, lineList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.LineStringPropertyType> lineList = new ArrayList<>();
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
            final List<org.geotoolkit.gml.xml.v321.SurfacePropertyType> polyList = new ArrayList<>();
            for (Polygon p : polygons) {
                polyList.add(new org.geotoolkit.gml.xml.v321.SurfacePropertyType((org.geotoolkit.gml.xml.v321.PolygonType) p));
            }
            return new org.geotoolkit.gml.xml.v321.MultiSurfaceType(srsName, polyList);
        } else if ("3.1.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v311.PolygonPropertyType> polyList = new ArrayList<>();
            for (Polygon p : polygons) {
                polyList.add(new org.geotoolkit.gml.xml.v311.PolygonPropertyType((org.geotoolkit.gml.xml.v311.PolygonType)p));
            }
            return new org.geotoolkit.gml.xml.v311.MultiPolygonType(srsName, polyList);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static LinearRing buildLinearRing(final String version,  final List<Double> coordList, final String srsName) {
        return buildLinearRing(version, coordList, srsName, null);
    }

    public static LinearRing buildLinearRing(final String version,  final List<Double> coordList, final String srsName, Integer srsDimension) {
        if ("3.2.1".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v321.DirectPositionListType(coordList);
            // Replaced previous version that omitted srs name, because there's been a corrigendum in GML 3.2.2.
            // The problem in 3.2.1 was that LinearRing extended AbstractRing, which did not extend AbstractCurve. That
            // was an error, and a correction has been added in corrigendum 3.2.2.
            final org.geotoolkit.gml.xml.v321.LinearRingType lr = new org.geotoolkit.gml.xml.v321.LinearRingType(srsName, dpList);
            lr.setSrsDimension(srsDimension);
            lr.setSrsName(srsName);
            return lr;
        } else if ("3.1.1".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionListType dpList = new org.geotoolkit.gml.xml.v311.DirectPositionListType(coordList);
            final org.geotoolkit.gml.xml.v311.LinearRingType lr = new org.geotoolkit.gml.xml.v311.LinearRingType(srsName, dpList);
            lr.setSrsDimension(srsDimension);
            lr.setSrsName(srsName);
            return lr;
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static Polygon buildPolygon(final String version, final AbstractRing gmlExterior, final List<AbstractRing> gmlInterior, final String srsName) {
        return buildPolygon(version, gmlExterior, gmlInterior, srsName, null);
    }

    public static Polygon buildPolygon(final String version, final AbstractRing gmlExterior, final List<AbstractRing> gmlInterior, final String srsName, final Integer srsDimension) {
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
            org.geotoolkit.gml.xml.v321.PolygonType po = new org.geotoolkit.gml.xml.v321.PolygonType(srsName, (org.geotoolkit.gml.xml.v321.AbstractRingType) gmlExterior, interiors);
            po.setSrsDimension(srsDimension);
            return po;
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
            org.geotoolkit.gml.xml.v311.PolygonType po = new org.geotoolkit.gml.xml.v311.PolygonType(srsName, (org.geotoolkit.gml.xml.v311.AbstractRingType)gmlExterior, interiors);
            po.setSrsDimension(srsDimension);
            return po;
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

    public static Envelope buildEnvelope(final String version, final org.opengis.geometry.Envelope envelope) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.EnvelopeType(envelope);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.EnvelopeType(envelope);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static BoundingShape buildBoundingShape(final String version, final org.opengis.geometry.Envelope envelope) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.BoundingShapeType(envelope);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.BoundingShapeType(envelope);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static GMLPeriod createTimePeriod(final String version, final String id, final Period p) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.TimePeriodType(id, p);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.TimePeriodType(id, p);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static GMLPeriod createTimePeriod(final String version, final String id, final String dateBegin, final String dateEnd) {
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

    @Deprecated
    public static GMLPeriod createTimePeriod(final String version, final Date dateBegin, final Date dateEnd) {
        return createTimePeriod(version, null, dateBegin, dateEnd);
    }

    @Deprecated
    public static GMLPeriod createTimePeriod(final String version, final String id, final Date dateBegin, final Date dateEnd) {
        return createTimePeriod(version, id, instant(dateBegin), instant(dateEnd));
    }

    @Deprecated
    private static Instant instant(Date date) {
        return (date != null) ? date.toInstant() : null;
    }

    public static GMLPeriod createTimePeriod(final String version, final String id, final Instant dateBegin, final Instant dateEnd) {
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

    public static GMLPeriod createTimePeriod(final String version, final AbstractTimePosition dateBegin, final AbstractTimePosition dateEnd) {
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
                return new org.geotoolkit.gml.xml.v311.TimePeriodType(null,
                       new org.geotoolkit.gml.xml.v311.TimeInstantType((org.geotoolkit.gml.xml.v311.TimePositionType)dateBegin),
                       new org.geotoolkit.gml.xml.v311.TimeInstantType((org.geotoolkit.gml.xml.v311.TimePositionType)dateEnd));
            }
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static GMLInstant createTimeInstant(final String version, final AbstractTimePosition date) {
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

    public static GMLInstant createTimeInstant(final String version, final String id, final String date) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.TimeInstantType(id, date);

        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.TimeInstantType(id, date);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    @Deprecated
    public static GMLInstant createTimeInstant(final String version, final Date date) {
        return createTimeInstant(version, null, date);
    }

    @Deprecated
    public static GMLInstant createTimeInstant(final String version, final String id, final Date date) {
        return createTimeInstant(version, id, instant(date));
    }

    public static GMLInstant createTimeInstant(final String version, final String id, final Instant date) {
        if ("3.2.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v321.TimeInstantType(id, date);
        } else if ("3.1.1".equals(version)) {
            return new org.geotoolkit.gml.xml.v311.TimeInstantType(id, date);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static GMLPeriod createTimePeriod(final String version, final TimeIndeterminateValueType dateBegin, final AbstractTimePosition dateEnd) {
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

    public static GMLPeriod createTimePeriod(final String version, final AbstractTimePosition dateBegin, final TimeIndeterminateValueType dateEnd) {
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

    @Deprecated
    public static GMLPeriod createTimePeriod(final String version, final TimeIndeterminateValueType dateBegin, final Date dateEnd) {
        return createTimePeriod(version, dateBegin, dateEnd.toInstant());
    }

    public static GMLPeriod createTimePeriod(final String version, final TimeIndeterminateValueType dateBegin, final Instant dateEnd) {
        if (dateEnd == null)
            throw new NullPointerException("dateEnd");
        if ("3.2.1".equals(version)) {
//            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
//                    throw new IllegalArgumentException("unexpected gml version for date end.");
//            }
            return new org.geotoolkit.gml.xml.v321.TimePeriodType(dateBegin, new org.geotoolkit.gml.xml.v321.TimePositionType(dateEnd));

        } else if ("3.1.1".equals(version)) {
//            if (dateEnd != null && !(dateEnd instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
//                    throw new IllegalArgumentException("unexpected gml version for date end.");
//            }
            return new org.geotoolkit.gml.xml.v311.TimePeriodType(dateBegin, new org.geotoolkit.gml.xml.v311.TimePositionType(dateEnd));
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    @Deprecated
    public static GMLPeriod createTimePeriod(final String version, final Date dateBegin, final TimeIndeterminateValueType dateEnd) {
        return createTimePeriod(version, dateBegin.toInstant(), dateEnd);
    }

    public static GMLPeriod createTimePeriod(final String version, final Instant dateBegin, final TimeIndeterminateValueType dateEnd) {
        if (dateBegin == null)
            throw new NullPointerException("dateBegin");
        if ("3.2.1".equals(version)) {
//            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v321.TimePositionType)) {
//                throw new IllegalArgumentException("unexpected gml version for date begin.");
//            }
            return new org.geotoolkit.gml.xml.v321.TimePeriodType(new org.geotoolkit.gml.xml.v321.TimePositionType(dateBegin), dateEnd);

        } else if ("3.1.1".equals(version)) {
//            if (dateBegin != null && !(dateBegin instanceof org.geotoolkit.gml.xml.v311.TimePositionType)) {
//                throw new IllegalArgumentException("unexpected gml version for date end.");
//            }
            return new org.geotoolkit.gml.xml.v311.TimePeriodType(new org.geotoolkit.gml.xml.v311.TimePositionType(dateBegin), dateEnd);
        } else {
            throw new IllegalArgumentException("unexpected gml version number:" + version);
        }
    }

    public static FeatureCollection createFeatureCollection(final String version, final String id, final String name, final String description,
            final List<FeatureProperty> features) {
        if ("3.2.1".equals(version)) {
            final List<org.geotoolkit.gml.xml.v321.FeaturePropertyType> features321 = new ArrayList<>();
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
            final List<org.geotoolkit.gml.xml.v311.FeaturePropertyType> features311 = new ArrayList<>();
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
