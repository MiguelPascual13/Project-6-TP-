package es.ucm.fdi.tp.project6.factories;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.ttt.TicTacToeFactory;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.moveControllers.TicTacToeMoveController;
import es.ucm.fdi.tp.project6.view.GenericSwingView;

@SuppressWarnings("serial")
public class TicTacToeFactoryExt extends TicTacToeFactory {

	private TicTacToeMoveController moveController;

	@Override
	public void createSwingView(final Observable<GameObserver> g,
			final Controller c, final Piece viewPiece, Player random,
			Player ai) {
		moveController = new TicTacToeMoveController();
		new GenericSwingView(g, (SwingController) (c), viewPiece,
				moveController, random, ai);
	}
}
