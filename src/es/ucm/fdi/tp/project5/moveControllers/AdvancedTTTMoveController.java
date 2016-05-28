package es.ucm.fdi.tp.project5.moveControllers;

import java.awt.event.MouseEvent;
import java.util.List;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTMove;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

/**
 * move controller for attt
 */
@SuppressWarnings("serial")
public class AdvancedTTTMoveController extends MoveController {

	private boolean somethingSelected = false;
	private int playerInAdvancedStatusCounter = 0;

	private int oldRow;
	private int oldCol;
	private int newRow;
	private int newCol;

	private static final String MOVE_IN_PROGRESS_MESSAGE = "Click on an empty destination\n";
	private static final String SIMPLE_GAME_START = "Click on an empty cell.\n";
	private static final String ADVANCED_GAME_START = "Click on an origin piece\n";

	@Override
	public Integer getSelectedRow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getSelectedColumn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces,
			GameRules rules) {
		return new AdvancedTTTMove(oldRow, oldCol, newRow, newCol, p);
	}

	@Override
	public List<Pair<Integer, Integer>> getFilterOnCells(Board board) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setDestinationCell(Integer row, Integer column) {
		this.newRow = row;
		this.newCol = column;
	}

	private Integer ataxxLikeManageClicks(int row, int column, Board board,
			MouseEvent mouseEvent,
			MoveStateChangeListener moveStateChangeListener, Piece turn) {
		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			if (somethingSelected) {
				if (board.getPosition(row, column) == null) {
					setDestinationCell(row, column);
					somethingSelected = false;
					return REPAINT_AND_MOVE;
				} else
					return NOTHING_TO_REPAINT;
			} else {
				if (board.getPosition(row, column) == turn) {
					somethingSelected = true;
					this.oldRow = row;
					this.oldCol = column;
					moveStateChangeListener
							.notifyMoveStateChange(MOVE_IN_PROGRESS_MESSAGE);
					return NOTHING_TO_REPAINT;
				} else {
					return NOTHING_TO_REPAINT;
				}
			}
		} else {
			somethingSelected = false;
			return NOTHING_TO_REPAINT;
		}
	}

	private Integer connectNLikeManageClicks(Board board, int row, int column) {
		if (board.getPosition(row, column) == null) {
			newRow = row;
			newCol = column;
			return REPAINT_AND_MOVE;
		} else
			return NOTHING_TO_REPAINT;
	}

	@Override
	public Integer manageClicks(Board board, int row, int column, Piece turn,
			Piece viewPiece, MouseEvent mouseEvent,
			MoveStateChangeListener moveStateChangeListener) {

		if (!checkMultiViewCase(turn, viewPiece))
			return NOTHING_TO_REPAINT;

		if (board.getPieceCount(turn) == 1)
			this.playerInAdvancedStatusCounter++;

		if (board.getPieceCount(turn) == 0) {
			return ataxxLikeManageClicks(row, column, board, mouseEvent,
					moveStateChangeListener, turn);
		} else {
			return connectNLikeManageClicks(board, row, column);
		}
	}

	@Override
	public String notifyMoveStartInstructions() {
		if (this.playerInAdvancedStatusCounter == 2)
			return ADVANCED_GAME_START;
		else
			return SIMPLE_GAME_START;
	}
}
