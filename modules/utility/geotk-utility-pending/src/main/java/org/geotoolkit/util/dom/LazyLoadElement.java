package org.geotoolkit.util.dom;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.DomUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link org.w3c.dom.Element} to lazy load XML file
 * on element access.
 * This may be used to reduce memory footprint of {@link javax.imageio.metadata.IIOMetadata}
 * when metadata tree is merged with an external metadata xml file.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class LazyLoadElement implements Element {

    private static final Logger LOGGER = Logging.getLogger(LazyLoadElement.class);

    private final File xmlFile;
    private volatile Element element;
    private volatile boolean loaded = false;

    public LazyLoadElement(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    /**
     * Use double check synchronization to ensure Element is loaded once by
     * the first thread using it.
     *
     * @return {@link org.w3c.dom.Element} or null if file can't be read or invalid.
     */
    private Element getElement() {
        if (!loaded) {
            synchronized (this) {
                if (!loaded) {
                    try {
                        Document doc = DomUtilities.read(xmlFile);
                        element = doc.getDocumentElement();
                    } catch (IOException | ParserConfigurationException | SAXException ex) {
                        LOGGER.log(Level.WARNING, "Unable to load file : "+xmlFile, ex);
                    }
                    loaded = true;
                }
            }
        }
        return element;
    }

    ///////////////////////////////////////////
    // Element method implementation
    ///////////////////////////////////////////

    @Override
    public String getTagName() {
        final Element elem = getElement();
        return elem != null ? elem.getTagName() : null;
    }

    @Override
    public String getAttribute(String name) {
        final Element elem = getElement();
        return elem != null ? elem.getAttribute(name) : null;
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.removeAttribute(name);
    }

    @Override
    public Attr getAttributeNode(String name) {
        final Element elem = getElement();
        return elem != null ? elem.getAttributeNode(name) : null;
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.setAttributeNode(newAttr) : null;
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.removeAttributeNode(oldAttr) : null;
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        final Element elem = getElement();
        return elem != null ? elem.getElementsByTagName(name) : null;
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.getAttributeNS(namespaceURI, localName) : null;
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setAttributeNS(namespaceURI, qualifiedName, value);
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.removeAttributeNS(namespaceURI, localName);
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.getAttributeNodeNS(namespaceURI, localName) : null;
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.setAttributeNodeNS(newAttr) : null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.getElementsByTagNameNS(namespaceURI, localName) : null;
    }

    @Override
    public boolean hasAttribute(String name) {
        final Element elem = getElement();
        return elem != null ? elem.hasAttribute(name) : Boolean.FALSE;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.hasAttributeNS(namespaceURI, localName) : Boolean.FALSE;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        final Element elem = getElement();
        return elem != null ? elem.getSchemaTypeInfo() : null;
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setIdAttribute(name, isId);
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setIdAttributeNS(namespaceURI, localName, isId);
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setIdAttributeNode(idAttr, isId);
    }

    ///////////////////////////////////////////
    // Node method implementation
    ///////////////////////////////////////////

    @Override
    public String getNodeName() {
        final Element elem = getElement();
        return elem != null ? elem.getNodeName() : null;
    }

    @Override
    public String getNodeValue() throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.getNodeValue() : null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setNodeValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        final Element elem = getElement();
        return elem != null ? elem.getNodeType() : Node.ELEMENT_NODE;
    }

    @Override
    public Node getParentNode() {
        final Element elem = getElement();
        return elem != null ? elem.getParentNode() : null;
    }

    @Override
    public NodeList getChildNodes() {
        final Element elem = getElement();
        return elem != null ? elem.getChildNodes() : null;
    }

    @Override
    public Node getFirstChild() {
        final Element elem = getElement();
        return elem != null ? elem.getFirstChild() : null;
    }

    @Override
    public Node getLastChild() {
        final Element elem = getElement();
        return elem != null ? elem.getLastChild() : null;
    }

    @Override
    public Node getPreviousSibling() {
        final Element elem = getElement();
        return elem != null ? elem.getPreviousSibling() : null;
    }

    @Override
    public Node getNextSibling() {
        final Element elem = getElement();
        return elem != null ? elem.getNextSibling() : null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        final Element elem = getElement();
        return elem != null ? elem.getAttributes() : null;
    }

    @Override
    public Document getOwnerDocument() {
        final Element elem = getElement();
        return elem != null ? elem.getOwnerDocument() : null;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.insertBefore(newChild, refChild) : null;
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.replaceChild(newChild, oldChild) : null;
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.removeChild(oldChild) : null;
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.appendChild(newChild) : null;
    }

    @Override
    public boolean hasChildNodes() {
        final Element elem = getElement();
        return elem != null ? elem.hasChildNodes() : Boolean.FALSE;
    }

    @Override
    public Node cloneNode(boolean deep) {
        final Element elem = getElement();
        return elem != null ? elem.cloneNode(deep) : null;
    }

    @Override
    public void normalize() {
        final Element elem = getElement();
        if (elem != null) elem.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        final Element elem = getElement();
        return elem != null ? elem.isSupported(feature, version) : Boolean.FALSE;
    }

    @Override
    public String getNamespaceURI() {
        final Element elem = getElement();
        return elem != null ? elem.getNamespaceURI() : null;
    }

    @Override
    public String getPrefix() {
        final Element elem = getElement();
        return elem != null ? elem.getPrefix() : null;
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        final Element elem = getElement();
        return elem != null ? elem.getLocalName() : null;
    }

    @Override
    public boolean hasAttributes() {
        final Element elem = getElement();
        return elem != null ? elem.hasAttributes() : Boolean.FALSE;
    }

    @Override
    public String getBaseURI() {
        final Element elem = getElement();
        return elem != null ? elem.getBaseURI() : null;
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.compareDocumentPosition(other) : 0;
    }

    @Override
    public String getTextContent() throws DOMException {
        final Element elem = getElement();
        return elem != null ? elem.getTextContent() : null;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        final Element elem = getElement();
        if (elem != null) elem.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(Node other) {
        final Element elem = getElement();
        return elem != null ? elem.isSameNode(other) : Boolean.FALSE;
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        final Element elem = getElement();
        return elem != null ? elem.lookupPrefix(namespaceURI) : null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        final Element elem = getElement();
        return elem != null ? elem.isDefaultNamespace(namespaceURI) : Boolean.FALSE;
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        final Element elem = getElement();
        return elem != null ? elem.lookupNamespaceURI(prefix) : null;
    }

    @Override
    public boolean isEqualNode(Node arg) {
        final Element elem = getElement();
        return elem != null ? elem.isEqualNode(arg) : Boolean.FALSE;
    }

    @Override
    public Object getFeature(String feature, String version) {
        final Element elem = getElement();
        return elem != null ? elem.getFeature(feature, version) : null;
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        final Element elem = getElement();
        return elem != null ? elem.setUserData(key, data, handler) : null;
    }

    @Override
    public Object getUserData(String key) {
        final Element elem = getElement();
        return elem != null ? elem.getUserData(key) : null;
    }
}
