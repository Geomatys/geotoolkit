package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps ItemIcon element.</p>
 *
 * <br />&lt;element name="ItemIcon" type="kml:ItemIconType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="ItemIconType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:state" minOccurs="0"/>
 * <br />&lt;element ref="kml:href" minOccurs="0"/>
 * <br />&lt;element ref="kml:ItemIconSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ItemIconObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ItemIconSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="ItemIconObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface ItemIcon extends AbstractObject {

    /**
     *
     * @return the list of icon states.
     */
    public List<ItemIconState> getStates();

    /**
     *
     * @return the href.
     */
    public String getHref();

    /**
     *
     * @return the ItemIcon list of simple extensions.
     */
    public List<SimpleType> getItemIconSimpleExtensions();

    /**
     *
     * @return the ItemIcon list of object extensions.
     */
    public List<AbstractObject> getItemIconObjectExtensions();

}
