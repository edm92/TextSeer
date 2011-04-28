package processBuilding.Parser;

//JAXP packages
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import processBuilding.process;

import java.io.*;


public class SigBPMNParser {
	public FileTypeCheck Handler;
	int Progress = 0;
	
	// Binary switch for progress (should make this binary if can't think of anything else to do with this?
	int FileCheck = 1;
	
	static process Result;
	
	
    /** All output will be use this encoding */
    static final String outputEncoding = "UTF-8";

    /** Output goes here */
    private PrintWriter out;

    /** Indent level */
    public int indent = 0;

    /** Indentation will be in multiples of basicIndent  */
    private final String basicIndent = "  ";

    SigBPMNParser(PrintWriter out) {
        this.out = out;
        init();
    }
    
    private void init(){
    	Handler = new FileTypeCheck(this);
    }
    boolean result = true;
    process constructed = null;

    /**
     * Echo common attributes of a DOM2 Node and terminate output with an
     * EOL character.
     */
    private void printlnCommon(Node n) {
    	
    	if(result)
    		result = Handler.checkName(n.getNodeName(), n);
    	else
    		if(constructed == null){
    			constructed = Handler.CreateProcess();
    			//m.E.writeConsole(constructed.structure.toString());
    			Result = constructed;
    		}
    	
//        out.print(" nodeName=\"" + n.getNodeName() + "\"");

//        String val = n.getNamespaceURI();
//        if (val != null) {
//            out.print(" uri=\"" + val + "\"");
//        }
//
//        val = n.getPrefix();
//        if (val != null) {
//            out.print(" pre=\"" + val + "\"");
//        }
//
//        val = n.getLocalName();
//        if (val != null) {
//            out.print(" local=\"" + val + "\"");
//        }
//
//        val = n.getNodeValue();
//        if (val != null) {
//            out.print(" nodeValue=");
//            if (val.trim().equals("")) {
//                // Whitespace
//                out.print("[WS]");
//            } else {
//                out.print("\"" + n.getNodeValue() + "\"");
//            }
//        }
//        out.println();
    }

    /**
     * Indent to the current level in multiples of basicIndent
     */
    private void outputIndentation() {
        for (int i = 0; i < indent; i++) {
            out.print(basicIndent);
        }
    }

    /**
     * Recursive routine to print out DOM tree nodes
     */
    private void echo(Node n) {
        // Indent to the current level before printing anything
        outputIndentation();
        
    

        int type = n.getNodeType();
        switch (type) {
        case Node.ATTRIBUTE_NODE:
            ///out.print("ATTR:");
            printlnCommon(n);
            break;
        case Node.CDATA_SECTION_NODE:
            //out.print("CDATA:");
            printlnCommon(n);
            break;
        case Node.COMMENT_NODE:
            //out.print("COMM:");
            printlnCommon(n);
            break;
        case Node.DOCUMENT_FRAGMENT_NODE:
            //out.print("DOC_FRAG:");
            printlnCommon(n);
            break;
        case Node.DOCUMENT_NODE:
            //out.print("DOC:");
            printlnCommon(n);
            break;
        case Node.DOCUMENT_TYPE_NODE:
            //out.print("DOC_TYPE:");
            printlnCommon(n);

            // Print entities if any
            NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
            indent += 2;
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Entity entity = (Entity)nodeMap.item(i);
                echo(entity);
            }
            indent -= 2;
            break;
        case Node.ELEMENT_NODE:
            //out.print("ELEM:");
            printlnCommon(n);

            // Print attributes if any.  Note: element attributes are not
            // children of ELEMENT_NODEs but are properties of their
            // associated ELEMENT_NODE.  For this reason, they are printed
            // with 2x the indent level to indicate this.
            NamedNodeMap atts = n.getAttributes();
            indent += 2;
            for (int i = 0; i < atts.getLength(); i++) {
                Node att = atts.item(i);
                echo(att);
            }
            indent -= 2;
            break;
        case Node.ENTITY_NODE:
            //out.print("ENT:");
            printlnCommon(n);
            break;
        case Node.ENTITY_REFERENCE_NODE:
            //out.print("ENT_REF:");
            printlnCommon(n);
            break;
        case Node.NOTATION_NODE:
           // out.print("NOTATION:");
            printlnCommon(n);
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            //out.print("PROC_INST:");
            printlnCommon(n);
            break;
        case Node.TEXT_NODE:
            //out.print("TEXT:");
            printlnCommon(n);
            break;
        default:
           // out.print("UNSUPPORTED NODE: " + type);
            printlnCommon(n);
            break;
        }

        // Print children if any
        indent++;
        for (Node child = n.getFirstChild(); child != null;
             child = child.getNextSibling()) {
            echo(child);
        }
        indent--;
    }

    private static void usage() {
        System.err.println("Usage: DOMEcho [-opts] <filename>");
        System.err.println("       -v = validation");
        System.err.println("       -ws = do not create whitespace nodes");
        System.err.println("       -co[mments] = do not create comment nodes");
        System.err.println("       -cd[ata] = put CDATA into Text nodes");
        System.err.println("       -e[ntity-ref] = create EntityReference nodes");
        System.exit(1);
    }

    public static process PARSEmain(String filename) {
        //String filename = null;
        boolean validation = false;
        
        

        boolean ignoreWhitespace = false;
        boolean ignoreComments = false;
        boolean putCDATAIntoText = false;
        boolean createEntityRefs = false;

//        for (int i = 0; i < args.length; i++) {
//            if (args[i].equals("-v")) {
//                validation = true;
//            } else if (args[i].equals("-ws")) {
//                ignoreWhitespace = true;
//            } else if (args[i].startsWith("-co")) {
//                ignoreComments = true;
//            } else if (args[i].startsWith("-cd")) {
//                putCDATAIntoText = true;
//            } else if (args[i].startsWith("-e")) {
//                createEntityRefs = true;
//            } else {
//                filename = args[i];
//
//                // Must be last arg
//                if (i != args.length - 1) {
//                    usage();
//                }
//            }
//        }
        if (filename == null) {
            usage();
        }

        // Step 1: create a DocumentBuilderFactory and configure it
        DocumentBuilderFactory dbf =
            DocumentBuilderFactory.newInstance();

        // Set namespaceAware to true to get a DOM Level 2 tree with nodes
        // containing namesapce information.  This is necessary because the
        // default value from JAXP 1.0 was defined to be false.
        dbf.setNamespaceAware(true);

        // Optional: set various configuration options
        dbf.setValidating(validation);
        dbf.setIgnoringComments(ignoreComments);
        dbf.setIgnoringElementContentWhitespace(ignoreWhitespace);
        dbf.setCoalescing(putCDATAIntoText);
        // The opposite of creating entity ref nodes is expanding them inline
        dbf.setExpandEntityReferences(!createEntityRefs);

        // At this point the DocumentBuilderFactory instance can be saved
        // and reused to create any number of DocumentBuilder instances
        // with the same configuration options.

        // Step 2: create a DocumentBuilder that satisfies the constraints
        // specified by the DocumentBuilderFactory
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
            System.exit(1);
        }

        // Set an ErrorHandler before parsing
        OutputStreamWriter errorWriter;
		try {
			errorWriter = new OutputStreamWriter(System.err, outputEncoding);
			db.setErrorHandler(
		            new MyErrorHandler(new PrintWriter(errorWriter, true)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        

        // Step 3: parse the input file
        Document doc = null;
        try {
            doc = db.parse(new File(filename));
        } catch (SAXException se) {
            System.err.println(se.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println(ioe);
            System.exit(1);
        }

        Result = new process();
        Result.name = filename;
        // Print out the DOM tree
        OutputStreamWriter outWriter;
		try {
			
			outWriter = new OutputStreamWriter( System.out, outputEncoding);
			new SigBPMNParser(new PrintWriter(outWriter, true)).echo(doc);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //
		return Result;
     
    }

    // Error handler to report errors and warnings
    private static class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private PrintWriter out;

        MyErrorHandler(PrintWriter out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }
        
        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

}