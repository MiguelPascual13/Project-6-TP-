package es.ucm.fdi.tp.project6.lateralpanel;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class StatusMessagePanel extends JPanel {
	private static final String PANEL_NAME_TEXT = "Status Message";

	private JScrollPane scrollPane;
	private JTextArea textArea;

	public StatusMessagePanel() {
		super(new BorderLayout());

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), PANEL_NAME_TEXT));
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void append(String message) {
		this.textArea.append(message);
		this.textArea.setCaretPosition(this.textArea.getDocument().getLength());
	}
}
