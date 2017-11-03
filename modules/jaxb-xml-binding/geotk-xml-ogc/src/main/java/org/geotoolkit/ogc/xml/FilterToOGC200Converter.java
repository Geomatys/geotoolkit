package org.geotoolkit.ogc.xml;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.factory.FactoryFinder;
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
import org.geotoolkit.ogc.xml.v200.FunctionType;
import org.geotoolkit.ogc.xml.v200.AndType;
import org.geotoolkit.ogc.xml.v200.BBOXType;
import org.geotoolkit.ogc.xml.v200.BinaryLogicOpType;
import org.geotoolkit.ogc.xml.v200.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v200.ContainsType;
import org.geotoolkit.ogc.xml.v200.CrossesType;
import org.geotoolkit.ogc.xml.v200.DWithinType;
import org.geotoolkit.ogc.xml.v200.DisjointType;
import org.geotoolkit.ogc.xml.v200.EqualsType;
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
import org.geotoolkit.ogc.xml.v200.UnaryLogicOpType;
import org.geotoolkit.ogc.xml.v200.UpperBoundaryType;
import org.geotoolkit.ogc.xml.v200.WithinType;
import org.geotoolkit.ogc.xml.v200.ObjectFactory;
import org.geotoolkit.ogc.xml.v200.ResourceIdType;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsGreaterThan;
import org.opengis.filter.PropertyIsGreaterThanOrEqualTo;
import org.opengis.filter.PropertyIsLessThan;
import org.opengis.filter.PropertyIsLessThanOrEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNotEqualTo;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.BinaryExpression;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterToOGC200Converter implements Function<Filter, JAXBElement> {

    private final ObjectFactory ogc_factory;
    private final org.geotoolkit.gml.xml.v321.ObjectFactory gml_factory;
    private final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public FilterToOGC200Converter() {
        this.ogc_factory = new ObjectFactory();
        this.gml_factory = new org.geotoolkit.gml.xml.v321.ObjectFactory();
    }

    @Override
    public JAXBElement apply(Filter filter) {
        if (filter.equals(Filter.INCLUDE) || filter.equals(Filter.EXCLUDE)) {
            return null;
        }

        if (filter instanceof PropertyIsBetween) {
            final PropertyIsBetween pib = (PropertyIsBetween) filter;
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));

            final PropertyIsBetweenType bot = new PropertyIsBetweenType();
            bot.setExpression(extract(pib.getExpression()));
            bot.setLowerBoundary(lbt);
            bot.setUpperBoundary(ubt);
            return ogc_factory.createPropertyIsBetween(bot);
        } else if (filter instanceof PropertyIsEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsEqualToType bot = new PropertyIsEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsEqualTo(bot);
        } else if (filter instanceof PropertyIsGreaterThan) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanType bot = new PropertyIsGreaterThanType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThan(bot);
        } else if (filter instanceof PropertyIsGreaterThanOrEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsGreaterThanOrEqualToType bot = new PropertyIsGreaterThanOrEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsGreaterThanOrEqualTo(bot);
        } else if (filter instanceof PropertyIsLessThan) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanType bot = new PropertyIsLessThanType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThan(bot);
        } else if (filter instanceof PropertyIsLessThanOrEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsLessThanOrEqualToType bot = new PropertyIsLessThanOrEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsLessThanOrEqualTo(bot);
        } else if (filter instanceof PropertyIsLike) {
            final PropertyIsLike pis = (PropertyIsLike) filter;
            final PropertyIsLikeType bot = ogc_factory.createPropertyIsLikeType();
            bot.setEscapeChar(pis.getEscape());
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.setContent(pis.getLiteral());
            bot.getExpression().add(ogc_factory.createLiteral(lt));
            if (!(pis.getExpression() instanceof PropertyName)) {
                throw new IllegalArgumentException("PropertyIsLike can support PropertyName only, but was a " + pis.getExpression());
            }
            bot.getExpression().add(0, extract(pis.getExpression()));
            bot.setSingleChar(pis.getSingleChar());
            bot.setWildCard(pis.getWildCard());
            return ogc_factory.createPropertyIsLike(bot);
        } else if (filter instanceof PropertyIsNotEqualTo) {
            final BinaryComparisonOperator pit = (BinaryComparisonOperator) filter;
            final PropertyIsNotEqualToType bot = new PropertyIsNotEqualToType();
            bot.getExpression().add(extract(pit.getExpression1()));
            bot.getExpression().add(extract(pit.getExpression2()));
            return ogc_factory.createPropertyIsNotEqualTo(bot);
        } else if (filter instanceof PropertyIsNull) {
            final PropertyIsNull pis = (PropertyIsNull) filter;
            final PropertyIsNullType bot = ogc_factory.createPropertyIsNullType();
            bot.setExpression(extract(pis.getExpression()));

            return ogc_factory.createPropertyIsNull(bot);
        } else if (filter instanceof And) {
            final And and = (And) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for (final Filter f : and.getChildren()) {
                final JAXBElement<? extends LogicOpsType> ele = (JAXBElement<? extends LogicOpsType>) apply(f);
                if (ele != null) {
                    lot.getLogicOps().add(ele);
                }
            }

            return ogc_factory.createAnd(new AndType(lot.getLogicOps().toArray()));
        } else if (filter instanceof Or) {
            final Or or = (Or) filter;
            final BinaryLogicOpType lot = ogc_factory.createBinaryLogicOpType();
            for (final Filter f : or.getChildren()) {
                final JAXBElement subFilter = apply(f);
                if (subFilter != null) {
                    lot.getLogicOps().add(subFilter);
                }
            }
            return ogc_factory.createOr(new OrType(lot.getLogicOps().toArray()));
        } else if (filter instanceof Not) {
            final Not not = (Not) filter;
            final UnaryLogicOpType lot = ogc_factory.createUnaryLogicOpType();
            final JAXBElement<?> sf = apply(not.getFilter());

            if (sf.getValue() instanceof ComparisonOpsType) {
                lot.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getComparisonOps().getValue()));
            }
            if (sf.getValue() instanceof LogicOpsType) {
                lot.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getLogicOps().getValue()));
            }
            if (sf.getValue() instanceof SpatialOpsType) {
                lot.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
                return ogc_factory.createNot(new NotType(lot.getSpatialOps().getValue()));
            }
            //should not happen
            throw new IllegalArgumentException("invalid filter element : " + sf);
        } else if (filter instanceof FeatureId) {
            throw new IllegalArgumentException("Not parsed yet : " + filter);
        } else if (filter instanceof BBOX) {
            final BBOX bbox = (BBOX) filter;

            final Expression left = bbox.getExpression1();
            final Expression right = bbox.getExpression2();

            final String property;
            final double minx;
            final double maxx;
            final double miny;
            final double maxy;
            final String srs;

            if (left instanceof PropertyName) {
                property = ((PropertyName) left).getPropertyName();

                final Object objGeom = ((Literal) right).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = ReferencingUtilities.lookupIdentifier(env.getCoordinateReferenceSystem(), true);
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

            } else if (right instanceof PropertyName) {
                property = ((PropertyName) right).getPropertyName();

                final Object objGeom = ((Literal) left).getValue();
                if (objGeom instanceof org.opengis.geometry.Envelope) {
                    final org.opengis.geometry.Envelope env = (org.opengis.geometry.Envelope) objGeom;
                    minx = env.getMinimum(0);
                    maxx = env.getMaximum(0);
                    miny = env.getMinimum(1);
                    maxy = env.getMaximum(1);
                    try {
                        srs = IdentifiedObjects.lookupURN(env.getCoordinateReferenceSystem(), null);
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
        } else if (filter instanceof Id) {
            final PropertyName n = FF.property(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            Id idFilter = (Id) filter;
            final Set<Identifier> identifiers = idFilter.getIdentifiers();
            if (identifiers.isEmpty()) {
                throw new IllegalArgumentException("Cannot filter an empty identifier.");
            } else if (identifiers.size() > 1) {
                throw new IllegalArgumentException("Multiple Identifier matching is not supported by Filter 2.0");
            }

            final Identifier id = identifiers.iterator().next();
            if (id.getID() instanceof String) {
                final ResourceIdType rId = ogc_factory.createResourceIdType();
                rId.setRid((String) id.getID());
                return ogc_factory.createResourceId(rId);
            } else {
                throw new IllegalArgumentException("Given identifier is not a string:" + (id.getID() == null ? "null" : id.getID().getClass()));
            }

        } else if (filter instanceof BinarySpatialOperator) {
            final BinarySpatialOperator spatialOp = (BinarySpatialOperator) filter;

            Expression exp1 = spatialOp.getExpression1();
            Expression exp2 = spatialOp.getExpression2();

            if (!(exp1 instanceof PropertyName)) {
                //flip order
                final Expression ex = exp1;
                exp1 = exp2;
                exp2 = ex;
            }

            if (!(exp1 instanceof PropertyName)) {
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

            if (filter instanceof Beyond) {
                throw new UnsupportedOperationException();

            } else if (filter instanceof Contains) {
                return ogc_factory.createContains(new ContainsType(pName, geometryExpression));
            } else if (filter instanceof Crosses) {
                ogc_factory.createCrosses(new CrossesType(pName, geometryExpression));
            } else if (filter instanceof DWithin) {
                DWithin f = (DWithin) filter;
                return ogc_factory.createDWithin(new DWithinType(pName, geometryExpression, f.getDistance(), f.getDistanceUnits()));
            } else if (filter instanceof Disjoint) {
                return ogc_factory.createDisjoint(new DisjointType(pName, geometryExpression));
            } else if (filter instanceof Equals) {
                return ogc_factory.createEquals(new EqualsType(pName, geometryExpression));
            } else if (filter instanceof Intersects) {
                return ogc_factory.createIntersects(new IntersectsType(pName, geometryExpression));
            } else if (filter instanceof Overlaps) {
                return ogc_factory.createOverlaps(new OverlapsType(pName, geometryExpression));
            } else if (filter instanceof Touches) {
                return ogc_factory.createTouches(new TouchesType(pName, geometryExpression));
            } else if (filter instanceof Within) {
                return ogc_factory.createWithin(new WithinType(pName, geometryExpression));
            }

            throw new IllegalArgumentException("Unknown filter element : " + filter + " class :" + filter.getClass());
        }

        throw new IllegalArgumentException("Unknown filter element : " + filter + " class :" + filter.getClass());
    }

    public JAXBElement<?> extract(final Expression exp) {
        JAXBElement<?> jax = null;

        if (exp instanceof org.opengis.filter.expression.Function) {
            final org.opengis.filter.expression.Function function = (org.opengis.filter.expression.Function) exp;
            final FunctionType ft = ogc_factory.createFunctionType();
            ft.setName(function.getName());
            for (final Expression ex : function.getParameters()) {
                ft.getExpression().add(extract(ex));
            }
            jax = ogc_factory.createFunction(ft);
        } else if (exp instanceof Literal) {
            final LiteralType literal = ogc_factory.createLiteralType();
            // TODO : check if all goes well if we do not convert to string
//            Object value = ((Literal) exp).getValue();
//            if(value instanceof Color){
//                value = colorToString((Color)value);
//            }
            literal.setContent(((Literal) exp).getValue());
            jax = ogc_factory.createLiteral(literal);

        } else if (exp instanceof Multiply) {
            final FunctionType function = convert((Multiply) exp);
            function.setName(OGCJAXBStatics.EXPRESSION_MUL);
            jax = ogc_factory.createFunction(function);
        } else if (exp instanceof Add) {
            final FunctionType function = convert((Add) exp);
            function.setName(OGCJAXBStatics.EXPRESSION_ADD);
            jax = ogc_factory.createFunction(function);
        } else if (exp instanceof Divide) {
            final FunctionType function = convert((Divide) exp);
            function.setName(OGCJAXBStatics.EXPRESSION_DIV);
            jax = ogc_factory.createFunction(function);
        } else if (exp instanceof Subtract) {
            final FunctionType function = convert((Subtract) exp);
            function.setName(OGCJAXBStatics.EXPRESSION_SUB);
            jax = ogc_factory.createFunction(function);
        } else if (exp instanceof PropertyName) {
            jax = ogc_factory.createValueReference(((PropertyName) exp).getPropertyName());
        } else if (exp instanceof NilExpression) {
            //DO nothing on NILL expression
        } else {
            throw new IllegalArgumentException("Unknown expression element :" + exp);
        }

        return jax;
    }

    private FunctionType convert(final BinaryExpression source) {
        final FunctionType function = ogc_factory.createFunctionType();
        function.getExpression().add(extract(source.getExpression1()));
        function.getExpression().add(extract(source.getExpression2()));
        return function;
    }

//    static String colorToString(Color color) {
//        String redCode = Integer.toHexString(color.getRed());
//        String greenCode = Integer.toHexString(color.getGreen());
//        String blueCode = Integer.toHexString(color.getBlue());
//        if (redCode.length() == 1)      redCode = "0" + redCode;
//        if (greenCode.length() == 1)    greenCode = "0" + greenCode;
//        if (blueCode.length() == 1)     blueCode = "0" + blueCode;
//
//        final String colorCode;
//        int alpha = color.getAlpha();
//        if(alpha != 255){
//            String alphaCode = Integer.toHexString(alpha);
//            if (alphaCode.length() == 1) alphaCode = "0" + alphaCode;
//            colorCode = "#" + alphaCode + redCode + greenCode + blueCode;
//        }else{
//            colorCode = "#" + redCode + greenCode + blueCode;
//        }
//        return colorCode.toUpperCase();
//    }
}
