package es.ucm.fdi.tp.project5.factories;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectn.ConnectNFactory;
import es.ucm.fdi.tp.project5.moveControllers.ConnectNMoveController;
import es.ucm.fdi.tp.project5.view.GenericSwingView;

/**
 * Class made for making possible to the connectN factory creating a swing view
 * of the game.
 */
@SuppressWarnings("serial")
public class ConnectNFactoryExt extends ConnectNFactory{

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
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					new GenericSwingView(g, c, viewPiece, moveController,
							random, ai);
				}
			});

		} catch (InterruptedException | InvocationTargetException e) {
			throw new GameError("Error while creating the SwingView");
		}
	}
}
