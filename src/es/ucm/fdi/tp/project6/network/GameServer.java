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

public class GameServer extends Controller implements GameObserver {

	private int port;
	private int numPlayers;
	private int numOfConnectedPlayers;
	private GameFactory gameFactory;
	private List<Connection> clients;
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
		this.game.addObserver(this); // We add the game of the controller as an
		// observer to receive information (the server)
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
		controlGUI() //No se si nosotros ya tenemos esta clase/metodo con otro nombre
		// pero me suena que si, si puedes haz tu esto o dime que tengo que hacer porque no me queda muy claras las transparencias. 
		startServer();
	}

	private void startServer() {
		server = new ServerSocket(port);
		stopped = false;

		while (!stopped) {
			try {
				Socket s = new Socket();
				s = server.accept();
				// Aqui hay que poner un mensaje pero no se cual.
				handleRequest(s);
			} catch (IOException e) {
				if (!stopped) {
					log("error while waiting for a connection: "
							+ e.getMessage());
					// Alvaro No se hacer el logger.
				}
			}

		}
	}

	private void handleRequest(Socket s) {
		try{
			Connection c = new Connection(s);
			Object clientRequest = c.getObject();
			if(!(clientRequest instanceof String) && !((String) clientRequest).equalsIgnoreCase("Connect")){
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}
			if(numPlayers == numOfConnectedPlayers){
				//Aqui hay que responder con un GameError the enough Clients o algo asi.
			}
			else{
				numOfConnectedPlayers++;
				clients.add(c);
				c.sendObject(gameFactory);
				c.sendObject(pieces.get(numOfConnectedPlayers-1));
			}
		
			if(numPlayers == numOfConnectedPlayers){
				if(){//Si es la primera vez start sino restart.
					//Podemos hacerlo con un boolean o un contador.
					start();
				}
				else {
					restart();
				}
			}
			
			startClientListener(c);
		}
		catch (IOException | ClassNotFoundException e){
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

	public interface Response extends java.io.Serializable {
		public void run(GameObserver observer);
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

	public class GameStartResponse implements Response {
		private static final long serialVersionUID = 1L;
		private Board board;
		private String gameDesc;
		private List<Piece> pieces;
		private Piece turn;

		public GameStartResponse(Board board, String gameDesc,
				List<Piece> pieces, Piece turn) {
			this.board = board;
			this.gameDesc = gameDesc;
			this.pieces = pieces;
			this.turn = turn;
		}

		public void run(GameObserver observer) {
			observer.onGameStart(board, gameDesc, pieces, turn);
		}
	}

	public void onGameOver(Board board, State state, Piece winner) {
		forwardNotification(new GameOverResponse(board, state, winner));
		game.stop();
	}

	public class GameOverResponse implements Response {

		private static final long serialVersionUID = 1L;
		private Board board;
		private State state;
		private Piece winner;

		public GameOverResponse(Board board, State state, Piece winner) {
			this.board = board;
			this.state = state;
			this.winner = winner;
		}

		public void run(GameObserver observer) {
			observer.onGameOver(board, state, winner);
		}

	}

	public void onMoveStart(Board board, Piece turn) {
		forwardNotification(new MoveStartResponse(board, turn));
	}

	public class MoveStartResponse implements Response {
		private static final long serialVersionUID = 1L;
		private Board board;
		private Piece turn;

		public MoveStartResponse(Board board, Piece turn) {
			this.board = board;
			this.turn = turn;
		}

		public void run(GameObserver observer) {
			observer.onMoveStart(board, turn);
		}

	}

	public void onMoveEnd(Board board, Piece turn, boolean success) {
		forwardNotification(new MoveEndResponse(board, turn, success));
	}

	public class MoveEndResponse implements Response {
		private static final long serialVersionUID = 1L;
		private Board board;
		private Piece turn;
		private boolean success;

		public MoveEndResponse(Board board, Piece turn, boolean success) {
			this.board = board;
			this.turn = turn;
			this.success = success;
		}

		public void run(GameObserver observer) {
			observer.onMoveEnd(board, turn, success);
		}

	}

	public void onChangeTurn(Board board, Piece turn) {
		forwardNotification(new ChangeTurnResponse(board, turn));
	}

	public class ChangeTurnResponse implements Response {

		private static final long serialVersionUID = 1L;
		private Board board;
		private Piece turn;

		public ChangeTurnResponse(Board board, Piece turn) {
			this.board = board;
			this.turn = turn;
		}

		public void run(GameObserver observer) {
			observer.onChangeTurn(board, turn);
		}
	}

	public void onError(String msg) {
		forwardNotification(new ErrorResponse(msg));
	}

	public class ErrorResponse implements Response {

		private static final long serialVersionUID = 1L;
		private String msg;

		public ErrorResponse(String msg) {
			this.msg = msg;
		}

		public void run(GameObserver observer) {
			observer.onError(msg);
		}
	}

}
