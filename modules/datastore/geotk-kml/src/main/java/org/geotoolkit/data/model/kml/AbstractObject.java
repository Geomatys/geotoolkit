package org.geotoolkit.data.model.kml;

import org.geotoolkit.data.model.xsd.SimpleType;
import java.util.List;

/**
 * <p>This interface maps an AbstractObjectGroup element.</p>
 *
 * <pre>
 * &lt;element name="AbstractObjectGroup" type="kml:AbstractObjectType" abstract="true"/>
 *
 * &lt;complexType name="AbstractObjectType" abstract="true">
 *  &lt;sequence>
 *      &lt;element ref="kml:ObjectSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attributeGroup ref="kml:idAttributes"/>
 * &lt;/complexType>
 *
 * &lt;element name="ObjectSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * </pre>
 *
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

    /**
     * 
     * @param objectSimpleExtentions
     */
    public void setObjectSimpleExtensions(List<SimpleType> objectSimpleExtentions);

    /**
     *
     * @param idAttributes
     */
    public void setIdAttributes(IdAttributes idAttributes);
}
