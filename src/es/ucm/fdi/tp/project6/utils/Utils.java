package es.ucm.fdi.tp.project6.utils;

public class Utils {

	/**
	 * Tells if a cell is too far in the ataxx sense to another, it probably
	 * will be better (more general) it we add another int parameter "distante"
	 * 
	 * @param oldRow
	 * @param oldColumn
	 * @param row
	 * @param column
	 * @return
	 */
	public static boolean InfiniteDistanceExceeded(int oldRow, int oldColumn,
			int row, int column) {
		return Math.max(Math.abs(oldRow - row),
				Math.abs(oldColumn - column)) > 2;
	}
}
