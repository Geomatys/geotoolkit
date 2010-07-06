package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleType;

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
 * <pre>
 * &lt;complexType name="BasicLinkType">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractObjectType">
 *          &lt;sequence>
 *              &lt;element ref="kml:href" minOccurs="0"/>
 *              &lt;element ref="kml:BasicLinkSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:BasicLinkObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="BasicLinkSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="BasicLinkObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
 * 
 * @author Samuel Andrés
 */
public interface BasicLink {

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
     * @param idAttributes
     */
    public void setIdAttributes(IdAttributes idAttributes);

    /**
     *
     * @param href
     */
    public void setHref(String href);

    /**
     * 
     * @return
     */
    public Extensions extensions();
}
