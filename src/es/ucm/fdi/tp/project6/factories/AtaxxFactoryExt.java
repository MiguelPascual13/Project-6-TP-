package es.ucm.fdi.tp.project6.factories;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.ataxx.AtaxxFactory;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.moveControllers.AtaxxMoveController;
import es.ucm.fdi.tp.project6.view.GenericSwingView;

/**
 * This class was made following the advice of the project statement.
 */

@SuppressWarnings("serial")
public class AtaxxFactoryExt extends AtaxxFactory {

	private AtaxxMoveController moveController;

	public AtaxxFactoryExt(Integer dimRows, Integer obstacles) {
		super(dimRows, obstacles);
	}

	public AtaxxFactoryExt() {
		super();
	}

	/**
	 * Tenemos un game observer lo cual siempre es bueno.
	 * 
	 * Dónde cojones se meten las piezas en el array de los cojones?
	 */
	@Override
	public void createSwingView(final Observable<GameObserver> g,
			final Controller c, final Piece viewPiece, Player random,
			Player ai) {
		moveController = new AtaxxMoveController();
		new GenericSwingView(g, (SwingController) (c), viewPiece,
				moveController, random, ai);
	}
}
