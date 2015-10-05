/*
 * Copyright (c) 2015, msamek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttx.transformer.ttmlplain;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.model.ttml1.tt.ObjectFactory;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttx.transformer.AbstractTransformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author msamek
 */
public class TTMLPlain {

    public static final String TRANSFORMER_NAME = "ttml-plain";

    /**
     * Decides whether provided string is inside TTML namespace.
     * @param nspace
     * @return 
     */
    public static boolean isTtmlNamespace(String nspace) {
        assert nspace != null;
        return (nspace.startsWith(TTML.Constants.NAMESPACE_PREFIX));
    }

    
    /**
     * 
     */
    public static class TTMLPlainTransformer extends AbstractTransformer {

        protected static final String defaultOutputFileNamePattern = "out{0,number,000000}.xml";
        protected static final Charset defaultOutputEncoding;

        static {
            Charset de;
            try {
                de = Charset.forName(DEFAULT_OUTPUT_ENCODING);
            } catch (RuntimeException e) {
                de = Charset.defaultCharset();
            }
            defaultOutputEncoding = de;
        }

        // option and usage info
        protected static final String[][] longOptionSpecifications = new String[][]{
            {"ttml-plain-output-clean", "", "clean (remove) all files in output directory prior to writing output"},
            {"ttml-plain-output-directory", "DIRECTORY", "specify path to directory where output is to be written"},
            {"ttml-plain-output-encoding", "ENCODING", "specify character encoding of output (default: " + defaultOutputEncoding.name() + ")"},
            {"ttml-plain-output-indent", "", "indent output (default: indent)"},
            {"ttml-plain-output-pattern", "PATTERN", "specify output file name pattern (default: 'isd00000')"},
            {"ttml-plain-output-stdout", "", "write transformed output to stdout"}};
        protected static final Collection<OptionSpecification> longOptions;

        static {
            longOptions = new java.util.TreeSet<>();
            for (String[] spec : longOptionSpecifications) {
                longOptions.add(new OptionSpecification(spec[0], spec[1], spec[2]));
            }
        }
        
        private static Integer sequenceIndex = 1;
        protected boolean outputDirectoryClean;
        protected String outputDirectoryPath;
        protected String outputEncodingName;
        protected boolean outputIndent;
        private boolean outputIndentSet;
        protected String outputPattern;
        protected File outputDirectory;
        protected Charset outputEncoding;
        private static boolean outputStdout;

        @Override
        public String getName() {
            return TRANSFORMER_NAME;
        }

        @Override
        public Collection<OptionSpecification> getShortOptionSpecs() {
            return null;
        }

        @Override
        public Collection<OptionSpecification> getLongOptionSpecs() {
            return longOptions;
        }

        /**
         * The actual transformator method.
         * 
         * @param args
         * @param root
         * @param context
         * @param out 
         */
        @Override
        public void transform(List<String> args, Object root, TransformerContext context, OutputStream out) {
            assert root instanceof TimedText;
            Reporter reporter = context.getReporter();
            JAXBContext jc;
            Marshaller marshaller;
            Unmarshaller unmarshaller;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            Document doc;
            JAXBElement<TimedText> tt;

            /**
             * Unmarshall the root and marshall it back. Workaround to get rid
             * of initial values introduced in the semantic validation phase
             */
            try {
                jc = JAXBContext.newInstance(context.getModel().getJAXBContextPath());
                unmarshaller = jc.createUnmarshaller();
                tt = unmarshaller.unmarshal(context.getXMLNode(root), TimedText.class);
                db = dbf.newDocumentBuilder();
                doc = db.newDocument();
                marshaller = jc.createMarshaller();
                marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new Ttml1NamespacePrefixMapper());
                marshaller.marshal(tt, doc);
            } catch (ParserConfigurationException | JAXBException ex) {
                reporter.logError(ex);
                return;
            }

            try {
                pruneNonTtmlElements(doc);
                pruneNonTtmlAttributes(doc);
                pruneEmptyAttributes(doc);
            } catch (Exception ex) {
                Logger.getLogger(TTMLPlain.class.getName()).log(Level.SEVERE, null, ex);
                reporter.logError(ex);
            }
            reporter.logInfo(reporter.message("*KEY*", "Non-ttml elements and attributes pruned"));

            boolean suppressOutput = (Boolean) context.getResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name());
            if (!suppressOutput && outputDirectoryClean) {
                cleanOutputDirectory(outputDirectory, context);
            }
            Object res;
            if (suppressOutput) {
                res = writeToByteArray(doc, context);
            } else if (outputStdout) {
                res = writeToStdout(doc, context);
            } else {
                res = writeToFile(doc, context);
            }
            context.setResourceState(TransformerContext.ResourceState.ttxOutput.name(), res);
        }

        
        /**
         * Prunes elements that are not inside TTML namespace. Traverses the
         * document using the visitor pattern.
         * 
         * @param doc
         * @throws Exception
         */
        private void pruneNonTtmlElements(Document doc) throws Exception {
            Traverse.traverseElements(doc, new PreVisitor() {

                @Override
                public boolean visit(Object content, Object parent, Visitor.Order order) throws Exception {
                    assert content instanceof Element && parent instanceof Element;
                    Element elementNode = (Element) content;
                    Element parentNode = (Element) parent;

                    String namespace = elementNode.getNamespaceURI();
                    if (!isTtmlNamespace(namespace)) {
                        parentNode.removeChild(elementNode);
                    }

                    return true;
                }
            });
        }

        
        /**
         * Prunes attributes that are not inside TTML namespace. Traverses the 
         * document using the visitor pattern.
         * 
         * @param doc
         * @throws Exception 
         */
        private void pruneNonTtmlAttributes(Document doc) throws Exception {
            Traverse.traverseElements(doc, new PreVisitor() {

                @Override
                public boolean visit(Object content, Object parent, Visitor.Order order) throws Exception {
                    assert content instanceof Element;
                    Element elementNode = (Element) content;
                    NamedNodeMap attributes = elementNode.getAttributes();
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        Attr attr = (Attr) attributes.item(i);
                        String attrNs = attr.getNamespaceURI();
                        if (attrNs == null || attrNs.isEmpty()) {
                            continue;
                        }
                        if (!isTtmlNamespace(attr.getNamespaceURI())) {
                            elementNode.removeAttributeNode(attr);
                        }
                    }
                    return true;
                }
            });
        }

        
        /**
         * Prunes attributes that are empty, that is their value is empty string.
         * @param doc
         * @throws Exception 
         */
        private void pruneEmptyAttributes(Document doc) throws Exception {
            Traverse.traverseElements(doc, new PreVisitor() {

                @Override
                public boolean visit(Object content, Object parent, Visitor.Order order) throws Exception {
                    assert content instanceof Element;
                    Element element = (Element) content;

                    NamedNodeMap attributes = element.getAttributes();
                    for (int i = 0; i < attributes.getLength(); ++i) {
                        Attr attr = (Attr) attributes.item(i);
                        if (attr.getValue().isEmpty()) {
                            element.removeAttributeNode(attr);
                        }
                    }
                    return true;
                }
            });
        }

        
        /**
         * Write the document to the provided output stream.
         * 
         * @param doc
         * @param context
         * @param out 
         */
        private void write(Document doc, TransformerContext context, OutputStream out) {

            Reporter reporter = context.getReporter();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            try {
                transformer = tf.newTransformer();

                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, outputIndent ? "yes" : "no");
                transformer.setOutputProperty(OutputKeys.ENCODING, outputEncodingName);

                transformer.transform(new DOMSource(doc), new StreamResult(out));
            } catch (TransformerException ex) {
                reporter.logError(ex);
            }
            sequenceIndex = sequenceIndex + 1;
        }

        
        /**
         * Output the document to file. Filename is created according to
         * outputPatern and the file gets created in the directory specified
         * by outputDirectory.
         * 
         * @param document
         * @param context
         * @return File reference
         */
        private Object writeToFile(Document document, TransformerContext context) {
            Reporter reporter = context.getReporter();
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                String outputFileName = MessageFormat.format(outputPattern, sequenceIndex);
                File outputFile = new File(outputDirectory, outputFileName);
                fos = new FileOutputStream(outputFile);
                bos = new BufferedOutputStream(fos);
                write(document, context, bos);
                reporter.logInfo(reporter.message("*KEY*", "Wrote ISD ''{0}''.", outputFile.getAbsolutePath()));
                return outputFile;
            } catch (FileNotFoundException e) {
                reporter.logError(e);
                return null;
            } finally {
                IOUtil.closeSafely(bos);
                IOUtil.closeSafely(fos);
            }
        }

        
        /**
         * Output the document to byte array.
         * 
         * @param document
         * @param context
         * @return byte[]
         */
        private Object writeToByteArray(Document document, TransformerContext context) {
            Reporter reporter = context.getReporter();
            ByteArrayOutputStream bas = null;
            BufferedOutputStream bos = null;
            try {
                bas = new ByteArrayOutputStream();
                bos = new BufferedOutputStream(bas);
                write(document, context, bos);
                return bas.toByteArray();
            } catch (Exception e) {
                reporter.logError(e);
                return null;
            } finally {
                IOUtil.closeSafely(bos);
                IOUtil.closeSafely(bas);
            }
        }
        

        /**
         * Output the document to standard output.
         * 
         * @param document
         * @param context
         * @return System.out (as PrintStream)
         */
        private Object writeToStdout(Document document, TransformerContext context) {
            Reporter reporter = context.getReporter();
            OutputStream out = System.out;
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(out);
                write(document, context, bos);
                return out;
            } catch (Exception e) {
                reporter.logError(e);
                return null;
            }
        }

        @Override
        public int parseLongOption(List<String> args, int index) {
            String arg = args.get(index);
            int numArgs = args.size();
            String option = arg;
            assert option.length() > 2;
            option = option.substring(2);
            switch (option) {
                case "ttml-plain-output-stdout":
                    outputStdout = true;
                    return index + 1;
                case "ttml-plain-output-clean":
                    outputDirectoryClean = true;
                    break;
                case "ttml-plain-output-directory":
                    if (index + 1 > numArgs) {
                        throw new MissingOptionArgumentException("--" + option);
                    }
                    outputDirectoryPath = args.get(++index);
                    break;
                case "ttml-plain-output-encoding":
                    if (index + 1 > numArgs) {
                        throw new MissingOptionArgumentException("--" + option);
                    }
                    outputEncodingName = args.get(++index);
                    break;
                case "ttml-plain-output-indent":
                    outputIndent = true;
                    break;
                case "ttml-plain-output-pattern":
                    if (index + 1 > numArgs) {
                        throw new MissingOptionArgumentException("--" + option);
                    }
                    outputPattern = args.get(++index);
                    break;
                default:
                    index = index - 1;
                    break;
            }
            return index + 1;
        }

        @Override
        public int parseShortOption(List<String> args, int index) {
            String arg = args.get(index);
            String option = arg;
            assert option.length() == 2;
            option = option.substring(1);
            throw new UnknownOptionException("-" + option);
        }

        @Override
        public void processDerivedOptions() {
            // output directory
            File directory;
            if (outputDirectoryPath != null) {
                directory = new File(outputDirectoryPath);
                if (!directory.exists()) {
                    throw new InvalidOptionUsageException("output-directory", "directory does not exist: " + outputDirectoryPath);
                } else if (!directory.isDirectory()) {
                    throw new InvalidOptionUsageException("output-directory", "not a directory: " + outputDirectoryPath);
                }
            } else {
                directory = new File(".");
            }
            this.outputDirectory = directory;
            // output encoding
            if (!outputIndentSet) {
                outputIndent = true;
            }

            Charset encoding;
            if (outputEncodingName != null) {
                try {
                    encoding = Charset.forName(outputEncodingName);
                } catch (Exception e) {
                    encoding = null;
                }
                if (encoding == null) {
                    throw new InvalidOptionUsageException("output-encoding", "unknown encoding: " + outputEncodingName);
                }
            } else {
                encoding = null;
            }
            if (encoding == null) {
                encoding = defaultOutputEncoding;
            }
            this.outputEncoding = encoding;
            this.outputEncodingName = encoding.displayName();
            // output pattern
            String pattern = this.outputPattern;
            if (pattern == null) {
                pattern = defaultOutputFileNamePattern;
            }
            this.outputPattern = pattern;
        }

        
        /**
         * Remove created artifacts from directory specified in outputDirectory.
         * 
         * @param directory
         * @param context 
         */
        protected static void cleanOutputDirectory(File directory, TransformerContext context) {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message("*KEY*", "Cleaning artifacts from output directory ''{0}''...", directory.getPath()));
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    //TODO determine based on outputPattern
                    if (name.indexOf("out") != 0) {
                        continue;
                    } else if (name.indexOf(".xml") != (name.length() - 4)) {
                        continue;
                    } else if (!f.delete()) {
                        throw new com.skynav.ttx.transformer.TransformerException("unable to clean output directory: can't delete: '" + name + "'");
                    }
                }
            }
        }

    }

    
    /**
     * Provides mapping for XML namespace prefixes.
     */
    private static class Ttml1NamespacePrefixMapper extends NamespacePrefixMapper {

        private static final Model model = new com.skynav.ttv.model.ttml.TTML1.TTML1Model();

        @Override
        public String getPreferredPrefix(String namespace, String suggestion, boolean prefixRequired) {
            Map<String, String> prefixes = model.getNormalizedPrefixes();
            return prefixes.get(namespace);
        }

    }
}
