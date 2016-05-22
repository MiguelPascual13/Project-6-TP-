package es.ucm.fdi.tp.project6.network.server.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * JPanel to represent the server log, just a big text area in which
 * notifications are printed.
 */
public class ServerLog extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String PANEL_NAME_TEXT = "Server Log";

	private JTextArea log;
	private JScrollPane scrollPane;

	/**
	 * Initializes and creates the panel.
	 */
	public ServerLog() {
		super(new BorderLayout());
		this.initializeComponents();
		this.configurePanel();
		this.addComponents();
	}

	public void append(String message) {
		this.log.append(message+"\n");
		this.scrollToTheBotton();
	}

	private void initializeComponents() {
		this.log = new JTextArea();
		this.scrollPane = new JScrollPane(log);
	}

	private void configurePanel() {
		this.log.setEditable(false);
		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), PANEL_NAME_TEXT));
	}

	private void addComponents() {
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void scrollToTheBotton() {
		this.log.setCaretPosition(this.log.getDocument().getLength());
	}
}
