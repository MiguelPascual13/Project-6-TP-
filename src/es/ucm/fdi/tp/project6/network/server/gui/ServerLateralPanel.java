package es.ucm.fdi.tp.project6.network.server.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ServerLateralPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String STOP_BUTTON_TEXT = "Stop";

	private JButton stopButton;

	public interface StopButtonListener {
		public void stopButtonClicked();
	}

	public ServerLateralPanel(StopButtonListener stopButtonListener) {
		super(new FlowLayout());
		this.initializeComponents();
		this.addListeners(stopButtonListener);
		this.addComponents();
	}

	private void initializeComponents() {
		this.stopButton = new JButton(STOP_BUTTON_TEXT);
	}

	private void addComponents() {
		this.add(this.stopButton);
	}

	private void addListeners(StopButtonListener stopButtonListener) {
		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stopButtonListener.stopButtonClicked();
			}
		});
	}
}
