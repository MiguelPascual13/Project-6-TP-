package es.ucm.fdi.tp.project5.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project5.Main;

public class PlayersMap {
	private int availablePlayerModes = 1;
	private Player randomPlayer;
	private Player aiPlayer;

	// cambiar por un enum.
	public static final int MANUAL = 0;
	public static final int RANDOM = 1;
	public static final int INTELLIGENT = 2;

	protected Map<Piece, String> players;
	protected String playerModesStringArray[];
	private List<Piece> pieces;
	
	public PlayersMap(List<Piece> pieces) {
		this.pieces = pieces;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
		this.initializePiecePlayersMap(pieces);
	}

	public void setRandomPlayer(Player randomPlayer) {
		this.randomPlayer = randomPlayer;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
		this.initializePiecePlayersMap(pieces);
	}

	public void setAiPlayer(Player aiPlayer) {
		this.aiPlayer = aiPlayer;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
		this.initializePiecePlayersMap(pieces);
	}

	private void initializePiecePlayersMap(List<Piece> pieces) {
		players = new HashMap<Piece, String>();
		if (pieces != null) {
			for (int i = 0; i < pieces.size(); i++) {
				players.put(pieces.get(i), this.playerModesStringArray[MANUAL]);
			}
		}
	}

	/**
	 * Internamente la clase guarda un array de strings de tamaño el número de
	 * modos de juego disponibles de manera que para modo de juego realmente
	 * disponible guarda su descripción y para cada modo de juego no disponible
	 * guarda un nulo. Por ejemplo:
	 * 
	 * Supongamos que los modos de juego disponibles en el main son MANUAL
	 * RANDOM INTELLIGENT ZOMBI
	 * 
	 * pero que sin embargo solo están realmente disponibles el manual y el
	 * zombi, internamente se guardaría el array MANUAL NULL NULL ZOMBI y la
	 * variable availablePlayerModes estaría a 2.
	 * 
	 * Los parámetros son meramente indicadores de disponibilidad de modos de
	 * juego.
	 * 
	 * @param randomPlayer
	 * @param aiPlayer
	 */
	private void initializePlayerModesStringArray(Player randomPlayer,
			Player aiPlayer) {

		/*
		 * Array con las descripciones de los disstintos modos de juego
		 * disponibles.
		 */
		String[] provisionalPlayerModesStringArray = Main
				.getPlayerModesDescriptions();
		int length = provisionalPlayerModesStringArray.length;

		this.playerModesStringArray = new String[length];
		this.playerModesStringArray[MANUAL] = provisionalPlayerModesStringArray[MANUAL];
		if (randomPlayer != null) {
			this.playerModesStringArray[RANDOM] = provisionalPlayerModesStringArray[RANDOM];
			this.availablePlayerModes++;
		} else {
			this.playerModesStringArray[RANDOM] = null;
		}
		if (aiPlayer != null) {
			this.playerModesStringArray[INTELLIGENT] = provisionalPlayerModesStringArray[INTELLIGENT];
			this.availablePlayerModes++;
		} else {
			this.playerModesStringArray[INTELLIGENT] = null;
		}
	}

	/* NOTE: Maybe is a little bit intrincated, and it maybe be changed. */

	/**
	 * Returns an array of strings with the valid player modes
	 * 
	 * @return the internal array previously exposed.
	 */
	public String[] getPlayerModesStringArray() {
		return this.playerModesStringArray;
	}

	/**
	 * Assuming that the string is a valid player mode says if the piece
	 * specified corresponds to that player mode
	 * 
	 * @param piece
	 * @param type
	 * @return
	 */
	public boolean isPlayerOfType(Piece piece, String type) {
		return this.players.get(piece) == type;
	}

	/**
	 * Returns an string of the player mode of the piece specified
	 * 
	 * @param piece
	 * @return
	 */
	public String getPlayerType(Piece piece) {
		return this.players.get(piece);
	}

	/**
	 * asuming that the string represents a valid playerMode, it changes the
	 * player mode of the piece specified.
	 * 
	 * @param piece
	 * @param type
	 */
	public void setPlayerType(Piece piece, String type) {
		this.players.put(piece, type);
	}

	/**
	 * Return an string of the player mode corresponding to the index specified
	 * 
	 * @param index
	 * @return
	 */
	public String getPlayerModeString(int index) {
		return this.playerModesStringArray[index];
	}

	public int getAvailablePlayerModes() {
		return this.availablePlayerModes;
	}
}
