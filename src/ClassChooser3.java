import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ClassChooser3 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 42L;
	File file;
	private JMenu fileMenu;
	private JMenu arbreMenu;
	String line;
	private JFileChooser fc = null;
	BufferedReader br;
	JTextArea ta = new JTextArea();
	private String currentFileBeingEdited = null;
	private String fichierTmp = null;
	JScrollPane scrollPane2 = new JScrollPane();
	JScrollPane scrollPane = new JScrollPane(ta);
	int currentCaretPosition;
	static String balise = "";

	public ClassChooser3() {
		scrollPane.setPreferredSize(new Dimension(750, 200));
		scrollPane2.setPreferredSize(new Dimension(430, 200));
		ta.setText("");
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollPane, BorderLayout.EAST);
		content.add(scrollPane2, BorderLayout.WEST);

		createFileMenu();
		createArbreMenu();
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		menuBar.add(fileMenu);
		menuBar.add(arbreMenu);

		actionsDroite();

		this.setContentPane(content);
		this.setTitle("Editeur XML Bêta");
		this.setVisible(true);
		this.setSize(1200, 600);
		ta.setBounds(100, 100, 612, 474);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GestionFenetre();
	}

	// a finir (à peine commencé)
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
		scrollPane2.setViewportView(new XML2JTree(root, showDetails));

		// IntendationAuto();
	}

	// Actions à effectuer lors de modification de la fenêtre
	public void GestionFenetre() {

		this.addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
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
			System.out.println("Chemin selec : " + file2.getAbsolutePath());
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

	// Actions sur l'écran de droite qui influent sur tmp et écran de gauche :
	public void actionsDroite() {
		ta.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				majTmp();
				if (fichierTmp != null
						&& isWellFormed.renvoie_bool(fichierTmp) == true) {
					AjoutArbre(fichierTmp);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});
		ta.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				majTmp();
				if (fichierTmp != null
						&& isWellFormed.renvoie_bool(fichierTmp) == true) {
					AjoutArbre(fichierTmp);
				}
				if (e.getKeyChar() != '\u0008' && e.getKeyChar() != '\u007F') {
					// ni un return ni un suppr
					IndenteTexte(e.getKeyChar());
					ProposeFermetureBalise(e.getKeyChar());
				}
			}
		});
	}
	public void IndenteBalise(char c)
	{
		int i = 1;
		int currentCaretPosition = ta.getCaretPosition();
		String text = ta.getText();
		boolean baliseFermante = false;
		
		if(currentCaretPosition - i - 1 <= 0)
			return;
		
		while(text.charAt(currentCaretPosition - i - 1) != '	' && i + 1 < text.length())
		{			
			if(i + 1 < text.length())
			{
				if(text.charAt(currentCaretPosition - i - 1) == '>' && text.charAt(currentCaretPosition - i - 2) == '/')
					return;
			}
			if(i + 2 < text.length())
			{
				if(text.charAt(currentCaretPosition - i - 1) == '/' && text.charAt(currentCaretPosition - i - 2) == '<')
				{
					if(text.charAt(currentCaretPosition - i - 3) == '	')
						baliseFermante = true;	
				}
				if(text.charAt(currentCaretPosition - i - 1) == '?' && text.charAt(currentCaretPosition - i - 2) == '<')
					return;
			}
			i++;
		}
		if(baliseFermante == true)
			ta.replaceRange(null, text.length() - i - 1, text.length() - i );
		
	}
	public void IndenteTexte(char c)
	{
		if(c == '\n')
		{
			IndenteBalise(c);
			
			int nombreBaliseFermante = 0, i = 0, nombreBaliseOuvrante = 0;
			int currentCaretPosition = ta.getCaretPosition();
			boolean baliseFermante, baliseOuvranteFermante,balise;
			String text = ta.getText();
			char caractereCurseur = text.charAt(currentCaretPosition - i - 1);
			
			while(i < text.length())
			{
				balise = false;
				baliseFermante = false;
				baliseOuvranteFermante = false;
				if (caractereCurseur != '>')
				{
					while (caractereCurseur != '>' && i < text.length())
					{
						caractereCurseur = text	.charAt(currentCaretPosition - i - 1);
						i++;
					}
				}
				if(caractereCurseur == '>')
					balise = true;
				
				if(balise == true)
				{
					if(i+1 < text.length())
					{
						i++;
						caractereCurseur = text.charAt(currentCaretPosition - i);
						if(caractereCurseur == '/')
						{
							i++;
							baliseOuvranteFermante = true;
						}
					}
					while(i<text.length() && caractereCurseur != '<' )
					{
						caractereCurseur = text.charAt(currentCaretPosition - i - 1);
						if(caractereCurseur == '/' && i+1 < text.length())
						{
							if(text.charAt(currentCaretPosition - i - 2) == '<')
								baliseFermante = true;
						}
						if(caractereCurseur == '?' && i+1 < text.length())
						{
							if(text.charAt(currentCaretPosition - i - 2) == '<')
								baliseOuvranteFermante = true;
						}
						
						i++;
					}
					if(caractereCurseur == '<')
					{
						if(i > 1)
						{
							if(text.charAt(currentCaretPosition - i + 1) == '?')
								baliseOuvranteFermante = true;
						}
						if(baliseFermante == true)
							nombreBaliseFermante++;
						else if(baliseOuvranteFermante != true)
							nombreBaliseOuvrante++;
					}
				}
			}
			for(i=0;i<nombreBaliseOuvrante - nombreBaliseFermante;i++)
				ta.insert("	", ta.getCaretPosition());
		}
	}
	
	
	
	public int ScanBaliseChange()
	{
		int nombreBaliseFermante = 0,i = 0,nombreBaliseOuvrante = 0;
		boolean baliseFermante = false, baliseOuvranteFermante = false;
		int currentCaretPosition = ta.getCaretPosition();
		String text = ta.getText();
		char caractereCurseur = text.charAt(currentCaretPosition - i - 1);
		
		while(nombreBaliseFermante >= nombreBaliseOuvrante && i<text.length())
		{
			baliseFermante = false;
			baliseOuvranteFermante = false;
			
			if(caractereCurseur != '>')
			{
				while(caractereCurseur != '>' && i < text.length())
				{
					caractereCurseur = text.charAt(currentCaretPosition - i - 1);
					i++;
				}
			}
			if(i >= text.length() && caractereCurseur != '>')
				return -1;
						
			if(i+1 < text.length())
			{
				i++;
				caractereCurseur = text.charAt(currentCaretPosition - i);
				if(caractereCurseur == '/')
				{
					i++;
					baliseOuvranteFermante = true;
				}
			}
			while(i<text.length() && caractereCurseur != '<' )
			{
				caractereCurseur = text.charAt(currentCaretPosition - i - 1);
				if(caractereCurseur == '/' && i+1 < text.length())
				{
					if(text.charAt(currentCaretPosition - i - 2) == '<')
						baliseFermante = true;
				}
				
				i++;
			}
			if(i > 1)
			{
				if(text.charAt(currentCaretPosition - i + 1) == '?')
					baliseOuvranteFermante = true;
			}
			if(baliseFermante == true)
				nombreBaliseFermante++;
			else if(baliseOuvranteFermante != true)
				nombreBaliseOuvrante++;
		}
		if(nombreBaliseFermante >= nombreBaliseOuvrante)
			return -1;
		
		return i;
	}
	public int FermetureDirecte()
	{
		int i = 0;
		String text = ta.getText();
		int currentCaretPosition = ta.getCaretPosition();
		
		while(currentCaretPosition - i > 0 && text.charAt(currentCaretPosition - i - 1) != '<' && i<text.length())
		{
			i++;
			if(text.charAt(currentCaretPosition - i - 1) == '/' || text.charAt(currentCaretPosition - i - 1) == '?')
				return -1;
		}
		if(currentCaretPosition - i - 1 <= 0 && text.charAt(currentCaretPosition - i - 1) != '<')
			return -1;
		
		return i;
		
	}
	public void ProposeFermetureBalise(char c) {
		
		int i = 0;
		int positionBalise = 0;
		String text = ta.getText();
		String balise ="";
		
		if(c == '<' && ta.getCaretPosition() != 1)
			positionBalise = text.length() - ScanBaliseChange();
		else if(c == '>' && ta.getCaretPosition() != 1)
			positionBalise = text.length() - FermetureDirecte();
		
		if(c == '>' || c == '<' &&ta.getCaretPosition() != 1 )
		{
			
			if(positionBalise >= text.length())
				return;
			
			while(text.charAt(positionBalise + i) != '>' && text.charAt(positionBalise + i) != ' ' && positionBalise + i < text.length())
			{
				if(text.charAt(positionBalise + i) != '<'&& text.charAt(positionBalise + i) != '>' && text.charAt(positionBalise + i) != ' ')
					balise = balise + text.charAt(positionBalise + i);
				
				i++;
			}
						
			if(balise != "" && c == '<')
			{
				ta.insert('/' + balise + '>', ta.getCaretPosition());
				ta.select(ta.getCaretPosition() - balise.length() - 2, ta.getCaretPosition() );
			}
			else if(balise != "" && c == '>')
			{
				ta.insert("</" + balise + '>', ta.getCaretPosition());
				ta.select(ta.getCaretPosition() - balise.length() - 3, ta.getCaretPosition() );
			}
		}
		

		// ta.insert(String str, int pos);
		// Inserts the specified text at the specified position in this text
		// area.
	}

	// création et màj du fichier tmp :
	public void majTmp() {
		// création fichier tmp :
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

		// écriture (màj) dans le fichier tmp :
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
		fileMenu = new JMenu("File");
		item = new JMenuItem("Save to"); // ok
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Open"); // ok
		item.addActionListener(new OpenListener());
		fileMenu.add(item);
		item = new JMenuItem("Save"); // en cours
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Rename"); // pas encore traité
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem("Delete"); // ne marche pas : buffers à fermer...
										// a l'air de marcher maintenant ?!
		item.addActionListener(this);
		fileMenu.add(item);
		fileMenu.addSeparator();
		item = new JMenuItem("Exit"); // ok (sans suppr les fic tmp)
		item.addActionListener(this);
		fileMenu.add(item);
	}

	// Menu de gestion des arbres
	public void createArbreMenu() {
		JMenuItem item;
		arbreMenu = new JMenu("Arbre");
		item = new JMenuItem("Créer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Effacer");
		item.addActionListener(new OpenListener());
		arbreMenu.add(item);
		item = new JMenuItem("Rennomer");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Copier");
		item.addActionListener(this);
		arbreMenu.add(item);
		item = new JMenuItem("Déplacer");
		item.addActionListener(this);
		arbreMenu.add(item);
	}

	// Ouverture et impression fichier dans affichage de droite
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ta.setText("");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setSelectedFile(fc.getCurrentDirectory());
			fc.setDialogTitle("Selection fichier");
			fc.setMultiSelectionEnabled(false);

			int retVal = fc.showOpenDialog(ClassChooser3.this);
			if (retVal == fc.APPROVE_OPTION) {
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
			}
		}
	}

	// Classe main :
	public static void main(String[] args) {
		new ClassChooser3();
	}

	// Sauvegarde fichier :
	private void Sauvegarder(String currentFileBeingEdited) {
		PrintWriter pw = null;
		System.out.println("Fichier : " + currentFileBeingEdited
				+ " sauvegardé.");
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
	}

	public void creerFichier() {
		// ta.setText("");
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setSelectedFile(fc.getCurrentDirectory());
		fc.setDialogTitle("Selection dossier");
		fc.setMultiSelectionEnabled(false);

		int choix = fc.showOpenDialog(ClassChooser3.this);
		file = fc.getSelectedFile();
		currentFileBeingEdited = file.getAbsolutePath();

		if (choix == fc.APPROVE_OPTION) {
			String s = (String) JOptionPane.showInputDialog(null,
					"Chemin selectionné : " + currentFileBeingEdited + "\n"
							+ "Nom du fichier: ", "Choix du nom de fichier",
					JOptionPane.PLAIN_MESSAGE, null, null, "Nouveau_fichier");

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
				JOptionPane.showMessageDialog(null, "Fichier non créé !");
			}
			Sauvegarder(currentFileBeingEdited);
		}
	}

	// Action sur le menu fichier :
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String menuName;
		menuName = e.getActionCommand();

		if (menuName.equals("Exit")) { // Quitter
			System.exit(0);
		} else if ("Save".equalsIgnoreCase(menuName)) { // Sauvegarder
			if (currentFileBeingEdited != null) {
				Sauvegarder(currentFileBeingEdited);
			} else {
				JOptionPane
						.showMessageDialog(
								null,
								"Aucun fichier n'est ouvert actuellement...\nFaire \"Sauvegarder a\" pour enregistrer le nouveau fichier.");
			}
		} else if ("Save to".equalsIgnoreCase(menuName)) { // Créer nouveau
															// fichier
			creerFichier();
		} else if ("Delete".equalsIgnoreCase(menuName)) { // Supprimer fichier
			if (currentFileBeingEdited != null) {
				Object[] options = { "Oui", "Non" };
				int n = JOptionPane
						.showOptionDialog(null, "Suppression de "
								+ currentFileBeingEdited,
								"Confirmation requise",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[1]);
				if (n == 0) {
					File MyFile = new File(currentFileBeingEdited);
					System.out.println("suppression de "
							+ currentFileBeingEdited);
					// erreur à cet endroit !!
					// Mais pas obligatoire !
					MyFile.delete();
					ta.setText("");
				}
			} else {
				JOptionPane.showMessageDialog(null,
						"Aucun fichier ouvert actuellement.");
			}
		} // reste rennomage à faire : copie + création + suppression
			// (optionnel)
	}
}
