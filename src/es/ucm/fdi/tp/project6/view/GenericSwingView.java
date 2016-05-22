package es.ucm.fdi.tp.project6.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.Main;
import es.ucm.fdi.tp.project6.boardpanel.Cell.CellClickedListener;
import es.ucm.fdi.tp.project6.controller.PlayersMap;
import es.ucm.fdi.tp.project6.lateralpanel.AutomaticMovesPanel.IntelligentButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.AutomaticMovesPanel.RandomButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.PieceColorsPanel.ColorChangeListener;
import es.ucm.fdi.tp.project6.lateralpanel.PlayerModesPanel.PlayerModesChangeListener;
import es.ucm.fdi.tp.project6.lateralpanel.QuitRestartPanel.QuitButtonListener;
import es.ucm.fdi.tp.project6.lateralpanel.QuitRestartPanel.RestartButtonListener;
import es.ucm.fdi.tp.project6.moveControllers.MoveController;
import es.ucm.fdi.tp.project6.moveControllers.MoveController.MoveStateChangeListener;
import es.ucm.fdi.tp.project6.utils.PieceColorMap;

/**
 * This class is a kind of equivalent to GenericConsoleView on the basecode,
 * catch the model events and this the help of the controller updates the view.
 */
public class GenericSwingView implements GameObserver {

	private static final String startingMessage = "Starting ";
	private static final String changeTurnMessage = "Turn for ";
	private static final String gameOverMessage = "Game Over!!\n";
	private static final String gameStatusMessage = "Game Status: ";
	private static final String winnerMessage = "Winner: ";
	private static final String titleMessage = "Board Games: ";
	private static final String youMessage = " You ";

	private PieceColorMap colorChooser;
	private Controller controller;
	private PlayersMap playersMap;
	private Piece viewPiece;
	private Piece actualTurn;
	private GUI gui;
	private MoveController moveController;
	private Player random;
	private Player ai;

	/**
	 * Constructor to match the arguments of the method createSwing view in the
	 * factories of the basecode.
	 * 
	 * @param g
	 *            game in play
	 * @param c
	 *            controller to use
	 * @param viewPiece
	 *            owner of the view
	 * @param moveController
	 *            kind of move generator (depends on the concrete game).
	 * @param random
	 *            ramdom move generator
	 * @param ai
	 *            intelligent move generator
	 */
	public GenericSwingView(Observable<GameObserver> g, Controller c,
			final Piece viewPiece, MoveController moveController, Player random,
			Player ai) {

		this.random = random;
		this.ai = ai;
		this.playersMap = Main.getPlayersMap();
		this.playersMap.setRandomPlayer(random);
		this.playersMap.setAiPlayer(ai);
		this.moveController = moveController;
		this.controller = c;
		this.viewPiece = viewPiece;
		g.addObserver(this);
		colorChooser = new PieceColorMap();
	}

	@Override
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces,
			Piece turn) {

		/* to avoid strange behaviors in case of restarting */
		if (gui != null)
			gui.dispose();

		this.actualTurn = turn;

		gui = new GUI(board, pieces, colorChooser, turn, moveController,
				this.viewPiece, playersMap,
				this.getQuitButtonListener(controller),
				this.getRestartButtonListener(controller),
				this.getRandomButtonListener(board),
				this.getIntelligentButtonListener(board),
				this.getColorChangeListener(board),
				this.getPlayerModesChangeListener(board),
				this.getCellClickedListener(board));

		setGUITitle(gameDesc);
		checkForDisablingButtons();
		update(board);
		gui.appendToStatusMessagePanel(
				startingMessage + "'" + gameDesc + "'\n");
		if (this.viewPiece.equals(this.actualTurn)) {
			gui.appendToStatusMessagePanel(
					changeTurnMessage + youMessage + this.actualTurn + "\n");
		} else {
			gui.appendToStatusMessagePanel(
					changeTurnMessage + this.actualTurn + "\n");
		}
		checkForMoreMoveIndications();
		setGUIvisible();
	}

	@Override
	public void onGameOver(Board board, State state, Piece winner) {
		update(board);

		gui.appendToStatusMessagePanel(gameOverMessage);
		gui.appendToStatusMessagePanel(gameStatusMessage + state + "\n");
		if (winner != null) {
			gui.appendToStatusMessagePanel(winnerMessage + winner + "\n");
			JOptionPane.showMessageDialog(new JFrame(), winnerMessage + winner,
					gameOverMessage, JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(new JFrame(),
					gameStatusMessage + state, gameOverMessage,
					JOptionPane.PLAIN_MESSAGE);
		}

		if (viewPiece == null) {
			disableRestartButton(false);
			disableQuitButton(false);
		}

		update(board);
	}

	@Override
	public void onMoveStart(Board board, Piece turn) {
	}

	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) {
	}

	@Override
	public void onChangeTurn(Board board, Piece turn) {
		this.actualTurn = turn;
		checkForDisablingButtons();
		appendChangeTurnMessage();
		checkForMoreMoveIndications();
		update(board);
		checkForAutomaticMoves(board);
	}

	@Override
	public void onError(String msg) {
		if (viewPiece == null || viewPiece.equals(actualTurn)) {
			JOptionPane.showMessageDialog(new JFrame(), msg, "Game error",
					JOptionPane.ERROR_MESSAGE);
			gui.appendToStatusMessagePanel(msg + "\n");
		}
	}

	private void randomMakeMove(Board board) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				controller.makeMove(random);
				update(board);
			}
		});
	}

	private void intelligentMakeMove(Board board) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				controller.makeMove(ai);
				update(board);
			}
		});
	}

	private QuitButtonListener getQuitButtonListener(Controller controller) {
		return new QuitButtonListener() {

			@Override
			public void QuitButtonClicked() {

				JFrame ventanaQuit = new JFrame();
				int n = JOptionPane.showConfirmDialog(ventanaQuit,
						"Are you sure you want to quit?", "Quit",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					controller.stop();
					System.exit(0);
				} else {
					ventanaQuit.dispose();
				}
			}

		};
	}

	/*-----LISTENERS-----*/

	private RestartButtonListener getRestartButtonListener(
			Controller controller) {
		return new RestartButtonListener() {

			@Override
			public void RestartButtonClicked() {
				gui.dispose();
				gui = null;
				controller.restart();
			}

		};
	}

	private RandomButtonListener getRandomButtonListener(Board board) {
		return new RandomButtonListener() {

			@Override
			public void RandomButtonClicked() {
				randomMakeMove(board);
			}

		};
	}

	private IntelligentButtonListener getIntelligentButtonListener(
			Board board) {
		return new IntelligentButtonListener() {

			@Override
			public void IntelligentButtonClicked() {
				intelligentMakeMove(board);
			}

		};
	}

	private ColorChangeListener getColorChangeListener(Board board) {
		return new ColorChangeListener() {

			@Override
			public void colorChanged(Piece piece, Color color) {
				colorChooser.setColorFor(piece, color);
				update(board);
			}

		};
	}

	private PlayerModesChangeListener getPlayerModesChangeListener(
			Board board) {
		return new PlayerModesChangeListener() {

			@Override
			public void SetButtonClicked(Piece piece, String mode) {
				if (playersMap.getPlayerType(piece) != mode) {
					playersMap.setPlayerType(piece, mode);
					checkForDisablingButtons();
				}
				update(board);
			}

		};
	}

	private CellClickedListener getCellClickedListener(Board board) {
		return new CellClickedListener() {

			@Override
			public void cellWasClicked(int row, int column, MouseEvent e) {
				Integer answer = moveController.manageClicks(board, row, column,
						actualTurn, viewPiece, e, getMoveStateChangeListener());
				if (answer != null) {
					if (answer.equals(MoveController.REPAINT_AND_MOVE)) {
						controller.makeMove(moveController);
						update(board);
					} else if (answer
							.equals(MoveController.SOMETHING_TO_REPAINT)) {
						update(board);
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
			gui.setTitle(titleMessage + gameDesc);
		} else {
			gui.setTitle(titleMessage + gameDesc + " (" + viewPiece + ")");
		}
	}

	private void setGUIvisible() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				gui.setVisible(true);
			}
		});
	}

	private void appendChangeTurnMessage() {
		if (this.viewPiece.equals(this.actualTurn)) {
			gui.appendToStatusMessagePanel(
					changeTurnMessage + youMessage + this.actualTurn + "\n");
		} else {
			gui.appendToStatusMessagePanel(
					changeTurnMessage + this.actualTurn + "\n");
		}
	}

	private void checkForAutomaticMoves(Board board) {
		if (viewPiece == null || actualTurn.equals(viewPiece)) {
			if (playersMap.isPlayerOfType(this.actualTurn,
					playersMap.getPlayerModeString(PlayersMap.RANDOM))) {
				randomMakeMove(board);
			} else if (playersMap.isPlayerOfType(this.actualTurn,
					playersMap.getPlayerModeString(PlayersMap.INTELLIGENT))) {
				intelligentMakeMove(board);
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
		} else if (viewPiece.equals(this.actualTurn)) {
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

	private void update(Board board) {
		gui.update(moveController.getSelectedRow(),
				moveController.getSelectedColumn(),
				moveController.getFilterOnCells(board), actualTurn);
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
		} else if (viewPiece.equals(actualTurn)
				&& playersMap.isPlayerOfType(actualTurn,
						playersMap.getPlayerModeString(PlayersMap.MANUAL))) {
			gui.appendToStatusMessagePanel(
					moveController.notifyMoveStartInstructions());
		}
	}
}
