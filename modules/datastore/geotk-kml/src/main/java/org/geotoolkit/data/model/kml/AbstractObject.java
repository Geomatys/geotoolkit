package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;
import java.util.List;

/**
 * <p>This interface maps an AbstractObjectGroup element.</p>
 *
 * <br />&lt;element name="AbstractObjectGroup" type="kml:AbstractObjectType" abstract="true"/>
 * <br />&lt;complexType name="AbstractObjectType" abstract="true">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:ObjectSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;attributeGroup ref="kml:idAttributes"/>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ObjectSimpleExtensionGroup" abstract="true" type="anySimpleType"/>

 * @author Samuel Andrés
 */
public interface AbstractObject {

    /**
     *
     * @return The list of simple extensions.
     */
    public List<SimpleType> getObjectSimpleExtensions();

    /**
     *
     * @return The identification attributes.
     */
    public IdAttributes getIdAttributes();
}
