package org.geotoolkit.wps.xml.v200;

import static org.geotoolkit.wps.xml.v200.FilterByVersion.isV1;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
class FilterV1<V> extends FilterByVersion<V> {

    @Override
    public V marshal(V v) throws Exception {
        return isV1() ? v : null;
    }

    static class Boolean extends FilterV1<java.lang.Boolean> {}

    static class ResponseForm extends FilterV1<org.geotoolkit.wps.xml.v200.Execute.ResponseForm> {}

    static class SupportedUOMs extends FilterV1<org.geotoolkit.wps.xml.v100.SupportedUOMs> {}

    static class ProcessSummary extends FilterV1<org.geotoolkit.wps.xml.v200.ProcessSummary> {}

    static class CodeType extends FilterV1<org.geotoolkit.ows.xml.v200.CodeType> {}

    // Warning works only for XMLAttribute, not XMLElement
    static class String extends FilterV1<java.lang.String> {}

    static class WSDL extends FilterV1<org.geotoolkit.wps.xml.v100.WSDL> {}

    static class LegacyStatus extends FilterV1<org.geotoolkit.wps.xml.v100.LegacyStatus> {}

    static class XMLGregorianCalendar extends FilterV1<javax.xml.datatype.XMLGregorianCalendar> {}
}
