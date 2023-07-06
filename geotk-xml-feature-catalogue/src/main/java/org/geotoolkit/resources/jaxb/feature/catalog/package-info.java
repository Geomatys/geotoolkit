/**
 * <p>JAXB designed package for adapters. </p>
 * <p>This package regroups all adapters which makes a link between interfaces and their
 * implementation. We must use adapters, since JAXB is not able to annotate interfaces.
 * Consequently the goal of these adapters is to replace interfaces.<br/>
 * Each time JAXB is trying to deal with an interface, the adapter will get in action
 * in order to do the same process, but on the implementing class.
 * </p>
 *
 * @see jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 * @author Cédric Briançon
 */
@XmlSchema(elementFormDefault= XmlNsForm.QUALIFIED,
namespace="http://www.isotc211.org/2005/gfc",
xmlns = {
    @XmlNs(prefix = "gfc", namespaceURI = "http://www.isotc211.org/2005/gfc"),
    @XmlNs(prefix = "gmd", namespaceURI = "http://www.isotc211.org/2005/gmd"),
    @XmlNs(prefix = "gco", namespaceURI = "http://www.isotc211.org/2005/gco"),
    @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance")
})
@XmlAccessorType(XmlAccessType.NONE)
package org.geotoolkit.resources.jaxb.feature.catalog;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;
