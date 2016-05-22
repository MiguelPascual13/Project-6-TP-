package es.ucm.fdi.tp.project6.network.responseclasses;

import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;

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
