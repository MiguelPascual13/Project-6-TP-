package es.ucm.fdi.tp.project6.moveControllers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.ataxx.AtaxxMove;

/**
 * Move controller for ataxx.
 */
@SuppressWarnings("serial")
public class AtaxxMoveController extends MoveController {

	private static final String MOVE_START_MESSAGE = "Click on an origin piece\n";
	private static final String MOVE_IN_PROGRESS_MESSAGE = "Click on an empty destination\n";

	private boolean somethingSelected = false;

	/* Destination piece. */
	private Integer oldRow = null;
	private Integer oldColumn = null;

	/* Piece that the view use to show the possible moves filter. */
	private Integer selectedRow = null;
	private Integer selectedColumn = null;

	/* Destination piece. */
	private Integer newRow = null;
	private Integer newColumn = null;

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces,
			GameRules rules) {
		return new AtaxxMove(oldRow, oldColumn, newRow, newColumn, p);
	}

	public Integer manageClicks(Board board, int row, int column, Piece turn,
			Piece viewPiece, MouseEvent mouseEvent,
			MoveStateChangeListener moveStateChangeListener) {

		if (!checkMultiViewCase(turn, viewPiece))
			return NOTHING_TO_REPAINT;

		if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
			if (somethingSelected) {
				if (board.getPosition(row, column) == null) {
					setDestinationCell(row, column);
					if (InfiniteDistanceExceeded(oldRow, oldColumn, row,
							column)) {
						return NOTHING_TO_REPAINT;
					}
					somethingSelected = false;
					/*
					 * Says to the view that the filter must be OFF in the next
					 * repaint.
					 */
					resetSelectedCell();
					return REPAINT_AND_MOVE;
				} else
					return NOTHING_TO_REPAINT;
			} else {
				if (board.getPosition(row, column).equals(turn)) {
					/*
					 * Says to the view that the filter must be ON in the next
					 * repaint.
					 */
					setSelectedCell(row, column);
					somethingSelected = true;
					moveStateChangeListener
							.notifyMoveStateChange(MOVE_IN_PROGRESS_MESSAGE);
					return SOMETHING_TO_REPAINT;
				} else {
					return NOTHING_TO_REPAINT;
				}
			}
		} else {
			somethingSelected = false;
			/*
			 * Says to the view that the filter must be OFF in the next repaint.
			 */
			resetSelectedCell();
			return SOMETHING_TO_REPAINT;
		}
	}

	/* La vista trabaja con selected, el movimiento con old */
	// GET
	public Integer getSelectedRow() {
		return this.selectedRow;
	}

	public Integer getSelectedColumn() {
		return this.selectedColumn;
	}

	// RESET
	private void resetSelectedCell() {
		this.selectedRow = null;
		this.selectedColumn = null;
	}

	// SET
	private void setSelectedCell(Integer row, Integer column) {
		this.oldRow = row;
		this.oldColumn = column;
		this.selectedRow = row;
		this.selectedColumn = column;
	}

	private void setDestinationCell(Integer row, Integer column) {
		this.newRow = row;
		this.newColumn = column;
	}

	@Override
	public List<Pair<Integer, Integer>> getFilterOnCells(Board board) {
		if (selectedRow != null && selectedColumn != null) {
			List<Pair<Integer, Integer>> filterOnCellsList = new ArrayList<Pair<Integer, Integer>>();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getCols(); j++) {
					if ((!InfiniteDistanceExceeded(selectedRow,
							selectedColumn, i, j)
							&& board.getPosition(i, j) == null)
							|| ((i == selectedRow) && (j == selectedColumn))) {
						filterOnCellsList.add(new Pair<Integer, Integer>(i, j));
					}
				}
			}
			return filterOnCellsList;
		} else
			return null;
	}

	@Override
	public String notifyMoveStartInstructions() {
		return MOVE_START_MESSAGE;
	}
	
	private static boolean InfiniteDistanceExceeded(int oldRow, int oldColumn,
			int row, int column) {
		return Math.max(Math.abs(oldRow - row),
				Math.abs(oldColumn - column)) > 2;
	}
}
