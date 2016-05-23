package es.ucm.fdi.tp.project6.utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.util.Collections;

import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.Main;

/**
 * Provides the function getColorFor, this class should be re-writing using the
 * basecodes functions in the Utils class, unfortunately we find it out too
 * late.
 */
public class PieceColorMap {

	private Map<Piece, Color> colorMap;
	public static final Color OBSTACLE_COLOR = Color.BLACK;
	public static final Color EMPTY_COLOR = Color.GRAY;

	/**
	 * Seven colors for seven players.
	 */
	private final Color colors[] = { Color.BLUE, Color.GREEN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW };

	public PieceColorMap() {
		colorMap = new HashMap<Piece, Color>();
		Collections.shuffle(Arrays.asList(colors));
	}

	/**
	 * Devuelve un color aleatorio para cada pieza no nula, a las nulas les
	 * asigna siempre el gris.
	 * 
	 * @param piece
	 * @return
	 */
	public Color getColorFor(Piece piece) {
		if (piece == null) return EMPTY_COLOR;
		
		if (isObstacle(piece)) {
			return OBSTACLE_COLOR;
		} else {
			if (!colorMap.containsKey(piece)) {
				colorMap.put(piece, colors[colorMap.size()]);
			}
			return colorMap.get(piece);
		}
	}

	private boolean isObstacle(Piece piece) {
		return piece.getId().equals(Main.OBSTACLE);
	}

	public void setColorFor(Piece piece, Color color) {
		colorMap.put(piece, color);
	}
}
