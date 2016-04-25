package es.ucm.fdi.tp.project6.lateralpanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class PieceColorsPanel extends JPanel {
	private static final String CHOOSE_COLOR_BUTTON_TEXT = "Choose Color";
	private static final String PANEL_NAME_TEXT = "Piece Colors";

	private JButton chooseColorButton;
	private JComboBox<Piece> playerName;

	public interface ColorChangeListener {
		void colorChanged(Piece piece, Color color);
	}

	public PieceColorsPanel(Piece pieces[], ColorChangeListener listener) {
		super(new FlowLayout());

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), PANEL_NAME_TEXT));
		
		playerName = new JComboBox<Piece>(pieces);
		chooseColorButton = new JButton(CHOOSE_COLOR_BUTTON_TEXT);

		this.add(playerName);
		this.add(chooseColorButton);
		this.addListeners(listener, pieces);

	}

	private void addListeners(ColorChangeListener listener, Piece pieces[]) {
		chooseColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Color c = JColorChooser.showDialog(getParent(),
						"Elige el color al que deseas cambiar", Color.BLUE);
				listener.colorChanged(pieces[playerName.getSelectedIndex()],
						c);
			}
		});
	}
}
