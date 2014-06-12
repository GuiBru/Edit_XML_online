import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XML2JTree extends JPanel {
	private JTree jTree;
	private static JFrame frame;

	public XML2JTree(Node root, boolean showDetails) {
		DefaultMutableTreeNode top = createTreeNode(root, showDetails);
		DefaultTreeModel dtModel = new DefaultTreeModel(top);

		jTree = new JTree(dtModel);
		jTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		jTree.setShowsRootHandles(true);
		jTree.setEditable(false);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(410, 500));
		panel.add("Center", jTree);
		add("Center", panel);
	}

	protected DefaultMutableTreeNode createTreeNode(Node root,
			boolean showDetails) {
		DefaultMutableTreeNode dmtNode = null;

		String type = getNodeType(root);
		String name = root.getNodeName();
		String value = root.getNodeValue();

		if (showDetails) {
			dmtNode = new DefaultMutableTreeNode("[" + type + "] --> " + name
					+ " = " + value);
		} else {
			dmtNode = new DefaultMutableTreeNode(
					root.getNodeType() == Node.TEXT_NODE ? value : name);
		}

		NamedNodeMap attribs = root.getAttributes();
		if (attribs != null && showDetails) {
			for (int i = 0; i < attribs.getLength(); i++) {
				Node attNode = attribs.item(i);
				String attName = attNode.getNodeName().trim();
				String attValue = attNode.getNodeValue().trim();

				if (attValue != null) {
					if (attValue.length() > 0) {
						dmtNode.add(new DefaultMutableTreeNode(
								"[Attribute] --> " + attName + "=\"" + attValue
										+ "\""));
					}
				}
			}
		}

		if (root.hasChildNodes()) {
			NodeList childNodes = root.getChildNodes();
			if (childNodes != null) {
				for (int k = 0; k < childNodes.getLength(); k++) {
					Node nd = childNodes.item(k);
					if (nd != null) {
						// A special case could be made for each Node type.
						if (nd.getNodeType() == Node.ELEMENT_NODE) {
							dmtNode.add(createTreeNode(nd, showDetails));
						}

						// This is the default
						String data = nd.getNodeValue();
						if (data != null) {
							data = data.trim();
							if (!data.equals("\n") && !data.equals("\r\n")
									&& data.length() > 0) {
								dmtNode.add(createTreeNode(nd, showDetails));
							}
						}
					}
				}
			}
		}
		return dmtNode;
	}

	public String getNodeType(Node node) {
		String type;
		switch (node.getNodeType()) {
		case Node.ELEMENT_NODE: {
			type = "Element";
			break;
		}
		case Node.ATTRIBUTE_NODE: {
			type = "Attribute";
			break;
		}
		case Node.TEXT_NODE: {
			type = "Text";
			break;
		}
		case Node.CDATA_SECTION_NODE: {
			type = "CData section";
			break;
		}
		case Node.ENTITY_REFERENCE_NODE: {
			type = "Entity reference";
			break;
		}
		case Node.ENTITY_NODE: {
			type = "Entity";
			break;
		}
		case Node.PROCESSING_INSTRUCTION_NODE: {
			type = "Processing instruction";
			break;
		}
		case Node.COMMENT_NODE: {
			type = "Comment";
			break;
		}
		case Node.DOCUMENT_NODE: {
			type = "Document";
			break;
		}
		case Node.DOCUMENT_TYPE_NODE: {
			type = "Document type";
			break;
		}
		case Node.DOCUMENT_FRAGMENT_NODE: {
			type = "Document fragment";
			break;
		}
		case Node.NOTATION_NODE: {
			type = "Notation";
			break;
		}
		default: {
			type = "INCONNU !!!";
			break;
		}
		}
		return type;
	}

	public static void main(String name) {
		Document doc = null;

		String filename = name;

		boolean showDetails = false;

		frame = new JFrame("XML to JTree");
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		int screenHeight = dim.height;
		int screenWidth = dim.width;
		frame.setBounds((screenWidth - 400) / 2, (screenHeight - 400) / 2, 400,
				400);
		frame.setBackground(Color.lightGray);
		frame.getContentPane().setLayout(new BorderLayout());

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(filename);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(frame, filename + " was not found",
					"Warning", JOptionPane.WARNING_MESSAGE);
			System.out.println("erreur!");
			// System.exit(0);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage(), "Exception",
					JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
			// System.exit(0);
		}

		Node root = (Node) doc.getDocumentElement();

		frame.getContentPane().add(new XML2JTree(root, showDetails),
				BorderLayout.CENTER);
		frame.validate();
		frame.setVisible(true);
	}
}