package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps ResourceMap element.</p>
 *
 * <br />&lt;element name="ResourceMap" type="kml:ResourceMapType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="ResourceMapType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:Alias" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ResourceMapSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:ResourceMapObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="ResourceMapsimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="ResourceMapObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface ResourceMap extends AbstractObject {

    /**
     *
     * @return
     */
    public List<Alias> getAliases();

    /**
     *
     * @return th elist of ResourceMap simple extensions.
     */
    public List<SimpleType> getResourceMapSimpleExtensions();

    /**
     *
     * @return the list of ResourceMap object extensions.
     */
    public List<AbstractObject> getResourceMapObjectExtensions();
    
}
