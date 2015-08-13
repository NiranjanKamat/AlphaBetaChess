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
	static int mouseX, mouseY, newMouseX, newMouseY;
	static int squareSize = 60;
	static boolean humanAsWhite = true;

	// static boolean isPieceBeingDragged = false;
	// static String draggedPiece = "";

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
		Color currentColor = black;
		for (int i = 0; i < 8; i++) {
			currentColor = swapColor(white, black, currentColor);
			for (int j = 0; j < 8; j++) {
				g.setColor(currentColor);
				g.fillRect(i * squareSize, j * squareSize, squareSize,
						squareSize);
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

	void paintPieces(Graphics g, Image chessPiecesImage) {
		for (int i = 0; i < 64; i++) {
			int j = -1, k = -1;
			switch (AlphaBetaChess.chessBoard[i / 8][i % 8]) {
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

	@Override
	public void mousePressed(MouseEvent e) {
		if (withinBoard(e)) {
			// if inside the board
			mouseX = e.getX();
			mouseY = e.getY();
		}
		// if (withinBoard(e)) {
		// RowColumn rowColumn = new RowColumn(e);
		// int row = rowColumn.getRow();
		// int column = rowColumn.getColumn();
		// if (!AlphaBetaChess.chessBoard[row][column].equals("")) {
		// isPieceBeingDragged = true;
		// draggedPiece = AlphaBetaChess.chessBoard[row][column];
		// AlphaBetaChess.chessBoard[row][column] = "";
		// repaint();
		// }
		// }

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (withinBoard(e)) {
			// if inside the board
			newMouseX = e.getX();
			newMouseY = e.getY();
			if (e.getButton() == MouseEvent.BUTTON1) {
				String dragMove;
				if (newMouseY / squareSize == 0
						&& mouseY / squareSize == 1
						&& "P".equals(AlphaBetaChess.chessBoard[mouseY
								/ squareSize][mouseX / squareSize])) {
					// pawn promotion
					dragMove = ""
							+ mouseX
							/ squareSize
							+ newMouseX
							/ squareSize
							+ AlphaBetaChess.chessBoard[newMouseY / squareSize][newMouseX
									/ squareSize] + "QP";
				} else {
					// regular move
					dragMove = ""
							+ mouseY
							/ squareSize
							+ mouseX
							/ squareSize
							+ newMouseY
							/ squareSize
							+ newMouseX
							/ squareSize
							+ AlphaBetaChess.chessBoard[newMouseY / squareSize][newMouseX
									/ squareSize];
				}
				String userPosibilities = AlphaBetaChess.posibleMoves();
				if (userPosibilities.contains(dragMove)) {
					// if valid move
					AlphaBetaChess.makeMove(dragMove);
					AlphaBetaChess.flipBoard();
					AlphaBetaChess.makeMove(AlphaBetaChess.alphaBeta(
							AlphaBetaChess.globalDepth, 1000000, -1000000, "",
							0));
					AlphaBetaChess.flipBoard();
					repaint();
				}
			}
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
}