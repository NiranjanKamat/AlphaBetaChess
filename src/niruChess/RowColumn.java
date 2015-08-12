package niruChess;

import java.awt.event.MouseEvent;

public class RowColumn {
	private int row;
	private int column;

	RowColumn(MouseEvent e) {
		this(e.getY() / UserInterface.squareSize, e.getX()
				/ UserInterface.squareSize);
	}

	RowColumn(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
