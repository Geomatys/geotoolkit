
package org.geotoolkit.style.ogc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import junit.framework.TestCase;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.internal.jaxb.v100.ogc.BinaryComparisonOpType;
import org.geotoolkit.internal.jaxb.v100.ogc.BinaryLogicOpType;
import org.geotoolkit.internal.jaxb.v100.ogc.BinaryOperatorType;
import org.geotoolkit.internal.jaxb.v100.ogc.ComparisonOpsType;
import org.geotoolkit.internal.jaxb.v100.ogc.FilterType;
import org.geotoolkit.internal.jaxb.v100.ogc.FunctionType;
import org.geotoolkit.internal.jaxb.v100.ogc.LiteralType;
import org.geotoolkit.internal.jaxb.v100.ogc.LogicOpsType;
import org.geotoolkit.internal.jaxb.v100.ogc.PropertyIsBetweenType;
import org.geotoolkit.internal.jaxb.v100.ogc.PropertyIsLikeType;
import org.geotoolkit.internal.jaxb.v100.ogc.PropertyIsNullType;
import org.geotoolkit.internal.jaxb.v100.ogc.PropertyNameType;
import org.geotoolkit.internal.jaxb.v100.ogc.UnaryLogicOpType;
import org.geotoolkit.internal.jaxb.v100.sld.ParameterValueType;
import org.geotoolkit.style.xml.GTtoSE100Transformer;
import org.geotoolkit.style.xml.JAXBStatics;
import org.geotoolkit.style.xml.NamespacePrefixMapperImpl;
import org.geotoolkit.style.xml.OGC100toGTTransformer;
import org.opengis.filter.And;
import org.opengis.filter.BinaryComparisonOperator;
import org.opengis.filter.BinaryLogicOperator;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Not;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsBetween;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.PropertyIsLike;
import org.opengis.filter.PropertyIsNull;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;

/**
 * Test class for Filter and Expression jaxb marshelling and unmarshelling.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class OGCforSLD100Test extends TestCase{

    private static final FilterFactory2 FILTER_FACTORY;

    static{
        final Hints hints = new Hints();
        hints.put(Hints.FILTER_FACTORY, FilterFactory2.class);
        FILTER_FACTORY = (FilterFactory2) FactoryFinder.getFilterFactory(hints);
    }

    private static final NamespacePrefixMapperImpl SLD_NAMESPACE = new NamespacePrefixMapperImpl("http://www.opengis.net/sld");
    
    private static Unmarshaller UNMARSHALLER = null;
    private static Marshaller MARSHALLER = null;
    private static OGC100toGTTransformer TRANSFORMER_GT = null;
    private static GTtoSE100Transformer TRANSFORMER_OGC = null;
    
    private static final String valueStr = "feature_property_name";
    private static final float valueF = 456f;
    private static final String valueFStr = "456";
        
    
    //FILES -------------------------------------
    private static File FILE_EXP_ADD = null;
    private static File FILE_EXP_SUB = null;
    private static File FILE_EXP_MUL = null;
    private static File FILE_EXP_DIV = null;
    private static File FILE_EXP_PROPERTYNAME = null;
    private static File FILE_EXP_LITERAL = null;
    private static File FILE_EXP_FUNCTION = null;
    
    private static File TEST_FILE_EXP_ADD = null;
    private static File TEST_FILE_EXP_SUB = null;
    private static File TEST_FILE_EXP_MUL = null;
    private static File TEST_FILE_EXP_DIV = null;
    private static File TEST_FILE_EXP_PROPERTYNAME = null;
    private static File TEST_FILE_EXP_LITERAL = null;
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
    private static File TEST_FILE_FIL_SPA_TOUCHES = null;
    private static File TEST_FILE_FIL_SPA_WITHIN = null;
    
    
    static {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(org.geotoolkit.internal.jaxb.v100.sld.StyledLayerDescriptor.class);
            UNMARSHALLER = jaxbContext.createUnmarshaller();
            MARSHALLER = jaxbContext.createMarshaller();
            MARSHALLER.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",SLD_NAMESPACE);
            MARSHALLER.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            MARSHALLER.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException ex) {ex.printStackTrace();}
        assertNotNull(UNMARSHALLER);
        
        TRANSFORMER_GT = new OGC100toGTTransformer(FILTER_FACTORY);
        assertNotNull(TRANSFORMER_GT);
        
        TRANSFORMER_OGC = new GTtoSE100Transformer();
        assertNotNull(TRANSFORMER_OGC);
        
        try { 
            FILE_EXP_ADD = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Add.xml").toURI() );
            FILE_EXP_SUB = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Sub.xml").toURI() );
            FILE_EXP_MUL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Mul.xml").toURI() );
            FILE_EXP_DIV = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Div.xml").toURI() );
            FILE_EXP_PROPERTYNAME = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_PropertyName.xml").toURI() );
            FILE_EXP_LITERAL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Literal.xml").toURI() );
            FILE_EXP_FUNCTION = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Expression_Function.xml").toURI() );
            
            FILE_FIL_COMP_ISBETWEEN = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsBetween.xml").toURI() );
            FILE_FIL_COMP_ISEQUAL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISGREATER = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsGreaterThan.xml").toURI() );
            FILE_FIL_COMP_ISGREATEROREQUAL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsGreaterThanOrEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISLESS = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLessThan.xml").toURI() );
            FILE_FIL_COMP_ISLESSOREQUAL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLessThanOrEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISLIKE = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsLike_v100.xml").toURI() );
            FILE_FIL_COMP_ISNOTEQUAL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsNotEqualTo.xml").toURI() );
            FILE_FIL_COMP_ISNULL = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Comparison_PropertyIsNull.xml").toURI() );
            FILE_FIL_LOG_AND = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_And.xml").toURI() );
            FILE_FIL_LOG_OR = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_Or.xml").toURI() );
            FILE_FIL_LOG_NOT = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Logical_Not.xml").toURI() );
            FILE_FIL_SPA_BBOX = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_BBOX.xml").toURI() );
            FILE_FIL_SPA_BEYOND = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Beyond.xml").toURI() );
            FILE_FIL_SPA_CONTAINS = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Contains.xml").toURI() );
            FILE_FIL_SPA_CROSSES = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Crosses.xml").toURI() );
            FILE_FIL_SPA_DISJOINT = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_DWithin.xml").toURI() );
            FILE_FIL_SPA_DWITHIN = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Disjoint.xml").toURI() );
            FILE_FIL_SPA_EQUALS = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Equals.xml").toURI() );
            FILE_FIL_SPA_INTERSECTS = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Intersects.xml").toURI() );
            FILE_FIL_SPA_OVERLAPS = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Overlaps.xml").toURI() );
            FILE_FIL_SPA_TOUCHES = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Touches.xml").toURI() );
            FILE_FIL_SPA_WITHIN = new File( OGCforSLD100Test.class.getResource("/org/geotoolkit/sample/Filter_Spatial_Within.xml").toURI() );
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
        assertNotNull(FILE_FIL_SPA_TOUCHES);
        assertNotNull(FILE_FIL_SPA_WITHIN);
        
        try{
            TEST_FILE_EXP_ADD = File.createTempFile("test_exp_add_v100", ".xml");
            TEST_FILE_EXP_SUB = File.createTempFile("test_exp_sub_v100", ".xml");
            TEST_FILE_EXP_MUL = File.createTempFile("test_exp_mul_v100", ".xml");
            TEST_FILE_EXP_DIV = File.createTempFile("test_exp_div_v100", ".xml");
            TEST_FILE_EXP_PROPERTYNAME = File.createTempFile("test_exp_propertyname_v100", ".xml");
            TEST_FILE_EXP_LITERAL = File.createTempFile("test_exp_literal_v100", ".xml");
            TEST_FILE_EXP_FUNCTION = File.createTempFile("test_exp_function_v100", ".xml");

            TEST_FILE_FIL_COMP_ISBETWEEN = File.createTempFile("test_fil_comp_isbetween_v100", ".xml");
            TEST_FILE_FIL_COMP_ISEQUAL = File.createTempFile("test_fil_comp_isequal_v100", ".xml");
            TEST_FILE_FIL_COMP_ISGREATER = File.createTempFile("test_fil_comp_isgreater_v100", ".xml");
            TEST_FILE_FIL_COMP_ISGREATEROREQUAL = File.createTempFile("test_fil_comp_isgreaterorequal_v100", ".xml");
            TEST_FILE_FIL_COMP_ISLESS = File.createTempFile("test_fil_comp_isless_v100", ".xml");
            TEST_FILE_FIL_COMP_ISLESSOREQUAL = File.createTempFile("test_fil_comp_islessorequal_v100", ".xml");
            TEST_FILE_FIL_COMP_ISLIKE = File.createTempFile("test_fil_comp_islike_v100", ".xml");
            TEST_FILE_FIL_COMP_ISNOTEQUAL = File.createTempFile("test_fil_comp_isnotequal_v100", ".xml");
            TEST_FILE_FIL_COMP_ISNULL = File.createTempFile("test_fil_comp_isnull_v100", ".xml");
            TEST_FILE_FIL_LOG_AND = File.createTempFile("test_fil_log_and_v100", ".xml");
            TEST_FILE_FIL_LOG_NOT = File.createTempFile("test_fil_log_not_v100", ".xml");
            TEST_FILE_FIL_LOG_OR = File.createTempFile("test_fil_log_or_v100", ".xml");
            TEST_FILE_FIL_SPA_BBOX = File.createTempFile("test_fil_spa_bbox_v100", ".xml");
            TEST_FILE_FIL_SPA_BEYOND = File.createTempFile("test_fil_spa_beyond_v100", ".xml");
            TEST_FILE_FIL_SPA_CONTAINS = File.createTempFile("test_fil_spa_contains_v100", ".xml");
            TEST_FILE_FIL_SPA_CROSSES = File.createTempFile("test_fil_spa_crosses_v100", ".xml");
            TEST_FILE_FIL_SPA_DISJOINT = File.createTempFile("test_fil_spa_disjoint_v100", ".xml");
            TEST_FILE_FIL_SPA_DWITHIN = File.createTempFile("test_fil_spa_dwithin_v100", ".xml");
            TEST_FILE_FIL_SPA_EQUALS = File.createTempFile("test_fil_spa_equals_v100", ".xml");
            TEST_FILE_FIL_SPA_INTERSECTS = File.createTempFile("test_fil_spa_intersects_v100", ".xml");
            TEST_FILE_FIL_SPA_OVERLAPS = File.createTempFile("test_fil_spa_overlaps_v100", ".xml");
            TEST_FILE_FIL_SPA_TOUCHES = File.createTempFile("test_fil_spa_touches_v100", ".xml");
            TEST_FILE_FIL_SPA_WITHIN = File.createTempFile("test_fil_spa_within_v100", ".xml");
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
            TEST_FILE_FIL_SPA_TOUCHES.deleteOnExit();
            TEST_FILE_FIL_SPA_WITHIN.deleteOnExit();
        }
        
    
    }
    
    private Object unMarshall(File testFile) throws JAXBException{
        return UNMARSHALLER.unmarshal(testFile);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR EXPRESSION //////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public void testExpAdd() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_ADD);
        assertNotNull(obj);
        
        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Add exp = (Add) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);
        
        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF);
        
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<BinaryOperatorType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        assertEquals(jax.getName().getLocalPart(), JAXBStatics.EXPRESSION_ADD);
                
        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_ADD);
    }
    
    public void testExpDiv() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_DIV);
        assertNotNull(obj);
        
        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Divide exp = (Divide) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);
        
        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF);
        
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<BinaryOperatorType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        assertEquals(jax.getName().getLocalPart(), JAXBStatics.EXPRESSION_DIV);
                
        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_DIV);
    }
    
    public void testExpFunction() throws JAXBException{
        final String valueName = "sin";
        
        //Read test
        Object obj = unMarshall(FILE_EXP_FUNCTION);
        assertNotNull(obj);
        
        JAXBElement<FunctionType> jax = (JAXBElement<FunctionType>) obj;
        Function fct = (Function) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(fct);
        
        assertEquals(fct.getName(),valueName);
        
        PropertyName left = (PropertyName) fct.getParameters().get(0);
        assertNotNull(left);
        
        assertEquals(left.getPropertyName(), valueStr);        
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(fct);
        assertNotNull(pvt);
        
        jax = (JAXBElement<FunctionType>) pvt.getContent().get(0);
        assertNotNull(jax);
                        
        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_FUNCTION);
    }
    
    public void testExpLiteral() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_LITERAL);
        assertNotNull(obj);
        
        JAXBElement<LiteralType> jax = (JAXBElement<LiteralType>) obj;
        Literal exp = (Literal) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        float val = exp.evaluate( null, Float.class );
        assertEquals(val, valueF);
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<LiteralType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        String str = jax.getValue().getContent().get(0).toString().trim();
        assertEquals(Float.valueOf(str), valueF);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_LITERAL);
    }
    
    public void testExpMul() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_MUL);
        assertNotNull(obj);
        
        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Multiply exp = (Multiply) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);
        
        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF);
        
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<BinaryOperatorType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        assertEquals(jax.getName().getLocalPart(), JAXBStatics.EXPRESSION_MUL);
                
        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_MUL);
    }
    
    public void testExpPropertyName() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_PROPERTYNAME);
        assertNotNull(obj);
        
        JAXBElement<PropertyNameType> jax = (JAXBElement<PropertyNameType>) obj;
        PropertyName exp = (PropertyName) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        String val = exp.getPropertyName().trim();
        assertEquals(val, valueStr);
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<PropertyNameType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        String str = jax.getValue().getContent().trim();
        assertEquals(str, valueStr);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_PROPERTYNAME);
    }
    
    public void testExpSub() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_EXP_SUB);
        assertNotNull(obj);
        
        JAXBElement<BinaryOperatorType> jax = (JAXBElement<BinaryOperatorType>) obj;
        Subtract exp = (Subtract) TRANSFORMER_GT.visitExpression(jax);
        assertNotNull(exp);
        
        PropertyName left = (PropertyName) exp.getExpression1();
        Literal right = (Literal) exp.getExpression2();
        assertNotNull(left);
        assertNotNull(right);
        
        assertEquals(left.getPropertyName(), valueStr);
        assertEquals(right.evaluate( null, Float.class ), valueF);
        
        
        //Write test
        ParameterValueType pvt = TRANSFORMER_OGC.visitExpression(exp);
        assertNotNull(pvt);
        
        jax = (JAXBElement<BinaryOperatorType>) pvt.getContent().get(0);
        assertNotNull(jax);
        
        assertEquals(jax.getName().getLocalPart(), JAXBStatics.EXPRESSION_SUB);
                
        JAXBElement<PropertyNameType> ele1 = (JAXBElement<PropertyNameType>) jax.getValue().getExpression().get(0);
        JAXBElement<LiteralType> ele2 = (JAXBElement<LiteralType>) jax.getValue().getExpression().get(1);
        
        MARSHALLER.marshal(jax, TEST_FILE_EXP_SUB);
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR COMPARISON FILTERS //////////
    ////////////////////////////////////////////////////////////////////////////
    
    public void testFilterComparisonPropertyIsBetween() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISBETWEEN);
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
        assertEquals( lower.evaluate(null, Float.class) , 455f);
        assertEquals( upper.evaluate(null, Float.class) , 457f);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsBetweenType pibt = (PropertyIsBetweenType) cot;
        
        PropertyNameType pnt = (PropertyNameType) pibt.getExpression().getValue();
        LiteralType low = (LiteralType) pibt.getLowerBoundary().getExpression().getValue();
        LiteralType up = (LiteralType) pibt.getUpperBoundary().getExpression().getValue();

        assertEquals(pnt.getContent(), valueStr);
        assertEquals(low.getContent().get(0).toString().trim(), "455" );
        assertEquals(up.getContent().get(0).toString().trim(), "457" );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISBETWEEN);
    }
    
    public void testFilterComparisonPropertyIsEqualTo() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISEQUAL);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISEQUAL);
        
    }
    
    public void testFilterComparisonPropertyIsGreaterThan() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISGREATER);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISGREATER);
        
    }
    
    public void testFilterComparisonPropertyIsGreaterThanOrEqual() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISGREATEROREQUAL);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISGREATEROREQUAL);
    }
    
    public void testFilterComparisonPropertyIsLessThan() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISLESS);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLESS);
    }
    
    public void testFilterComparisonPropertyIsLessThanOrEqual() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISLESSOREQUAL);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLESSOREQUAL);
    }
    
    public void testFilterComparisonPropertyIsLike() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISLIKE);
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
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsLikeType pibt = (PropertyIsLikeType) cot;
        
        PropertyNameType lf = pibt.getPropertyName();
        LiteralType lt = pibt.getLiteral();
        String esc = pibt.getEscape();
        String sin = pibt.getSingleChar();
        String wi = pibt.getWildCard();

        assertEquals(lf.getContent(), "LAST_NAME");
        assertEquals( lt.getContent().get(0).toString().trim() , "JOHN*");
        assertEquals( esc , "!");
        assertEquals( sin , "#");
        assertEquals( wi , "*");
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISLIKE);
        
    }
    
    public void testFilterComparisonPropertyIsNotEqualTo() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISNOTEQUAL);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        BinaryComparisonOperator prop = (BinaryComparisonOperator) filter;
        PropertyName left = (PropertyName) prop.getExpression1();
        Literal right = (Literal) prop.getExpression2();
        
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        BinaryComparisonOpType pibt = (BinaryComparisonOpType) cot;
        
        PropertyNameType lf = (PropertyNameType) pibt.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) pibt.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISNOTEQUAL);
        
    }
    
    public void testFilterComparisonPropertyIsNull() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_COMP_ISNULL);
        assertNotNull(obj);
        
        JAXBElement<? extends FilterType> jaxfilter = (JAXBElement<? extends FilterType>) obj;
        assertNotNull(jaxfilter);
        Filter filter = TRANSFORMER_GT.visitFilter(jaxfilter.getValue());
        assertNotNull(filter);
        
        PropertyIsNull prop = (PropertyIsNull) filter;
        PropertyName center = (PropertyName) prop.getExpression();
        
        assertEquals( center.getPropertyName() , valueStr);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getComparisonOps());
        
        ComparisonOpsType cot = ft.getComparisonOps().getValue();
        PropertyIsNullType pibt = (PropertyIsNullType) cot;
        
        PropertyNameType pnt = (PropertyNameType) pibt.getPropertyName();

        assertEquals(pnt.getContent(), valueStr);
                
        MARSHALLER.marshal(ft.getComparisonOps(), TEST_FILE_FIL_COMP_ISNULL);
        
    }
    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR LOGIC FILTERS ///////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public void testFilterLogicalAnd() throws JAXBException{
       
        //Read test
        Object obj = unMarshall(FILE_FIL_LOG_AND);
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
        assertEquals( right.evaluate(null, Float.class) , 455f);
        
        left = (PropertyName) rightop.getExpression1();
        right = (Literal) rightop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 457f);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getLogicOps());
        
        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), JAXBStatics.FILTER_LOGIC_AND);
        BinaryLogicOpType pibt = (BinaryLogicOpType) cot;
        
        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOpsOrSpatialOpsOrLogicOps().get(0).getValue();
        BinaryComparisonOpType rightoptype = (BinaryComparisonOpType) pibt.getComparisonOpsOrSpatialOpsOrLogicOps().get(1).getValue();
        
        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), "455" );
        
        lf = (PropertyNameType) rightoptype.getExpression().get(0).getValue();
        rg = (LiteralType) rightoptype.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), "457" );
        
                
        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_AND);
        
    }
    
    public void testFilterLogicalOr() throws JAXBException{
        
        //Read test
        Object obj = unMarshall(FILE_FIL_LOG_OR);
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
        assertEquals( right.evaluate(null, Float.class) , 455f);
        
        left = (PropertyName) rightop.getExpression1();
        right = (Literal) rightop.getExpression2();
        assertEquals( left.getPropertyName() , valueStr);
        assertEquals( right.evaluate(null, Float.class) , 457f);
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getLogicOps());
        
        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), JAXBStatics.FILTER_LOGIC_OR);
        BinaryLogicOpType pibt = (BinaryLogicOpType) cot;
        
        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOpsOrSpatialOpsOrLogicOps().get(0).getValue();
        BinaryComparisonOpType rightoptype = (BinaryComparisonOpType) pibt.getComparisonOpsOrSpatialOpsOrLogicOps().get(1).getValue();
        
        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), "455" );
        
        lf = (PropertyNameType) rightoptype.getExpression().get(0).getValue();
        rg = (LiteralType) rightoptype.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), "457" );
        
                
        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_OR);
        
    }
    
    public void testFilterLogicalNot() throws JAXBException{
     
        //Read test
        Object obj = unMarshall(FILE_FIL_LOG_NOT);
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
        assertEquals( right.evaluate(null, Float.class) , valueF);
        
        
        //write test
        FilterType ft = TRANSFORMER_OGC.visit(filter);
        assertNotNull(ft.getLogicOps());
        
        LogicOpsType cot = ft.getLogicOps().getValue();
        assertEquals( ft.getLogicOps().getName().getLocalPart(), JAXBStatics.FILTER_LOGIC_NOT);
        UnaryLogicOpType pibt = (UnaryLogicOpType) cot;
        
        BinaryComparisonOpType leftoptype = (BinaryComparisonOpType) pibt.getComparisonOps().getValue();
        
        PropertyNameType lf = (PropertyNameType) leftoptype.getExpression().get(0).getValue();
        LiteralType rg = (LiteralType) leftoptype.getExpression().get(1).getValue();

        assertEquals(lf.getContent(), valueStr);
        assertEquals(rg.getContent().get(0).toString().trim(), valueFStr );
         
        MARSHALLER.marshal(ft.getLogicOps(), TEST_FILE_FIL_LOG_NOT);
        
    }
    
    
 
    ////////////////////////////////////////////////////////////////////////////
    // JAXB TEST MARSHELLING AND UNMARSHELLING FOR SPATIAL FILTERS /////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public void testFilterSpatialBBOX() throws JAXBException{
        
    }
    
    public void testFilterSpatialBeyond() throws JAXBException{
        
    }
    
    public void testFilterSpatialContains() throws JAXBException{
        
    }
    
    public void testFilterSpatialCrosses() throws JAXBException{
        
    }
    
    public void testFilterSpatialDWithin() throws JAXBException{
        
    }
    
    public void testFilterSpatialDisjoint() throws JAXBException{
        
    }
    
    public void testFilterSpatialEquals() throws JAXBException{
        
    }
    
    public void testFilterSpatialIntersects() throws JAXBException{
        
    }
    
    public void testFilterSpatialOverlaps() throws JAXBException{
        
    }
    
    public void testFilterSpatialTouches() throws JAXBException{
        
    }
    
    public void testFilterSpatialWithin() throws JAXBException{
        
    }
    
}
