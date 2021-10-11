
package org.geotoolkit.wps.xml.v200;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;

import static org.geotoolkit.wps.xml.WPSMarshallerPool.*;

/**
 * Transform WPS 2 bindings into WPS 1 xml information. What we look at :
 * <ul>
 * <li>WPS Namespaces</li>
 * <li>OWS Namespaces</li>
 * <li>Element names</li>
 * </ul>
 *
 * @implNote This object is NOT thread-safe.
 *
 * @author Alexis Manin (Geomatys)
 */
abstract class LegacyTransformer {

    private static final Map<String, String> REPLACEMENTS_V1 = getElementsV1();
    private static final Map<String, String> REPLACEMENTS_V2 = getElementsV2();

    final ArrayDeque<String> context = new ArrayDeque<>();

    final XMLEventFactory eventFactory = XMLEventFactory.newFactory();

    String exportNS(final String javaNS) {
        switch (javaNS) {
            case OWS_2_0_NAMESPACE:
                return OWS_1_1_NAMESPACE;
            case WPS_2_0_NAMESPACE:
                return WPS_1_0_NAMESPACE;
        }

        return javaNS;
    }

    String importNS(final String xmlNS) {
        switch (xmlNS) {
            case OWS_1_1_NAMESPACE:
                return OWS_2_0_NAMESPACE;
            case WPS_1_0_NAMESPACE:
                return WPS_2_0_NAMESPACE;
        }

        return xmlNS;
    }

    String exportLocalName(final String localName) {
        return map(localName, REPLACEMENTS_V2);
    }

    String importLocalName(final String localName) {
        return map(localName, REPLACEMENTS_V1);
    }

    private String map(final String localName, final Map<String, String> mappingTable) {
        final String parent = context.peekLast();
        if (parent != null) {
            final String result = mappingTable.get(parent + '.' + localName);
            if (result != null) {
                return result;
            }
        }

        return mappingTable.getOrDefault(localName, localName);
    }

    QName exportName(final QName origin) {
        final String newNS = exportNS(origin.getNamespaceURI());
        final String newLocal = exportLocalName(origin.getLocalPart());
        if (Objects.equals(newNS, origin.getNamespaceURI()) && Objects.equals(newLocal, origin.getLocalPart())) {
            return origin;
        }

        return new QName(newNS, newLocal, origin.getPrefix());
    }

    QName importName(final QName origin) {
        String newNS = importNS(origin.getNamespaceURI());
        final String newLocal = importLocalName(origin.getLocalPart());
        if (XMLConstants.NULL_NS_URI.equals(newNS)) {
            switch (newLocal) {
                case "LiteralData":
                case "ComplexData":
                case "DefaultValue": {
                    newNS = WPS_2_0_NAMESPACE;
                    break;
                }
            }
        }
        if (Objects.equals(newNS, origin.getNamespaceURI()) && Objects.equals(newLocal, origin.getLocalPart())) {
            return origin;
        }

        return new QName(newNS, newLocal, origin.getPrefix());
    }

    private static String getLastPart(final String source) {
        int lastPoint = source.lastIndexOf('.');
        return lastPoint < 0 ? source : source.substring(lastPoint+1);
    }

    /**
     * Defines mapping between WPS v1 types to WPS v2 types.
     *
     * Key = [WPS v1 parent element (optional)].[WPS v1 type]
     * Val = [WPS v2 parent element (optional)].[WPS v2 type]
     */
    private static Map<String, String> getMapping() {
        final HashMap<String, String> replacements = new HashMap<>(12);
        // First, replacements happening on root elements or in any contexts
        replacements.put("ExecuteResponse", "Result");
        replacements.put("CRS", "supportedCRS");
        replacements.put("ProcessDescriptions", "ProcessOfferings");

        // Then, replacements in special contexts
        replacements.put("Capabilities.ProcessOfferings", "Capabilities.Contents");
        replacements.put("Data.BoundingBoxData", "Data.BoundingBox");
        replacements.put("Data.LiteralData", "Data.LiteralValue");
        replacements.put("Output.ComplexOutput", "Output.ComplexData");
        replacements.put("Output.LiteralOutput", "Output.LiteralData");
        replacements.put("Output.BoundingBoxOutput", "Output.BoundingBoxData");
        replacements.put("ProcessOfferings.Process", "Contents.ProcessSummary");

        return Collections.unmodifiableMap(replacements);
    }

    private static Map<String, String> getElementsV1() {
        final Map<String, String> v1 = getMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> getLastPart(entry.getValue())));
        return Collections.unmodifiableMap(v1);
    }

    private static Map<String, String> getElementsV2() {
        final Map<String, String> v2 =  getMapping().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, entry -> getLastPart(entry.getKey())));
        return Collections.unmodifiableMap(v2);
    }
}
