package niruChess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class UserInterface extends JPanel implements MouseListener,
		MouseMotionListener {
	static int oldColumn, oldRow;
	static int newColumn, newRow;
	static int squareSize = 60;
	static boolean humanAsWhite = true;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		paintSquares(g);
		Image chessPiecesImage = new ImageIcon("ChessPieces.png").getImage();
		paintPieces(g, chessPiecesImage);
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
				g.setColor(currentColor);
				g.fillRect(i * squareSize, j * squareSize, squareSize, squareSize);
				currentColor = swapColor(white, black, currentColor);
			}
		}
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
		if (withinBoard(e)) {
			oldColumn = e.getX() / squareSize;
			oldRow = e.getY() / squareSize;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (withinBoard(e)) {
			// if inside the board
			newColumn = e.getX() / squareSize;
			newRow = e.getY() / squareSize;
			System.out
					.println(oldColumn + "" + newColumn + "" + oldRow + "" + newRow);
			if (e.getButton() == MouseEvent.BUTTON1) {
				String dragMove;
				if ((oldColumn == 4)
						&& (newColumn == 6)
						&& (oldRow == 7)
						&& (newRow == 7)
						&& ("A".equals(AlphaBetaChess.chessBoard
								.get(oldRow, oldColumn)))) {
					dragMove = "7476Z";
				} else if ((oldColumn == 4)
						&& (newColumn == 2)
						&& (oldRow == 7)
						&& (newRow == 7)
						&& ("A".equals(AlphaBetaChess.chessBoard
								.get(oldRow, oldColumn)))) {
					dragMove = "7472Y";
				} else if (newRow == 0
						&& oldRow == 1
						&& "P"
								.equals(AlphaBetaChess.chessBoard.get(oldRow, oldColumn))) {
					// pawn promotion
					dragMove = "" + oldColumn + newColumn
							+ AlphaBetaChess.chessBoard.get(newRow, newColumn) + "QP";
				} else {
					// regular move
					dragMove = "" + oldRow + oldColumn + newRow + newColumn
							+ AlphaBetaChess.chessBoard.get(newRow, newColumn);
				}
				String userPosibilities = AlphaBetaChess.posibleMoves();
				// System.out.println("dragMove " + dragMove);
				if (userPosibilities.contains(dragMove)) {
					// if valid move
					AlphaBetaChess.makeMove(dragMove);
					postMove(dragMove);

					String computerMove = AlphaBetaChess.alphaBeta(
							AlphaBetaChess.globalDepth, 1000000, -1000000, "", 0).substring(
							0, 5);
					System.out.println("computerMove " + computerMove);
					AlphaBetaChess.makeMove(computerMove);
					postMove(computerMove);
					repaint();
				} else {
					System.out.println(dragMove + " is not allowed");
					System.out.println("userPosibilities " + userPosibilities);
				}
			}
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

	void paintPieces(Graphics g, Image chessPiecesImage) {
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
						* squareSize, (i % 8 + 1) * squareSize, (i / 8 + 1) * squareSize,
						j * 64, k * 64, (j + 1) * 64, (k + 1) * 64, this);
			}
		}
	}

}