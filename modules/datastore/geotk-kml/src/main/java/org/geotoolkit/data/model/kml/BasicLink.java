package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * <p>This interface maps BasicLinkType type.</p>
 * 
 * <p>This element is an extension of AbstractObject but is not member of
 * the substitution group of this abstract element. So it cannot replace it
 * as java inheritance would allow.</p>
 * 
 * <p>This interface is not an extension of AbstractObject, and 
 * redefines AbstractObject fields.</p>
 *
 * <br />&lt;complexType name="BasicLinkType">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractObjectType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:href" minOccurs="0"/>
 * <br />&lt;element ref="kml:BasicLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:BasicLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="BasicLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="BasicLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface BasicLink {

    /**
     *
     * @return the list of simple extensions.
     */
    public List<SimpleType> getObjectSimpleExtensions();

    /**
     *
     * @return the identification attributes.
     */
    public IdAttributes getIdAttributes();

    /**
     *
     * @return the href.
     */
    public String getHref();

    /**
     *
     * @return the list of BasicLink simple extensions.
     */
    public List<SimpleType> getBasicLinkSimpleExtensions();

    /**
     *
     * @return the list of BasicLink object extensions.
     */
    public List<AbstractObject> getBasicLinkObjectExtensions();
}
