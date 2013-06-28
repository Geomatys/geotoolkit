
package org.geotoolkit.display2d.ext.cellular;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRegistry
public class CellSymbolizerObjectFactory {
    
    private static final QName _CellSymbolizer_QNAME = new QName("http://geotoolkit.org", "CellSymbolizer");
    
    public CellSymbolizer createCellSymbolizer() {
        return new CellSymbolizer();
    }
    
    @XmlElementDecl(namespace = "http://geotoolkit.org", name = "CellSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<CellSymbolizer> createCellSymbolizer(final CellSymbolizer value) {
        return new JAXBElement<CellSymbolizer>(_CellSymbolizer_QNAME, CellSymbolizer.class, null, value);
    }
    
}
