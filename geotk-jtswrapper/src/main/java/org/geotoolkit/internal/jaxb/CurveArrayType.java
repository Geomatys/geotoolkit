
package org.geotoolkit.internal.jaxb;

import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.opengis.geometry.primitive.CurveSegment;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CurveArrayType {

    /**
     * Component parts of the Curve.  Each element must implement CurveSegment.
     */
    @XmlElement(name="LineStringSegment", namespace= "http://www.opengis.net/gml")
    @XmlJavaTypeAdapter(CurveSegmentAdapter.class)
    private List<CurveSegment> curveSegments;


    public CurveArrayType()  {

    }

    public CurveArrayType(final List<CurveSegment> curveSegments)  {
        this.curveSegments = curveSegments;
    }

    /**
     * @return the curveSegments
     */
    public List<CurveSegment> getCurveSegments() {
        return curveSegments;
    }

    /**
     * @param curveSegments the curveSegments to set
     */
    public void setCurveSegments(final List<CurveSegment> curveSegments) {
        this.curveSegments = curveSegments;
    }
}
