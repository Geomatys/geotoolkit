package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps SimpleField element.</p>
 *
 * <pre>
 * &lt;element name="SimpleField" type="kml:SimpleFieldType"/>
 *
 * &lt;complexType name="SimpleFieldType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:displayName" minOccurs="0"/>
 *      &lt;element ref="kml:SimpleFieldExtension" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="type" type="string"/>
 *  &lt;attribute name="name" type="string"/>
 *  &lt;/complexType>
 * 
 *  &lt;element name="SimpleFieldExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SimpleField {

    /**
     *
     * @return
     */
    public Object getDisplayName();

    /**
     *
     * @return
     */
    public String getType();

    /**
     * 
     * @return
     */
    public String getName();

    /**
     *
     * @return
     */
    public List<Object> getSimpleFieldExtensions();

    /**
     *
     * @param displayName
     */
    public void setDisplayName(Object displayName);

    /**
     *
     * @param type
     */
    public void setType(String type);

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     * 
     * @param simpleFieldExtensions
     */
    public void setSimpleFieldExtensions(List<Object> simpleFieldExtensions);
}
