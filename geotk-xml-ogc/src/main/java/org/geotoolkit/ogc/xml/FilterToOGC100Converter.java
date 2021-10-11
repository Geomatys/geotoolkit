/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.ogc.xml;

import java.awt.Color;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.sis.internal.referencing.AxisDirections;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.gml.xml.v212.BoxType;
import org.geotoolkit.gml.xml.v212.CoordType;
import org.geotoolkit.ogc.xml.v100.AndType;
import org.geotoolkit.ogc.xml.v100.BBOXType;
import org.geotoolkit.ogc.xml.v100.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v100.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v100.FilterType;
import org.geotoolkit.ogc.xml.v100.FunctionType;
import org.geotoolkit.ogc.xml.v100.LiteralType;
import org.geotoolkit.ogc.xml.v100.LogicOpsType;
import org.geotoolkit.ogc.xml.v100.LowerBoundaryType;
import org.geotoolkit.ogc.xml.v100.NotType;
import org.geotoolkit.ogc.xml.v100.ObjectFactory;
import org.geotoolkit.ogc.xml.v100.OrType;
import org.geotoolkit.ogc.xml.v100.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v100.PropertyIsEqualToType;
import org.geotoolkit.ogc.xml.v100.PropertyIsGreaterThanOrEqualToType;
import org.geotoolkit.ogc.xml.v100.PropertyIsGreaterThanType;
import org.geotoolkit.ogc.xml.v100.PropertyIsLessThanOrEqualToType;
import org.geotoolkit.ogc.xml.v100.PropertyIsLessThanType;
import org.geotoolkit.ogc.xml.v100.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v100.PropertyIsNotEqualToType;
import org.geotoolkit.ogc.xml.v100.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v100.PropertyNameType;
import org.geotoolkit.ogc.xml.v100.SpatialOpsType;
import org.geotoolkit.ogc.xml.v100.UpperBoundaryType;
import org.opengis.filter.BetweenComparisonOperator;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinarySpatialOperator;
import org.opengis.filter.ComparisonOperatorName;
import org.opengis.filter.Filter;
import org.opengis.filter.Expression;
import org.opengis.filter.LikeOperator;
import org.opengis.filter.Literal;
import org.opengis.filter.LogicalOperator;
import org.opengis.filter.LogicalOperatorName;
import org.opengis.filter.NullOperator;
import org.opengis.filter.SpatialOperatorName;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.util.CodeList;
import org.opengis.util.FactoryException;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FilterToOGC100Converter implements FilterToOGCConverter<FilterType> {

    public final ObjectFactory ogc_factory;

    public FilterToOGC100Converter() {
        ogc_factory = new ObjectFactory();
    }

    /**
     * Transform an expression in jaxb element.
     */
    public JAXBElement<?> extract(final Expression<Object,?> exp) {
        final JAXBElement<?> jax;
        final List<Expression<? super Object, ?>> parameters = exp.getParameters();
        switch (exp.getFunctionName().tip().toString()) {
            case "Multiply": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createMul(bot);
                break;
            }
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
            case "Add": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createAdd(bot);
                break;
            }
            case "Divide": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createDiv(bot);
                break;
            }
            case "Subtract": {
                final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
                bot.getExpression().add(extract(parameters.get(0)));
                bot.getExpression().add(extract(parameters.get(1)));
                jax = ogc_factory.createSub(bot);
                break;
            }
            case "PropertyName":
            case "ValueReference": {
                final PropertyNameType literal = ogc_factory.createPropertyNameType();
                literal.setContent(((ValueReference) exp).getXPath());
                jax = ogc_factory.createPropertyName(literal);
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

        //        JAXBElementBinaryOperatorType>
//                JAXBElementMapItemType>
//                JAXBElementBinaryOperatorType>
//                JAXBElementLiteralType>
//                JAXBElementInterpolateType>
//                JAXBElementConcatenateType>
//                JAXBElementChangeCaseType>
//                JAXBElementPropertyNameType>
//                JAXBElementTrimType>
//                JAXBElementBinaryOperatorType>
//                JAXBElementnet.opengis.ogc.FunctionType>
//                JAXBElementFormatDateType>
//                JAXBElementCategorizeType>
//                JAXBElementBinaryOperatorType>
//                JAXBElementExpressionType>
//                JAXBElementInterpolationPointType>
//                JAXBElementStringLengthType>
//                JAXBElementRecodeType> String
//                JAXBElementnet.opengis.se.FunctionType>
//                JAXBElementFormatNumberType>
//                JAXBElementSubstringType>
//                JAXBElementStringPositionType>
        }
        return jax;
    }

    @Override
    public JAXBElement<?> visit(final Filter filter) {
        if (filter.equals(Filter.include())) {
            return null;
        }
        if (filter.equals(Filter.exclude())) {
            return null;
        }
        final CodeList<?> type = filter.getOperatorType();
        if (filter instanceof BetweenComparisonOperator) {
            final BetweenComparisonOperator pib = (BetweenComparisonOperator) filter;
            final PropertyIsBetweenType bot = ogc_factory.createPropertyIsBetweenType();
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));
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
            final PropertyIsGreaterThanType bot =  new PropertyIsGreaterThanType();
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
            lt.getContent().add(((Literal) expressions.get(1)).getValue());
            bot.setLiteral(lt);
            final Expression expression = expressions.get(0);
            if (!(expression instanceof ValueReference)) {
                throw new IllegalArgumentException("LikeOperator can support ValueReference only, but was a " + expression);
            }
            final PropertyNameType pnt = (PropertyNameType) extract(expression).getValue();
            bot.setPropertyName(pnt);
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
            final Object obj = extract((Expression) pis.getExpressions().get(0)).getValue();
            if (obj instanceof LiteralType) {
                bot.setLiteral((LiteralType) obj);
            } else if (obj instanceof PropertyNameType) {
                bot.setPropertyName((PropertyNameType) obj);
            } else {
                //should not be possible
                throw new IllegalArgumentException("Invalid expression element : " + obj);
            }
            return ogc_factory.createPropertyIsNull(bot);
        } else if (type == LogicalOperatorName.AND) {
            final LogicalOperator and = (LogicalOperator) filter;
            final AndType lot = new AndType();
            for (final Filter f : (List<Filter>) and.getOperands()) {
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visit(f));
            }
            return ogc_factory.createAnd(lot);
        } else if (type == LogicalOperatorName.OR) {
            final LogicalOperator or = (LogicalOperator) filter;
            final OrType lot = new OrType();
            for (final Filter f : (List<Filter>) or.getOperands()) {
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visit(f));
            }
            return ogc_factory.createOr(lot);
        } else if (type == LogicalOperatorName.NOT) {
            final LogicalOperator not = (LogicalOperator) filter;
            final NotType lot = new NotType();
            JAXBElement<?> sf = visit((Filter) not.getOperands().get(0));
            if (sf.getValue() instanceof ComparisonOpsType) {
                lot.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
            } else if (sf.getValue() instanceof LogicOpsType) {
                lot.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
            } else if (sf.getValue() instanceof SpatialOpsType) {
                lot.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
            } else {
                //should not happen
                throw new IllegalArgumentException("invalid filter element : " + sf);
            }
            return ogc_factory.createNot(lot);
        } else if (type == SpatialOperatorName.BBOX) {
            final BBOX bbox = BBOX.wrap((BinarySpatialOperator) filter);
            final BBOXType bboxType = ogc_factory.createBBOXType();
            final Expression sourceExp1 = bbox.getOperand1();
            final JAXBElement<?> exp1 = extract(sourceExp1);
            final Expression sourceExp2 = bbox.getOperand2();
            JAXBElement<?> exp2 = extract(sourceExp2);
            final PropertyNameType pName;
            final BoxType boxType;
            if (exp1 != null && exp1.getValue() instanceof PropertyNameType) {
                pName = (PropertyNameType) exp1.getValue();
            } else if (exp2 != null && exp2.getValue() instanceof PropertyNameType) {
                pName = (PropertyNameType) exp2.getValue();
            } else
                throw new IllegalArgumentException("No property name found in given bbox filter");
            if (sourceExp1 instanceof Literal) {
                boxType = toBox((Literal) sourceExp1);
            } else if (sourceExp2 instanceof Literal) {
                boxType = toBox((Literal) sourceExp2);
            } else
                throw new IllegalArgumentException("No bbox found in given bbox filter");
            bboxType.setPropertyName(pName);
            bboxType.setBox(boxType);
            return ogc_factory.createBBOX(bboxType);
        }
        throw new IllegalArgumentException("Unknowed filter element : " + filter + " class :" + filter.getClass());
    }

    /**
     * Analyze given literal and try to transform it into GML 2 bbox. The
     * conversion is possible only if the given literal contains an
     * {@link Envelope} or a {@link GeographicBoundingBox}.
     *
     * @param input The literal to extract a bbox from.
     * @return Built bbox, never null.
     * @throws IllegalArgumentException If given literal does not contain any
     * usable envelope. Note that an envelope cannot be used if its dimension is
     * more than 2D and no CRS is present to allow us to find horizontal piece.
     */
    private static BoxType toBox(final Literal input) throws IllegalArgumentException {
        final Object val = input.getValue();
        if (val == null) {
            throw new IllegalArgumentException("No value in input literal object. Conversion to BoxType impossible");
        }
        final BoxType bType = new BoxType();
        CoordinateReferenceSystem boxCrs = null;
        if (val instanceof Envelope) {
            final Envelope env = (Envelope) val;
            boxCrs = env.getCoordinateReferenceSystem();
            final int xAxis;
            if (env.getDimension() == 2) {
                xAxis = 0;
            } else if (env.getDimension() > 2) {
                if (boxCrs == null) {
                    throw new IllegalArgumentException("Cannot find horizontal axis of given envelope");
                }
                final SingleCRS hCrs = CRS.getHorizontalComponent(boxCrs);
                if (hCrs == null) {
                    throw new IllegalArgumentException("Cannot find horizontal axis of given envelope");
                }
                xAxis = AxisDirections.indexOfColinear(boxCrs.getCoordinateSystem(), hCrs.getCoordinateSystem());
                boxCrs = hCrs;
            } else
                throw new IllegalArgumentException(String.format(
                        "Given envelope has not enough dimension. Cannot build a bbox from it.%nExpected: %d%nBut was: %d",
                        2, env.getDimension()
                ));
            final int yAxis = xAxis + 1;
            bType.getCoord().add(new CoordType(env.getMinimum(xAxis), env.getMinimum(yAxis)));
            bType.getCoord().add(new CoordType(env.getMaximum(xAxis), env.getMaximum(yAxis)));
        } else if (val instanceof GeographicBoundingBox) {
            final GeographicBoundingBox e = (GeographicBoundingBox) val;
            bType.getCoord().add(new CoordType(e.getWestBoundLongitude(), e.getSouthBoundLatitude()));
            bType.getCoord().add(new CoordType(e.getEastBoundLongitude(), e.getNorthBoundLatitude()));
            boxCrs = CommonCRS.defaultGeographic();
        }
        if (boxCrs != null) {
            String srsName;
            try {
                srsName = IdentifiedObjects.lookupURN(boxCrs, null);
            } catch (FactoryException ex) {
                srsName = IdentifiedObjects.getSimpleNameOrIdentifier(boxCrs);
            }
            if (srsName != null) {
                bType.setSrsName(srsName);
            }
        }
        return bType;
    }

    @Override
    public FilterType apply(final Filter filter) {
        if (filter == null) {
            return null;
        }
        JAXBElement<?> sf = visit(filter);
        if (sf == null) {
            return null;
        }
        final FilterType ft = ogc_factory.createFilterType();
        if (sf.getValue() instanceof ComparisonOpsType) {
            ft.setComparisonOps((JAXBElement<? extends ComparisonOpsType>) sf);
        } else if (sf.getValue() instanceof LogicOpsType) {
            ft.setLogicOps((JAXBElement<? extends LogicOpsType>) sf);
        } else if (sf.getValue() instanceof SpatialOpsType) {
            ft.setSpatialOps((JAXBElement<? extends SpatialOpsType>) sf);
        } else {
            //should not happen
            throw new IllegalArgumentException("invalide filter element : " + sf);
        }
        return ft;
    }
}
