package es.ucm.fdi.tp.project6.network.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.network.Connection;
import es.ucm.fdi.tp.project6.network.responseclasses.ChangeTurnResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.ErrorResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameOverResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameStartResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveEndResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveStartResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.Response;
import es.ucm.fdi.tp.project6.network.server.gui.ControlGUI;
import es.ucm.fdi.tp.project6.network.server.gui.ServerLateralPanel.StopButtonListener;

/**
 * Class that represents the server, is a controller, so the view interacts with
 * it to send things to the model, also is a GameObserver, (uses the Observer
 * pattern), so the views can be refreshed when necessary.
 */
public class GameServer extends Controller implements GameObserver {

	/**
	 * The server will listen to that port.
	 */
	private int portNumber;

	/**
	 * Number of necessary clients to start the game, notice that only the
	 * server knows what game is going to be executed.
	 */
	private int requiredClients;

	/**
	 * Current clients connected to the server.
	 */
	private int currentClients;

	private GameFactory gameFactory;

	/**
	 * Digamos que se usa una especie de patrón observador alterado.
	 */
	private List<Connection> clients;

	private ControlGUI controlGUI;

	private boolean isTheFirstTime;

	/*---SHARED VALUES (RequestHandler and ServerControl, declared as volatile)---*/
	private volatile ServerSocket server;
	private volatile boolean stopped;
	private volatile boolean gameOver;

	/**
	 * Set the server configuration values and determines the reference to the
	 * gameFactory and the list of pieces. Initializes everything necessary to
	 * avoid nullPointers and registers as an observer of the model. This
	 * constructor does not initializes the Server.
	 * 
	 * @param gameFactory
	 * @param pieces
	 * @param portNumber
	 */
	public GameServer(GameFactory gameFactory, List<Piece> pieces,
			int portNumber) {

		super(new Game(gameFactory.gameRules()), pieces);

		this.portNumber = portNumber;
		this.requiredClients = pieces.size();
		this.gameFactory = gameFactory;
		this.stopped = false;
		this.gameOver = false;
		this.clients = new ArrayList<Connection>();
		this.isTheFirstTime = true;

		this.game.addObserver(this);
	}

	@Override
	public synchronized void makeMove(Player player) {
		try {
			super.makeMove(player);
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void stop() {
		try {
			super.stop();
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void restart() {
		try {
			super.restart();
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		startControlGUI();
		startServer();
	}

	public void append(String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				controlGUI.append(message);
			}
		});
	}

	private void startServer() {
		try {
			server = new ServerSocket(this.portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.stopped = false;
		while (!stopped) {
			try {
				Socket socket = server.accept();
				this.append("Client connected succesfully");
				this.handleRequest(socket);
			} catch (IOException e) {
				if (!stopped)
					this.append("Error while waiting for a connection");
				e.printStackTrace();
			}
		}
	}

	private void handleRequest(Socket socket) {
		try {
			Connection connection = new Connection(socket);
			Object clientRequest = connection.getObject();

			/*
			 * Si el primer mensaje mandado por el cliente no es un string o
			 * bien no es igual a la señal de conexión que hemos establecido,
			 * mandamos una excepción al cliente.
			 */
			if (!(clientRequest instanceof String)
					|| !((String) clientRequest).equalsIgnoreCase("Connect")) {
				connection.sendObject(new GameError("Invalid Request"));
				connection.stop();
				return;
			}
			this.serverIsFull(connection);
			this.currentClients++;
			this.clients.add(connection);
			connection.sendObject("OK");
			connection.sendObject(this.gameFactory);
			connection.sendObject(this.pieces.get(currentClients - 1));

			if (this.currentClients == this.requiredClients) {
				if (isTheFirstTime) {
					game.start(pieces);
					append("The game starts now!!!");
					isTheFirstTime = false;
				} else {
					game.restart();
					restart();
				}
			}
			startClientListener(connection);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startClientListener(Connection connection) {
		this.gameOver = false;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopped && !gameOver) {
					try {
						Command command = (Command) connection.getObject();
						command.execute(GameServer.this);
					} catch (ClassNotFoundException | IOException e) {
						if (!stopped && !gameOver) {
							game.stop();
							stopped = true;
							e.printStackTrace();
						}
					}
				}
			}
		});
		thread.start();
	}

	/**
	 * Initializes the GUI to control the server.
	 */
	private void startControlGUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					controlGUI = new ControlGUI(getStopButtonListener());
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGameStart(Board board, String gameDesc, List<Piece> pieces,
			Piece turn) {
		this.forwardNotification(
				new GameStartResponse(board, gameDesc, pieces, turn));
	}

	@Override
	public void onGameOver(Board board, State state, Piece winner) {
		this.forwardNotification(new GameOverResponse(board, state, winner));
	}

	@Override
	public void onMoveStart(Board board, Piece turn) {
		this.forwardNotification(new MoveStartResponse(board, turn));
	}

	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) {
		this.forwardNotification(new MoveEndResponse(board, turn, success));
	}

	@Override
	public void onChangeTurn(Board board, Piece turn) {
		this.forwardNotification(new ChangeTurnResponse(board, turn));
	}

	@Override
	public void onError(String msg) {
		this.forwardNotification(new ErrorResponse(msg));
	}

	private StopButtonListener getStopButtonListener() {
		return new StopButtonListener() {
			@Override
			public void stopButtonClicked() {
				controlGUI.append("Server Stopped");
				controlGUI.dispose();
			}
		};
	}

	/**
	 * Sends a notification to every client connected. Used to re send the
	 * observers notifications.
	 * 
	 * @param response
	 */
	private void forwardNotification(Response response) {
		for (int i = 0; i < this.clients.size(); i++) {
			try {
				clients.get(i).sendObject(response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void serverIsFull(Connection connection) {
		if (this.currentClients == this.requiredClients) {
			try {
				connection.sendObject(new GameError("Server Full of People"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
