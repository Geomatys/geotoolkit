/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2015, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2015, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.collection.DefaultTreeTable;
import org.apache.sis.util.collection.TableColumn;
import org.apache.sis.util.collection.TreeTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Note : each setter of this class can throw {@link IllegalArgumentException} if
 * given object appears not to be valid.
 *
 * TODO : possibility to add css
 * TODO : prrogress monitor
 *
 * @author Alexis Manin (Geomatys)
 */
public class HtmlBuilder {

    static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HtmlBuilder.class.getCanonicalName());
    static final String DEFAULT_NAME = "default";

    private Path folder;
    private DatabaseMetaData source;
    private String schema;
    private String catalog;
    private String css;

    /**
     * Set output folder in which html files will be generated.
     * @param folder Path to the wanted output directory. Null not allowed.
     * @return this
     * @throws IOException If given path is not a directory, and we cannot create it.
     */
    public HtmlBuilder setOutput(final Path folder) throws IOException {
        ArgumentChecks.ensureNonNull("Root folder", folder);
        if (!Files.isDirectory(folder)) {
            Files.createDirectories(folder);
        }
        this.folder = folder;

        return this;
    }

    /**
     * Specify which CSS file should be used for styling of generated documents.
     * @param styleSheet URL to the CSS file to use. If it's a file under root
     * folder, relative links will be made into created html files.
     * @return this.
     */
    public HtmlBuilder setStyleSheet(final URL styleSheet) {
        if (styleSheet == null)
            css = null;
        else
            css = styleSheet.toExternalForm();
        return this;
    }

    /**
     * Set source database to analyze.
     * @param source A connection to database whose structure will bee printed. Null not allowed
     * @return this
     * @throws SQLException If we cannot access given database.
     */
    public HtmlBuilder setSource(final Connection source) throws SQLException {
        ArgumentChecks.ensureNonNull("Data to print", source);
        this.source = source.getMetaData();
        if (this.schema != null && !this.source.getSchemas(null, this.schema).next()) {
            this.source = null;
            throw new IllegalArgumentException("Given connection has not any schema named "+this.schema);
        }
        return this;
    }

    /**
     * If set, only tables present in given schema will be analyzed.
     * @param schema The schema to filter database analysis on, or null for no filter.
     * @return this
     * @throws SQLException If source database is set (see {@link #setSource(java.sql.Connection) }), but we cannot check if given schema is present in it.
     */
    public HtmlBuilder setSchema(final String schema) throws SQLException {
        if (schema == null || schema.isEmpty()) {
            this.schema = null;
        } else if (source !=  null && !this.source.getSchemas(null, schema).next()) {
            throw new IllegalArgumentException("Given schema ("+schema+") does not exist in set database.");
        } else {
            this.schema = schema;
        }
        return this;
    }

    /**
     * If set, only tables present in given catalog will be analyzed.
     * @param catalog The catalog to filter database analysis on, or null for no filter.
     * @return this
     * @throws SQLException If source database is set (see {@link #setSource(java.sql.Connection) }), but we cannot check if given catalog is present in it.
     */
    public HtmlBuilder setCatalog(final String catalog) throws SQLException {
        if (catalog == null || catalog.isEmpty()) {
            this.catalog = null;
        } else if (this.source != null) {
            final ResultSet catalogs = source.getCatalogs();
            boolean catalogIsHere = false;
            while (!catalogIsHere && catalogs.next()) {
                if (catalogs.getString("TABLE_CAT").equals(catalog)) {
                    catalogIsHere = true;
                }
            }

            if (!catalogIsHere) {
                throw new IllegalArgumentException("Given catalog cannot be found in set database.");
            }

            this.catalog = catalog;
        } else {
            this.catalog = catalog;
        }
        return this;
    }

    /**
     * TODO : improve index to split entries by schema / index / table type.
     *
     * @return Path to an "index.html" file, referencing every created document.
     * @throws SQLException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws java.io.IOException
     */
    public Path build() throws SQLException, ParserConfigurationException, IOException, TransformerException {
        ArgumentChecks.ensureNonNull("Output directory", folder);
        ArgumentChecks.ensureNonNull("Database connection", source);

        final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = domFactory.newDocumentBuilder();
        final Transformer transformer = createHtmlTransformer();

        final HashMap<String, HashSet<String>> catalogs = new HashMap<>();
        final HashMap<String, HashSet<String>> schemas = new HashMap<>();
        final HashMap<String, Path> generatedFiles = new HashMap<>();

        if (css == null) {
            final Path defaultCss = folder.resolve("defaultStyle.css");
            try (final InputStream stream = HtmlBuilder.class.getResourceAsStream("defaultStyle.css")) {
                Files.copy(stream, defaultCss);
            }
            css = defaultCss.toUri().toURL().toString();
        }

        Path htmlFile;
        Document doc;
        Element body, list, table, row;
        String catalogName, schemaName, tableName, remarks;
        HashSet<String> tmpSet;
        //ResultSet columns, primaryKeys, foreignKeys;
        try (final ResultSet tables = source.getTables(catalog, schema, null, null)) {
            while (tables.next()) {
                catalogName = tables.getString(1);
                if (catalogName == null)
                    catalogName = "default";
                schemaName = tables.getString(2);
                if (schemaName == null)
                    schemaName = "default";
                tableName = tables.getString(3);

                tmpSet = catalogs.get(catalogName);
                if (tmpSet == null) {
                    tmpSet = new HashSet<>();
                    catalogs.put(catalogName, tmpSet);
                }
                tmpSet.add(schemaName);

                tmpSet = schemas.get(schemaName);
                if (tmpSet == null) {
                    tmpSet = new HashSet<>();
                    schemas.put(schemaName, tmpSet);
                }
                tmpSet.add(tableName);

                // ensure tree file is present.
                htmlFile = folder.resolve(catalogName).resolve(schemaName).resolve(tableName + ".html");
                Files.createDirectories(htmlFile.getParent());
                generatedFiles.put(tableName, htmlFile);

                doc = newHtmlDocument(builder, tableName);
                setStyleSheet(doc, htmlFile);

                body = (Element) doc.getElementsByTagName("body").item(0);

                remarks = tables.getString(5);
                if (remarks != null && !remarks.isEmpty()) {
                    appendChild(doc, body, "p", remarks);
                }

                // Display list of primary keys.
                appendChild(doc, body, "h2", BUNDLE.getString("pKeys"));
                try (final ResultSet primaryKeys = source.getPrimaryKeys(catalogName, schemaName, tableName)) {
                    if (primaryKeys.next()) {
                        final String keyName  = primaryKeys.getString(6);
                        if (keyName != null && !keyName.isEmpty())  {
                            appendChild(doc, body, "h3", keyName);
                        }
                        appendChild(doc, body, "h4", BUNDLE.getString("cols"));
                        list = appendChild(doc, body, "ul");
                        appendChild(doc, list, "li", primaryKeys.getString(4));
                        while (primaryKeys.next()) {
                            appendChild(doc, list, "li", primaryKeys.getString(4));
                        }
                    } else {
                        appendChild(doc, body, "p", BUNDLE.getString("noEntry"));
                    }
                }

                // Same for foreign keys
                appendChild(doc, body, "h2", BUNDLE.getString("fKeys"));
                printForeignKeys(doc, body, catalogName, schemaName, tableName, htmlFile.getParent().relativize(folder));

                // Display a table for column descriptions.
                appendChild(doc, body, "h2", BUNDLE.getString("cols"));
                try (final ResultSet columns = source.getColumns(catalogName, schemaName, tableName, null)) {
                    if (columns.next()) {
                        table = appendChild(doc, body, "table");
                        row = appendChild(doc, table, "tr");
                        appendChild(doc, row, "th", BUNDLE.getString("name"));
                        appendChild(doc, row, "th", BUNDLE.getString("desc"));
                        appendChild(doc, row, "th", BUNDLE.getString("type"));
                        appendChild(doc, row, "th", BUNDLE.getString("defaultVal"));
                        appendChild(doc, row, "th", BUNDLE.getString("maxLength"));
                        appendChild(doc, row, "th", BUNDLE.getString("nullable"));
                        appendChild(doc, row, "th", BUNDLE.getString("autoIncrement"));
                        appendChild(doc, row, "th", BUNDLE.getString("generated"));
                        putTableRow(doc, appendChild(doc, table, "tr"), columns);
                        while (columns.next()) {
                            putTableRow(doc, appendChild(doc, table, "tr"), columns);
                        }
                    } else {
                        appendChild(doc, body, "p", BUNDLE.getString("noEntry"));
                    }
                }

                write(doc, htmlFile, transformer);
            }

            /*
             * Once we've created description of all Tables, we make an index to
             * reference all documents.
             */
            // Root
            htmlFile = folder.resolve("index.html");

            final NameComparator comparator = new NameComparator();
            final ArrayList<String> catalogKeys = new ArrayList<>(catalogs.keySet());
            Collections.sort(catalogKeys, comparator);
            doc = newHtmlDocument(builder, BUNDLE.getString("catalogs"));
            setStyleSheet(doc, htmlFile);

            body = (Element) doc.getElementsByTagName("body").item(0);
            Element link, tableList, catDetails, schemaDetails;
            ArrayList<String> schemaKeys, tableKeys;
            for (final String catName : catalogKeys) {
                schemaKeys = new ArrayList<>(catalogs.get(catName));
                Collections.sort(schemaKeys, comparator);

                catDetails = appendChild(doc, body, "details");
                appendChild(doc, appendChild(doc, catDetails, "summary"), "h2", catName);

                // List schemas available in current catalog
                for (final String schemName : schemaKeys) {
                    tableKeys = new ArrayList<>(schemas.get(schemName));
                    Collections.sort(tableKeys, comparator);

                    schemaDetails = appendChild(doc, catDetails, "details");
                    appendChild(doc, appendChild(doc, schemaDetails, "summary"), "h3", schemName);
                    tableList = appendChild(doc, schemaDetails, "ul");

                    // List of tables available in current schema
                    for (final String tName : tableKeys) {
                        link = appendChild(doc, appendChild(doc, tableList, "li"), "a", tName);
                        link.setAttribute("hRef", folder.relativize(generatedFiles.get(tName)).toString());
                    }
                }
            }

            write(doc, htmlFile, transformer);

            return htmlFile;
        }
    }

    /**
     * Set stylesheet link in the given document. If css file is located in the
     * root folder, a relative link is made.
     * @param doc Document to set CSS for.
     * @param docPath Path to the file in which html file will be written.
     * @throws MalformedURLException If root folder path cannot be converted into  URL.
     */
    private void setStyleSheet(final Document doc, final Path docPath) throws MalformedURLException {
        final String folderURL = folder.toUri().toURL().toString();
        final Element link = appendChild(doc, doc.getElementsByTagName("head").item(0), "link");
        link.setAttribute("rel", "stylesheet");
        link.setAttribute("type", "text/css");

        // Set stylesheet
        if (css.startsWith(folderURL)) {
            link.setAttribute("href", docPath.getParent().relativize(folder).resolve(css.substring(folderURL.length())).toString());
        } else {
            link.setAttribute("href", css);
        }
    }

    /**
     * Put description of the column described by the current reult set position into the given html row (tr).
     * @param doc Source document containing html row to edit.
     * @param row html row to edit.
     * @param columns Result set describing columns.
     * @throws SQLException
     */
    private static void putTableRow(final Document doc, final Element row, final ResultSet columns) throws SQLException {
        appendChild(doc, row, "td", columns.getString(4));
        appendChild(doc, row, "td", columns.getString(12));
        appendChild(doc, row, "td", columns.getString(6));
        appendChild(doc, row, "td", columns.getString(13));
        appendChild(doc, row, "td", String.valueOf(columns.getInt(16)));
        appendChild(doc, row, "td", columns.getString(18));
        appendChild(doc, row, "td", columns.getString(23));
        appendChild(doc, row, "td", columns.getString(24));
    }

    /**
     * Just write content of the given document into specified file, using input transformer.
     * @param toWrite Doc to marshal.
     * @param output File to write into (will be created / erased if it doesn't exist)
     * @param writer Transformer to use for writing operation.
     * @throws IOException If output file cannot be opened / written
     * @throws TransformerException If given document cannot be processed by the transformer.
     */
    static void write(final Document toWrite, final Path output, final Transformer writer) throws IOException, TransformerException {
        try (final OutputStream stream = Files.newOutputStream(output)) {
            writer.transform(new DOMSource(toWrite), new StreamResult(stream));
        }
    }

    /**
     * Create a new element with the given name in source document and add it to
     * chosen parent before returning it.
     *
     * Note : no null check performed (performance reason).
     *
     * @param source Source document to create node in.
     * @param parent Parent node to use for new node.
     * @param name Name of the node to create.
     * @return The newly created node.
     */
    static Element appendChild(final Document source, final Node parent, final String name) {
        return appendChild(source, parent, name, null);
    }

    /**
     * Create a new element with the given name in source document and add it to
     * chosen parent before returning it.
     *
     * Note : no null check performed (performance reason).
     *
     * @param source Source document to create node in.
     * @param parent Parent node to use for new node.
     * @param name Name of the node to create.
     * @param textContent If not null, created node will be initiated with it as text content.
     * @return The newly created node.
     */
    static Element appendChild(final Document source, final Node parent, final String name, final String textContent) {
        final Element newbie = source.createElement(name);
        if (textContent != null)
            newbie.setTextContent(textContent);
        parent.appendChild(newbie);
        return newbie;
    }

    /**
     * Create an empty html document with an head and body containing given title.
     * @param builder
     * @param title
     * @return Created document.
     */
    static Document newHtmlDocument(final DocumentBuilder builder, final String title) {
        final Document doc = builder.newDocument();
        final Element html = appendChild(doc, doc, "html");

        // head
        appendChild(doc, appendChild(doc, html, "head"), "title", title);

        // body
        final Element body = appendChild(doc, html, "body");
        appendChild(doc, body, "h1", title);
        return doc;
    }

    /**
     * Create a javax transformer ready to write html documents. A legacy doctype
     * is introduced, because it's the only one that can be generated to be compatible
     * with W3C requirements.
     * @return A transformer, ready to use.
     * @throws TransformerConfigurationException
     */
    static Transformer createHtmlTransformer() throws TransformerConfigurationException {
        final Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "about:legacy-compat");

        return transformer;
    }

    /**
     * A simple comparator to order input names alphabetically, but with {@link #DEFAULT_NAME}
     * as last element.
     */
    public static class NameComparator implements Comparator<String> {

        private final Collator c = Collator.getInstance();

        @Override
        public int compare(String o1, String o2) {
            if (DEFAULT_NAME.equals(o1)) {
                return 1;
            } else if (DEFAULT_NAME.equals(o2)) {
                return -1;
            } else {
                return c.compare(o1, o2);
            }
        }
    }

    public void printForeignKeys(final Document doc, final Element body, final String catalog, final String schema, final String tableName, final Path toRootFolder) throws SQLException {
        /*
         * First, we'll sort foreign key information in a tree :
         * root
         * --> Per imported table
         *         --> All other information
         */
        final TableColumn<Path> pkTable = new TableColumn<>(Path.class, "pk_table");
        final TableColumn<String> pkColumn = new TableColumn<>(String.class, "pk_column");
        final TableColumn<String> fkColumn = new TableColumn<>(String.class, "fk_column");
        final TableColumn<String> fkName = new TableColumn<>(String.class, "fk_name");
        final TableColumn<String> pkName = new TableColumn<>(String.class, "pk_name");

        final DefaultTreeTable ttable = new DefaultTreeTable(pkTable, pkColumn, fkName, pkName, fkColumn);
        TreeTable.Node fkNode;
        Path tablePath;
        try (final ResultSet foreignKeys = source.getImportedKeys(catalog, schema, tableName)) {
            String pkCatName, pkSchemaName, pkTableName;
            while (foreignKeys.next()) {
                pkCatName = foreignKeys.getString(1);
                pkSchemaName = foreignKeys.getString(2);
                pkTableName = foreignKeys.getString(3);
                tablePath = Paths.get(
                        pkCatName == null? DEFAULT_NAME : pkCatName,
                        pkSchemaName == null? DEFAULT_NAME : pkSchemaName,
                        pkTableName);

                fkNode = getOrCreateNode(ttable.getRoot(), pkTable, tablePath).newChild();
                fkNode.setValue(pkColumn, foreignKeys.getString(4));
                fkNode.setValue(fkColumn, foreignKeys.getString(8));
                fkNode.setValue(fkName, foreignKeys.getString(12));
                fkNode.setValue(pkName, foreignKeys.getString(13));
            }
        }


        // Once our tree is built, we can print it
        Element h4, link, list;
        String pkTableName;
        for (final TreeTable.Node tableNode : ttable.getRoot().getChildren()) {
            tablePath = tableNode.getValue(pkTable);
            h4 = appendChild(doc, body, "h4");
            h4.appendChild(doc.createTextNode(BUNDLE.getString("importedFrom") + " "));
            pkTableName = tablePath.getFileName().toString();
            link = appendChild(doc, h4, "a", pkTableName);
            link.setAttribute("href", toRootFolder.resolve(Paths.get(tablePath.toString().concat(".html"))).toString());

            list = appendChild(doc, body, "ul");
            for (final TreeTable.Node colNode : tableNode.getChildren()) {
                appendChild(doc, list, "li", colNode.getValue(fkColumn) +" " + BUNDLE.getString("refers") + " " + pkTableName +"."+colNode.getValue(pkColumn));
            }
        }
    }

    /**
     * Search in given tree node direct children for a node with a table column equals to some specific value.
     * If we cannot find any matching, we add a new child into input node, initiated with the wanted value.
     * @param <T> Table column value type.
     * @param parent Node to search into (only direct children)
     * @param searchCriteria The table column to search in.
     * @param searchValue Searched value.
     * @return A matching node, created if necessary.
     */
    public static <T> TreeTable.Node getOrCreateNode(final TreeTable.Node parent, final TableColumn<T> searchCriteria, final T searchValue) {
        for (final TreeTable.Node n : parent.getChildren()) {
            if (searchValue.equals(n.getValue(searchCriteria))) {
                return n;
            }
        }

        final TreeTable.Node newChild = parent.newChild();
        newChild.setValue(searchCriteria, searchValue);
        return newChild;
    }
}
