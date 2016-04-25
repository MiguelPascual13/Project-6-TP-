package es.ucm.fdi.tp.project6.boardpanel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

@SuppressWarnings("serial")

public class Cell extends JLabel {

	public interface CellClickedListener {
		public void cellWasClicked(int row, int column, MouseEvent e);
	}

	public Cell(int row, int column, CellClickedListener listener) {
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				listener.cellWasClicked(row, column, e);
			}
		});
	}
}
