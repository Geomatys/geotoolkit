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

    /**
     * 
     * @param objectSimpleExtensions
     */
    public void setObjectSimpleExtensions(List<SimpleType> objectSimpleExtensions);

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
     * @param basicLinkSimpleExtensions
     */
    public void setBasicLinkSimpleExtensions(List<SimpleType> basicLinkSimpleExtensions);

    /**
     *
     * @param basicLinkObjectExtensions
     */
    public void setBasicLinkObjectExtensions(List<AbstractObject> basicLinkObjectExtensions);
}
