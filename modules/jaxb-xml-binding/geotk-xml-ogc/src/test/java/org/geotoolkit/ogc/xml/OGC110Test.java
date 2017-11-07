/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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

import com.vividsolutions.jts.geom.Polygon;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.ogc.xml.v110.BinaryComparisonOpType;
import org.geotoolkit.ogc.xml.v110.BinaryLogicOpType;
import org.geotoolkit.ogc.xml.v110.BinaryOperatorType;
import org.geotoolkit.ogc.xml.v110.ComparisonOpsType;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.LiteralType;
import org.geotoolkit.ogc.xml.v110.LogicOpsType;
import org.geotoolkit.ogc.xml.v110.PropertyIsBetweenType;
import org.geotoolkit.ogc.xml.v110.PropertyIsLikeType;
import org.geotoolkit.ogc.xml.v110.PropertyIsNullType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.ogc.xml.v110.UnaryLogicOpType;
import org.apache.sis.xml.MarshallerPool;
import org.junit.Test;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.filter.spatial.BinarySpatialOperator;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import static org.junit.Assert.*;

/**
 * Test class for Filter and Expression jaxb marshelling and unmarshelling.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OGC110Test {

    private static final double DELTA = 0.00000001;

    private static final FilterFactory2 FILTER_FACTORY;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
    }

    private static MarshallerPool POOL;
    private static final OGC110toGTTransformer TRANSFORMER_GT;
    private static final FilterToOGC110Converter TRANSFORMER_OGC;

    private static final String valueStr = "feature_property_name";
    private static final float valueF = 456f;


    //FILES -------------------------------------
    private static File FILE_EXP_ADD = null;
    private static File FILE_EXP_SUB = null;
    private static File FILE_EXP_MUL = null;
    private static File FILE_EXP_DIV = null;
    private static File FILE_EXP_PROPERTYNAME = null;
    private static File FILE_EXP_LITERAL = null;
    private static File FILE_EXP_LITERAL_COLOR = null;
    private static File FILE_EXP_FUNCTION = null;

    private static File TEST_FILE_EXP_ADD = null;
    private static File TEST_FILE_EXP_SUB = null;
    private static File TEST_FILE_EXP_MUL = null;
    private static File TEST_FILE_EXP_DIV = null;
    private static File TEST_FILE_EXP_PROPERTYNAME = null;
    private static File TEST_FILE_EXP_LITERAL = null;
    private static File TEST_FILE_EXP_LITERAL_COLOR = null;
    private static File TEST_FILE_EXP_FUNCTION = null;

    private static File FILE_FIL_COMP_ISBETWEEN = null;
    private static File FILE_FIL_COMP_ISEQUAL = null;
    private static File FILE_FIL_COMP_ISGREATER = null;
    private static File FILE_FIL_COMP_ISGREATEROREQUAL = null;
    private static File FILE_FIL_COMP_ISLESS = null;
    private static File FILE_FIL_COMP_ISLESSOREQUAL = null;
    private static File FILE_FIL_COMP_ISLIKE = null;
    private static File FILE_FIL_COMP_ISNOTEQUAL = null;
    private static File FILE_FIL_COMP_ISNULL = null;
    private static File FILE_FIL_LOG_AND = null;
    private static File FILE_FIL_LOG_OR = null;
    private static File FILE_FIL_LOG_NOT = null;
    private static File FILE_FIL_SPA_BBOX = null;
    private static File FILE_FIL_SPA_BEYOND = null;
    private static File FILE_FIL_SPA_CONTAINS = null;
    private static File FILE_FIL_SPA_CROSSES = null;
    private static File FILE_FIL_SPA_DWITHIN = null;
    private static File FILE_FIL_SPA_DISJOINT = null;
    private static File FILE_FIL_SPA_EQUALS = null;
    private static File FILE_FIL_SPA_INTERSECTS = null;
    private static File FILE_FIL_SPA_OVERLAPS = null;
    private static File FILE_FIL_SPA_OVERLAPS2 = null;
    private static File FILE_FIL_SPA_TOUCHES = null;
    private static File FILE_FIL_SPA_WITHIN = null;

    private static File TEST_FILE_FIL_COMP_ISBETWEEN = null;
    private static File TEST_FILE_FIL_COMP_ISEQUAL = null;
    private static File TEST_FILE_FIL_COMP_ISGREATER = null;
    private static File TEST_FILE_FIL_COMP_ISGREATEROREQUAL = null;
    private static File TEST_FILE_FIL_COMP_ISLESS = null;
    private static File TEST_FILE_FIL_COMP_ISLESSOREQUAL = null;
    private static File TEST_FILE_FIL_COMP_ISLIKE = null;
    private static File TEST_FILE_FIL_COMP_ISNOTEQUAL = null;
    private static File TEST_FILE_FIL_COMP_ISNULL = null;
    private static File TEST_FILE_FIL_LOG_AND = null;
    private static File TEST_FILE_FIL_LOG_OR = null;
    private static File TEST_FILE_FIL_LOG_NOT = null;
    private static File TEST_FILE_FIL_SPA_BBOX = null;
    private static File TEST_FILE_FIL_SPA_BEYOND = null;
    private static File TEST_FILE_FIL_SPA_CONTAINS = null;
    private static File TEST_FILE_FIL_SPA_CROSSES = null;
    private static File TEST_FILE_FIL_SPA_DWITHIN = null;
    private static File TEST_FILE_FIL_SPA_DISJOINT = null;
    private static File TEST_FILE_FIL_SPA_EQUALS = null;
    private static File TEST_FILE_FIL_SPA_INTERSECTS = null;
    private static File TEST_FILE_FIL_SPA_OVERLAPS = null;
    private static File TEST_FILE_FIL_SPA_OVERLAPS2 = null;
    private static File TEST_FILE_FIL_SPA_TOUCHES = null;
    private static File TEST_FILE_FIL_SPA_WITHIN = null;

    static {

        POOL = FilterMarshallerPool.getInstance(FilterVersion.V110);

        TRANSFORMER_GT = new OGC110toGTTransformer(FILTER_FACTORY);
        assertNotNull(TRANSFORMER_GT);

        TRANSFORMER_OGC = new FilterToOGC110Converter();
        assertNotNull(TRANSFORMER_OGC);

        try {
            FILE_EXP_ADD = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Add.xml").toURI() );
            FILE_EXP_SUB = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Sub.xml").toURI() );
            FILE_EXP_MUL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Mul.xml").toURI() );
            FILE_EXP_DIV = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Div.xml").toURI() );
            FILE_EXP_PROPERTYNAME = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_PropertyName.xml").toURI() );
            FILE_EXP_LITERAL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Literal.xml").toURI() );
            FILE_EXP_LITERAL_COLOR = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_LiteralColor.xml").toURI() );
            FILE_EXP_FUNCTION = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Expression_Function.xml").toURI() );

            FILE_FIL_COMP_ISBETWEEN = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsBetween.xml").toURI() );
            FILE_FIL_COMP_ISEQUAL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISGREATER = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsGreaterThan.xml").toURI() );
            FILE_FIL_COMP_ISGREATEROREQUAL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsGreaterThanOrEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISLESS = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLessThan.xml").toURI() );
            FILE_FIL_COMP_ISLESSOREQUAL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLessThanOrEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISLIKE = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLike_v110.xml").toURI() );
            FILE_FIL_COMP_ISNOTEQUAL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsNotEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISNULL = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsNull.xml").toURI() );
            FILE_FIL_LOG_AND = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_And.xml").toURI() );
            FILE_FIL_LOG_OR = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_Or.xml").toURI() );
            FILE_FIL_LOG_NOT = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_Not.xml").toURI() );
            FILE_FIL_SPA_BBOX = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_BBOX.xml").toURI() );
            FILE_FIL_SPA_BEYOND = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Beyond.xml").toURI() );
            FILE_FIL_SPA_CONTAINS = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Contains.xml").toURI() );
            FILE_FIL_SPA_CROSSES = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Crosses.xml").toURI() );
            FILE_FIL_SPA_DISJOINT = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Disjoint.xml").toURI() );
            FILE_FIL_SPA_DWITHIN = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Disjoint.xml").toURI() );
            FILE_FIL_SPA_EQUALS = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Equals.xml").toURI() );
            FILE_FIL_SPA_INTERSECTS = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Intersects.xml").toURI() );
            FILE_FIL_SPA_OVERLAPS = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Overlaps.xml").toURI() );
            FILE_FIL_SPA_OVERLAPS2 = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Overlaps2.xml").toURI() );
            FILE_FIL_SPA_TOUCHES = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Touches.xml").toURI() );
            FILE_FIL_SPA_WITHIN = new File( OGC110Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Within.xml").toURI() );
        } catch (URISyntaxException ex) { ex.printStackTrace(); }

        assertNotNull(FILE_EXP_ADD);
        assertNotNull(FILE_EXP_SUB);
        assertNotNull(FILE_EXP_MUL);
        assertNotNull(FILE_EXP_DIV);
        assertNotNull(FILE_EXP_PROPERTYNAME);
        assertNotNull(FILE_EXP_LITERAL);
        assertNotNull(FILE_EXP_FUNCTION);

        assertNotNull(FILE_FIL_COMP_ISBETWEEN);
        assertNotNull(FILE_FIL_COMP_ISEQUAL);
        assertNotNull(FILE_FIL_COMP_ISGREATER);
        assertNotNull(FILE_FIL_COMP_ISGREATEROREQUAL);
        assertNotNull(FILE_FIL_COMP_ISLESS);
        assertNotNull(FILE_FIL_COMP_ISLESSOREQUAL);
        assertNotNull(FILE_FIL_COMP_ISLIKE);
        assertNotNull(FILE_FIL_COMP_ISNOTEQUAL);
        assertNotNull(FILE_FIL_COMP_ISNULL);
        assertNotNull(FILE_FIL_LOG_AND);
        assertNotNull(FILE_FIL_LOG_NOT);
        assertNotNull(FILE_FIL_LOG_OR);
        assertNotNull(FILE_FIL_SPA_BBOX);
        assertNotNull(FILE_FIL_SPA_BEYOND);
        assertNotNull(FILE_FIL_SPA_CONTAINS);
        assertNotNull(FILE_FIL_SPA_CROSSES);
        assertNotNull(FILE_FIL_SPA_DISJOINT);
        assertNotNull(FILE_FIL_SPA_DWITHIN);
        assertNotNull(FILE_FIL_SPA_EQUALS);
        assertNotNull(FILE_FIL_SPA_INTERSECTS);
        assertNotNull(FILE_FIL_SPA_OVERLAPS);
        assertNotNull(FILE_FIL_SPA_OVERLAPS2);
        assertNotNull(FILE_FIL_SPA_TOUCHES);
        assertNotNull(FILE_FIL_SPA_WITHIN);

        try{
            TEST_FILE_EXP_ADD = File.createTempFile("test_exp_add_v110", ".xml");
            TEST_FILE_EXP_SUB = File.createTempFile("test_exp_sub_v110", ".xml");
            TEST_FILE_EXP_MUL = File.createTempFile("test_exp_mul_v110", ".xml");
            TEST_FILE_EXP_DIV = File.createTempFile("test_exp_div_v110", ".xml");
            TEST_FILE_EXP_PROPERTYNAME = File.createTempFile("test_exp_propertyname_v110", ".xml");
            TEST_FILE_EXP_LITERAL = File.createTempFile("test_exp_literal_v110", ".xml");
            TEST_FILE_EXP_LITERAL_COLOR = File.createTempFile("test_exp_literal_v110", ".xml");
            TEST_FILE_EXP_FUNCTION = File.createTempFile("test_exp_function_v110", ".xml");

            TEST_FILE_FIL_COMP_ISBETWEEN = File.createTempFile("test_fil_comp_isbetween_v110", ".xml");
            TEST_FILE_FIL_COMP_ISEQUAL = File.createTempFile("test_fil_comp_isequal_v110", ".xml");
            TEST_FILE_FIL_COMP_ISGREATER = File.createTempFile("test_fil_comp_isgreater_v110", ".xml");
            TEST_FILE_FIL_COMP_ISGREATEROREQUAL = File.createTempFile("test_fil_comp_isgreaterorequal_v110", ".xml");
            TEST_FILE_FIL_COMP_ISLESS = File.createTempFile("test_fil_comp_isless_v110", ".xml");
            TEST_FILE_FIL_COMP_ISLESSOREQUAL = File.createTempFile("test_fil_comp_islessorequal_v110", ".xml");
            TEST_FILE_FIL_COMP_ISLIKE = File.createTempFile("test_fil_comp_islike_v110", ".xml");
            TEST_FILE_FIL_COMP_ISNOTEQUAL = File.createTempFile("test_fil_comp_isnotequal_v110", ".xml");
            TEST_FILE_FIL_COMP_ISNULL = File.createTempFile("test_fil_comp_isnull_v110", ".xml");
            TEST_FILE_FIL_LOG_AND = File.createTempFile("test_fil_log_and_v110", ".xml");
            TEST_FILE_FIL_LOG_NOT = File.createTempFile("test_fil_log_not_v110", ".xml");
            TEST_FILE_FIL_LOG_OR = File.createTempFile("test_fil_log_or_v110", ".xml");
            TEST_FILE_FIL_SPA_BBOX = File.createTempFile("test_fil_spa_bbox_v110", ".xml");
            TEST_FILE_FIL_SPA_BEYOND = File.createTempFile("test_fil_spa_beyond_v110", ".xml");
            TEST_FILE_FIL_SPA_CONTAINS = File.createTempFile("test_fil_spa_contains_v110", ".xml");
            TEST_FILE_FIL_SPA_CROSSES = File.createTempFile("test_fil_spa_crosses_v110", ".xml");
            TEST_FILE_FIL_SPA_DISJOINT = File.createTempFile("test_fil_spa_disjoint_v110", ".xml");
            TEST_FILE_FIL_SPA_DWITHIN = File.createTempFile("test_fil_spa_dwithin_v110", ".xml");
            TEST_FILE_FIL_SPA_EQUALS = File.createTempFile("test_fil_spa_equals_v110", ".xml");
            TEST_FILE_FIL_SPA_INTERSECTS = File.createTempFile("test_fil_spa_intersects_v110", ".xml");
            TEST_FILE_FIL_SPA_OVERLAPS = File.createTempFile("test_fil_spa_overlaps_v110", ".xml");
            TEST_FILE_FIL_SPA_OVERLAPS2 = File.createTempFile("test_fil_spa_overlaps2_v110", ".xml");
            TEST_FILE_FIL_SPA_TOUCHES = File.createTempFile("test_fil_spa_touches_v110", ".xml");
            TEST_FILE_FIL_SPA_WITHIN = File.createTempFile("test_fil_spa_within_v110", ".xml");
        }catch(IOException ex){
            ex.printStackTrace();
        }

        //switch to false to avoid temp files to be deleted
        if(true){
            TEST_FILE_EXP_ADD.deleteOnExit();
            TEST_FILE_EXP_SUB.deleteOnExit();
            TEST_FILE_EXP_MUL.deleteOnExit();
            TEST_FILE_EXP_DIV.deleteOnExit();
            TEST_FILE_EXP_PROPERTYNAME.deleteOnExit();
            TEST_FILE_EXP_LITERAL.deleteOnExit();
            TEST_FILE_EXP_LITERAL_COLOR.deleteOnExit();
            TEST_FILE_EXP_FUNCTION.deleteOnExit();

            TEST_FILE_FIL_COMP_ISBETWEEN.deleteOnExit();
            TEST_FILE_FIL_COMP_ISEQUAL.deleteOnExit();
            TEST_FILE_FIL_COMP_ISGREATER.deleteOnExit();
            TEST_FILE_FIL_COMP_ISGREATEROREQUAL.deleteOnExit();
            TEST_FILE_FIL_COMP_ISLESS.deleteOnExit();
            TEST_FILE_FIL_COMP_ISLESSOREQUAL.deleteOnExit();
            TEST_FILE_FIL_COMP_ISLIKE.deleteOnExit();
            TEST_FILE_FIL_COMP_ISNOTEQUAL.deleteOnExit();
            TEST_FILE_FIL_COMP_ISNULL.deleteOnExit();
            TEST_FILE_FIL_LOG_AND.deleteOnExit();
            TEST_FILE_FIL_LOG_NOT.deleteOnExit();
            TEST_FILE_FIL_LOG_OR.deleteOnExit();
            TEST_FILE_FIL_SPA_BBOX.deleteOnExit();
            TEST_FILE_FIL_SPA_BEYOND.deleteOnExit();
            TEST_FILE_FIL_SPA_CONTAINS.deleteOnExit();
            TEST_FILE_FIL_SPA_CROSSES.deleteOnExit();
            TEST_FILE_FIL_SPA_DISJOINT.deleteOnExit();
            TEST_FILE_FIL_SPA_DWITHIN.deleteOnExit();
            TEST_FILE_FIL_SPA_EQUALS.deleteOnExit();
            TEST_FILE_FIL_SPA_INTERSECTS.deleteOnExit();
            TEST_FILE_FIL_SPA_OVERLAPS.deleteOnExit();
            TEST_FILE_FIL_SPA_OVERLAPS2.deleteOnExit();
            TEST_FILE_FIL_SPA_TOUCHES.deleteOnExit();
            TEST_FILE_FIL_SPA_WITHIN.deleteOnExit();
        }

    }


    private static void numberEquals(Number reference, Object candidate){
        assertEquals(reference.doubleValue(),Double.parseDouble(candidate.toString()),DELTA);
    }

    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR EXPRESSION //////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testExpAdd() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_ADD);
        assertNotNull(obj);

        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Add exp = (Add) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);

        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF, DELTA);


        //Write test
        jax = (JAXBElement<BinaryOperatorType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        assertEquals(jax.getName().getLocalPart(), OGCJAXBStatics.EXPRESSION_ADD);

        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_ADD);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpDiv() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_DIV);
        assertNotNull(obj);

        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Divide exp = (Divide) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);

        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF, DELTA);


        //Write test
        jax = (JAXBElement<BinaryOperatorType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        assertEquals(jax.getName().getLocalPart(), OGCJAXBStatics.EXPRESSION_DIV);

        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_DIV);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpLiteral() throws JAXBException{
        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_LITERAL);
        assertNotNull(obj);

        JAXBElement<LiteralType> jax = (JAXBElement<LiteralType>) obj;
        Literal exp = (Literal) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        float val = exp.evaluate( null, Float.class );
        assertEquals(val, valueF, DELTA);

        //Write test
        jax = (JAXBElement<LiteralType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        String str = jax.getValue().getContent().get(0).toString().trim();
        assertEquals(Float.valueOf(str), valueF, DELTA);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_LITERAL);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpLiteralColor() throws JAXBException{
        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_LITERAL_COLOR);
        assertNotNull(obj);

        JAXBElement<LiteralType> jax = (JAXBElement<LiteralType>) obj;
        Literal exp = (Literal) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        Object value = exp.getValue();
        assertEquals(new Color(255, 0, 255), value);

        //Write test
        jax = (JAXBElement<LiteralType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        String str = jax.getValue().getContent().get(0).toString().trim();
        assertEquals("#FF00FF", str);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_LITERAL_COLOR);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpMul() throws JAXBException{
        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_MUL);
        assertNotNull(obj);

        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Multiply exp = (Multiply) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);

        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF, DELTA);


        //Write test
        jax = (JAXBElement<BinaryOperatorType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        assertEquals(jax.getName().getLocalPart(),OGCJAXBStatics.EXPRESSION_MUL);

        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_MUL);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpPropertyName() throws JAXBException{
        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_PROPERTYNAME);
        assertNotNull(obj);

        JAXBElement<PropertyNameType> jax = (JAXBElement<PropertyNameType>) obj;
        PropertyName exp = (PropertyName) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        String val = exp.getPropertyName().trim();
        assertEquals(val, valueStr);

        //Write test
        jax = (JAXBElement<PropertyNameType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        String str = jax.getValue().getContent().trim();
        assertEquals(str, valueStr);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_PROPERTYNAME);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testExpSub() throws JAXBException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_EXP_SUB);
        assertNotNull(obj);

        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Subtract exp = (Subtract) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);

        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);

        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF, DELTA);


        //Write test
        jax = (JAXBElement<BinaryOperatorType>) TRANSFORMER_OGC.extract(exp);
        assertNotNull(jax);

        assertEquals(jax.getName().getLocalPart(), OGCJAXBStatics.EXPRESSION_SUB);

        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);

        MARSHALLER.marshal(jax, TEST_FILE_EXP_SUB);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }



    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR COMPARISON FILTERS //////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFilterComparisonPropertyIsBetween()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISBETWEEN);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        PropertyIsBetween prop = (PropertyIsBetween) filter;
        PropertyName center = (PropertyName) prop.getExpression();
        Literal lower = (Literal) prop.getLowerBoundary();
        Literal upper = (Literal) prop.getUpperBoundary();

        assertEquals( center.getPropertyName() , valueStr);
        assertEquals( lower.evaluate(null, Float.class) , 455f, DELTA);
        assertEquals( upper.evaluate(null, Float.class) , 457f, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsBetweenType pibt = (PropertyIsBetweenType) cot;

        PropertyNameType pnt = (PropertyNameType) pibt.getExpressionType().getValue();
        LiteralType low = (LiteralType) pibt.getLowerBoundary().getExpression().getValue();
        LiteralType up = (LiteralType) pibt.getUpperBoundary().getExpression().getValue();

        assertEquals(valueStr,pnt.getContent());
        numberEquals(455,low.getContent().get(0));
        numberEquals(457,up.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISBETWEEN);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterComparisonPropertyIsEqualTo()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISEQUAL);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISEQUAL);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }

    @Test
    public void testFilterComparisonPropertyIsGreaterThan()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISGREATER);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISGREATER);

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterComparisonPropertyIsGreaterThanOrEqual()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();
        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISGREATEROREQUAL);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISGREATEROREQUAL);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterComparisonPropertyIsLessThan()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISLESS);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLESS);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterComparisonPropertyIsLessThanOrEqual()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISLESSOREQUAL);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLESSOREQUAL);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterComparisonPropertyIsLike()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISLIKE);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        PropertyIsLike prop = (PropertyIsLike) filter;
        PropertyName exp = (PropertyName) prop.getExpression();
        String escape = prop.getEscape();
        String literal = prop.getLiteral();
        String single = prop.getSingleChar();
        String wild = prop.getWildCard();

        assertEquals( exp.getPropertyName() , "LAST_NAME");
        assertEquals( literal , "JOHN*");
        assertEquals( escape , "!");
        assertEquals( single , "#");
        assertEquals( wild , "*");

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsLikeType pibt = (PropertyIsLikeType) cot;

        PropertyNameType lf = pibt.getPropertyName();
        LiteralType lt = pibt.getLiteralType();
        String esc = pibt.getEscapeChar();
        String sin = pibt.getSingleChar();
        String wi = pibt.getWildCard();

        assertEquals(lf.getContent(), "LAST_NAME");
        assertEquals( lt.getContent().get(0).toString().trim() , "JOHN*");
        assertEquals( esc , "!");
        assertEquals( sin , "#");
        assertEquals( wi , "*");

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLIKE);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }

    @Test
    public void testFilterComparisonPropertyIsNotEqualTo()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISNOTEQUAL);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();

        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;

        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISNOTEQUAL);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }

    @Test
    public void testFilterComparisonPropertyIsNull()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_COMP_ISNULL);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        PropertyIsNull prop = (PropertyIsNull) filter;
        PropertyName center = (PropertyName) prop.getExpression();

        assertEquals( center.getPropertyName() , valueStr);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getComparisonOps());

        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsNullType pibt = (PropertyIsNullType) cot;

        PropertyNameType pnt = (PropertyNameType) pibt.getPropertyName();

        assertEquals(pnt.getContent(), valueStr);

        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISNULL);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }



    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR LOGIC FILTERS ///////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFilterLogicalAnd()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_LOG_AND);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        And prop = (And) filter;
        BinaryComparisonOperator leftop =  (BinaryComparisonOperator) prop.getChildren().get(0);
        BinaryComparisonOperator rightop = (BinaryComparisonOperator) prop.getChildren().get(1);

        PropertyName left = (PropertyName) leftop.getExpression1();
        Literal right = (Literal) leftop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 455f, DELTA);

        left = (PropertyName) rightop.getExpression1();
        right = (Literal) rightop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 457f, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getLogicOps());

        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), OGCJAXBStatics.FILTER_LOGIC_AND);
        BinaryLogicOpType pibt = (BinaryLogicOpType) cot;

        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOps().get(0).getValue();
        BinaryComparisonOpType rightoptype = (BinaryComparisonOpType) pibt.getComparisonOps().get(1).getValue();

        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(455,rg.getContent().get(0));

        lf = (PropertyNameType) rightoptype.getExpression().get(0).getValue();
        rg = (LiteralType) rightoptype.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(457,rg.getContent().get(0));


        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_AND);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }

    @Test
    public void testFilterLogicalOr()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_LOG_OR);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        Or prop = (Or) filter;
        BinaryComparisonOperator leftop =  (BinaryComparisonOperator) prop.getChildren().get(0);
        BinaryComparisonOperator rightop = (BinaryComparisonOperator) prop.getChildren().get(1);

        PropertyName left = (PropertyName) leftop.getExpression1();
        Literal right = (Literal) leftop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 455f, DELTA);

        left = (PropertyName) rightop.getExpression1();
        right = (Literal) rightop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 457f, DELTA);

        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getLogicOps());

        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), OGCJAXBStatics.FILTER_LOGIC_OR);
        BinaryLogicOpType pibt = (BinaryLogicOpType) cot;

        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOps().get(0).getValue();
        BinaryComparisonOpType rightoptype = (BinaryComparisonOpType) pibt.getComparisonOps().get(1).getValue();

        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(455,rg.getContent().get(0));

        lf = (PropertyNameType) rightoptype.getExpression().get(0).getValue();
        rg = (LiteralType) rightoptype.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(457,rg.getContent().get(0));


        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_OR);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }

    @Test
    public void testFilterLogicalNot()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_LOG_NOT);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        Not prop = (Not) filter;
        BinaryComparisonOperator subfilter =  (BinaryComparisonOperator) prop.getFilter();

        PropertyName left = (PropertyName) subfilter.getExpression1();
        Literal right = (Literal) subfilter.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF, DELTA);


        //write test
        FilterType ft = TRANSFORMER_OGC.apply(filter);
        assertNotNull(ft.getLogicOps());

        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), OGCJAXBStatics.FILTER_LOGIC_NOT);
        UnaryLogicOpType pibt = (UnaryLogicOpType) cot;

        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOps().getValue();

        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(valueStr,lf.getContent());
        numberEquals(valueF,rg.getContent().get(0));

        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_NOT);
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);

    }



    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR SPATIAL FILTERS /////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testFilterSpatialBBOX() throws JAXBException{

    }

    @Test
    public void testFilterSpatialBeyond() throws JAXBException{

    }

    @Test
    public void testFilterSpatialContains() throws JAXBException{

    }

    @Test
    public void testFilterSpatialCrosses() throws JAXBException{

    }

    @Test
    public void testFilterSpatialDWithin() throws JAXBException{

    }

    @Test
    public void testFilterSpatialDisjoint()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_SPA_DISJOINT);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        Disjoint prop = (Disjoint) filter;
        BinarySpatialOperator subfilter =  (BinarySpatialOperator) prop;

        PropertyName left = (PropertyName) subfilter.getExpression1();
        Literal right = (Literal) subfilter.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertTrue( right.evaluate(null) instanceof Polygon);
        assertEquals( right.evaluate(null).toString().trim() , "POLYGON ((48 18, 48 21, 52 21, 52 18, 48 18))");
        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);
    }

    @Test
    public void testFilterSpatialEquals() throws JAXBException{

    }

    @Test
    public void testFilterSpatialIntersects() throws JAXBException{

    }

    @Test
    public void testFilterSpatialOverlaps()
            throws JAXBException, NoSuchAuthorityCodeException, FactoryException{

        final Unmarshaller UNMARSHALLER = POOL.acquireUnmarshaller();
        final Marshaller MARSHALLER     = POOL.acquireMarshaller();

        //Read test
        Object obj = UNMARSHALLER.unmarshal(FILE_FIL_SPA_OVERLAPS2);
        assertNotNull(obj);

        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);

        Overlaps prop = (Overlaps) filter;
        BinarySpatialOperator subfilter =  (BinarySpatialOperator) prop;

        PropertyName left = (PropertyName) subfilter.getExpression1();
        Literal right = (Literal) subfilter.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null).toString().trim() , "LINESTRING (46.652 10.466, 47.114 11.021, 46.114 12.114, 45.725 12.523)");

        POOL.recycle(MARSHALLER);
        POOL.recycle(UNMARSHALLER);


        //write test - not yet
//        FilterType ft = TRANSFORMER_OGC.apply(filter);
//        assertNotNull(ft.getSpatialOps());
//
//        SpatialOpsType cot = ft.getSpatialOps().getValue();
//        assertEquals( ft.getLogicOps().getName().getLocalPart(), SEJAXBStatics.FILTER_SPATIAL_OVERLAPS);
//        OverlapsType pibt = (OverlapsType) cot;
//
//        BinarySpatialOperator leftoptype = (BinarySpatialOperator) pibt.getComparisonOps().getValue();
//
//        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
//        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();
//
//        assertEquals(lf.getContent(), valueStr);
//        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
//
//        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_SPA_OVERLAPS2);

    }
}
