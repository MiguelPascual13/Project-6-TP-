package es.ucm.fdi.tp.project5.lateralpanel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class QuitRestartPanel extends JPanel {
	public static final String QUIT_BUTTON_TEXT = "Quit";
	public static final String RESTART_BUTTON_TEXT = "Restart";

	private JButton quitButton;
	private JButton restartButton;

	public interface QuitButtonListener {
		void quitButtonClicked();
	}

	public interface RestartButtonListener {
		void restartButtonClicked();
	}

	public QuitRestartPanel(QuitButtonListener quitButtonListener,
			RestartButtonListener restartButtonListener, Piece viewPiece) {
		super(new FlowLayout());

		quitButton = new JButton(QUIT_BUTTON_TEXT);
		restartButton = new JButton(RESTART_BUTTON_TEXT);

		this.add(quitButton);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quitButtonListener.quitButtonClicked();
			}
		});
		if (viewPiece == null) {
			this.add(restartButton);
			restartButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					restartButtonListener.restartButtonClicked();
				}
			});
		}
	}

	public void disableQuitButton(boolean disable) {
		this.quitButton.setEnabled(!disable);
	}

	public void disableRestartButton(boolean disable) {
		this.restartButton.setEnabled(!disable);
	}
}
