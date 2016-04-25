package es.ucm.fdi.tp.project6.utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.tp.basecode.bgame.Utils;
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
	private static int CHOOSABLE_COLORS = 7;

	/**
	 * Seven colors for seven players.
	 */
	private Color colors[] = { Color.BLUE, Color.GREEN, Color.MAGENTA,
			Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW };

	public PieceColorMap() {
		colorMap = new HashMap<Piece, Color>();
	}

	/**
	 * Devuelve un color aleatorio para cada pieza no nula, a las nulas les
	 * asigna siempre el gris.
	 * 
	 * @param piece
	 * @return
	 */
	public Color getColorFor(Piece piece) {
		if (piece != null) {
			if (isObstacle(piece))
				return OBSTACLE_COLOR;
			else {
				if (colorMap.containsKey(piece))
					return colorMap.get(piece);
				else {
					Color newColor = colors[Utils.randomInt(CHOOSABLE_COLORS)];
					while (colorMap.containsValue(newColor)) {
						newColor = colors[Utils.randomInt(CHOOSABLE_COLORS)];
					}
					colorMap.put(piece, newColor);
					return colorMap.get(piece);
				}
			}
		} else
			return EMPTY_COLOR;
	}

	private boolean isObstacle(Piece piece) {
		return piece.getId() == Main.OBSTACLE;
	}

	public void setColorFor(Piece piece, Color color) {
		colorMap.put(piece, color);
	}
}
