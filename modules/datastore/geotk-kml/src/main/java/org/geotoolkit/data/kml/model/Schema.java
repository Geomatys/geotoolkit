package org.geotoolkit.data.kml.model;

import java.util.List;

/**
 * <p>This interface maps Schema element.</p>
 *
 * <pre>
 * &lt;element name="Schema" type="kml:SchemaType"/>
 *
 * &lt;complexType name="SchemaType" final="#all">
 *  &lt;sequence>
 *      &lt;element ref="kml:SimpleField" minOccurs="0" maxOccurs="unbounded"/>
 *      &lt;element ref="kml:SchemaExtension" minOccurs="0" maxOccurs="unbounded"/>
 *  &lt;/sequence>
 *  &lt;attribute name="name" type="string"/>
 *  &lt;attribute name="id" type="ID"/>
 * &lt;/complexType>
 *
 * &lt;element name="SchemaExtension" abstract="true"/>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface Schema {

    /**
     *
     * @return
     */
    public List<SimpleField> getSimpleFields();

    /**
     *
     * @return
     */
    public String getName();

    /**
     * 
     * @return
     */
    public String getId();

    /**
     *
     * @return
     */
    public List<Object> getSchemaExtensions();

    /**
     *
     * @param simpleFields
     */
    public void setSimpleFields(List<SimpleField> simpleFields);

    /**
     *
     * @param name
     */
    public void setName(String name);

    /**
     *
     * @param id
     */
    public void setId(String id);

    /**
     * 
     * @param schemaExtensions
     */
    public void setSchemaExtensions(List<Object> schemaExtensions);
}
