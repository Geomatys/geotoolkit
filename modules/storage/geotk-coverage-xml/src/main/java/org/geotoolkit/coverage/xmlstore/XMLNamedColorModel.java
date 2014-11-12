

package org.geotoolkit.coverage.xmlstore;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLNamedColorModel {
    
    @XmlElement(name="Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
