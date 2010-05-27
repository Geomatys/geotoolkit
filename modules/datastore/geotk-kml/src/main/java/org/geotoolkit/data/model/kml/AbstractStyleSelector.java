package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractStyleSelectorGroup element.</p>
 *
 * <br />&lt;element name="AbstractStyleSelectorGroup" type="kml:AbstractStyleSelectorType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="AbstractStyleSelectorType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractStyleSelectorSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractStyleSelectorObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractStyleSelectorSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractStyleSelectorObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface AbstractStyleSelector extends AbstractObject {

    /**
     *
     * @return the list of AbstractStyleSelector simple extensions
     */
    public List<SimpleType> getAbstractStyleSelectorSimpleExtensions();

    /**
     *
     * @return the list of AbstractStyle Selector object extensions.
     */
    public List<AbstractObject> getAbstractStyleSelectorObjectExtensions();

}
