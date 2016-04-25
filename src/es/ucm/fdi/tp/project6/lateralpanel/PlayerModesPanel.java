package es.ucm.fdi.tp.project6.lateralpanel;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class PlayerModesPanel extends JPanel {
	private static final String SET_BUTTON_TEXT = "Set";
	private static final String PANEL_NAME_TEXT = "Player Modes";

	private JButton setButton;
	private JComboBox<Piece> playerName;
	private JComboBox<String> playerGameModes;

	public interface PlayerModesChangeListener {
		void SetButtonClicked(Piece piece, String mode);
	}

	public PlayerModesPanel(Piece pieces[], PlayerModesChangeListener listener,
			Piece viewPiece, String playerModesArray[]) {

		super(new FlowLayout());
		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), PANEL_NAME_TEXT));

		this.fillPlayerNameJComboBox(pieces, viewPiece);
		this.fillPlayerModesJComboBox(viewPiece, playerModesArray);
		setButton = new JButton(SET_BUTTON_TEXT);

		this.addListeners(listener, pieces, playerModesArray, viewPiece);

		this.add(playerName);
		this.add(playerGameModes);
		this.add(setButton);

	}

	private void fillPlayerNameJComboBox(Piece pieces[], Piece viewPiece) {
		if (viewPiece == null) {
			playerName = new JComboBox<Piece>(pieces);
		} else {
			playerName = new JComboBox<Piece>();
			playerName.addItem(viewPiece);
		}
	}

	private void fillPlayerModesJComboBox(Piece viewPiece,
			String playerModesArray[]) {
		playerGameModes = new JComboBox<String>();
		for (int i = 0; i < playerModesArray.length; i++) {
			if (playerModesArray[i] != null)
				playerGameModes.addItem(playerModesArray[i]);
		}
	}

	private void addListeners(PlayerModesChangeListener listener,
			Piece pieces[], String playerModesArray[], Piece viewPiece) {
		setButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (viewPiece == null) {
					listener.SetButtonClicked(
							pieces[playerName.getSelectedIndex()],
							playerModesArray[playerGameModes
									.getSelectedIndex()]);
				} else {
					listener.SetButtonClicked(viewPiece,
							(String) playerGameModes.getSelectedItem());
				}
			}
		});
	}
}
