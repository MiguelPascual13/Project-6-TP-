package es.ucm.fdi.tp.project6.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.network.ControlGUI.StopServerButtonListener;
import es.ucm.fdi.tp.project6.network.responseclasses.ChangeTurnResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.ErrorResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameOverResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.GameStartResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveEndResponse;
import es.ucm.fdi.tp.project6.network.responseclasses.MoveStartResponse;

public class GameServer extends SwingController implements GameObserver {

	private int port;
	private int numPlayers;
	private int numConnections;
	private GameFactory gameFactory;
	private List<Connection> clients;
	private ControlGUI controlWindow;
	private boolean isTheFirstTime;
	volatile private ServerSocket server;
	volatile private boolean stopped;

	volatile boolean gameOver;

	public GameServer(GameFactory gameFactory, List<Piece> pieces, int port,
			AIAlgorithm aiPlayerAlg) {
		super(new Game(gameFactory.gameRules()), pieces, gameFactory
				.createRandomPlayer(), gameFactory.createAIPlayer(aiPlayerAlg));
		this.port = port;
		this.numPlayers = pieces.size();
		this.gameFactory = gameFactory;
		this.stopped = false;
		this.gameOver = false;
		this.clients = new ArrayList<Connection>();
		this.game.addObserver(this);// We add the game of the controller as an
		// observer to receive information (the server)
		this.controlWindow = new ControlGUI();
		this.isTheFirstTime = true;
	}

	public synchronized void makeMove(Player player) {
		try {
			super.makeMove(player);
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		try {
			super.stop();
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	public synchronized void restart() {
		try {
			super.restart();
		} catch (GameError e) {
			e.printStackTrace();
		}
	}

	public void start() {
		controlWindow.controlGUI(getStopServerButtonListener());
		startServer();
	}

	public StopServerButtonListener getStopServerButtonListener() {
		return new StopServerButtonListener() {
			public void StopServerButtonClicked() {
				try {
					stopped = true;
					for(int i=0; i<clients.size(); i++){
						clients.get(i).stop();
					}
					controlWindow.out();
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
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
				controlWindow.log("Connection with Client succed \n");
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
				if (isTheFirstTime) {
					game.start(pieces);
					controlWindow.log("The game starts now!!! \n");
					isTheFirstTime = false;
				} else {
					game.restart();
				}
			}
			startClientListener(c);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startClientListener(Connection c) {
		gameOver = false;
		Thread t = new Thread() {
			public void run() {
				while (!stopped && !gameOver) {
					try {
						Command cmd;
						cmd = (Command) c.getObject();
						cmd.execute(GameServer.this);
					} catch (ClassNotFoundException | IOException e) {
						if (!stopped && !gameOver) {
							game.stop();
							stopped = true;
							e.printStackTrace();
						}
					}
				}
			}
		};
		t.start();
	}

	void forwardNotification(Response r) {
		for (int i = 0; i < clients.size(); i++) {
			try {
				clients.get(i).sendObject(r);
			} catch (IOException e) {
				e.printStackTrace();
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
