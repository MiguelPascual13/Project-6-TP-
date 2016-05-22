package es.ucm.fdi.tp.project6.network.responseclasses;

import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;

/**
 * Es una interfaz muy tonta que simplmente ejecuta el método de GameObserver
 * correspondiente. A cada cliente se le pasa una response cuando el modelo
 * provoca un evento el el servidor. Por su parte, el cliente la ejecutará. Es
 * una especie de propagación del patrón oobservador.
 */
public interface Response extends java.io.Serializable {
	public void run(GameObserver observer);
}
