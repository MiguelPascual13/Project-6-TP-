package es.ucm.fdi.tp.project6.lateralpanel;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.lateralpanel.AutomaticMovesPanel.IntelligentButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.AutomaticMovesPanel.RandomButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.PieceColorsPanel.ColorChangeListener;
import es.ucm.fdi.tp.project6.lateralpanel.PlayerModesPanel.PlayerModesChangeListener;
import es.ucm.fdi.tp.project6.lateralpanel.QuitRestartPanel.QuitButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.QuitRestartPanel.RestartButtonListener;
import es.ucm.fdi.tp.project6.utils.PieceColorMap;

/**
 * Panel that saves all the lateral panel option information
 */
@SuppressWarnings("serial")
public class LateralPanel extends JPanel {

	private PlayerModesPanel playerModesPanel;
	private PieceColorsPanel pieceColorsPanel;
	private AutomaticMovesPanel automaticMovesPanel;
	private QuitRestartPanel quitRestartPanel;
	private StatusMessagePanel statusMessagePanel;
	private PlayerInformationPanel playerInformationPanel;
	private Piece piecesArray[];

	public LateralPanel(List<Piece> pieces, PieceColorMap colorChooser,
			Board board, Piece viewPiece, SwingController controller,
			Piece turn, QuitButtonListener quitButtonListener,
			RestartButtonListener restartButtonListener,
			RandomButtonListener randomButtonListener,
			IntelligentButtonListener intelligentButtonListener,
			ColorChangeListener colorChangeListener,
			PlayerModesChangeListener playerModesChangeListener) {
		super(new GridLayout(0, 1));

		this.piecesArray = this.piecesListToArrayOfPieces(pieces);

		statusMessagePanel = new StatusMessagePanel();
		playerInformationPanel = new PlayerInformationPanel(pieces, board,
				colorChooser, viewPiece, controller);
		this.buildPieceColorPanel(pieces, colorChangeListener);
		this.buildQuitRestartPanel(viewPiece, quitButtonListener,
				restartButtonListener);

		this.add(statusMessagePanel);
		this.add(playerInformationPanel);
		this.add(pieceColorsPanel);
		this.buildAndAddPlayerModesPanel(piecesArray, viewPiece, controller,
				playerModesChangeListener);
		this.buildAndAddAutomaticMovesPanel(controller, randomButtonListener,
				intelligentButtonListener);
		this.add(quitRestartPanel);

	}

	public void updateTable() {
		playerInformationPanel.updateTableInfo();
	}

	public void appendToStatusMessagePanel(String message) {
		this.statusMessagePanel.append(message);
	}

	public void disableAutomaticMoves(boolean disable) {
		if (automaticMovesPanel != null) {
			this.automaticMovesPanel.disablePanel(disable);
		}
	}

	private boolean buildPlayerModesPanel(Piece pieces[], Piece viewPiece,
			SwingController controller,
			PlayerModesChangeListener playerModesChangeListener) {
		if (controller.getAvailablePlayerModes() == 1) {
			return false;
		} else {
			playerModesPanel = new PlayerModesPanel(pieces,
					playerModesChangeListener, viewPiece,
					controller.getPlayerModesStringArray());
			return true;
		}
	}

	private Piece[] piecesListToArrayOfPieces(List<Piece> pieces) {
		Piece piecesArray[] = new Piece[pieces.size()];
		for (int i = 0; i < pieces.size(); i++) {
			piecesArray[i] = pieces.get(i);
		}
		return piecesArray;
	}

	private void buildAndAddPlayerModesPanel(Piece pieces[], Piece viewPiece,
			SwingController controller,
			PlayerModesChangeListener playerModesChangeListener) {
		if (this.buildPlayerModesPanel(pieces, viewPiece, controller,
				playerModesChangeListener))
			this.add(playerModesPanel);
	}

	private void buildPieceColorPanel(List<Piece> pieces,
			ColorChangeListener listener) {
		Piece piecesArray[] = this.piecesListToArrayOfPieces(pieces);
		pieceColorsPanel = new PieceColorsPanel(piecesArray, listener);
	}

	private boolean buildAutomaticMovesPanel(SwingController controller,
			RandomButtonListener randomButtonListener,
			IntelligentButtonListener intelligentButtonListener) {
		if (controller.getAvailablePlayerModes() == 1) {
			return false;
		} else {
			automaticMovesPanel = new AutomaticMovesPanel(randomButtonListener,
					intelligentButtonListener,
					controller.getPlayerModesStringArray());
			return true;
		}
	}

	private void buildAndAddAutomaticMovesPanel(SwingController controller,
			RandomButtonListener randomButtonListener,
			IntelligentButtonListener intelligentButtonListener) {
		if (this.buildAutomaticMovesPanel(controller, randomButtonListener,
				intelligentButtonListener))
			this.add(automaticMovesPanel);
	}

	private void buildQuitRestartPanel(Piece viewPiece,
			QuitButtonListener quitButtonListener,
			RestartButtonListener restartButtonListener) {
		quitRestartPanel = new QuitRestartPanel(quitButtonListener,
				restartButtonListener, viewPiece);
	}

	public void disableQuitButton(boolean disable) {
		this.quitRestartPanel.disableQuitButton(disable);
	}

	public void disableRestartButton(boolean disable) {
		this.quitRestartPanel.disableRestartButton(disable);
	}
}
