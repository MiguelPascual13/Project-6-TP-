package es.ucm.fdi.tp.project6.network.responseclasses;

import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;

/**
 * Es una interfaz muy tonta que simplmente ejecuta el m�todo de GameObserver
 * correspondiente. A cada cliente se le pasa una response cuando el modelo
 * provoca un evento el el servidor. Por su parte, el cliente la ejecutar�. Es
 * una especie de propagaci�n del patr�n oobservador.
 */
public interface Response extends java.io.Serializable {
	public void run(GameObserver observer);
}
