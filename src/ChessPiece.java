
import java.awt.Graphics;
//import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class ChessPiece {
	public int x;
	public int y;
	
	public double animateX;
	public double animateY;
	public double targetX;
	public double targetY;
	public int framesMoved;
	public static final int targetFrames = 10;
	public boolean animating = false;
	
	public double drawX;
	public double drawY;
	public double drawZ;
	
	public boolean white;
	
	public int number;
	
	public boolean moved;
	public boolean movedTwo;
	
	public int value;
	
	public int ID;
	public static int totalPieces = 0;
	
	public int index;
	
	public ChessPiece(int x, int y, int number, int value, boolean white, int index) {
		this.x = x;
		this.y = y;
		this.number = number;
		this.white = white;
		moved = false;
		movedTwo = false;
		this.value = value;
		ID = totalPieces;
		totalPieces++;
		this.index = index;
	}
	
	/*public void draw(Graphics g) {
		g.drawImage(piece, (int) drawX, (int) drawY, (int) (piece.getWidth() / drawZ), (int) (piece.getHeight() / drawZ), null);
	}*/
	
	public abstract void draw(Graphics g);
	
	public abstract ArrayList<Integer> getMoves(int kx, int ky, int[][] board, ArrayList<ChessPiece> pieces);
	
	public abstract void setPic();
	
	public abstract boolean checking(int kx, int ky, int[][] board);
	
	public void filterMoves(int kx, int ky, int[][]board, ArrayList<ChessPiece> pieces, ArrayList<Integer> moves, boolean isKing) {
		int[][] copyBoard = new int[board.length][board[0].length];
		for (int row = 0; row < board.length; row++)
			for (int col = 0; col < board[0].length; col++)
				copyBoard[row][col] = board[row][col];
		
		int holdNum;
		//int holdX = x;
		//int holdY = y;
		
		f: for (int i = 0; i < moves.size(); i+= 2) {
			holdNum = copyBoard[moves.get(i+1)][moves.get(i)];
			
			copyBoard[moves.get(i+1)][moves.get(i)] = number;
			copyBoard[y][x] = 0;
			if (isKing) {
				kx = moves.get(i);
				ky = moves.get(i+1);
			}
			//x = moves.get(i);
			//y = moves.get(i+1);
			for (int j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).white != white && pieces.get(j).checking(kx,  ky, copyBoard) && !(pieces.get(j).x == moves.get(i) && pieces.get(j).y == moves.get(i+1))) {
					//System.out.println("check:" + pieces.get(j).number + " : " + pieces.get(j).x + " : " + pieces.get(j).y + " : " + kx + " : " + ky);
					copyBoard[moves.get(i+1)][moves.get(i)] = holdNum;//undo move
					//x = holdX;
					//y = holdY;
					copyBoard[y][x] = number;
					moves.remove(i);
					moves.remove(i);
					i -= 2;
					continue f;
				}
			}
			copyBoard[moves.get(i+1)][moves.get(i)] = holdNum;//undo move
			//x = holdX;
			//y = holdY;
			copyBoard[y][x] = number;
		}
	}
	
	public abstract ChessPiece copy(int index);
	
	public void startAnimateMove(int targetX, int targetY) {
		this.targetX = targetX;
		this.targetY = targetY;
		framesMoved = 0;
		animateX = x;
		animateY = y;
		animating = true;
	}
	
	public boolean animateMove() {//false if animating STOPS
		if (animating) {
			animateX += (x - animateX) / (targetFrames - framesMoved);
			animateY += (y - animateY) / (targetFrames - framesMoved);
			framesMoved++;
			if (framesMoved == targetFrames) {
				animating = false;
				return false;
			}
		}
		return true;
	}
	
	public void rotateToYAxis(double theta, double zShift) {
		
		double xx = animating? animateX: x;
		double yy = animating? animateY: y;
		
		drawX = (xx + 0.5)/ 8.0 - 0.5;
		drawZ = (yy + 0.5)/ 8.0 - 0.5;
		
		double copyX = drawX;
		
		drawX = drawX * Math.cos(-theta) - drawZ * Math.sin(-theta);
		drawZ = drawZ * Math.cos(-theta) + copyX * Math.sin(-theta);
		
		drawZ += zShift;
		
		drawX /= drawZ;
		drawX = drawX * 400 + 200;
		
		drawY = 200 / drawZ;
		
	}
}
