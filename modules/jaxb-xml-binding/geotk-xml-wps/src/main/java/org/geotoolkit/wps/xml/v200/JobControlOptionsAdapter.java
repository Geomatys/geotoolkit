package org.geotoolkit.wps.xml.v200;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
final class JobControlOptionsAdapter extends XmlAdapter<String, JobControlOptions> {

    @Override
    public JobControlOptions unmarshal(String v) throws Exception {
        return v == null? null : JobControlOptions.valueOf(v);
    }

    @Override
    public String marshal(JobControlOptions v) throws Exception {
        return v == null ? null : v.name();
    }

}
