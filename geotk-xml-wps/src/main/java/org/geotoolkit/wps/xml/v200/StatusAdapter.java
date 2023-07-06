package org.geotoolkit.wps.xml.v200;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
final class StatusAdapter extends XmlAdapter<String, Status> {

    @Override
    public Status unmarshal(String v) throws Exception {
        return v == null ? null : Status.valueOf(v);
    }

    @Override
    public String marshal(Status v) throws Exception {
        return v == null? null : v.name();
    }
}
