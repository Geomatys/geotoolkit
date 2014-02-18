
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.GeometryAdapter;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.primitive.Primitive;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
@XmlType(name="MultiGeometryType", namespace="http://www.opengis.net/gml")
public class JTSMultiPrimitive extends AbstractJTSAggregate<Primitive> implements MultiPrimitive {

    @Override
    public Set<Primitive> getElements() {
        return super.getElements();
    }

    @XmlElement(name="geometryMember", namespace = "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(GeometryAdapter.class)
    @Override
    public void setElements(final Set<Primitive> element) {
        super.setElements(element);
    }
}
