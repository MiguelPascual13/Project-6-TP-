package es.ucm.fdi.tp.project6.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.Main;

/**
 * A Class representing a move for Ataxx.
 * 
 * <p>
 * Clase para representar un movimiento del juego Ataxx.
 * 
 */
public class AtaxxMove extends GameMove {

	private static final long serialVersionUID = 1L;

	/**
	 * The row where to place the piece return by {@link GameMove#getPiece()}.
	 * <p>
	 * Fila en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int row;

	/**
	 * The column where to place the piece return by {@link GameMove#getPiece()}
	 * .
	 * <p>
	 * Columna en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int col;

	/**
	 * The row which the piece is moved from.
	 * 
	 * <p>
	 * Fila desde la que se intenta mover la pieza.
	 */
	protected int oldRow;

	/**
	 * The column which the piece is moved from.
	 * 
	 * <p>
	 * Columna desde la que se intenta mover la pieza.
	 */
	protected int oldCol;

	/**
	 * This constructor should be used ONLY to get an instance of
	 * {@link AtaxxMove} to generate game moves from strings by calling
	 * {@link #fromString(String)}
	 * 
	 * <p>
	 * Solo se debe usar este constructor para obtener objetos de
	 * {@link AtaxxMove} para generar movimientos a partir de strings usando el
	 * metodo {@link #fromString(String)}
	 * 
	 */
	public AtaxxMove() {
	}

	/**
	 * Constructs a move for placing (int the ataxx sense) a piece of the type
	 * referenced by {@code p} from a valid position in the board to another
	 * valid position.
	 * <p>
	 * In this case we consider a valid position somewhere in the closed ball of
	 * radius 2 (if we are in the d_infinite distance topology).
	 * 
	 * @param oldRow
	 * @param oldCol
	 * @param row
	 * @param col
	 * @param p
	 */
	public AtaxxMove(int oldRow, int oldCol, int row, int col, Piece p) {
		super(p);
		this.oldRow = oldRow;
		this.oldCol = oldCol;
		this.row = row;
		this.col = col;

		/* Checking if the destination is out of range. */
		if (infiniteDistance() > 2) {
			/*
			 * WHY DO WE THROW A NUMBER FORMAT EXCEPTION??? WHERE IT IS CAUGHT??
			 */
			throw new NumberFormatException();
		}
	}

	@Override
	public void execute(Board board, List<Piece> pieces) {
		/* When we do things legally... */
		if (board.getPosition(oldRow, oldCol).equals(getPiece())) {
			/* When we move to an empty position... */
			if (board.getPosition(row, col) == null) {
				if (infiniteDistance() == 1) {
					moveToAdjacent(board, pieces);
				} else if (infiniteDistance() == 2) {
					moveFar(board, pieces);
				} else {
					throw new GameError("Position (" + row + "," + col
							+ ") is illegal!");
				}
			} else {
				throw new GameError("Position (" + row + "," + col
						+ ") is already occupied!");
			}
		} else {
			throw new GameError("In the position (" + oldRow + "," + oldCol
					+ ") there is no piece of yours.");
		}
	}

	/**
	 * @return the infinite distance between the original piece and the
	 *         destination one.
	 */
	private int infiniteDistance() {
		return Math.max(Math.abs(oldRow - row), Math.abs(oldCol - col));
	}

	/**
	 * Moves piece from its original position to an adjacent one according to
	 * the rules of Ataxx.
	 * 
	 * @param board
	 *            The game board.
	 * @param pieces
	 *            The list of pieces in the board.
	 */
	private void moveToAdjacent(Board board, List<Piece> pieces) {
		board.setPosition(row, col, getPiece());
		board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) + 1);
		transformNeighbors(board, pieces, row, col);
	}

	/**
	 * Moves piece from its original position to a non adjacent one according to
	 * the rules of Ataxx.
	 * 
	 * @param board
	 *            The game board.
	 * @param pieces
	 *            The list of pieces in the board.
	 */
	private void moveFar(Board board, List<Piece> pieces) {
		board.setPosition(row, col, getPiece());
		board.setPosition(oldRow, oldCol, null);
		transformNeighbors(board, pieces, row, col);
	}

	private static final int deltas[][] = { { 0, 1 }, { 1, 1 }, { 1, 0 },
			{ 1, -1 }, { 0, -1 }, { -1, 1 }, { -1, 0 }, { -1, -1 }, };

	/**
	 * Given a position and the dimension of the squared board, decides if the
	 * position is in or out of the board.
	 * 
	 * @param x
	 *            row
	 * @param y
	 *            column
	 * @param dim
	 *            dimension
	 * @return {@code true} if in, {@code false} if not.
	 */
	private boolean inBoard(int x, int y, int dim) {
		return x >= 0 && y >= 0 && x < dim && y < dim;
	}

	private void transformNeighbors(Board board, List<Piece> pieces,
			int oldRow, int oldCol) {
		for (int[] ds : deltas) {
			int x = oldRow + ds[0];
			int y = oldCol + ds[1];
			/*
			 * If is in, the position is not empty an is not an obstacle or a
			 * piece of yours.
			 */
			Piece p;
			if (inBoard(x, y, board.getRows())
					&& (p = board.getPosition(x, y)) != null && !isObstacle(p)
					&& !p.getId().equals(getPiece().getId())) {
				board.setPieceCount(p, board.getPieceCount(p) - 1);
				board.setPieceCount(getPiece(),
						board.getPieceCount(getPiece()) + 1);
				board.setPosition(x, y, getPiece());
			}
		}
	}

	/**
	 * Decides is a piece is an obstacle or not.
	 * 
	 * @param p
	 *            Piece to check.
	 * @return {@code true} if is and obstacle, {@code false} if not.
	 */
	private boolean isObstacle(Piece p) {
		return p.getId().equals(Main.OBSTACLE);
	}

	/**
	 * This move can be constructed from a string of the form
	 * "oldRow SPACE oldCol SPACE row SPACE col".
	 */
	@Override
	public GameMove fromString(Piece p, String str) {
		String[] words = str.split(" ");
		if (words.length != 4) {
			return null;
		}

		try {
			int oldRow = Integer.parseInt(words[0]);
			int oldCol = Integer.parseInt(words[1]);
			int row = Integer.parseInt(words[2]);
			int col = Integer.parseInt(words[3]);
			return createMove(oldRow, oldCol, row, col, p);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	/**
	 * Creates a move that is called from {@link #fromString(Piece, String)}.
	 * Separating it from that method allows us to use this class for other
	 * similar games by overriding this method.
	 * 
	 * @param oldRow
	 *            row from we move
	 * @param oldCol
	 *            column from we move
	 * @param row
	 *            row where we move
	 * @param col
	 *            column where we move
	 * @param p
	 *            piece we move
	 * @return an instance of AtaxxMove
	 */
	protected GameMove createMove(int oldRow, int oldCol, int row, int col,
			Piece p) {
		return new AtaxxMove(oldRow, oldCol, row, col, p);
	}

	@Override
	public String help() {
		return "Row and column for origin and for destination, separated by spaces (four numbers).";
	}
	
	public int getRow(){
		return row;
	}
	public int getCol(){
		return col;
	}
	
	@Override
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Place a piece '" + getPiece() + "' from (" + oldRow + ","
					+ oldCol + ") at (" + row + "," + col + ")";
		}
	}
}