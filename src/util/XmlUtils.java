package util;

import gmmlVision.GmmlVision;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import data.GmmlData;

public class XmlUtils {
	/**
	 * This sax handler can be used to quickly parse pathway information from
	 * a gmml file
	 */
	public static class PathwayParser extends DefaultHandler {
		String name;
		private ArrayList<Gene> genes;
		
		public PathwayParser() {
			name = "";
			genes = new ArrayList<Gene>();
		}
		
		public PathwayParser(XMLReader xmlReader) {
			this();
			xmlReader.setContentHandler(this);
			xmlReader.setEntityResolver(this);
		}
		
		public ArrayList<Gene> getGenes() { return genes; }
		
		public String getName() { return name; }
		
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if(localName.equals("GeneProduct")) { //For every geneproduct, store gene/code
				String name = attributes.getValue("Name");
				String sysName = attributes.getValue("GeneProduct-Data-Source");
				String code = GmmlData.sysName2Code.get(sysName);
				String symbol = attributes.getValue("GeneID");
				name = name == null ? "" : name;
				sysName = sysName == null ? "" : sysName;
				code = code == null ? "" : code;
				symbol = symbol == null ? "" : symbol;
				genes.add(new Gene(name, code, symbol));
			}
			else if(localName.equals("Pathway")) {
				name = attributes.getValue("Name");
			}
		}
		
		public void error(SAXParseException e) { 
			GmmlVision.log.error("Error while parsing xml document", e);
		}
		
		public void fatalError(SAXParseException e) throws SAXParseException { 
			GmmlVision.log.error("Fatal error while parsing xml document", e);
			throw new SAXParseException("Fatal error, parsing of this document aborted", null);
		}
		
		public void warning(SAXParseException e) { 
			GmmlVision.log.error("Warning while parsing xml document", e);
		}
		
		public class Gene {
			String id;
			String code;
			String symbol;
			public Gene(String id, String code, String symbol) 
			{ this.id = id; this.code = code; this.symbol = symbol; }
			
			public String toString() { return getId(); }
			public String getId() { return id; }
			public String getCode() { return code; }
			public String getSymbol() { return symbol; }
		}
	}
}
