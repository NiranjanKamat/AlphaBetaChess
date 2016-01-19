package niruChess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class UserInterface extends JPanel implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = 1L;
	static int oldColumn, oldRow;
	static int newColumn, newRow;
	static int squareSize = 60;
	static boolean humanAsWhite = true;
	static boolean mouseClicked = false;

	static long lastClickTime = System.currentTimeMillis();

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		paintSquares(g);
		paintPieces(g);
		paintRowColNumbers(g);
	}

	void paintSquares(Graphics g) {
		Color white = new Color(255, 200, 100);
		Color black = new Color(150, 50, 30);
		Color currentColor = null;
		if (UserInterface.humanAsWhite) {
			currentColor = black;
		} else {
			currentColor = white;
		}
		for (int i = 0; i < 8; i++) {
			currentColor = swapColor(white, black, currentColor);
			for (int j = 0; j < 8; j++) {
				paintSquare(i, j, currentColor, g);
				currentColor = swapColor(white, black, currentColor);
			}
		}
		if (mouseClicked) {// highlight clicked square
			Color clicked = new Color(200, 125, 65);
			paintSquare(oldColumn, oldRow, clicked, g);
			System.out.println("oldColumn " + oldColumn + " oldRow " + oldRow);
			String possibleMoves = AlphaBetaChess.posibleMoves();
			System.out.println("possibleMoves " + possibleMoves);
			for (int moveIndex = 0; moveIndex < possibleMoves.length() / 5; moveIndex++) {
				int moveStart = moveIndex * 5;
				if ((Character.getNumericValue(possibleMoves.charAt(moveStart)) == oldRow)
						&& (Character.getNumericValue(possibleMoves
								.charAt(moveStart + 1)) == oldColumn)) {
					paintSquare(Character.getNumericValue(possibleMoves
							.charAt(moveStart + 3)),
							Character.getNumericValue(possibleMoves
									.charAt(moveStart + 2)), clicked, g);
				}
			}
		}
	}

	private void paintSquare(int column, int row, Color color, Graphics g) {
		g.setColor(color);
		g.fillRect(column * squareSize, row * squareSize, squareSize,
				squareSize);
	}

	private Color swapColor(Color white, Color black, Color currentColor) {
		if (currentColor == white) {
			return black;
		} else {
			return white;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	void humanAndComputerMove() {
		String dragMove = dragMove();
		String userPosibilities = AlphaBetaChess.posibleMoves();
		if (checkEnd(userPosibilities)) {
			System.out.println("returning");
			return;
		}
		if (userPosibilities.contains(dragMove)) {// if valid move
			// AlphaBetaChess.humanMoves.add(dragMove);
			AlphaBetaChess.makeMove(dragMove);
			postMove(dragMove);
			// AlphaBetaChess.opponentMoves = AlphaBetaChess.humanMoves;

			String computerPossibilities = AlphaBetaChess.posibleMoves();
			checkEnd(computerPossibilities);

			String computerMove = AlphaBetaChess.alphaBeta(
					AlphaBetaChess.globalDepth, 1000000, -1000000, "", 0)
					.substring(0, 5);
			System.out.println("computerMove " + computerMove);
			// AlphaBetaChess.computerMoves.add(computerMove);
			AlphaBetaChess.makeMove(computerMove);
			postMove(computerMove);
			// AlphaBetaChess.opponentMoves = AlphaBetaChess.computerMoves;
			repaint();
		} else {
			System.out.println(dragMove + " is not allowed");
			System.out.println("userPosibilities " + userPosibilities);
		}
	}

	String dragMove() {
		String dragMove;
		if ((oldColumn == 4)
				&& (newColumn == 6)
				&& (oldRow == 7)
				&& (newRow == 7)
				&& ("A".equals(AlphaBetaChess.chessBoard.get(oldRow, oldColumn)))) {
			dragMove = "7476Z";
		} else if ((oldColumn == 4)
				&& (newColumn == 2)
				&& (oldRow == 7)
				&& (newRow == 7)
				&& ("A".equals(AlphaBetaChess.chessBoard.get(oldRow, oldColumn)))) {
			dragMove = "7472Y";
		} else if (newRow == 0 && oldRow == 1
				&& "P".equals(AlphaBetaChess.chessBoard.get(oldRow, oldColumn))) {
			// pawn promotion
			dragMove = "" + oldColumn + newColumn
					+ AlphaBetaChess.chessBoard.get(newRow, newColumn)
					+ pawnPromotion() + "P";
		} else {
			// regular move
			dragMove = "" + oldRow + oldColumn + newRow + newColumn
					+ AlphaBetaChess.chessBoard.get(newRow, newColumn);
		}
		return dragMove;
	}

	private boolean checkEnd(String userPossibilities) {
		if (userPossibilities.length() == 0) {
			if (AlphaBetaChess.kingSafe()) {
				System.out.println("Stalemate");
			} else {
				System.out.println("Checkmate");
			}
			return true;
		} else {
			return false;
		}
	}

	void postMove(String move) {
		checkKingCMoved(move);
		checkRook56Moved(move);
		checkRook63Moved(move);
		AlphaBetaChess.flipBoard();
	}

	private void checkRook63Moved(String move) {
		if (move.charAt(4) == 'Z') {
			AlphaBetaChess.rook63Moved = true;
		} else if (move.charAt(0) == '7' && move.charAt(1) == '7') {
			AlphaBetaChess.rook63Moved = true;
		}
	}

	private void checkRook56Moved(String move) {
		if (move.charAt(4) == 'Y') {
			AlphaBetaChess.rook56Moved = true;
		} else if (move.charAt(0) == '7' && move.charAt(1) == '0') {
			AlphaBetaChess.rook56Moved = true;
		}
	}

	void checkKingCMoved(String move) {
		if ((move.charAt(4) == 'A') || (move.charAt(4) == 'Y')
				|| (move.charAt(4) == 'Z')) {
			AlphaBetaChess.kingCMoved = true;
		}
	}

	boolean withinBoard(MouseEvent e) {
		return e.getX() < 8 * squareSize && e.getY() < 8 * squareSize;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isDoubleClick()) {
			return;
		}
		if (withinBoard(e)) {
			if (!mouseClicked) {
				mouseClicked = true;
				oldColumn = e.getX() / squareSize;
				oldRow = e.getY() / squareSize;
				repaint();
			} else {
				mouseClicked = false;
				newColumn = e.getX() / squareSize;
				newRow = e.getY() / squareSize;
				if ((oldColumn == newColumn) && (oldRow == newRow)) {
					return;
				} else {
					humanAndComputerMove();
				}
			}
		}
	}

	// After the first click, subsequent clicks result in mouseClicked
	// getting called twice
	boolean isDoubleClick() {
		if (System.currentTimeMillis() - lastClickTime < 100) {
			return true;
		} else {
			lastClickTime = System.currentTimeMillis();
			return false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	void paintPieces(Graphics g) {
		Image chessPiecesImage = new ImageIcon("ChessPieces.png").getImage();
		for (int i = 0; i < 64; i++) {
			int j = -1, k = -1;
			switch (AlphaBetaChess.chessBoard.get(i / 8, i % 8)) {
				case "P":
					j = 5;
					k = 0;
					break;
				case "p":
					j = 5;
					k = 1;
					break;
				case "R":
					j = 2;
					k = 0;
					break;
				case "r":
					j = 2;
					k = 1;
					break;
				case "K":
					j = 4;
					k = 0;
					break;
				case "k":
					j = 4;
					k = 1;
					break;
				case "B":
					j = 3;
					k = 0;
					break;
				case "b":
					j = 3;
					k = 1;
					break;
				case "Q":
					j = 1;
					k = 0;
					break;
				case "q":
					j = 1;
					k = 1;
					break;
				case "A":
					j = 0;
					k = 0;
					break;
				case "a":
					j = 0;
					k = 1;
					break;
			}
			if (!humanAsWhite) {
				k = 1 - k;// switch black white graphics
			}
			if (j != -1 && k != -1) {
				g.drawImage(chessPiecesImage, (i % 8) * squareSize, (i / 8)
						* squareSize, (i % 8 + 1) * squareSize, (i / 8 + 1)
						* squareSize, j * 64, k * 64, (j + 1) * 64,
						(k + 1) * 64, this);
			}
		}
	}

	private void paintRowColNumbers(Graphics g) {
		Image numbers = new ImageIcon("numbers.jpeg").getImage();
		for (int i = 0; i < 8; i++) {
			List<Integer> jk = imageIndex(i);
			int j = jk.get(0);
			int k = jk.get(1);
			g.drawImage(numbers, 480 + 0 * squareSize, i * squareSize,
					480 + 1 * squareSize, (i + 1) * squareSize, j * 62, k * 85,
					(j + 1) * 62, (k + 1) * 85, this);
		}
		for (int i = 0; i < 8; i++) {
			List<Integer> jk = imageIndex(i);
			int j = jk.get(0);
			int k = jk.get(1);
			g.drawImage(numbers, i * squareSize, 480 + 0 * squareSize, (i + 1)
					* squareSize, 480 + (1) * squareSize, j * 62, k * 85,
					(j + 1) * 62, (k + 1) * 85, this);
		}
	}

	private List<Integer> imageIndex(int i) {
		int j = -1, k = -1;
		switch (i) {
			case 0:
				j = 0;
				k = 0;
				break;
			case 1:
				j = 1;
				k = 0;
				break;
			case 2:
				j = 2;
				k = 0;
				break;
			case 3:
				j = 3;
				k = 0;
				break;
			case 4:
				j = 4;
				k = 0;
				break;
			case 5:
				j = 0;
				k = 1;
				break;
			case 6:
				j = 1;
				k = 1;
				break;
			case 7:
				j = 2;
				k = 1;
				break;
		}
		List<Integer> jk = new ArrayList<Integer>();
		jk.add(j);
		jk.add(k);
		return jk;
	}

	static String pawnPromotion() {
		while (true) {
			Object[] optionNew = { "Knight", "Bishop", "Rook", "Queen" };
			int out = JOptionPane
					.showOptionDialog(null, "Promote To", "Pawn Promotion",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, optionNew,
							optionNew[0]);
			switch (out) {
				case 0:
					return "K";
				case 1:
					return "B";
				case 2:
					return "R";
				case 3:
					return "Q";
			}
		}
	}
}