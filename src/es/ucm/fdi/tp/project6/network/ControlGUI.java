package es.ucm.fdi.tp.project6.network;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.bgame.model.GameError;

public class ControlGUI {
	private JTextArea textArea;

	protected void controlGUI(StopServerButtonListener stopServerButtonListener) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					constructGUI(stopServerButtonListener);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError(
					"Something went wrong when constructing the GUI");
		}
	}
	
	public interface StopServerButtonListener {
		void StopServerButtonClicked();
	}

	private void constructGUI(StopServerButtonListener stopServerButtonListener) {
		JFrame window = new JFrame("Game Server");
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(this.textArea);
		JButton stopServerButton = new JButton("Stop Server");
		stopServerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopServerButtonListener.StopServerButtonClicked();
			}
		});
		window.add(scrollPane);
		window.add(stopServerButton);
		
		window.setPreferredSize(new Dimension(600,600));
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}
	
	public void log(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				textArea.append(msg);
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}
		});
	}
}
