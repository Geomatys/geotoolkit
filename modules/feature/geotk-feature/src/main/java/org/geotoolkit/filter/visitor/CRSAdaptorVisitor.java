package org.geotoolkit.filter.visitor;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.binaryspatial.UnreprojectedLooseBBox;
import org.geotoolkit.geometry.DefaultBoundingBox;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.spatial.BBOX;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.util.Utilities;

/**
 * @author Johann Sorel (Geomatys)
 */
public class CRSAdaptorVisitor extends DuplicatingFilterVisitor {

    private final ComplexType dataType;

    public CRSAdaptorVisitor(ComplexType type){
        super();
        this.dataType = type;
    }

    @Override
    public Object visit(BBOX filter, final Object extraData) {

        final Expression exp1 = visit(filter.getExpression1(),extraData);
        final Expression exp2 = filter.getExpression2();
        if(!(exp2 instanceof Literal)){
            //this value is supposed to hold a BoundingBox
            throw new IllegalArgumentException("Illegal BBOX filter, "
                    + "second expression should have been a literal with a boundingBox value: \n" + filter);
        }else{
            CoordinateReferenceSystem targetCrs = null;
            if(exp1 instanceof PropertyName){
                final PropertyName pn = (PropertyName)exp1;
                final PropertyDescriptor desc = dataType.getDescriptor(pn.getPropertyName());
                if(desc != null && desc instanceof GeometryDescriptor){
                    targetCrs = ((GeometryDescriptor) desc).getCoordinateReferenceSystem();
                }
            }

            Literal l = (Literal)visit(exp2,extraData);
            if(targetCrs!=null){
                Object lo = l.getValue();
                try{
                    out:
                    if(lo instanceof Geometry){
                        Geometry geom = (Geometry)lo;
                        final CoordinateReferenceSystem sourceCRS = JTS.findCoordinateReferenceSystem(geom);
                        if (Utilities.equalsIgnoreMetadata(sourceCRS, targetCrs)) break out;
                        final MathTransform trs = CRS.findMathTransform(sourceCRS, targetCrs);
                        geom = JTS.transform(geom, trs);
                        l = ff.literal(geom);
                    }else if(lo instanceof Envelope){
                        Envelope env = CRS.transform((Envelope) lo, targetCrs);
                        l = ff.literal(new DefaultBoundingBox(env));
                    }
                }catch (Exception ex){
                    throw new IllegalArgumentException(ex.getMessage(),ex);
                }
            }

            final Object obj = l.getValue();
            if(obj instanceof BoundingBox){
                if (filter instanceof UnreprojectedLooseBBox) {
                    return new UnreprojectedLooseBBox((PropertyName)exp1, new DefaultLiteral<BoundingBox>((BoundingBox) obj));
                } else if (filter instanceof LooseBBox) {
                    return new LooseBBox((PropertyName)exp1, new DefaultLiteral<BoundingBox>((BoundingBox) obj));
                } else {
                    return getFactory(extraData).bbox(exp1, (BoundingBox) obj);
                }
            }else{
                throw new IllegalArgumentException("Illegal BBOX filter, "
                        + "second expression should have been a literal with a boundingBox value but value was a : \n" + obj.getClass());
            }
        }
    }


}
