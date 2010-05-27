package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractSubStyle element.</p>
 *
 * <br />&lt;element name="AbstractSubStyleGroup" type="kml:AbstractSubStyleType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="AbstractSubStyleType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractSubStyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractSubStyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractSubStyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractSubStyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface AbstractSubStyle extends AbstractObject {

    /**
     *
     * @return The AbstractSubStyle simple extensions.
     */
    public List<SimpleType> getSubStyleSimpleExtensions();

    /**
     *
     * @return The AbstractSubStyle object extensions.
     */
    public List<AbstractObject> getSubStyleObjectExtensions();
}
