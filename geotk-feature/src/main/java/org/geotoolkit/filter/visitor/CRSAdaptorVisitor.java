package org.geotoolkit.filter.visitor;

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.binaryspatial.LooseBBox;
import org.geotoolkit.filter.binaryspatial.UnreprojectedLooseBBox;
import org.geotoolkit.geometry.jts.JTS;
import org.apache.sis.referencing.CRS;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.filter.ValueReference;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.apache.sis.geometry.Envelopes;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.SpatialOperator;
import org.opengis.filter.SpatialOperatorName;

/**
 * @author Johann Sorel (Geomatys)
 */
public class CRSAdaptorVisitor extends DuplicatingFilterVisitor {

    private final FeatureType dataType;

    public CRSAdaptorVisitor(FeatureType type){
        super();
        this.dataType = type;
        setFilterHandler(SpatialOperatorName.BBOX, (f) -> {
            final SpatialOperator<Object> filter = (SpatialOperator<Object>) f;
            final Expression exp1 = (Expression) visit(filter.getExpressions().get(0));
            final Expression exp2 = filter.getExpressions().get(1);
            if (!(exp2 instanceof Literal)) {
                //this value is supposed to hold a BoundingBox
                throw new IllegalArgumentException("Illegal BBOX filter, "
                        + "second expression should have been a literal with a boundingBox value: \n" + filter);
            } else {
                CoordinateReferenceSystem targetCrs = null;
                if (exp1 instanceof ValueReference) {
                    final ValueReference pn = (ValueReference)exp1;
                    try{
                        final PropertyType desc = dataType.getProperty(pn.getXPath());
                        if(desc instanceof AttributeType){
                            targetCrs = FeatureExt.getCRS(desc);
                        }
                    } catch (PropertyNotFoundException ex) {/* not important*/}
                }
                Literal l = (Literal) visit(exp2);
                if (targetCrs != null) {
                    Object lo = l.getValue();
                    try {
                        out:
                        if (lo instanceof Geometry) {
                            Geometry geom = (Geometry)lo;
                            final CoordinateReferenceSystem sourceCRS = JTS.findCoordinateReferenceSystem(geom);
                            if (CRS.equivalent(sourceCRS, targetCrs)) break out;
                            final MathTransform trs = CRS.findOperation(sourceCRS, targetCrs, null).getMathTransform();
                            geom = org.apache.sis.geometry.wrapper.jts.JTS.transform(geom, trs);
                            l = ff.literal(geom);
                        } else if (lo instanceof Envelope) {
                            Envelope env = Envelopes.transform((Envelope) lo, targetCrs);
                            l = ff.literal(env);
                        }
                    } catch (Exception ex){
                        throw new IllegalArgumentException(ex.getMessage(), ex);
                    }
                }
                final Object obj = l.getValue();
                if (obj instanceof Envelope env) {
                    FilterFactory ff = FilterUtilities.FF;
                    if (filter instanceof UnreprojectedLooseBBox) {
                        return new UnreprojectedLooseBBox((ValueReference)exp1, ff.literal(env));
                    } else if (filter instanceof LooseBBox) {
                        return new LooseBBox((ValueReference)exp1, ff.literal(env));
                    } else {
                        return ff.bbox(exp1, env);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal BBOX filter, "
                            + "second expression should have been a literal with a boundingBox value but value was a : \n" + obj.getClass());
                }
            }
        });
    }
}
