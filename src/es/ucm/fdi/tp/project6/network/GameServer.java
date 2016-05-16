package es.ucm.fdi.tp.project6.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

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
import es.ucm.fdi.tp.project6.network.responseclasses.ChangeTurnResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.ErrorResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameOverResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameStartResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveEndResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveStartResponse;

public class GameServer extends Controller implements GameObserver {

	private int port;
	private int numPlayers;
	private int numConnections;
	private GameFactory gameFactory;
	private List<Connection> clients;
	private ControlGUI controlWindow;
	volatile private ServerSocket server;
	volatile private boolean stopped;
	volatile boolean gameOver;

	public GameServer(GameFactory gameFactory, List<Piece> pieces, int port) {
		super(new Game(gameFactory.gameRules()), pieces);
		this.port = port;
		this.numPlayers = pieces.size();
		this.gameFactory = gameFactory;
		this.stopped = false;
		this.gameOver = false;
		this.game.addObserver(this);// We add the game of the controller as an
		// observer to receive information (the server)
		this.controlWindow = new ControlGUI();
	}

	public synchronized void makeMove(Player player) {
		try {
			super.makeMove(player);
		} catch (GameError e) {
		}
	}

	public synchronized void stop() {
		try {
			super.stop();
		} catch (GameError e) {
		}
	}

	public synchronized void restart() {
		try {
			super.restart();
		} catch (GameError e) {
		}
	}

	public void start() {
		controlWindow.controlGUI(); // Hay que crear y añadir el listener del
									// stopServerButton, como no se si quieres
									// usar un patron o no te lo dejo a ti para
									// que lo hagas.
		startServer();
	}

	private void startServer() {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			controlWindow.log("Error creating the server" + e.getMessage());
		}
		stopped = false;

		while (!stopped) {
			try {
				Socket s = new Socket();
				s = server.accept();
				controlWindow.log("Connection with Client succed");
				handleRequest(s);
			} catch (IOException e) {
				if (!stopped) {
					controlWindow.log("error while waiting for a connection: "
							+ e.getMessage());
				}
			}

		}
	}

	private void handleRequest(Socket s) {
		try {
			Connection c = new Connection(s);
			Object clientRequest = c.getObject();
			if (!(clientRequest instanceof String)
					&& !((String) clientRequest).equalsIgnoreCase("Connect")) {
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}
			if (numPlayers == numConnections) {
				c.sendObject(new GameError("Number of Clients exceeded"));
			} else {
				numConnections++;
				clients.add(c);
				c.sendObject("OK");
				c.sendObject(gameFactory);
				c.sendObject(pieces.get(numConnections - 1));
			}

			if (numPlayers == numConnections) {
				if(){//Si es la primera vez start sino restart.
				// Podemos hacerlo con un boolean o un contador.
				start();
				 }
				 else {
				 restart();
				 }
			}

			startClientListener(c);
		} catch (IOException | ClassNotFoundException e) {
		}
	}

	private void startClientListener(Connection c) {
		gameOver = false;
		new Thread() {
			public void run() {
				while (!stopped && !gameOver) {
					try {
						Command cmd;
						cmd = (Command) c.getObject();
						cmd.execute(GameServer.this);
					} catch (ClassNotFoundException | IOException e) {
						if (!stopped && !gameOver) {
							game.stop();
						}
					}
				}
			}
		}.start();
	}

	void forwardNotification(Response r) {
		// en las transparencias pone call c.sendObject(r) for each client
		// connection `c´, creo que es esto.
		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).sendObject(r);
			} catch (IOException e) {
				// No se si hace falta tratarlo.
			}
		}
	}

	public void onGameStart(Board board, String gameDesc, List<Piece> pieces,
			Piece turn) {
		forwardNotification(new GameStartResponse(board, gameDesc, pieces, turn));
	}

	public void onGameOver(Board board, State state, Piece winner) {
		forwardNotification(new GameOverResponse(board, state, winner));
		game.stop();
	}

	public void onMoveStart(Board board, Piece turn) {
		forwardNotification(new MoveStartResponse(board, turn));
	}

	public void onMoveEnd(Board board, Piece turn, boolean success) {
		forwardNotification(new MoveEndResponse(board, turn, success));
	}

	public void onChangeTurn(Board board, Piece turn) {
		forwardNotification(new ChangeTurnResponse(board, turn));
	}

	public void onError(String msg) {
		forwardNotification(new ErrorResponse(msg));
	}

}
