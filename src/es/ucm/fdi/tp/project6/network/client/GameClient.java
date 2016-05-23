package es.ucm.fdi.tp.project6.network.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.control.commands.PlayCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.QuitCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.RestartCommand;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.network.Connection;
import es.ucm.fdi.tp.project6.network.responseclasses.Response;

public class GameClient extends Controller implements Observable<GameObserver> {

	private String host;
	private int port;

	private List<GameObserver> observers;

	private Piece localPiece;
	private GameFactory gameFactory;

	private Connection connectionToServer;

	private boolean gameOver;

	public GameClient(String host, int port) throws Exception {

		/* Realmente gameClient será un controlador fantasma */
		super(null, null);

		/* Configuración del servidor al que se va a conectar */
		this.host = host;
		this.port = port;

		/*
		 * Inicializa la lista de observadores, usualmente solo será uno, la
		 * vista del cliente concreto.
		 */
		this.observers = new ArrayList<GameObserver>();

		/* Trata de conectarse al servidor */
		connect();
	}

	private void connect() throws Exception {
		// creamos el "canuto" para comunicarnos con el servidor
		connectionToServer = new Connection(new Socket(host, port));

		/* Mandamos la señal de conexion */
		connectionToServer.sendObject("Connect");

		/* El servidor nos responde, vamos a ver cómo */
		Object response = connectionToServer.getObject();

		/* Si nos responde con una excepción la relanzaremos */
		if (response instanceof Exception) {
			throw (Exception) response;
		}
		try {
			/*
			 * En caso de no haber sido una excepción se trata de el string de
			 * confirmación seguido de los objetos de factoría y pieza local.
			 */
			gameFactory = (GameFactory) connectionToServer.getObject();
			localPiece = (Piece) connectionToServer.getObject();
		} catch (Exception e) {
			throw new GameError("Unknown server response: " + e.getMessage());
		}
	}

	public GameFactory getGameFactory() {
		return gameFactory;
	}

	public Piece getPlayerPiece() {
		return localPiece;
	}

	public void addObserver(GameObserver o) {
		this.observers.add(o);
	}

	public void removeObserver(GameObserver o) {
		this.observers.remove(o);
	}

	/*---SOBREESCRITURA DE MÉTODOS DE CONTROLLER---*/
	@Override
	public void makeMove(Player player) {
		forwardCommand(new PlayCommand(player));
	}

	@Override
	public void stop() {
		forwardCommand(new QuitCommand());
	}

	@Override
	public void restart() {
		forwardCommand(new RestartCommand());
	}

	/**
	 * Si el servidor está parado no hace nada, en caso contrario manda un
	 * comando al servidor.
	 * 
	 * @param cmd
	 */
	private void forwardCommand(Command command) {
		if (!gameOver) {
			try {
				connectionToServer.sendObject(command);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {

		this.observers.add(getAnonimusGameObserver());
		gameOver = false;
		while (!gameOver) {
			try {
				Object obj = connectionToServer.getObject();
				if (obj instanceof String) {
					this.stop();
					this.connectionToServer.stop();
				} else {
					Response res = (Response) obj;
					for (GameObserver o : observers) {
						res.run(o);
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				this.gameOver = true;
			}
		}
	}

	private GameObserver getAnonimusGameObserver() {
		return new GameObserver() {
			public void onGameStart(Board board, String gameDesc,
					List<Piece> pieces, Piece turn) {
			}

			public void onGameOver(Board board, State state, Piece winner) {
				GameClient.this.gameOver = true;
				try {
					GameClient.this.connectionToServer.stop();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void onMoveStart(Board board, Piece turn) {
			}

			public void onMoveEnd(Board board, Piece turn, boolean success) {
			}

			public void onChangeTurn(Board board, Piece turn) {
			}

			public void onError(String msg) {
			}
		};
	}
}
