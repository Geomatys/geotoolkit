package org.geotoolkit.wps.xml.v200;

import org.geotoolkit.ows.xml.v200.CapabilitiesBaseType;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class FilterV2<V> extends FilterByVersion<V> {

    @Override
    public V marshal(V v) throws Exception {
        return isV2() ? v : null;
    }

    static class ProcessDescription extends FilterV2<org.geotoolkit.wps.xml.v200.ProcessDescription> {}

    static class String extends FilterV2<java.lang.String> {}

    static class Boolean extends FilterV2<java.lang.Boolean> {}

    static class Integer extends FilterV2<java.lang.Integer> {}

    static class JobControlOptions extends FilterV2<org.geotoolkit.wps.xml.v200.JobControlOptions> {}

    static class DataTransmissionMode extends FilterV2<org.geotoolkit.wps.xml.v200.DataTransmissionMode> {}

    static class CapabilitiesExtension extends FilterV2<Capabilities.Extension> {}

    static class CapabilitiesLanguages extends FilterV2<CapabilitiesBaseType.Languages> {}
}
