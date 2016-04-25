package es.ucm.fdi.tp.project6.factories;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectn.ConnectNFactory;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.moveControllers.ConnectNMoveController;
import es.ucm.fdi.tp.project6.view.GenericSwingView;

/**
 * Class made for making possible to the connectN factory creating a swing view
 * of the game.
 */
@SuppressWarnings("serial")
public class ConnectNFactoryExt extends ConnectNFactory {

	private ConnectNMoveController moveController;

	public ConnectNFactoryExt() {
		super();
	}

	public ConnectNFactoryExt(Integer dimRows) {
		super(dimRows);
	}

	@Override
	public void createSwingView(final Observable<GameObserver> g,
			final Controller c, final Piece viewPiece, Player random,
			Player ai) {
		moveController = new ConnectNMoveController();
		new GenericSwingView(g, (SwingController) (c), viewPiece,
				moveController, random, ai);
	}

}
