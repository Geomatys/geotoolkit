package org.geotoolkit.data.model.xal;

/**
 *
 * <p>This interface maps PremiseNumberPrefix element.</p>
 *
 * <p>A in A12</p>
 *
 * <pre>
 * &lt;xs:element name="PremiseNumberPrefix">
 *  &lt;xs:complexType>
 *      &lt;xs:simpleContent>
 *          &lt;xs:extension base="xs:string">
 *              &lt;xs:attribute name="NumberPrefixSeparator">...
 *              &lt;/xs:attribute>
 *              &lt;xs:attribute name="Type"/>
 *              &lt;xs:attributeGroup ref="grPostal"/>
 *              &lt;xs:anyAttribute namespace="##other"/>
 *          &lt;/xs:extension>
 *      &lt;/xs:simpleContent>
 *  &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface PremiseNumberPrefix extends GenericTypedGrPostal {

    /**
     * <p>A-12 where 12 is number and A is prefix and "-" is the separator.</p>
     *
     * @return
     */
    public String getNumberPrefixSeparator();

}
