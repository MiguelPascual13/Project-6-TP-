package es.ucm.fdi.tp.project5.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project5.Main;
import es.ucm.fdi.tp.project5.boardpanel.Cell.CellClickedListener;
import es.ucm.fdi.tp.project5.controller.PlayersMap;
import es.ucm.fdi.tp.project5.lateralpanel.AutomaticMovesPanel.IntelligentButtonListener;
import es.ucm.fdi.tp.project5.lateralpanel.AutomaticMovesPanel.RandomButtonListener;
import es.ucm.fdi.tp.project5.lateralpanel.PieceColorsPanel.ColorChangeListener;
import es.ucm.fdi.tp.project5.lateralpanel.PlayerModesPanel.PlayerModesChangeListener;
import es.ucm.fdi.tp.project5.lateralpanel.QuitRestartPanel.QuitButtonListener;
import es.ucm.fdi.tp.project5.lateralpanel.QuitRestartPanel.RestartButtonListener;
import es.ucm.fdi.tp.project5.moveControllers.MoveController;
import es.ucm.fdi.tp.project5.moveControllers.MoveController.MoveStateChangeListener;
import es.ucm.fdi.tp.project5.utils.PieceColorMap;

/**
 * This class is a kind of equivalent to GenericConsoleView on the basecode,
 * catch the model events and this the help of the controller updates the view.
 */
public class GenericSwingView implements GameObserver {

	private static final String STARTING_MESSAGE = "Starting ";
	private static final String CHANGE_TURN_MESSAGE = "Turn for ";
	private static final String GAME_OVER_MESSAGE = "Game Over!!\n";
	private static final String GAME_STATUS_MESSAGE = "Game Status: ";
	private static final String WINNER_MESSAGE = "Winner: ";
	private static final String TITLE_MESSAGE = "Board Games: ";
	private static final String YOU_MESSAGE = " You ";

	private PieceColorMap colorChooser;
	private Controller controller;
	private PlayersMap playersMap;
	private Piece viewPiece;
	private Piece actualTurn;
	private GUI gui;
	private MoveController moveController;
	private Player random;
	private Player ai;
	private Board board;
	private List<Piece> pieces;

	public GenericSwingView(Observable<GameObserver> g, Controller c,
			final Piece viewPiece, MoveController moveController, Player random,
			Player ai) {

		// Automatic players references.
		this.random = random;
		this.ai = ai;

		// PlayersMap configuration.
		this.playersMap = Main.getPlayersMap();
		this.playersMap.setRandomPlayer(random);
		this.playersMap.setAiPlayer(ai);

		this.moveController = moveController;
		this.controller = c;

		this.viewPiece = viewPiece;

		/*
		 * Suscription to the model events, in the client-server application
		 * mode, the application subscribes to the client events.
		 */
		g.addObserver(this);

		colorChooser = new PieceColorMap();
	}

	@Override
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces,
			Piece turn) {

		this.board = board;
		this.pieces = pieces;

		/* to avoid strange behaviors in case of restarting */
		if (gui != null)
			gui.dispose();

		this.actualTurn = turn;

		gui = new GUI(this.board, pieces, colorChooser, turn, moveController,
				this.viewPiece, playersMap, this.getQuitButtonListener(),
				this.getRestartButtonListener(), this.getRandomButtonListener(),
				this.getIntelligentButtonListener(),
				this.getColorChangeListener(),
				this.getPlayerModesChangeListener(),
				this.getCellClickedListener());

		setGUITitle(gameDesc);
		checkForDisablingButtons();
		
		update();
		
		gui.appendToStatusMessagePanel(
				STARTING_MESSAGE + "'" + gameDesc + "'\n");
		if (viewPiece != null && this.viewPiece.equals(this.actualTurn)) {
			gui.appendToStatusMessagePanel(
					CHANGE_TURN_MESSAGE + YOU_MESSAGE + this.actualTurn + "\n");
		} else {
			gui.appendToStatusMessagePanel(
					CHANGE_TURN_MESSAGE + this.actualTurn + "\n");
		}
		checkForMoreMoveIndications();
		
		setGUIvisible();
	}

	@Override
	public void onGameOver(Board board, State state, Piece winner) {
		this.board = board;
		update();

		gui.appendToStatusMessagePanel(GAME_OVER_MESSAGE);
		gui.appendToStatusMessagePanel(GAME_STATUS_MESSAGE + state + "\n");
		if (winner != null) {
			gui.appendToStatusMessagePanel(WINNER_MESSAGE + winner + "\n");
			JOptionPane.showMessageDialog(new JFrame(), WINNER_MESSAGE + winner,
					GAME_OVER_MESSAGE, JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(new JFrame(),
					GAME_STATUS_MESSAGE + state, GAME_OVER_MESSAGE,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (viewPiece == null) {
			disableRestartButton(false);
		}
		gui.disableAutomaticMoves(true);
		disableQuitButton(false);
		update();
	}

	@Override
	public void onMoveStart(Board board, Piece turn) {
	}

	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) {
	}

	@Override
	public void onChangeTurn(Board board, Piece turn) {
		this.board = board;
		this.actualTurn = turn;
		checkForDisablingButtons();
		appendChangeTurnMessage();
		checkForMoreMoveIndications();
		checkForAutomaticMoves();
		update();
	}

	@Override
	public void onError(String msg) {
		if (viewPiece == null || viewPiece.equals(actualTurn)) {
			JOptionPane.showMessageDialog(new JFrame(), msg, "Game error",
					JOptionPane.ERROR_MESSAGE);
			gui.appendToStatusMessagePanel(msg + "\n");
		}
	}

	private void randomMakeMove() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				controller.makeMove(random);
				update();
			}
		});
	}

	private void intelligentMakeMove() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				controller.makeMove(ai);
				update();
			}
		});
	}

	private QuitButtonListener getQuitButtonListener() {

		return new QuitButtonListener() {

			@Override
			public void quitButtonClicked() {

				JFrame ventanaQuit = new JFrame();
				int n = JOptionPane.showConfirmDialog(ventanaQuit,
						"Are you sure you want to quit?", "Quit",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					GenericSwingView.this.controller.stop();
					System.exit(0);
				} else {
					ventanaQuit.dispose();
				}
			}

		};
	}

	/*-----LISTENERS-----*/

	private RestartButtonListener getRestartButtonListener() {
		return new RestartButtonListener() {

			@Override
			public void restartButtonClicked() {
				gui.dispose();
				gui = null;
				GenericSwingView.this.controller.restart();
			}

		};
	}

	private RandomButtonListener getRandomButtonListener() {
		return new RandomButtonListener() {

			@Override
			public void randomButtonClicked() {
				randomMakeMove();
			}

		};
	}

	private IntelligentButtonListener getIntelligentButtonListener() {

		return new IntelligentButtonListener() {

			@Override
			public void intelligentButtonClicked() {
				intelligentMakeMove();
			}

		};
	}

	private ColorChangeListener getColorChangeListener() {

		return new ColorChangeListener() {

			@Override
			public void colorChanged(Piece piece, Color color) {
				colorChooser.setColorFor(piece, color);
				update();
			}

		};
	}

	private PlayerModesChangeListener getPlayerModesChangeListener() {

		return new PlayerModesChangeListener() {

			@Override
			public void setButtonClicked(Piece piece, String mode) {
				if (playersMap.getPlayerType(piece) != mode) {
					playersMap.setPlayerType(piece, mode);
					checkForDisablingButtons();
				}
				update();
			}

		};
	}

	private CellClickedListener getCellClickedListener() {
		return new CellClickedListener() {
			@Override
			public void cellWasClicked(int row, int column, MouseEvent e) {
				Integer answer = moveController.manageClicks(
						GenericSwingView.this.board, row, column, actualTurn,
						viewPiece, e, getMoveStateChangeListener());
				if (answer != null) {
					if (answer.equals(MoveController.REPAINT_AND_MOVE)) {
						controller.makeMove(moveController);
						update();
					} else if (answer
							.equals(MoveController.SOMETHING_TO_REPAINT)) {
						update();
					}
				}
			}

		};

	}

	private MoveStateChangeListener getMoveStateChangeListener() {
		return new MoveStateChangeListener() {

			@Override
			public void notifyMoveStateChange(String string) {
				gui.appendToStatusMessagePanel(string);
			}
		};
	}

	/* Some auxiliary private methods */

	private void setGUITitle(String gameDesc) {
		if (viewPiece == null) {
			gui.setTitle(TITLE_MESSAGE + gameDesc);
		} else {
			gui.setTitle(TITLE_MESSAGE + gameDesc + " (" + viewPiece + ")");
		}
	}

	private void setGUIvisible() {
		try {
			EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					gui.setVisible(true);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			throw new GameError("Error while creating the SwingView");
		}
	}

	private void appendChangeTurnMessage() {
		if (this.viewPiece != null && this.viewPiece.equals(this.actualTurn)) {
			gui.appendToStatusMessagePanel(
					CHANGE_TURN_MESSAGE + YOU_MESSAGE + this.actualTurn + "\n");
		} else {
			gui.appendToStatusMessagePanel(
					CHANGE_TURN_MESSAGE + this.actualTurn + "\n");
		}
	}

	private void checkForAutomaticMoves() {
		if (viewPiece == null || actualTurn.equals(viewPiece)) {
			if (playersMap.isPlayerOfType(this.actualTurn,
					playersMap.getPlayerModeString(PlayersMap.RANDOM))) {
				randomMakeMove();
			} else if (playersMap.isPlayerOfType(this.actualTurn,
					playersMap.getPlayerModeString(PlayersMap.INTELLIGENT))) {
				intelligentMakeMove();
			}
		}
	}

	/**
	 * Checks if we have to disable some buttons in the lateral panel.
	 */
	private void checkForDisablingButtons() {
		if (this.viewPiece != null && !this.viewPiece.equals(this.actualTurn)) {
			gui.disableAutomaticMoves(true);
			disableQuitButton(true);
		} else if (viewPiece != null && viewPiece.equals(this.actualTurn)) {
			if (!playersMap.isPlayerOfType(actualTurn,
					playersMap.getPlayerModeString(PlayersMap.MANUAL))) {
				gui.disableAutomaticMoves(true);
			} else {
				gui.disableAutomaticMoves(false);
				gui.disableQuitButton(false);
			}
		} else if (viewPiece == null && !playersMap.isPlayerOfType(actualTurn,
				playersMap.getPlayerModeString(PlayersMap.MANUAL))) {
			gui.disableAutomaticMoves(true);
			disableRestartButton(true);
			disableQuitButton(true);
		} else {
			gui.disableAutomaticMoves(false);
			disableRestartButton(false);
			disableQuitButton(false);
		}
	}

	private void disableQuitButton(boolean disable) {
		gui.disableQuitButton(disable);
	}

	private void disableRestartButton(boolean disable) {
		gui.disableRestartButton(disable);
	}

	private void update() {
		gui.update(moveController.getSelectedRow(),
				moveController.getSelectedColumn(),
				moveController.getFilterOnCells(this.board), this.actualTurn,
				this.board, pieces, actualTurn, playersMap, colorChooser);
	}

	/**
	 * Look after possible status massage panel updates due to some
	 * moveController indications. (We could have made it implementing
	 * observable and game observer, but that seems to be cleaner)
	 */
	private void checkForMoreMoveIndications() {
		if (viewPiece == null && playersMap.isPlayerOfType(actualTurn,
				playersMap.getPlayerModeString(PlayersMap.MANUAL))) {
			gui.appendToStatusMessagePanel(
					moveController.notifyMoveStartInstructions());
		} else if (viewPiece != null && viewPiece.equals(actualTurn)
				&& playersMap.isPlayerOfType(actualTurn,
						playersMap.getPlayerModeString(PlayersMap.MANUAL))) {
			gui.appendToStatusMessagePanel(
					moveController.notifyMoveStartInstructions());
		}
	}
}
