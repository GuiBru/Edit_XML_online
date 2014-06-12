import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultEditorKit;

public class Ligne_Selec extends JFrame implements MouseListener {
	JTextArea textArea;
	Action selectLine;

	public Ligne_Selec() {

		textArea = new JTextArea("Une\nDeux\nTrois\nQuatre", 10, 30);
		textArea.addMouseListener(this);

		JScrollPane scrollPane = new JScrollPane(textArea);
		getContentPane().add(scrollPane, BorderLayout.SOUTH);
		getContentPane().add(new JTextArea());

		selectLine = getAction(DefaultEditorKit.selectLineAction);

	}

	private Action getAction(String name) {
		Action action = null;
		Action[] actions = textArea.getActions();

		for (int i = 0; i < actions.length; i++) {
			if (name.equals(actions[i].getValue(Action.NAME).toString())) {
				action = actions[i];
				break;
			}
		}

		return action;
	}

	public void mouseClicked(MouseEvent e) {
		textArea.setCaretPosition(textArea.viewToModel(e.getPoint()));
		System.out.println(textArea.getCaretPosition());
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public static void main(String[] args) {
		TextAreaSelectLine frame = new TextAreaSelectLine();
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}