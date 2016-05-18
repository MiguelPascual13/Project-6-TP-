package es.ucm.fdi.tp.project6.factories;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.project6.controller.SwingController;
import es.ucm.fdi.tp.project6.moveControllers.AdvancedTTTMoveController;
import es.ucm.fdi.tp.project6.view.GenericSwingView;

@SuppressWarnings("serial")
public class AdvancedTTTFactoryExt extends AdvancedTTTFactory {

	private AdvancedTTTMoveController moveController;
	private Player randomPlayer;
	private Player aiPlayer;

	@Override
	public void createSwingView(final Observable<GameObserver> g,
			final Controller c, final Piece viewPiece, Player random,
			Player ai) {
		this.randomPlayer = random;
		this.aiPlayer = ai;
		moveController = new AdvancedTTTMoveController();
		try{
			SwingUtilities.invokeAndWait(new Runnable(){

				@Override
				public void run() {
					new GenericSwingView(g, (SwingController) (c), viewPiece,
							moveController, random, ai);
				}
			});
		
		}
		catch(InterruptedException | InvocationTargetException e){
			throw new GameError("Error while creating the SwingView");
		}
		}
	public Player getRandomPlayer(){
		return this.randomPlayer;
	}
	public Player getAiPlayer(){
		return this.aiPlayer;
	}
}
