

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

    @XmlElement(name = "Ring", namespace = "http://www.opengis.net/gml")
    private JTSRing ring;
    
    public RingAdapter() {
        
    }
    
    public RingAdapter(JTSRing ring) {
        this.ring = ring;
    }

    @Override
    public Ring unmarshal(RingAdapter v) throws Exception {
        if (v != null)
            return v.ring;
        return null;
    }

    @Override
    public RingAdapter marshal(Ring v) throws Exception {
        if (v instanceof JTSRing)
            return new RingAdapter((JTSRing) v);
        return null;
    }

}