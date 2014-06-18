
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//IATIC3
//Bruneau Guillaume
//Diop Jonathan
//Douirmi Makarime
//Ganne Jonathan
//Sebbah Raphael

public class Editeur_XML extends JFrame implements ActionListener {

	private static final long serialVersionUID = 42L;
	private JMenu fileMenu;
	private JMenu arbreMenu;
	private JFileChooser fc = null;
	private String currentFileBeingEdited = null;
	private String fichierTmp = null;
	private JTree jTree;
	private final JButton btnNewButton = new JButton("Deplier encore l'arbre");
	private final JButton btnNewButton2 = new JButton("Plier tout l'arbre");
	private final JButton btnNewButton3 = new JButton("Formater selection");
	private final JLabel label = new JLabel("");
	static String balise = "";
	static String balise2 = "";
	int currentCaretPosition;
	File file;
	String line;
	BufferedReader br;
	JTextArea ta = new JTextArea();
	JScrollPane scrollPane2 = new JScrollPane();
	JScrollPane scrollPane = new JScrollPane(ta);
	Highlighter hl = ta.getHighlighter();

	public Editeur_XML() {
		scrollPane.setPreferredSize(new Dimension(750, 200));
		scrollPane2.setPreferredSize(new Dimension(430, 200));
		ta.setText("");
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollPane, BorderLayout.EAST);
		content.add(scrollPane2, BorderLayout.WEST);
		createFileMenu();
		createArbreMenu();
		Boutons();
		actionsDroite();
		actionsGauche();
		CommandesSpeciales();
		proprietesGraphiquesGeneral();

		this.setContentPane(content);
		this.setTitle("Editeur XML Projet ID");
		this.setVisible(true);
		this.setSize(1200, 600);
		ta.setBounds(100, 100, 612, 474);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GestionFenetre();
	}

	public void Boutons() {
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(arbreMenu);
		this.setLayout(null);
		label.setFont(new Font("Tahoma", Font.PLAIN, 17));
		arbreMenu.setEnabled(false);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OuvrirArbre();
			}
		});
		btnNewButton2.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNewButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FermerArbre();
			}
		});
		btnNewButton3.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnNewButton3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Formater();
			}
		});
		menuBar.add(btnNewButton);
		menuBar.add(btnNewButton2);
		if (jTree == null) {
			btnNewButton.setEnabled(false);
			btnNewButton2.setEnabled(false);
		}
		menuBar.add(btnNewButton3);

		menuBar.add(label);
	}

	public void Formater() {
		int i = 0;
		int index = ta.getCaretPosition();
		String s = ta.getSelectedText();
		String s2 = "";
		if (s != null) {
			while (i != s.length()) {
				if (s.charAt(i) == '\n' || s.charAt(i) == '\t') {
					// ne rien faire (ignorer les retours a la ligne)
				} else {
					s2 = s2 + s.charAt(i);
				}
				i++;
			}

		} else {
			JOptionPane.showMessageDialog(null, "Rien a formater.");
		}
		ta.replaceRange(s2, index - s.length(), index);
	}

	public void proprietesGraphiquesGeneral() {
		final Font currentFont = ta.getFont();
		final Font bigFont = new Font(currentFont.getName(),
				currentFont.getStyle(), currentFont.getSize() + 5);
		ta.setFont(bigFont);
		fileMenu.setFont(bigFont.deriveFont(Font.BOLD));
		arbreMenu.setFont(bigFont.deriveFont(Font.BOLD));
	}

	public void proprietesGraphiques() {
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) jTree
				.getCellRenderer();
		Icon closedIcon = new ImageIcon("icons/balise.jpg");
		Icon openIcon = new ImageIcon("icons/balisevert.jpg");
		Icon leafIcon = new ImageIcon("icons/file.png");
		renderer.setClosedIcon(closedIcon);
		renderer.setOpenIcon(openIcon);
		renderer.setLeafIcon(leafIcon);
		renderer.setBackgroundSelectionColor(Color.ORANGE);
		renderer.setTextNonSelectionColor(Color.BLUE);
		renderer.setTextSelectionColor(Color.BLUE);
		renderer.setBorderSelectionColor(Color.BLUE);

		final Font currentFont = jTree.getFont();
		final Font bigFont = new Font(currentFont.getName(),
				currentFont.getStyle(), currentFont.getSize() + 5);
		jTree.setFont(bigFont);

	}

	public void actionsGauche() {
		arbreMenu.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				if (isWellFormed.renvoie_bool(fichierTmp) == true) {
					arbreMenu.setEnabled(true);
					label.setText("   Format correct");
				} else {
					arbreMenu.setEnabled(false);
					label.setText("   Format incorrect");
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
	}

	public void XML2JTree(Node root, boolean showDetails) {
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
		getContentPane().add("Center", panel);
	}

	protected DefaultMutableTreeNode createTreeNode(Node root,
			boolean showDetails) {
		DefaultMutableTreeNode dmtNode = null;

		String name = root.getNodeName();
		String value = root.getNodeValue();

		if (showDetails) {
			dmtNode = new DefaultMutableTreeNode(name);
		} else {
			dmtNode = new DefaultMutableTreeNode(
					root.getNodeType() == Node.TEXT_NODE ? value : name);
		}

		NamedNodeMap attribs = root.getAttributes();
		if (attribs != null) {

			for (int i = 0; i < attribs.getLength(); i++) {
				Node attNode = attribs.item(i);
				String attName = attNode.getNodeName().trim();
				String attValue = attNode.getNodeValue().trim();

				if (attValue != null) {
					if (attValue.length() > 0) {
						dmtNode.add(new DefaultMutableTreeNode(attName + "=\""
								+ attValue + "\""));

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
						if (nd.getNodeType() == Node.ELEMENT_NODE) {
							dmtNode.add(createTreeNode(nd, showDetails));
						}

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

	public void CommandesSpeciales() {
		ta.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if ((e.getKeyChar() == '\u0008')
						&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					SaisirBalise();
					// a terminer (sert pour l'englobage)
				}
				if ((e.getKeyCode() == KeyEvent.VK_F)
						&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					Recherche_mot();
				}
			}

			public void keyReleased(KeyEvent e) {

			}
		});
	}

	public void Recherche_mot() {
		String text = ta.getText();
		int i = 0;
		int nbBalises = 0;
		String aComparer = "";

		String s = (String) JOptionPane.showInputDialog(null,
				"Quel mot recherchez-vous?", "Tapez votre recherche",
				JOptionPane.OK_OPTION, null, null, "");

		if (s == null || s.length() == 0 || s.charAt(0) == '/'
				|| s.charAt(1) == '/') {
			JOptionPane.showMessageDialog(null, "Recherche annulee.");
		} else {
			while (i != text.length()) {
				if (text.charAt(i) == s.charAt(1)) {
					aComparer = text.substring(i - 1, i + s.length() - 1);
					if (aComparer.equals(s)) {
						nbBalises++;
						try {
							hl.addHighlight(i - 1, i + s.length() - 1,
									DefaultHighlighter.DefaultPainter);
						} catch (BadLocationException ex) {
							ex.printStackTrace();
						}
					}
				}
				i++;
			}
			JOptionPane.showMessageDialog(null, "Occurences trouvees :"
					+ nbBalises);
		}
	}

	public void SaisirBalise() {
		String text = ta.getText();
		int i = 0;
		int nbBalises = 0;
		String aComparer = "";

		String s = (String) JOptionPane.showInputDialog(null,
				"Quelle balise recherchez-vous?", "Choix d'une balise",
				JOptionPane.OK_OPTION, null, null, "");

		if (s == null || s.length() == 0 || s.charAt(0) == '/'
				|| s.charAt(1) == '/') {
			JOptionPane.showMessageDialog(null, "Recherche annulee.");
		} else {
			balise2 = s;

			while (i != text.length()) {
				if (text.charAt(i) == s.charAt(1)) {
					if (text.charAt(i - 2) == '<') {
						aComparer = text.substring(i - 1, i + s.length() - 1);
						if (aComparer.equals(s)) {
							nbBalises++;
							try {
								hl.addHighlight(i - 1, i + s.length() - 1,
										DefaultHighlighter.DefaultPainter);
							} catch (BadLocationException ex) {
								ex.printStackTrace();
							}
						}
					}

				}
				i++;
			}

			if (nbBalises > 0) {
				ChoixBaliseEnglober(nbBalises, s);
			} else {
				JOptionPane.showMessageDialog(null, "Balise non trouvee.");
			}
		}
	}

	public void ChoixBaliseEnglober(int nbBalises, String laBalise) {
		String text = ta.getText();
		String aComparer = "";
		int i = 0;
		int numeroBalise = 0;
		int choixint;
		int indexFin = 0;
		int indexDebut = 0;
		String choix = (String) JOptionPane.showInputDialog(null, nbBalises
				+ " balises correspondantes. Laquelle cacher ?",
				"Choix balise a cacher", JOptionPane.OK_OPTION, null, null, "");

		choixint = Integer.parseInt(choix);

		if (choixint > nbBalises || choixint <= 0) {
			JOptionPane.showMessageDialog(null, "Vous sortez des bornes.");
		} else {
			hl.removeAllHighlights();

			while (i != text.length()) {
				if (text.charAt(i) == laBalise.charAt(1)) {
					if (text.charAt(i - 2) == '<') {
						aComparer = text.substring(i - 1, i + laBalise.length()
								- 1);
						numeroBalise++;
						if (aComparer.equals(laBalise)
								&& choixint == numeroBalise) {
							indexDebut = i;
							try {
								hl.addHighlight(i - 1, i + laBalise.length()
										- 1, DefaultHighlighter.DefaultPainter);
							} catch (BadLocationException ex) {
								ex.printStackTrace();
							}
						}
					}
				}
				i++;
			}

			i = indexDebut;

			while (i != text.length()) {
				if (text.charAt(i) == laBalise.charAt(laBalise.length() - 1)) {
					if (text.charAt(i + 1) == '>') {
						aComparer = text.substring(i - laBalise.length() + 1,
								i + 1);
						numeroBalise++;
						if (aComparer.equals(laBalise)) {
							indexFin = i + laBalise.length() + 6;
							try {
								hl.addHighlight(i - laBalise.length() + 1,
										i + 1,
										DefaultHighlighter.DefaultPainter);
							} catch (BadLocationException ex) {
								ex.printStackTrace();
							}
							i = text.length() - 3;
						}
					}
				}
				i++;
			}

			JOptionPane.showMessageDialog(null, "Cachage en cours...");
			indexDebut = indexDebut - 2;
			indexFin = indexFin + 1;
			ta.insert("<![CDATA[ ", indexDebut);
			ta.insert(" ]]>", indexFin);
			JOptionPane.showMessageDialog(null, "Balise cachee.");
		}
	}

	// a finir (a peine commence)
	public void IntendationAuto() {
		String text = ta.getText();
		char carEnCours = text.charAt(1);
		int positionCar = 1;

		while (positionCar != text.length() - 1) {
			if (carEnCours == '\n') {
				System.out.println("saut ligne trouve !");
				if (text.charAt(positionCar) != '\t') {
					System.out.println("Mais pas de tabulation");
				}
			}
			carEnCours = text.charAt(positionCar);
			positionCar++;
		}
	}

	public void AjoutArbre(String name) {
		Document doc = null;
		String filename = name;
		boolean showDetails = false;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(filename);
		} catch (FileNotFoundException fnfEx) {
			JOptionPane.showMessageDialog(scrollPane2, filename
					+ " was not found", "Warning", JOptionPane.WARNING_MESSAGE);
			System.out.println("erreur!");
			System.exit(0);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(scrollPane2, ex.getMessage(),
					"Exception", JOptionPane.WARNING_MESSAGE);
			ex.printStackTrace();
			System.exit(0);
		}

		Node root = (Node) doc.getDocumentElement();
		XML2JTree(root, showDetails);
		scrollPane2.setViewportView(jTree);
		proprietesGraphiques();
		btnNewButton.setEnabled(true);
		btnNewButton2.setEnabled(true);
	}

	// Actions a effectuer lors d'une action sur la fenetre
	public void GestionFenetre() {
		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				String txt = ta.getText();
				if (txt == "" || txt == " " || txt == null) {
				} else {
					int n = JOptionPane.showConfirmDialog(null,
							"Quitter sans sauvegarder entrainera la perte\n"
									+ "de toute donnee non enregistree.\n"
									+ "Sauvegarder avant de quitter ?",
							"Quitter ?", JOptionPane.YES_NO_OPTION);
					if (n == 0) {
						if (currentFileBeingEdited != null) {
							Sauvegarder(currentFileBeingEdited);
						} else {
							creerFichier();
						}
					}
				}
				suppressionTmp(fichierTmp);
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
			}
		});
	}

	// suppression de tous les fic tmp :
	public void suppressionTmp(String nomFic) {
		if (currentFileBeingEdited == null) {
			File file2 = new File("");
			walkDir(new File(file2.getAbsolutePath()),
					Pattern.compile(".*tmp.xml"));
		} else {
			File file2 = new File(currentFileBeingEdited);
			String dossierCourant = file2.getAbsolutePath().substring(0,
					file2.getAbsolutePath().lastIndexOf('\\'));
			File file3 = new File(file2.getAbsolutePath().substring(0,
					file2.getAbsolutePath().lastIndexOf('\\')));
			System.out.println(dossierCourant);
			walkDir(new File(file3.getAbsolutePath()),
					Pattern.compile(".*tmp.xml"));
		}
	}

	// supprime les fichiers avec une certaine extension :
	private static void walkDir(final File dir, final Pattern pattern) {
		final File[] files = dir.listFiles();
		if (files != null) {
			for (final File file : files) {
				if (pattern.matcher(file.getName()).matches()) {
					System.out.println("Supprime : " + file.getAbsolutePath());
					File MyFile = file;
					MyFile.delete();
				}
			}
		}
	}

	// Actions sur l'ecran de droite qui influent sur tmp et ecran de gauche :
	public void actionsDroite() {
		ta.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				hl.removeAllHighlights();
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				majTmp();
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		ta.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				hl.removeAllHighlights();
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				majTmp();
				if (fichierTmp != null
						&& isWellFormed.renvoie_bool(fichierTmp) == true) {
					AjoutArbre(fichierTmp);
					label.setText("   Format correct");
				} else {
					label.setText("   Format incorrect");
				}
				if (e.getKeyChar() != '\u0008' && e.getKeyChar() != '\u007F') {
					IndenteTexte(e.getKeyChar());
					ProposeFermetureBalise(e.getKeyChar());
				}
			}
		});
	}

	public void IndenteBalise(char c, int tabulation) {
		int i = 1, n = 0;
		int currentCaretPosition = ta.getCaretPosition();
		String text = ta.getText();
		boolean baliseFermante = false;

		if (i + 1 > currentCaretPosition)
			return;

		while (text.charAt(currentCaretPosition - i - 1) == '	'
				&& i + 2 < currentCaretPosition)
			i++;

		i++;
		while (text.charAt(currentCaretPosition - i - 1) != '	'
				&& i + 1 < currentCaretPosition
				&& text.charAt(currentCaretPosition - i - 1) != '\n') {
			if (i + 1 < currentCaretPosition) {
				if (text.charAt(currentCaretPosition - i - 1) == '>'
						&& text.charAt(currentCaretPosition - i - 2) == '/')
					return;
			}
			if (i + 2 < currentCaretPosition) {
				if (text.charAt(currentCaretPosition - i - 1) == '/'
						&& text.charAt(currentCaretPosition - i - 2) == '<') {
					if (text.charAt(currentCaretPosition - i - 3) == '	')
						baliseFermante = true;
				}
				if (text.charAt(currentCaretPosition - i - 1) == '?'
						&& text.charAt(currentCaretPosition - i - 2) == '<')
					return;
			}
			i++;
		}
		while (text.charAt(currentCaretPosition - i - 1) != '\n'
				&& i + 1 < currentCaretPosition) {
			if (text.charAt(currentCaretPosition - i - 1) == '	')
				n++;

			i++;
		}

		if (baliseFermante == true && n > tabulation)
			ta.replaceRange(null, currentCaretPosition - i,
					currentCaretPosition - i + (n - tabulation));

	}

	public void IndenteTexte(char c) {

		if (c == '\n') {
			int currentCaretPosition = ta.getCaretPosition();
			int nombreBaliseFermante = 0, i = 0, nombreBaliseOuvrante = 0;
			boolean baliseFermante, baliseOuvranteFermante, balise;
			String text = ta.getText();
			char caractereCurseur = c;

			while (i < currentCaretPosition) {
				balise = false;
				baliseFermante = false;
				baliseOuvranteFermante = false;
				if (caractereCurseur != '>') {
					while (caractereCurseur != '>' && i < currentCaretPosition) {
						if (currentCaretPosition - i - 1 > 0
								&& currentCaretPosition - i - 1 < currentCaretPosition)
							caractereCurseur = text.charAt(currentCaretPosition
									- i - 1);

						i++;
					}
				}
				if (caractereCurseur == '>')
					balise = true;

				if (balise == true) {
					if (i + 1 < currentCaretPosition) {
						i++;
						caractereCurseur = text
								.charAt(currentCaretPosition - i);
						if (caractereCurseur == '/') {
							i++;
							baliseOuvranteFermante = true;
						}
					}
					while (i < currentCaretPosition && caractereCurseur != '<') {
						caractereCurseur = text.charAt(currentCaretPosition - i
								- 1);
						if (caractereCurseur == '/'
								&& i + 1 < currentCaretPosition) {
							if (text.charAt(currentCaretPosition - i - 2) == '<')
								baliseFermante = true;
						}
						if (caractereCurseur == '?'
								&& i + 1 < currentCaretPosition) {
							if (text.charAt(currentCaretPosition - i - 2) == '<')
								baliseOuvranteFermante = true;
						}

						i++;
					}
					if (caractereCurseur == '<') {
						if (i > 1) {
							if (text.charAt(currentCaretPosition - i + 1) == '?')
								baliseOuvranteFermante = true;
						}
						if (baliseFermante == true)
							nombreBaliseFermante++;
						else if (baliseOuvranteFermante != true)
							nombreBaliseOuvrante++;
					}
				}
			}
			for (i = 0; i < nombreBaliseOuvrante - nombreBaliseFermante; i++)
				ta.insert("	", ta.getCaretPosition());

			IndenteBalise(c, nombreBaliseOuvrante - nombreBaliseFermante);
		}
	}

	public int ScanBaliseChange() {
		int nombreBaliseFermante = 0, i = 0, nombreBaliseOuvrante = 0;
		boolean baliseFermante = false, baliseOuvranteFermante = false;
		int currentCaretPosition = ta.getCaretPosition();
		String text = ta.getText();
		char caractereCurseur = text.charAt(currentCaretPosition - i - 1);

		while (nombreBaliseFermante >= nombreBaliseOuvrante
				&& i < currentCaretPosition) {
			baliseFermante = false;
			baliseOuvranteFermante = false;

			if (caractereCurseur != '>') {
				while (caractereCurseur != '>' && i < currentCaretPosition) {
					caractereCurseur = text
							.charAt(currentCaretPosition - i - 1);
					i++;
				}
			}
			if (i >= currentCaretPosition && caractereCurseur != '>')
				return -1;

			if (i + 1 < currentCaretPosition) {
				i++;
				caractereCurseur = text.charAt(currentCaretPosition - i);
				if (caractereCurseur == '/') {
					i++;
					baliseOuvranteFermante = true;
				}
			}
			while (i < currentCaretPosition && caractereCurseur != '<') {
				caractereCurseur = text.charAt(currentCaretPosition - i - 1);
				if (caractereCurseur == '/' && i + 1 < currentCaretPosition) {
					if (text.charAt(currentCaretPosition - i - 2) == '<')
						baliseFermante = true;
				}

				i++;
			}
			if (i > 1) {
				if (text.charAt(currentCaretPosition - i + 1) == '?')
					baliseOuvranteFermante = true;
			}
			if (baliseFermante == true)
				nombreBaliseFermante++;
			else if (baliseOuvranteFermante != true)
				nombreBaliseOuvrante++;
		}
		if (nombreBaliseFermante >= nombreBaliseOuvrante)
			return -1;

		return i;
	}

	public int FermetureDirecte() {
		int i = 0;
		String text = ta.getText();
		int currentCaretPosition = ta.getCaretPosition();

		while (currentCaretPosition - i > 0
				&& text.charAt(currentCaretPosition - i - 1) != '<'
				&& i < currentCaretPosition) {

			i++;
			if (text.charAt(currentCaretPosition - i - 1) == '/'
					|| text.charAt(currentCaretPosition - i - 1) == '?'
					|| text.charAt(currentCaretPosition - i - 1) == '>')
				return -1;
		}
		if (currentCaretPosition - i - 1 <= 0
				&& text.charAt(currentCaretPosition - i - 1) != '<')
			return -1;

		return i;
	}

	public void ProposeFermetureBalise(char c) {
		int i = 0;
		int positionBalise = 0;
		int currentCaretPosition = ta.getCaretPosition();
		Highlighter hl = ta.getHighlighter();
		hl.removeAllHighlights();
		String text = ta.getText();
		String balise = "";

		if (c == '<' && ta.getCaretPosition() != 1)
			positionBalise = currentCaretPosition - ScanBaliseChange();
		else if (c == '>' && ta.getCaretPosition() != 1)
			positionBalise = currentCaretPosition - FermetureDirecte();

		if (c == '>' || c == '<' && ta.getCaretPosition() != 1) {

			if (positionBalise >= currentCaretPosition)
				return;

			while (text.charAt(positionBalise + i) != '>'
					&& text.charAt(positionBalise + i) != ' '
					&& positionBalise + i < currentCaretPosition) {
				if (text.charAt(positionBalise + i) != '<'
						&& text.charAt(positionBalise + i) != '>'
						&& text.charAt(positionBalise + i) != ' ')
					balise = balise + text.charAt(positionBalise + i);

				i++;
			}
			if (balise != "" && c == '<') {
				ta.insert('/' + balise + '>', ta.getCaretPosition());
				try {
					hl.addHighlight(
							ta.getCaretPosition() - balise.length() - 2,
							ta.getCaretPosition(),
							DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			} else if (balise != "" && c == '>') {
				ta.insert("</" + balise + '>', ta.getCaretPosition());
				try {
					hl.addHighlight(
							ta.getCaretPosition() - balise.length() - 3,
							ta.getCaretPosition(),
							DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	// creation et maj du fichier tmp :
	public void majTmp() {
		if (currentFileBeingEdited != null) {
			fichierTmp = currentFileBeingEdited;
			fichierTmp = (fichierTmp != null) ? fichierTmp.substring(0,
					fichierTmp.indexOf('.')) : "";
			fichierTmp = fichierTmp + "_tmp.xml";
		} else {
			File file = new File("");
			fichierTmp = file.getAbsolutePath() + "\\tmp.xml";
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					fichierTmp)));
			writer.write("");
			writer.close();
		} catch (IOException e10) {
			e10.printStackTrace();
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fichierTmp));
			pw.println(ta.getText());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	public void createFileMenu() {
		JMenuItem item;
		fileMenu = new JMenu("Fichier");
		item = new JMenuItem("Sauvegarder nouveau");
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Ouvrir");
		item.addActionListener(new OpenListener());
		fileMenu.add(item);
		item = new JMenuItem("Sauvegarder");
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Recommencer");
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();
		item = new JMenuItem("Quitter");
		item.addActionListener(this);
		fileMenu.add(item);
	}

	public void createArbreMenu() {
		JMenuItem item;
		arbreMenu = new JMenu("Arbre");
		item = new JMenuItem("Creer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Effacer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Renommer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Copier");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Deplacer");
		item.addActionListener(this);
		arbreMenu.add(item);
	}

	// Ouverture et impression fichier dans affichage de droite
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection fichier");
			fc.setMultiSelectionEnabled(false);

			int retVal = fc.showOpenDialog(Editeur_XML.this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				ta.setText("");
				file = fc.getSelectedFile();
				currentFileBeingEdited = file.getAbsolutePath();
				try {
					br = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					line = br.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (line != null) {
					ta.append(line + "\n");
					try {
						line = br.readLine();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				MiseAjourTitre();
			}

			majTmp();
			if (fichierTmp != null
					&& isWellFormed.renvoie_bool(fichierTmp) == true) {
				AjoutArbre(fichierTmp);
				arbreMenu.setEnabled(true);
				label.setText("   Format correct");
			} else {
				label.setText("   Format incorrect");
			}
		}
		// else : fichier non ouvert.
	}

	public void MiseAjourTitre() {
		if (currentFileBeingEdited != null)
			this.setTitle("Editeur XML Projet ID - " + currentFileBeingEdited);
	}

	public static void main(String[] args) {
		new Editeur_XML();
	}

	private void Sauvegarder(String currentFileBeingEdited) {
		PrintWriter pw = null;
		System.out.println("Fichier : " + currentFileBeingEdited
				+ " sauvegarde.");
		try {
			pw = new PrintWriter(new File(currentFileBeingEdited));
			pw.println(ta.getText());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
		this.setTitle("Editeur XML Projet ID - " + currentFileBeingEdited);
	}

	public void creerFichier() {
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(fc.getCurrentDirectory());
		fc.setDialogTitle("Selection de l'emplacement de sauvegarde");
		fc.setMultiSelectionEnabled(false);

		int choix = fc.showOpenDialog(Editeur_XML.this);
		file = fc.getSelectedFile();
		currentFileBeingEdited = file.getAbsolutePath();

		if (choix == JFileChooser.APPROVE_OPTION) {
			String s = (String) JOptionPane
					.showInputDialog(
							null,
							"Chemin selectionne : "
									+ currentFileBeingEdited
									+ "\n"
									+ "Nom du fichier: (extension .xml ajoutee automatiquement)",
							"Choix du nom de fichier",
							JOptionPane.PLAIN_MESSAGE, null, null,
							"Nouveau_fichier");

			if ((s != null) && (s.length() > 0)) {
				currentFileBeingEdited = currentFileBeingEdited + "\\" + s
						+ ".xml";
				// creation du fichier :
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(currentFileBeingEdited)));
					writer.write("");
					writer.close();
				} catch (IOException e10) {
					e10.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Fichier non cree !");
			}
			Sauvegarder(currentFileBeingEdited);
		} else {
			JOptionPane.showMessageDialog(null, "Fichier non sauvegarde.\n");
		}
	}

	public void Restart_option() {
		int n = JOptionPane.showConfirmDialog(null,
				"Recommencer le fichier entrainera la perte\n"
						+ "de toute donnee non sauvegardee.\n"
						+ "Confirmer la remise aÂ  0 ?", "Recommencer ?",
				JOptionPane.YES_NO_OPTION);
		if (n == 0) {
			ta.setText("");
			currentFileBeingEdited = null;
			this.setTitle("Editeur XML Projet ID");
		}
	}

	public void Save_option() {
		if (currentFileBeingEdited != null) {
			Sauvegarder(currentFileBeingEdited);
		} else {
			JOptionPane
					.showMessageDialog(
							null,
							"Aucun fichier n'est ouvert actuellement...\n"
									+ "Faire \"Save to\" pour enregistrer le nouveau fichier.");
		}
	}

	public void NouveauNoeudArbre() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
				.getLastSelectedPathComponent();
		DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();

		Object[] options = { "Contenu", "Noeud" };
		int n = JOptionPane.showOptionDialog(null, "Quel type d'ajout ?",
				"Type d'ajout", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

		if (n == 0) {
			String s = (String) JOptionPane.showInputDialog(null,
					"Entrez le nouveau contenu fils...", "Nouveau contenu",
					JOptionPane.OK_OPTION, null, null, "");
			if (s == null || s.length() == 0 || s == "" || s.charAt(0) == '/') {
				JOptionPane.showMessageDialog(null, "Ajout annule.");
				return;
			} else {
				model.insertNodeInto(new DefaultMutableTreeNode(s), node,
						node.getChildCount());
				repaint();
			}
		} else {
			String s = (String) JOptionPane.showInputDialog(null,
					"Entrez le nom de la balise fille a ajouter...",
					"Nouvelle balise", JOptionPane.OK_OPTION, null, null, "");

			if (s == null || s.length() == 0 || s == "" || s.charAt(0) == '/') {
				JOptionPane.showMessageDialog(null, "Ajout annule.");
				return;
			} else {
				DefaultMutableTreeNode newBalise = new DefaultMutableTreeNode(
						s.replaceAll("\\s", ""));
				node.add(newBalise);

				DefaultMutableTreeNode newBalise2 = new DefaultMutableTreeNode(
						"Contenu_vide");
				newBalise.add(newBalise2);
			}
		}
	}

	public void SupprimeNoeuArbre() {
		DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();

		TreePath[] paths = jTree.getSelectionPaths();
		for (TreePath path : paths) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			if (node.getParent() != null) {
				model.removeNodeFromParent(node);
			}
		}
	}

	public void ChangerNomNoeud() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
				.getLastSelectedPathComponent();

		String s = (String) JOptionPane.showInputDialog(null,
				"Remplacer le nom du noeud / contenu par ?", "Renommage",
				JOptionPane.OK_OPTION, null, null, "");

		if (s != null && s.length() != 0 && s != "" && s.charAt(0) != '/') {
			node.setUserObject(s.replaceAll("\\s", ""));
			repaint();
		} else {
			JOptionPane.showMessageDialog(null, "Renommage annule !");
		}
	}

	public void CopieNoeudArbre() { // ne copie qu'un contenu

		// DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
		// .getLastSelectedPathComponent();
		// DefaultMutableTreeNode newBalise = new DefaultMutableTreeNode("s");
		// node.add(newBalise);

		DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree
				.getLastSelectedPathComponent();
		model.insertNodeInto(new DefaultMutableTreeNode(selectedNode), root, 0);
	}

	public void DeplacerNoeudArbre() {
		// Non fait
	}

	public void ArbreEnXML() {
		String txt = ta.getText();
		int i = 0;

		while (txt.charAt(i) != '>') {
			i++;
		}
		if (txt.charAt(i - 1) == '?') {
			ta.setText(txt.substring(0, i + 1) + "\n");
		} else {
			ta.setText("");
		}

		try {
			String nouveau_XML = toXml((DefaultTreeModel) jTree.getModel());
			System.out.println(nouveau_XML);
			ta.append(nouveau_XML);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		majTmp();
		AjoutArbre(fichierTmp);
		OuvrirArbre();
		OuvrirArbre();
	}

	public void FermerArbre() {
		for (int i = jTree.getRowCount() - 1; i >= 0; i--) {
			jTree.collapseRow(i);
		}
	}

	public void OuvrirArbre() {
		for (int i = jTree.getRowCount() - 1; i >= 0; i--) {
			jTree.expandRow(i);
		}
	}

	// Action sur le menu fichier OU arbre :
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String menuName;
		menuName = e.getActionCommand();
		if (menuName.equals("Quitter")) {
			JOptionPane.showMessageDialog(null,
					"Fermeture sans sauvegarde ni suppression des tmp.");
			System.exit(0);
		} else if ("Sauvegarder".equalsIgnoreCase(menuName)) {
			Save_option();
		} else if ("Sauvegarder nouveau".equalsIgnoreCase(menuName)) {
			creerFichier();
		} else if ("Recommencer".equalsIgnoreCase(menuName)) {
			Restart_option();
		}

		if ((DefaultMutableTreeNode) jTree.getLastSelectedPathComponent() != null) {
			if (menuName.equals("Creer")) {
				NouveauNoeudArbre();
				ArbreEnXML();
			} else if ("Effacer".equalsIgnoreCase(menuName)) {
				SupprimeNoeuArbre();
				ArbreEnXML();
			} else if ("Renommer".equalsIgnoreCase(menuName)) {
				ChangerNomNoeud();
				ArbreEnXML();
			} else if ("Copier".equalsIgnoreCase(menuName)) {
				System.out.println("Copier (a finir)");
				CopieNoeudArbre();
				ArbreEnXML();
			} else if ("Deplacer".equalsIgnoreCase(menuName)) {
				System.out.println("Deplacer (a faire)");
				DeplacerNoeudArbre();
				ArbreEnXML();
			}
		} else {
			JOptionPane.showMessageDialog(null,
					"Selectionnez un element de l'arbre !");
		}
	}

	public static String toXml(TreeModel model)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document doc = impl.createDocument(null, null, null);
		Element root = createTree(doc, model, model.getRoot());
		doc.appendChild(root);

		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "30");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		return sw.toString();
	}

	private static Element createTree(Document doc, TreeModel model, Object node) {
		Element el = doc.createElement(node.toString());
		for (int i = 0; i < model.getChildCount(node); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) model
					.getChild(node, i);
			if (child.isLeaf()) {
				el.setTextContent(child.toString());
			} else {
				el.appendChild(createTree(doc, model, child));
			}
		}
		return el;
	}
}
