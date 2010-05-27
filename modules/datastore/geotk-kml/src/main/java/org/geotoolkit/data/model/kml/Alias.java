package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps Alias element.</p>
 *
 * <br />&lt;element name="Alias" type="kml:AliasType" substitutionGroup="kml:AbstractObjectGroup"/>
 * <br />&lt;complexType name="AliasType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:targetHref" minOccurs="0"/>
 * <br />&lt;element ref="kml:sourceHref" minOccurs="0"/>
 * <br />&lt;element ref="kml:AliasSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:AliasObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="AliasSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="AliasObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Alias extends AbstractObject {

    /**
     *
     * @return the target href
     */
    public String getTargetHref();

    /**
     *
     * @return the source href
     */
    public String getSourceHref();

    /**
     *
     * @return the list of Alias simple extensions.
     */
    public List<SimpleType> getAliasSimpleExtensions();

    /**
     *
     * @return The list of Alias object extensions.
     */
    public List<AbstractObject> getAliasObjectExtensions();

}
