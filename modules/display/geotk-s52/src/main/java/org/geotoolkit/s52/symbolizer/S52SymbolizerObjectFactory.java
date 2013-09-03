
package org.geotoolkit.s52.symbolizer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class S52SymbolizerObjectFactory {

    private static final QName _PieSymbolizer_QNAME = new QName("http://geotoolkit.org", "S52Symbolizer");

    public S52Symbolizer createPieSymbolizer() {
        return new S52Symbolizer();
    }

    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "S52Symbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<S52Symbolizer> createPolygonSymbolizer(final S52Symbolizer value) {
        return new JAXBElement<>(_PieSymbolizer_QNAME, S52Symbolizer.class, null, value);
    }

}
