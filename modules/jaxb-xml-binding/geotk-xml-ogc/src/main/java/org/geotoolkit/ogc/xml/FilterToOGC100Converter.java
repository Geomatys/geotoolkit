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
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
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
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Beyond;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.SingleCRS;
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
    public JAXBElement<?> extract(final Expression exp) {
        JAXBElement<?> jax = null;

        if (exp instanceof Function) {
            final Function function = (Function) exp;
            final FunctionType ft = ogc_factory.createFunctionType();
            ft.setName(function.getName());
            for (final Expression ex : function.getParameters()) {
                ft.getExpression().add(extract(ex));
            }
            jax = ogc_factory.createFunction(ft);
        } else if (exp instanceof Multiply) {
            final Multiply multiply = (Multiply) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(multiply.getExpression1()));
            bot.getExpression().add(extract(multiply.getExpression2()));
            jax = ogc_factory.createMul(bot);
        } else if (exp instanceof Literal) {
            final LiteralType literal = ogc_factory.createLiteralType();
            Object val = ((Literal) exp).getValue();
            if (val instanceof Color) {
                val = FilterUtilities.toString((Color)val);
            }
            literal.setContent(val == null? null : val.toString());
            jax = ogc_factory.createLiteral(literal);
        } else if (exp instanceof Add) {
            final Add add = (Add) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(add.getExpression1()));
            bot.getExpression().add(extract(add.getExpression2()));
            jax = ogc_factory.createAdd(bot);
        } else if (exp instanceof Divide) {
            final Divide divide = (Divide) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(divide.getExpression1()));
            bot.getExpression().add(extract(divide.getExpression2()));
            jax = ogc_factory.createDiv(bot);
        } else if (exp instanceof Subtract) {
            final Subtract substract = (Subtract) exp;
            final BinaryOperatorType bot = ogc_factory.createBinaryOperatorType();
            bot.getExpression().add(extract(substract.getExpression1()));
            bot.getExpression().add(extract(substract.getExpression2()));
            jax = ogc_factory.createSub(bot);
        } else if (exp instanceof PropertyName) {
            final PropertyNameType literal = ogc_factory.createPropertyNameType();
            literal.setContent(((PropertyName) exp).getPropertyName());
            jax = ogc_factory.createPropertyName(literal);
        } else if (exp instanceof NilExpression) {
            //DO nothing on NILL expression
        } else {
            throw new IllegalArgumentException("Unknowed expression element :" + exp);
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
        return jax;
    }

    @Override
    public JAXBElement<?> visit(final Filter filter) {
        if (filter.equals(Filter.INCLUDE)) {
            return null;
        }
        if (filter.equals(Filter.EXCLUDE)) {
            return null;
        }

        if (filter instanceof PropertyIsBetween) {
            final PropertyIsBetween pib = (PropertyIsBetween) filter;
            final PropertyIsBetweenType bot = ogc_factory.createPropertyIsBetweenType();
            final LowerBoundaryType lbt = ogc_factory.createLowerBoundaryType();
            lbt.setExpression(extract(pib.getLowerBoundary()));
            final UpperBoundaryType ubt = ogc_factory.createUpperBoundaryType();
            ubt.setExpression(extract(pib.getUpperBoundary()));

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
            final PropertyIsGreaterThanType bot =  new PropertyIsGreaterThanType();
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
            bot.setEscape(pis.getEscape());
            final LiteralType lt = ogc_factory.createLiteralType();
            lt.getContent().add(pis.getLiteral());
            bot.setLiteral(lt);
            if (!(pis.getExpression() instanceof PropertyName)) {
                throw new IllegalArgumentException("PropertyIsLike can support PropertyName only, but was a " + pis.getExpression());
            }
            final PropertyNameType pnt = (PropertyNameType) extract(pis.getExpression()).getValue();
            bot.setPropertyName(pnt);
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
            final Object obj = extract(pis.getExpression()).getValue();
            if (obj instanceof LiteralType) {
                bot.setLiteral((LiteralType) obj);
            } else if (obj instanceof PropertyNameType) {
                bot.setPropertyName((PropertyNameType) obj);
            } else {
                //should not be possible
                throw new IllegalArgumentException("Invalid expression element : " + obj);
            }
            return ogc_factory.createPropertyIsNull(bot);
        } else if (filter instanceof And) {
            final And and = (And) filter;
            final AndType lot = new AndType();
            for (final Filter f : and.getChildren()) {
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visit(f));
            }
            return ogc_factory.createAnd(lot);
        } else if (filter instanceof Or) {
            final Or or = (Or) filter;
            final OrType lot = new OrType();
            for (final Filter f : or.getChildren()) {
                lot.getComparisonOpsOrSpatialOpsOrLogicOps().add(visit(f));
            }
            return ogc_factory.createOr(lot);
        } else if (filter instanceof Not) {
            final Not not = (Not) filter;
            final NotType lot = new NotType();
            JAXBElement<?> sf = visit(not.getFilter());

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
        } else if (filter instanceof BBOX) {
            final BBOX bbox = (BBOX) filter;
            final BBOXType bboxType = ogc_factory.createBBOXType();
            final Expression sourceExp1 = bbox.getExpression1();
            final JAXBElement<?> exp1 = extract(sourceExp1);
            final Expression sourceExp2 = bbox.getExpression2();
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
        } else if (filter instanceof Beyond) {

        } else if (filter instanceof Contains) {

        } else if (filter instanceof Crosses) {

        } else if (filter instanceof DWithin) {

        } else if (filter instanceof Disjoint) {

        } else if (filter instanceof Equals) {

        } else if (filter instanceof Intersects) {

        } else if (filter instanceof Overlaps) {

        } else if (filter instanceof Touches) {

        } else if (filter instanceof Within) {

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
                srsName = IdentifiedObjects.getUnicodeIdentifier(boxCrs);
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
