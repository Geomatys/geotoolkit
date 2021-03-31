package org.geotoolkit.ogc.xml;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.measure.Quantity;
import javax.xml.bind.JAXBElement;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.gml.JTStoGeometry;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.v321.CurveType;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.gml.xml.v321.LineStringType;
import org.geotoolkit.gml.xml.v321.MultiGeometryType;
import org.geotoolkit.gml.xml.v321.MultiCurveType;
import org.geotoolkit.gml.xml.v321.MultiPointType;
import org.geotoolkit.gml.xml.v321.MultiSurfaceType;
import org.geotoolkit.gml.xml.v321.PointType;
import org.geotoolkit.gml.xml.v321.PolygonType;
import org.geotoolkit.gml.xml.v321.SurfaceType;
import org.geotoolkit.ogc.xml.v200.AbstractIdType;
import org.geotoolkit.ogc.xml.v200.FunctionType;
import org.geotoolkit.ogc.xml.v200.AndType;
import org.geotoolkit.ogc.xml.v200.BBOXType;
import org.geotoolkit.ogc.xml.v200.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v200.ContainsType;
import org.geotoolkit.ogc.xml.v200.CrossesType;
import org.geotoolkit.ogc.xml.v200.DWithinType;
import org.geotoolkit.ogc.xml.v200.DisjointType;
import org.geotoolkit.ogc.xml.v200.EqualsType;
import org.geotoolkit.ogc.xml.v200.FilterType;
import org.geotoolkit.ogc.xml.v200.IntersectsType;
import org.geotoolkit.ogc.xml.v200.LiteralType;
import org.geotoolkit.ogc.xml.v200.LogicOpsType;
import org.geotoolkit.ogc.xml.v200.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v200.NotType;
import org.geotoolkit.ogc.xml.v200.OrType;
import org.geotoolkit.ogc.xml.v200.OverlapsType;
import org.geotoolkit.ogc.xml.v200.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v200.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.v200.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.v200.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.v200.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.v200.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.v200.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v200.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.v200.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v200.SpatialOpsType;
import org.geotoolkit.ogc.xml.v200.TouchesType;
import org.geotoolkit.ogc.xml.v200.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v200.WithinType;
import org.geotoolkit.ogc.xml.v200.ObjectFactory;
import org.geotoolkit.ogc.xml.v200.ResourceIdType;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.ResourceId;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.NullOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.DistanceOperator;
import org.opengis.filter.DistanceOperatorName;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.CodeList;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterToOGC200Converter implements FilterToOGCConverter<FilterType> {

    private final ObjectFactory ogc_factory;
    private final org.geotoolkit.gml.xml.v321.ObjectFactory gml_factory;
    private final FilterFactory FF = org.geotoolkit.filter.FilterUtilities.FF;

    public FilterToOGC200Converter() {
        this.ogc_factory = new ObjectFactory();
        this.gml_factory = new org.geotoolkit.gml.xml.v321.ObjectFactory();
    }

    public JAXBElement visit(Filter filter) {
        if (filter.equals(Filter.include()) || filter.equals(Filter.exclude())) {
            return null;
        }
        final CodeList<?> type = filter.getOperatorType();
        if (filter instanceof BetweenComparisonOperator) {
            final BetweenComparisonOperator pib = (BetweenComparisonOperator) filter;
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));

            final PropertyIsBetweenType bot = new PropertyIsBetweenType();
            bot.setExpression(extract(pib.getExpression()));
            bot.setLowerBoundary(lbt);
            bot.setUpperBoundary(ubt);
            return ogc_factory.createPropertyIsBetween(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsEqualToType bot = new PropertyIsEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsEqualTo(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanType bot = new PropertyIsGreaterThanType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsGreaterThan(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_GREATER_THAN_OR_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanOrEqualToType bot = new PropertyIsGreaterThanOrEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanType bot = new PropertyIsLessThanType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsLessThan(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_LESS_THAN_OR_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanOrEqualToType bot = new PropertyIsLessThanOrEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(bot);
        } else if (filter instanceof LikeOperator) {
            final LikeOperator pis = (LikeOperator) filter;
            final List<Expression> expressions = filter.getExpressions();
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscape(String.valueOf(pis.getEscapeChar()));
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.setContent(((Literal) expressions.get(1)).getValue());
            bot.getElements().add(ogc_factory.createLiteral(lt));
            final Expression expression = expressions.get(0);
            if (!(expression instanceof ValueReference)) {
                throw new IllegalArgumentException("LikeOperator can support ValueReference only, but was a " + expression);
            }
            bot.getElements().add(0, extract(expression));
            bot.setSingleChar(String.valueOf(pis.getSingleChar()));
            bot.setWildCard(String.valueOf(pis.getWildCard()));
            return ogc_factory.createPropertyIsLike(bot);
        } else if (type == ComparisonOperatorName.PROPERTY_IS_NOT_EQUAL_TO) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsNotEqualToType bot = new PropertyIsNotEqualToType();
            bot.getExpression().add(extract(pit.getOperand1()));
            bot.getExpression().add(extract(pit.getOperand2()));
            return ogc_factory.createPropertyIsNotEqualTo(bot);
        } else if (filter instanceof NullOperator) {
            final NullOperator pis = (NullOperator) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            bot.setExpression(extract((Expression) pis.getExpressions().get(0)));
            return ogc_factory.createPropertyIsNull(bot);
        } else if (type == LogicalOperatorName.AND) {
            final LogicalOperator and = (LogicalOperator) filter;
            final List<JAXBElement> lot = new ArrayList<>();
            for (final Filter f : (List<Filter>) and.getOperands()) {
                final JAXBElement<?> ele = visit(f);
                if (ele != null && ele.getValue() instanceof LogicOpsType) {
                    lot.add(ele);
                }
            }
            return ogc_factory.createAnd(new AndType(lot.toArray()));
        } else if (type == LogicalOperatorName.OR) {
            final LogicalOperator or = (LogicalOperator) filter;
            final List<JAXBElement> lot = new ArrayList<>();
            for (final Filter f : (List<Filter>) or.getOperands()) {
                final JAXBElement subFilter = visit(f);
                if (subFilter != null) {
                    lot.add(subFilter);
                }
            }
            return ogc_factory.createOr(new OrType(lot.toArray()));
        } else if (type == LogicalOperatorName.NOT) {
            final LogicalOperator not = (LogicalOperator) filter;
            final JAXBElement<?> sf = visit((Filter) not.getOperands().get(0));
            //should not happen
            return ogc_factory.createNot(new NotType(sf));
        } else if (filter instanceof ResourceId) {
            throw new IllegalArgumentException("Not parsed yet : " + filter);
        } else if (type == SpatialOperatorName.BBOX) {
            final BBOX bbox = BBOX.wrap((BinarySpatialOperator) filter);
            final Expression left = bbox.getOperand1();
            final Expression right = bbox.getOperand2();
            final String property;
            final double minx;
            final double maxx;
            final double miny;
            final double maxy;
            String srs;
            if (left instanceof ValueReference) {
                property = ((ValueReference) left).getXPath();
                final Object objGeom = ((Literal) right).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
                        if (srs == null) {
                            srs = ReferencingUtilities.lookupIdentifier(env.getCoordinateReferenceSystem(), true);
                        }
                    } catch (FactoryException ex) {
                        throw new IllegalArgumentException("invalid bbox element : " + filter + " " + ex.getMessage(), ex);
                    }
                } else if (objGeom instanceof Geometry) {
                    final Geometry geom = (Geometry) objGeom;
                    final Envelope env = geom.getEnvelopeInternal();
                    minx = env.getMinX();
                    maxx = env.getMaxX();
                    miny = env.getMinY();
                    maxy = env.getMaxY();
                    srs = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                } else {
                    throw new IllegalArgumentException("invalid bbox element : " + filter);
                }
            } else if (right instanceof ValueReference) {
                property = ((ValueReference) right).getXPath();
                final Object objGeom = ((Literal) left).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
                        if (srs == null) {
                            srs = ReferencingUtilities.lookupIdentifier(env.getCoordinateReferenceSystem(), true);
                        }
                    } catch (FactoryException ex) {
                        throw new IllegalArgumentException("invalid bbox element : " + filter + " " + ex.getMessage(), ex);
                    }
                } else if (objGeom instanceof Geometry) {
                    final Geometry geom = (Geometry) objGeom;
                    final Envelope env = geom.getEnvelopeInternal();
                    minx = env.getMinX();
                    maxx = env.getMaxX();
                    miny = env.getMinY();
                    maxy = env.getMaxY();
                    srs = SRIDGenerator.toSRS(geom.getSRID(), SRIDGenerator.Version.V1);
                } else {
                    throw new IllegalArgumentException("invalid bbox element : " + filter);
                }
            } else {
                throw new IllegalArgumentException("invalid bbox element : " + filter);
            }
            final BBOXType bbtype = new BBOXType(property, minx, miny, maxx, maxy, srs);
            return ogc_factory.createBBOX(bbtype);
        } else if (filter instanceof ResourceId) {
            final ValueReference n = FF.property(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            ResourceId idFilter = (ResourceId) filter;
            final String id = idFilter.getIdentifier();
            final ResourceIdType rId = ogc_factory.createResourceIdType();
            rId.setRid(id);
            return ogc_factory.createResourceId(rId);
        } else if (filter instanceof BinarySpatialOperator) {
            final BinarySpatialOperator spatialOp = (BinarySpatialOperator) filter;
            Expression exp1 = spatialOp.getOperand1();
            Expression exp2 = spatialOp.getOperand2();
            if (!(exp1 instanceof ValueReference)) {
                //flip order
                final Expression ex = exp1;
                exp1 = exp2;
                exp2 = ex;
            }
            if (!(exp1 instanceof ValueReference)) {
                throw new IllegalArgumentException("Filter can not be transformed in xml filter, "
                        + "expression are not of the required type ");
            } else if (!(exp2 instanceof Literal)) {
                throw new IllegalArgumentException("Spatial operator should use a literal object containing the filtering geometry.");
            }
            final JAXBElement pnt = extract(exp1);
            final String pName;
            if (pnt.getValue() instanceof String) {
                pName = (String) pnt.getValue();
            } else {
                throw new IllegalArgumentException("Property name cannot be cast to string.");
            }
            final JAXBElement<?> geometryExpression;
            final Object geom = ((Literal) exp2).getValue();
            if (geom instanceof Geometry) {
                final Geometry jts = (Geometry) geom;
                final String srid = SRIDGenerator.toSRS(jts.getSRID(), SRIDGenerator.Version.V1);
                CoordinateReferenceSystem crs;
                try {
                    crs = CRS.forCode(srid);
                } catch (Exception ex) {
                    Logging.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                    crs = null;
                }
                final AbstractGeometry gmlGeom;
                try {
                    gmlGeom = JTStoGeometry.toGML("3.2.1", jts);
                } catch (FactoryException ex) {
                    throw new IllegalArgumentException(ex);
                }
                // TODO use gml method to return any JAXBElement
                if (gmlGeom instanceof PointType) {
                    geometryExpression = gml_factory.createPoint((PointType) gmlGeom);
                } else if (gmlGeom instanceof CurveType) {
                    geometryExpression = gml_factory.createCurve((CurveType) gmlGeom);
                } else if (gmlGeom instanceof LineStringType) {
                    geometryExpression = gml_factory.createLineString((LineStringType) gmlGeom);
                } else if (gmlGeom instanceof PolygonType) {
                    geometryExpression = gml_factory.createPolygon((PolygonType) gmlGeom);
                } else if (gmlGeom instanceof MultiSurfaceType) {
                    geometryExpression = gml_factory.createMultiSurface((MultiSurfaceType) gmlGeom);
                } else if (gmlGeom instanceof MultiCurveType) {
                    geometryExpression = gml_factory.createMultiCurve((MultiCurveType) gmlGeom);
                } else if (gmlGeom instanceof MultiPointType) {
                    geometryExpression = gml_factory.createMultiPoint((MultiPointType) gmlGeom);
                } else if (gmlGeom instanceof MultiGeometryType) {
                    geometryExpression = gml_factory.createMultiGeometry((MultiGeometryType) gmlGeom);
                } else if (gmlGeom instanceof SurfaceType) {
                    geometryExpression = gml_factory.createPolyhedralSurface((SurfaceType) gmlGeom);
                } else {
                    throw new IllegalArgumentException("Unexpected Geometry type:" + gmlGeom.getClass().getName());
                }
            } else if (geom instanceof org.opengis.geometry.Geometry) {
                throw new UnsupportedOperationException("No valid ISO implementation avaiable for now.");
            } else if (geom instanceof org.opengis.geometry.Envelope) {
                final org.opengis.geometry.Envelope genv = (org.opengis.geometry.Envelope) geom;
                EnvelopeType ee = gml_factory.createEnvelopeType();
                ee.setSrsDimension(genv.getDimension());
                if (genv.getCoordinateReferenceSystem() != null) {
                    String urn;
                    try {
                        urn = IdentifiedObjects.lookupURN(genv.getCoordinateReferenceSystem(), null);
                    } catch (FactoryException ex) {
                        Logging.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                        urn = null;
                    }
                    if (urn == null) {
                        urn = IdentifiedObjects.getIdentifierOrName(genv.getCoordinateReferenceSystem());
                    }
                    if (urn != null) {
                        ee.setSrsName(urn);
                    }
                }
                ee.setLowerCorner(new DirectPositionType(genv.getLowerCorner(), false));
                ee.setUpperCorner(new DirectPositionType(genv.getUpperCorner(), false));
                geometryExpression = gml_factory.createEnvelope(ee);
            } else {
                throw new IllegalArgumentException("Type is neither geometric nor envelope.");
            }
            if (type == DistanceOperatorName.BEYOND) {
                throw new UnsupportedOperationException();
            } else if (type == SpatialOperatorName.CONTAINS) {
                return ogc_factory.createContains(new ContainsType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.CROSSES) {
                ogc_factory.createCrosses(new CrossesType(pName, geometryExpression));
            } else if (type == DistanceOperatorName.WITHIN) {
                Quantity q = ((DistanceOperator) filter).getDistance();
                return ogc_factory.createDWithin(new DWithinType(pName, geometryExpression, q.getValue().doubleValue(), q.getUnit().toString()));
            } else if (type == SpatialOperatorName.DISJOINT) {
                return ogc_factory.createDisjoint(new DisjointType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.EQUALS) {
                return ogc_factory.createEquals(new EqualsType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.INTERSECTS) {
                return ogc_factory.createIntersects(new IntersectsType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.OVERLAPS) {
                return ogc_factory.createOverlaps(new OverlapsType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.TOUCHES) {
                return ogc_factory.createTouches(new TouchesType(pName, geometryExpression));
            } else if (type == SpatialOperatorName.WITHIN) {
                return ogc_factory.createWithin(new WithinType(pName, geometryExpression));
            }
            throw new IllegalArgumentException("Unknown filter element : " + filter + " class :" + filter.getClass());
        }
        throw new IllegalArgumentException("Unknown filter element : " + filter + " class :" + filter.getClass());
    }

    public JAXBElement<?> extract(final Expression exp) {
        final JAXBElement<?> jax;
        final List<Expression<? super Object, ?>> parameters = exp.getParameters();
        switch (exp.getFunctionName().tip().toString()) {
            case "Literal": {
                final LiteralType literal = ogc_factory.createLiteralType();
                Object val = ((Literal) exp).getValue();
                if (val instanceof Color) {
                    val = FilterUtilities.toString((Color)val);
                }
                literal.setContent(val == null? null : val.toString());
                jax = ogc_factory.createLiteral(literal);
                break;
            }
            case "Multiply": {
                final FunctionType function = convert(exp);
                function.setName(OGCJAXBStatics.EXPRESSION_MUL);
                jax = ogc_factory.createFunction(function);
                break;
            }
            case "Add": {
                final FunctionType function = convert(exp);
                function.setName(OGCJAXBStatics.EXPRESSION_ADD);
                jax = ogc_factory.createFunction(function);
                break;
            }
            case "Divide": {
                final FunctionType function = convert(exp);
                function.setName(OGCJAXBStatics.EXPRESSION_DIV);
                jax = ogc_factory.createFunction(function);
                break;
            }
            case "Subtract": {
                final FunctionType function = convert(exp);
                function.setName(OGCJAXBStatics.EXPRESSION_SUB);
                jax = ogc_factory.createFunction(function);
                break;
            }
            case "PropertyName":
            case "ValueReference": {
                jax = ogc_factory.createValueReference(((ValueReference) exp).getXPath());
                break;
            }
            default: {
                final FunctionType ft = ogc_factory.createFunctionType();
                ft.setName(exp.getFunctionName().tip().toString());
                for (final Expression ex : parameters) {
                    ft.getExpression().add(extract(ex));
                }
                jax = ogc_factory.createFunction(ft);
                break;
            }
        }
        return jax;
    }

    public JAXBElement<? extends AbstractIdType> visit(final ResourceId filter) {
        return ogc_factory.createResourceId(new ResourceIdType(filter.getIdentifier()));
    }

    private FunctionType convert(final Expression source) {
        List<Expression> parameters = source.getParameters();
        final FunctionType function = ogc_factory.createFunctionType();
        function.getExpression().add(extract(parameters.get(0)));
        function.getExpression().add(extract(parameters.get(1)));
        return function;
    }

    @Override
    public FilterType apply(Filter filter) {
        if (filter instanceof FilterType) {
            return (FilterType) filter;
        }
        final FilterType ft = ogc_factory.createFilterType();
        if (filter instanceof ResourceId) {
            ft.getId().add(visit((ResourceId)filter));
        } else {
            final JAXBElement<?> sf = visit(filter);
            if (sf == null) {
                return null;
            } else if (sf.getValue() instanceof ComparisonOpsType) {
                ft.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
            } else if (sf.getValue() instanceof LogicOpsType) {
                ft.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
            } else if (sf.getValue() instanceof SpatialOpsType) {
                ft.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
            } else {
                //should not happen
                throw new IllegalArgumentException("invalid filter element : " + sf);
            }
        }
        return ft;
    }
}
