

package org.geotoolkit.internal.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.primitive.JTSRing;
import org.opengis.geometry.primitive.Ring;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RingAdapter extends XmlAdapter<RingAdapter, Ring> {

    @XmlElement(name = "LinearRing", namespace = "http://www.opengis.net/gml")
    private LinearRingPosListType ring;
    
    public RingAdapter() {
        
    }
    
    public RingAdapter(JTSRing ring) {
        this.ring = new LinearRingPosListType(ring);
    }

    @Override
    public Ring unmarshal(RingAdapter v) throws Exception {
        if (v != null && v.ring != null) {
            return v.ring.getJTSRing();
        }
        return null;
    }

    @Override
    public RingAdapter marshal(Ring v) throws Exception {
        if (v instanceof JTSRing)
            return new RingAdapter((JTSRing) v);
        return null;
    }

}