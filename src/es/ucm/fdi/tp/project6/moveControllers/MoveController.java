package es.ucm.fdi.tp.project6.moveControllers;

import java.awt.event.MouseEvent;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
/**
 * This class manage the clicks in the main window and using the mouse event
 * information creates a valid move.
 */
public abstract class MoveController extends Player {

	public interface MoveStateChangeListener {
		public void notifyMoveStateChange(String string);
	}

	// cambiar a enum
	public static final Integer NOTHING_TO_REPAINT = null;
	public static final Integer SOMETHING_TO_REPAINT = -1;
	public static final Integer REPAINT_AND_MOVE = 1;

	/**
	 * Use this for every component of the piece location.
	 */
	public static final Integer DEFAULT_SELECTED_PIECE = -1;

	/**
	 * Manage the mouse event information and save the necessary to generate a
	 * move.
	 * 
	 * @param board
	 * @param row
	 * @param column
	 * @param turn
	 * @param viewPiece
	 * @param mouseEvent
	 * @param moveStateChangeListener
	 * @return
	 */
	public abstract Integer manageClicks(Board board, int row, int column,
			Piece turn, Piece viewPiece, MouseEvent mouseEvent,
			MoveStateChangeListener moveStateChangeListener);

	/**
	 * Checks if we are in the multiview mode and if then if is our turn.
	 * 
	 * @param turn
	 * @param viewPiece
	 * @return
	 */
	protected boolean checkMultiViewCase(Piece turn, Piece viewPiece) {
		if (viewPiece != null) {
			if (!turn.equals(viewPiece))
				return false;
			else
				return true;
		} else
			return true;
	}

	public abstract String notifyMoveStartInstructions();

	public abstract Integer getSelectedRow();

	public abstract Integer getSelectedColumn();

	/**
	 * EXPERIMENTAL: Returns a list of coordinates to be highlighted.
	 * 
	 * @param board
	 * @return
	 */
	public abstract List<Pair<Integer, Integer>> getFilterOnCells(Board board);
}
