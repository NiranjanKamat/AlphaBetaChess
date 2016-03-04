package niruChess;

import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class StopWatch extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel painel3;
	private JPanel painel5;
	private Container container;

	public StopWatch() {

		// frame
		setSize(1000, 900);
		// setLocation(200, 200);
		// setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		container = getContentPane();
		container.setLayout(null);

		painel5 = new JPanel();
		painel5.setBackground(Color.red);
		painel5.setBounds(120, 110, 100, 120);
		painel3 = new JPanel();
		painel3.setBackground(Color.white);
		painel3.add(painel5);
		painel3.setBounds(50, 50, 290, 220);

		container.add(painel3);

	}

	public static void main(String[] args) {
		new StopWatch();
	}
}