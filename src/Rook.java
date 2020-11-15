import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Rook extends ChessPiece{
	
	private static int baseY;
	
	public static BufferedImage whitePiece;
	public static BufferedImage blackPiece;
	public static boolean setPicYet = false;
	
	public Rook(int x, int y, boolean white, boolean temporary) {
		super(x, y, 2 + (white? 0: 6), 5, white, -1);
		if (temporary)
			totalPieces--;
	}
	
	public Rook(int x, int y, boolean white, int index) {
		super(x, y, 2 + (white? 0: 6), 5, white, index);
	}
	
	public void setPic() {
		if (!setPicYet) {
			File f = new File("images/white_rook.png");
			try {
				whitePiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			f = new File("images/black_rook.png");
			try {
				blackPiece = ImageIO.read(f);
			} catch (IOException exc) {
			}
			baseY = whitePiece.getHeight() - 7;//assumes both colors same dimensions
			setPicYet = true;
		}
	}
	
	public void draw(Graphics g) {
		int width = (int) ((white? whitePiece: blackPiece).getWidth() / drawZ);
		int height = (int) ((white? whitePiece: blackPiece).getHeight() / drawZ);
		g.drawImage((white? whitePiece: blackPiece), (int) drawX - width / 2, (int) drawY - (int) (baseY * ((double) height / whitePiece.getHeight())), width, height, null);
	}
	
	public ChessPiece copy(int index) {
		ChessPiece hold = new Rook(x, y, white, index);
		hold.ID = ID;
		totalPieces--;
		hold.moved = moved;
		hold.movedTwo = movedTwo;
		return hold;
	}

	public ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces) {
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for (int i = 1; i <= 7 - x; i++) {
			if (board[y][x + i] == 0) {
				moves.add(x + i);
				moves.add(y);
			} else {
				if (white? board[y][x+i] >= 7: (board[y][x+i] > 0 && board[y][x+i] < 7)) {
					moves.add(x + i);
					moves.add(y);
				}
				break;
			}
		}
		for (int i = 1; i <= x; i++) {
			if (board[y][x - i] == 0) {
				moves.add(x - i);
				moves.add(y);
			} else {
				if (white? board[y][x-i] >= 7: (board[y][x-i] > 0 && board[y][x-i] < 7)) {
					moves.add(x - i);
					moves.add(y);
				}
				
				break;
			}
		}
		for (int i = 1; i <= y; i++) {
			if (board[y - i][x] == 0) {
				moves.add(x);
				moves.add(y - i);
			} else {
				if (white? board[y-i][x] >= 7: (board[y-i][x] > 0 && board[y-i][x] < 7)) {
					moves.add(x);
					moves.add(y - i);
				}
				
				break;
			}
		}
		for (int i = 1; i <= 7 - y; i++) {
			if (board[y + i][x] == 0) {
				moves.add(x);
				moves.add(y + i);
			} else {
				if (white? board[y+i][x] >= 7: (board[y+i][x] > 0 && board[y+i][x] < 7)) {
					moves.add(x);
					moves.add(y + i);
				}
				
				break;
			}
		}
		
		
		filterMoves(kx, ky, board, pieces, moves, false);
		return moves;
	}
	
	public boolean checking(int kx, int ky, int[][] board) {
		if (x != kx && y != ky) {
			return false;
		}
		
		int shiftX = kx- x > 0? 1: (kx - x< 0? -1: 0);
		int shiftY = ky- y > 0? 1: (ky - y< 0? -1: 0);
		
		for (int i = 1; i <= (shiftX != 0? 3.5 + 3.5 * shiftX - shiftX * x: 3.5 + 3.5 * shiftY - shiftY * y); i++) {
			if (kx == x + i*shiftX && ky == y + i * shiftY) {
				return true;
			} else if (board[y + i*shiftY][x + i*shiftX] != 0) {
				return false;
			}
		}
		//System.out.println(2);
		return false;
	}
}