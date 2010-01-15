

package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.GeometryAdapter;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.primitive.OrientableCurve;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
@XmlType(name="MultiCurveType", namespace="http://www.opengis.net/gml")
public class JTSMultiCurve extends AbstractJTSAggregate<OrientableCurve> implements MultiCurve {

    public JTSMultiCurve() {
        this(null);
    }

    public JTSMultiCurve(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSMultiCurve clone() {
        return (JTSMultiCurve) super.clone();
    }

    @XmlElement(name="curveMember", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(GeometryAdapter.class)
    @Override
    public Set<OrientableCurve> getElements() {
        return super.getElements();
    }

    public double length() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
