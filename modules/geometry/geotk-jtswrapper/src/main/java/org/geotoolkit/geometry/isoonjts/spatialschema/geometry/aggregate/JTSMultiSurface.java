

package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.GeometryAdapter;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class JTSMultiSurface extends AbstractJTSAggregate<OrientableSurface> implements MultiSurface {

    public JTSMultiSurface() {
        this(null);
    }

    public JTSMultiSurface(final CoordinateReferenceSystem crs) {
        super(crs);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public JTSMultiCurve clone() {
        return (JTSMultiCurve) super.clone();
    }

    @XmlElement(name="surfaceMember", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(GeometryAdapter.class)
    @Override
    public Set<OrientableSurface> getElements() {
        return super.getElements();
    }

    public double length() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getArea() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
