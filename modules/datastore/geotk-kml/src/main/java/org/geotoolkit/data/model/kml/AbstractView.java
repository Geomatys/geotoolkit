package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps AbstractViewGroup element.</p>
 *
 * <br />&lt;element name="AbstractViewGroup" type="kml:AbstractViewType" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="AbstractViewType" abstract="true">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:AbstractViewSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AbstractViewObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AbstractViewSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AbstractViewObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>

 * @author Samuel Andrés
 */
public interface AbstractView extends AbstractObject{

    /**
     *
     * @return the list of AbstractView simple extensions.
     */
    public List<SimpleType> getAbstractViewSimpleExtensions();

    /**
     *
     * @return the list of AbstractView object extensions.
     */
    public List<AbstractObject> getAbstractViewObjectExtensions();

}
