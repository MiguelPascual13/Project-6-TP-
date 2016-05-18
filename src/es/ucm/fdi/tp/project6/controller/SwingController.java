package es.ucm.fdi.tp.project6.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.Main;

/**
 * Controller extended utilities for a swing view.
 */
public class SwingController extends Controller {

	private int availablePlayerModes = 1;
	private Player randomPlayer;
	private Player aiPlayer;

	// cambiar por un enum.
	public static final int MANUAL = 0;
	public static final int RANDOM = 1;
	public static final int INTELLIGENT = 2;

	protected Map<Piece, String> players;
	protected String playerModesStringArray[];

	public SwingController(Game game, List<Piece> pieces, Player randomPlayer,
			Player aiPlayer) {
		super(game, pieces);
		this.randomPlayer=randomPlayer;
		this.aiPlayer = aiPlayer;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
		this.initializePiecePlayersMap(pieces);
	}
	
	public void setRandomPlayer(Player randomPlayer){
		this.randomPlayer = randomPlayer;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
	}
	public void setAiPlayer(Player aiPlayer){
		this.aiPlayer = aiPlayer;
		this.initializePlayerModesStringArray(this.randomPlayer, this.aiPlayer);
	}

	private void initializePiecePlayersMap(List<Piece> pieces) {
		players = new HashMap<Piece, String>();
		if (pieces != null) {
			for (int i = 0; i < pieces.size(); i++) {
				players.put(pieces.get(i), this.playerModesStringArray[MANUAL]);
			}
		}
	}

	private void initializePlayerModesStringArray(Player randomPlayer,
			Player aiPlayer) {
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
	 * @return
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
