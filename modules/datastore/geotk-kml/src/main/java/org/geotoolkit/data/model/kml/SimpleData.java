package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps SimpleData element.</p>
 *
 * <pre>
 * &lt;element name="SimpleData" type="kml:SimpleDataType"/>
 *
 * &lt;complexType name="SimpleDataType" final="#all">
 *  &lt;simpleContent>
 *      &lt;extension base="string">
 *          &lt;attribute name="name" type="string" use="required"/>
 *      &lt;/extension>
 *  &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface SimpleData {

    /**
     *
     * @return
     */
    public String getName();

    /**
     * 
     * @return
     */
    public String getContent();

}
