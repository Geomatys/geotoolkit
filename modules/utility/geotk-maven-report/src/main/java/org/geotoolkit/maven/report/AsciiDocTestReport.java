/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2021, Geomatys
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
package org.geotoolkit.maven.report;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * JUnit test listener building individual test reports usable in an
 * asciidoc document.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class AsciiDocTestReport extends RunListener {

    public static final String STATUS_SUCCESS_EN = "SUCCESS";
    public static final String STATUS_FAILED_EN = "FAILED";
    public static final String STATUS_IGNORED_EN = "IGNORED";

    public static final String STATUS_SUCCESS_FR = "SUCCÈS";
    public static final String STATUS_FAILED_FR = "ÉCHOUÉ";
    public static final String STATUS_IGNORED_FR = "IGNORÉ";

    public static final String INFO_TIMEMS = "timems";
    public static final String INFO_TIME_PRETTY = "time_pretty";
    public static final String INFO_STATUS_EN = "status";
    public static final String INFO_STATUS_FR = "status_fr";
    public static final String INFO_EXCEPTION = "exception";

    private final Map<String,String> informations = new HashMap<>();

    private long startTime;
    private long endTime;

    /**
     * Current report instance.
     * TODO : must find the junit class cycle to avoid possible clashes here.
     * Since this listener is not marked as @ThreadSafe this approach works.
     */
    private static AsciiDocTestReport INSTANCE = null;

    /**
     * @return current AsciiDocTestReport instance
     */
    public static AsciiDocTestReport get(){
        return INSTANCE;
    }

    public AsciiDocTestReport() {
        INSTANCE = this;
    }

    /**
     * Add a new information in the produced test report.
     *
     * @param name report information key.
     * @param value report information text value.
     */
    public void addInfo(String name, String value) {
        informations.put(name, value);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testRunStarted(Description description) throws Exception {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testRunFinished(Result result) throws Exception {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testStarted(Description description) throws Exception {
        informations.clear();
        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testFinished(Description description) throws Exception {
        endTime = System.currentTimeMillis();
        informations.put(INFO_STATUS_EN, STATUS_SUCCESS_EN);
        informations.put(INFO_STATUS_FR, STATUS_SUCCESS_FR);
        save(description);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testFailure(Failure failure) throws Exception {
        endTime = System.currentTimeMillis();
        informations.put(INFO_STATUS_EN, STATUS_FAILED_EN);
        informations.put(INFO_STATUS_FR, STATUS_FAILED_FR);

        if (failure.getException() != null) {
            informations.put(INFO_EXCEPTION, toString(failure.getException()));
        }
        save(failure.getDescription());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testAssumptionFailure(Failure failure) {
        endTime = System.currentTimeMillis();
        informations.put(INFO_STATUS_EN, STATUS_FAILED_EN);
        informations.put(INFO_STATUS_FR, STATUS_FAILED_FR);
        save(failure.getDescription());
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void testIgnored(Description description) throws Exception {
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
        informations.put(INFO_STATUS_EN, STATUS_IGNORED_EN);
        informations.put(INFO_STATUS_FR, STATUS_IGNORED_FR);
        save(description);
    }

    /**
     * Save collected test execution informations.
     */
    private void save(Description description) {
        long timems = endTime - startTime;
        final String timePretty = TemporalUtilities.durationToString(timems);
        informations.put(INFO_TIMEMS, "" + timems);
        informations.put(INFO_TIME_PRETTY, timePretty);


        try {
            final Path target = Paths.get("target");
            final Path targetTest = target.resolve("test-reports");
            Files.createDirectories(targetTest);
            final Path report = targetTest.resolve(description.getClassName() + "-" + description.getMethodName() + "-adoc.txt");

            final List<String> lines = new ArrayList<>();
            for (Entry<String,String> entry : informations.entrySet()) {
                lines.add("// tag::" + entry.getKey() + "[]");
                lines.add(entry.getValue());
                lines.add("// end::" + entry.getKey() + "[]");
            }
            Files.write(report, lines, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String toString(Throwable ex) {
        final StringWriter w = new StringWriter();
        final PrintWriter writer = new PrintWriter(w);
        ex.printStackTrace(writer);
        writer.flush();
        w.flush();
        return w.getBuffer().toString();
    }
}
