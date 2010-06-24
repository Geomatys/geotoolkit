package org.geotoolkit.data.atom.model;

import java.util.List;

/**
 * <p>This interface maps Atom personConstruct type.</p>
 *
 * <pre>
 * &lt;complexType name="atomPersonConstruct">
 *  &lt;choice minOccurs="0" maxOccurs="unbounded">
 *      &lt;element ref="atom:name"/>
 *      &lt;element ref="atom:uri"/>
 *      &lt;element ref="atom:email"/>
 *  &lt;/choice>
 * &lt;/complexType>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AtomPersonConstruct {

    /**
     *
     * @return
     */
    public List<Object> getParams();

    /**
     * 
     * @param params
     */
    public void setParams(final List<Object> params);
}
