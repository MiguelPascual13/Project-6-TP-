package es.ucm.fdi.tp.project6.network.server.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

import es.ucm.fdi.tp.project6.network.server.gui.ServerLateralPanel.StopButtonListener;

/**
 * Frame to interact to control the server, view the status messages, close,...
 */
public class ControlGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String WINDOW_TITLE = "Server Control Panel";

	private ServerLog serverLog;
	private ServerLateralPanel serverLateralPanel;

	/**
	 * Set all the panels and configure the frame.
	 */
	public ControlGUI(StopButtonListener stopButtonListener) {
		super(WINDOW_TITLE);
		this.configureGUI();
		this.initializeComponents(stopButtonListener);
		this.addComponents();
	}

	public void append(String message) {
		this.serverLog.append(message);
	}

	/**
	 * Set all the basic frame configurations.
	 */
	private void configureGUI() {
		this.setLayout(new BorderLayout());
		this.setLocation(100, 50);
		this.setResizable(true);
		this.setSize(850, 600);
		this.setPreferredSize(new Dimension(850, 600));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.pack();
	}

	private void initializeComponents(StopButtonListener stopButtonListener) {
		this.serverLog = new ServerLog();
		this.serverLateralPanel = new ServerLateralPanel(stopButtonListener);
	}

	private void addComponents() {
		this.add(serverLog, BorderLayout.CENTER);
		this.add(serverLateralPanel, BorderLayout.EAST);
	}
}
