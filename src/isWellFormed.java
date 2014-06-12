import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

public class isWellFormed {

	public static boolean renvoie_bool(String documentName) {
		DefaultHandler handler = new DefaultHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(documentName), handler);
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

}
