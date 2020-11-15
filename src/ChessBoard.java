import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

public class ChessBoard {
	
	private boolean humanPlayer = true;//false if CPU vs. CPU
	
	int screenWidth, screenHeight;
	
	private static final int[][] setUpBoard = {
			{2, 3, 4, 5, 6, 4, 3, 2},//white
			{1, 1, 1, 1, 1, 1, 1, 1},
			{0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0},
			{7, 7, 7, 7, 7, 7, 7, 7},//black
			{8, 9, 10, 11, 12, 10, 9, 8}
			
	};
	
	private int[][] playingBoard;
	
	private ArrayList<Integer> moves;
	private int selected = -1;
	private boolean whiteTurn;
	
	private int whiteKingX = 3;
	private int whiteKingY = 0;
	private int blackKingX = 3;
	private int blackKingY = 7;
	
	private BufferedImage board;
	private BufferedImage canvas;
	
	private double theta = 0;
	
	private int selectedX = 0;
	private int selectedY = 0;
	
	private double zShift = 1.2;
	
	private boolean pressingRight = false;
	private boolean pressingLeft = false;
	private boolean pressingUp = false;
	private boolean pressingDown = false;
	
	private ArrayList<ChessPiece> pieces;
	
	private int totalMoves = 0;
	
	private boolean animating = false;
	private boolean gameOver = false;
	
	//2D array lists! for going back multiple moves
	private ArrayList<ArrayList<ChessPiece>> previousState;//copy of previous state of piece with ID num
	private ArrayList<ArrayList<CoordVal>> previousBoardPos;//stores x, y, val
	
	private File f;
	
	public ChessBoard(int w, int h) {
		screenWidth = w;
		screenHeight = h;
	}
	
	public void setUp() {
		f = new File("images/old_chess_board.jpg");
		try {
			board = ImageIO.read(f);
		} catch (IOException exc) {
		}
		
		f = new File("images/canvas.jpg");
		try {
			canvas = ImageIO.read(f);
		} catch (IOException exc) {
		}
		
		setPieces();
		
		moves = new ArrayList<Integer>();
		
		whiteTurn = true;
	}
	
	public void setPieces() {
		playingBoard = new int[setUpBoard.length][setUpBoard[0].length];
		
		pieces = new ArrayList<ChessPiece>();
		
		previousState = new ArrayList<ArrayList<ChessPiece>>();
		previousBoardPos = new ArrayList<ArrayList<CoordVal>>();
		
		for (int row = 0; row < setUpBoard.length; row++) {
			for (int col = 0; col < setUpBoard[row].length; col++) {
				switch (setUpBoard[row][col]) {
					case 1: pieces.add(new Pawn(col, row, true));break;
					case 2: pieces.add(new Rook(col, row, true, false));break;
					case 3: pieces.add(new Knight(col, row, true));break;
					case 4: pieces.add(new Bishop(col, row, true, false));break;
					case 5: pieces.add(new Queen(col, row, true));break;
					case 6: pieces.add(new King(col, row, true));
						whiteKingX=col;
						whiteKingY=row;
						break;
					case 7: pieces.add(new Pawn(col, row, false));break;
					case 8: pieces.add(new Rook(col, row, false, false));break;
					case 9: pieces.add(new Knight(col, row, false));break;
					case 10: pieces.add(new Bishop(col, row, false, false));break;
					case 11: pieces.add(new Queen(col, row, false));break;
					case 12: pieces.add(new King(col, row, false));
						blackKingX=col;
						blackKingY=row;
						break;
				}
				playingBoard[row][col] = setUpBoard[row][col];
			}
		}
		
		for (ChessPiece i: pieces) {
			i.setPic();
		}
		
	}
	
	public void draw(Graphics g) {
		try {
			canvas = ImageIO.read(f);
		} catch (IOException exc) {
		}
		
		if (pressingRight)
			theta += 0.1;
		else if (pressingLeft)
			theta -= 0.1;
		
		if (pressingUp && zShift > 0.8)
			zShift -= 0.05;
		else if (pressingDown && zShift < 3)
			zShift += 0.05;
		
		double cosT = Math.cos(theta);
		double sinT = Math.sin(theta);
		
		//long milliTime = System.currentTimeMillis();
		
		for (int x = 0; x < 400; x++) {
			for (int y = 0; y < 400; y++) {
				double pixX = ((double) x - 200) / 400;
				double pixY = ((double) y);
				double z1 = (200) / (pixY);
				double x1 = (pixX) * (z1);
				double copyX = x1;
				
				z1 -= zShift;
				
				x1 = x1 * cosT - z1 * sinT;
				z1 = z1 * cosT + copyX * sinT;
				if (x1 > -0.5 && x1 < 0.5 && z1 > -0.5 && z1 < 0.5) {
					x1 = (Math.abs(x1 + 0.5)) % 1;
					double z2 = (Math.abs(z1 + 0.5)) % 1;
					
					x1 = (int) (x1 * 800);
					z2 = (int) (z2 * 800);
					
					int realC = board.getRGB((int) x1, (int) z2);
					
					if (selected != -1 && x1 > selectedX * 100 && x1 < selectedX * 100 + 100 && z2 > selectedY * 100 && z2 < selectedY * 100 + 100) {
						realC = 0xaa00;
					} else {
						for (int i = 0; i < moves.size(); i += 2) {
							if (x1 > moves.get(i)* 100 && x1 < moves.get(i) * 100 + 100 && z2 > moves.get(i + 1) * 100 && z2 < moves.get(i + 1) * 100 + 100) {
								realC = 0xaa;
								break;
							}
						}
					}
					canvas.setRGB(x, y, realC);
					//g.setColor(new Color((realC >> 16) & 0xff, (realC >> 8) & 0xff, realC & 0xff));
					//g.fillRect(x, y, 1, 1);
				}
			}
		}
		//System.out.print(System.currentTimeMillis() - milliTime + " : ");
		
		g.drawImage(canvas, 0, 0, 400, 400, null);
		
		//milliTime = System.currentTimeMillis();
		
		for (ChessPiece i: pieces) {
			i.rotateToYAxis(theta, zShift);
		}
		
		sortPieces();
		
		for (ChessPiece i: pieces) {
			i.draw(g);
		}
		
		//System.out.println(System.currentTimeMillis() - milliTime);
		
		//g.drawImage(canvas, 0, 0, 400, 400, null);
	}
	
	public int[] artificialOpponent(int gen, int[][] board, ArrayList<ChessPiece> captured, int wkx, int wky, int bkx, int bky, boolean white) {
		
		int[] returned = new int[4];//piece index, move x, move y, score
		
		returned[3] = -200;//random value too low to appear. Integer.MIN_VALUE doesn't work with checkmate
		
		ArrayList<Integer> possibleMoves;
		
		int score = 0;
		
		int[][] copyBoard = new int[board.length][board[0].length];
		for (int r = 0; r < board.length; r++)
			for (int c = 0; c < board[0].length; c++)
				copyBoard[r][c] = board[r][c];
		
		boolean mated = true;//until proven otherwise
		
		ArrayList<int[]> randomizeMoves = new ArrayList<int[]>();
		randomizeMoves.add(new int[4]);
		randomizeMoves.get(0)[3] = Integer.MIN_VALUE;
		
		int realI = -1;//if index changed when piece moved, preserve i
		
		f1: for (int i = 0; i < pieces.size(); i++) {
			if (pieces.get(i).white != white) {
				continue f1;
			}
			possibleMoves = pieces.get(i).getMoves(white? wkx: bkx, white? wky: bky, copyBoard, pieces);
			realI = i;
			
			/*if (gen == 3 && pieces.get(realI).ID == 27)
				System.out.println("b: " + pieces.get(realI).number + " : " + pieces.get(realI).ID + " : " + realI);*/
			
			for (int j = 0; j < possibleMoves.size(); j += 2) {
				realI = i;
				score = 0;
				
				if (copyBoard[possibleMoves.get(j+1)][possibleMoves.get(j)] != 0) {
					int k;
					for (k = 0; k < pieces.size(); k++) {
						if (pieces.get(k).x == possibleMoves.get(j) && pieces.get(k).y == possibleMoves.get(j+1))
							break;
					}
					score += pieces.get(k).value * 2;
				}
				
				int holdIndex = movePiece(i, possibleMoves.get(j), possibleMoves.get(j+1), possibleMoves, white, copyBoard, false);
				if (holdIndex != -1)
					realI = holdIndex;
				else {
					undoLastMove(copyBoard);
					continue;
				}
				
				mated = false;
				
				if (pieces.get(realI).x > 3 && pieces.get(realI).x < 6 && pieces.get(realI).y > 3 && pieces.get(realI).y < 6)
					score += 1;
				
				if ((pieces.get(realI).number == 1 && pieces.get(realI).y == 7) || (pieces.get(realI).number == 7 && pieces.get(realI).y == 0))
					score += 16;
				
				if (pieces.get(realI).checking(white? bkx: wkx, white? bky: wky, copyBoard)) {
					score += 1;
				}
				
				if (gen > 0) {
					score -= artificialOpponent(gen - 1, copyBoard, captured, wkx, wky, bkx, bky, !white)[3];
				}
				
				undoLastMove(copyBoard);
				
				if (score == randomizeMoves.get(0)[3]) {
					int[] moveData = new int[4];
					moveData[3] = score;
					moveData[0] = pieces.get(i).ID;
					/*if (gen == 3)
						System.out.println(pieces.get(realI).number + " : " + pieces.get(realI).ID + " : " + realI);*/
					moveData[1] = possibleMoves.get(j);
					moveData[2] = possibleMoves.get(j+1);
					randomizeMoves.add(moveData);
				} else if (score > randomizeMoves.get(0)[3]) {
					randomizeMoves.clear();
					randomizeMoves.add(new int[4]);
					randomizeMoves.get(0)[3] = score;
					int[] moveData = new int[4];
					moveData[3] = score;
					moveData[0] = pieces.get(i).ID;
					/*if (gen == 3)
						System.out.println(pieces.get(realI).number + " : " + pieces.get(realI).ID + " : " + realI);*/
					moveData[1] = possibleMoves.get(j);
					moveData[2] = possibleMoves.get(j+1);
					randomizeMoves.add(moveData);
				}
			}
		}
		
		if (mated) {
			mated = false;//check for stale
			for (int j = 0; j < pieces.size(); j++) {
				if (pieces.get(j).white != white && pieces.get(j).checking(white? wkx: bkx,  white? wky: bky, copyBoard)) {
					mated = true;
				}
			}
			if (mated)
				returned[3] = -1000 * (gen + 1);
			else
				returned[3] = -50 * (gen + 1);
		} else {
			returned = randomizeMoves.get((int) (Math.random() * (randomizeMoves.size() - 1)) + 1);
		}
		
		for (int i = 0; i < pieces.size(); i++) {
			if (pieces.get(i).ID == returned[0]) {
				returned[0] = i;
				break;
			}
		}
		
		
		return returned;
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			pressingRight = true;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			pressingLeft = true;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			pressingUp = true;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			pressingDown = true;
		
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (undoLastMove(playingBoard));
				whiteTurn = !whiteTurn;
			if (undoLastMove(playingBoard));
				whiteTurn = !whiteTurn;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			pressingRight = false;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			pressingLeft = false;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			pressingUp = false;
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			pressingDown = false;
	}

	public int movePiece(int index, int x, int y, ArrayList<Integer> moves, boolean white, int[][] board, boolean animateMoves) {
		//animateMoves = false;
		int stateIndex = previousState.size();
		int posIndex = previousBoardPos.size();
		
		previousState.add(new ArrayList<ChessPiece>());
		previousBoardPos.add(new ArrayList<CoordVal>());
		
		previousState.get(stateIndex).add(pieces.get(index).copy(index));
		
		for (int i = 0; i < moves.size(); i += 2) {
			if (x == moves.get(i) && y == moves.get(i + 1)) {
				if (board[y][x] != 0) {
					int j;
					for (j = 0; j < pieces.size(); j++) {
						if (pieces.get(j).x == x && pieces.get(j).y == y) {
							pieces.get(j).index = j;
							previousState.get(stateIndex).add(pieces.remove(j));
							//pieces.remove(j);
							break;
						}
					}
					if (j < index)
						index--;
				} else {//special french move
					if ((pieces.get(index).number == 1 || pieces.get(index).number == 7) && (pieces.get(index).white? board[pieces.get(index).y][x] == 7: board[pieces.get(index).y][x] == 1)) {
						int j;
						for (j = 0; j < pieces.size(); j++) {
							if (pieces.get(j).x == x && pieces.get(j).y == pieces.get(index).y) {
								pieces.get(j).index = j;
								previousState.get(stateIndex).add(pieces.remove(j));
								//pieces.remove(j);
								break;
							}
						}
						if (j < index)
							index--;
						
						previousBoardPos.get(posIndex).add(new CoordVal(x, pieces.get(index).y, board));
						board[pieces.get(index).y][x] = 0;
					}
				}
				
				previousBoardPos.get(posIndex).add(new CoordVal(pieces.get(index).x, pieces.get(index).y, board));
				board[pieces.get(index).y][pieces.get(index).x] = 0;
				
				previousBoardPos.get(posIndex).add(new CoordVal(x, y, board));
				board[y][x] = pieces.get(index).number;
				
				pieces.get(index).moved = true;
				//System.out.println(pieces.get(index).moved + " : " + pieces.get(index).x + " : " + pieces.get(index).y);
				
				if((pieces.get(index).number == 1 || pieces.get(index).number == 7) && Math.abs(pieces.get(index).y - y) == 2)
					pieces.get(index).movedTwo = true;
				else
					pieces.get(index).movedTwo = false;
				
				if ((pieces.get(index).number == 6 || pieces.get(index).number == 12) && Math.abs(pieces.get(index).x - x) == 2) {//castling
					//System.out.println(1);
					int rookX = pieces.get(index).x - x > 0? 0: 7;//y is king's y
					int rookMoveX = rookX == 7? 5: 3;
					int j;
					for (j = 0; j < pieces.size(); j++) {
						if (pieces.get(j).x == rookX && pieces.get(j).y == pieces.get(index).y)
							break;
					}
					//System.out.println(rookMoveX + ", " + rookX);
					previousState.get(stateIndex).add(pieces.get(j).copy(j));
					
					if (animateMoves)
						pieces.get(j).startAnimateMove(rookMoveX, pieces.get(j).y);
					
					pieces.get(j).x = rookMoveX;
					previousBoardPos.get(posIndex).add(new CoordVal(rookX, y, board));
					board[y][rookX] = 0;
					previousBoardPos.get(posIndex).add(new CoordVal(rookMoveX, y, board));
					board[y][rookMoveX] = pieces.get(j).number;
				}
				
				if (animateMoves)
					pieces.get(index).startAnimateMove(x, y);
				
				pieces.get(index).x = x;
				pieces.get(index).y = y;
				pieces.get(index).moved = true;
				if (pieces.get(index).number == 6) {
					whiteKingX = x;
					whiteKingY = y;
				} else if (pieces.get(index).number == 12) {
					blackKingX = x;
					blackKingY = y;
				}
				
				if ((pieces.get(index).number == 1 || pieces.get(index).number == 7) && (pieces.get(index).y == 0 || pieces.get(index).y == 7)) {
					ChessPiece hold = new Queen(x, y, white);
					hold.animateX = pieces.get(index).animateX;
					hold.animateY = pieces.get(index).animateY;
					hold.animating = pieces.get(index).animating;
					hold.targetX = pieces.get(index).targetX;
					hold.targetY = pieces.get(index).targetY;
					hold.framesMoved = pieces.get(index).framesMoved;
					hold.moved = true;
					hold.ID = pieces.get(index).ID;
					pieces.set(index, hold);
				}
				totalMoves++;
				return index;
			}
		}
		return -1;
	}
	
	public boolean undoLastMove(int[][] board) {
		if (totalMoves == 0)
			return false;
		ArrayList<ChessPiece> states = previousState.remove(previousState.size() - 1);
		
		
		//boolean print = false;
		
		f: for (int j = states.size() - 1; j >= 0; j--) {//pieces added back order in which removed
			ChessPiece cP = states.get(j);
			for (int i = 0; i< pieces.size(); i++) {
				if (pieces.get(i).ID == cP.ID) {
					if (cP.number == 6) {
						whiteKingX = cP.x;
						whiteKingY = cP.y;
					} else if (cP.number == 12) {
						blackKingX = cP.x;
						blackKingY = cP.y;
					}
					
					/*if (pieces.get(i).number == 2) {
						System.out.println(pieces.get(i).x + " : " + cP.x + " : AAA : " + pieces.get(i).y + " : " + cP.y + " : BBB : " + board[0][0]);
						print = true;
					}*/
					
					pieces.set(i, cP);
					continue f;
				}
			}
			if (cP.number == 6) {
				whiteKingX = cP.x;
				whiteKingY = cP.y;
			} else if (cP.number == 12) {
				blackKingX = cP.x;
				blackKingY = cP.y;
			}
			
			/*if (cP.number == 2) {
				System.out.println(cP.x + " : " + cP.y + " : " + board[0][0]);
				print = true;
			}*/
			
			pieces.add(cP.index, cP);
		}
		
		ArrayList<CoordVal> pos = previousBoardPos.remove(previousBoardPos.size() - 1);
		
		for (CoordVal cV: pos) {
			board[cV.y][cV.x] = cV.val;
		}
		totalMoves--;
		
		//if (print)
		//	System.out.println(board[0][0]);
		
		return true;
	}
	
	public void mousePressed(MouseEvent m) {
		if (!gameOver) {
			if (!animating) {
				double cosT = Math.cos(theta);
				double sinT = Math.sin(theta);
				
				double pixX = ((double) m.getX() - 200) / 400;
				double pixY = ((double) m.getY() - 23);
				double z1 = (400) / (pixY * 2);
				double x1 = (pixX) * (z1);
				double copyX = x1;
				
				z1 -= zShift;
				
				x1 = x1 * cosT - z1 * sinT;
				z1 = z1 * cosT + copyX * sinT;
				
				if (x1 < -0.5 || x1 >= 0.5 || z1 < -0.5 || z1 >= 0.5) {
					selected = -1;
					moves.clear();
				}
				
				selectedX = (int) ((x1+ 0.5) * 8);
				selectedY = (int) ((z1+ 0.5) * 8);
			}
			
			if (selected != -1) {
				if (movePiece(selected, selectedX, selectedY, moves, whiteTurn, playingBoard, true) != -1) {
					animating = true;
					//pieces.get(selected).startAnimateMove(selectedX, selectedY);
					moves.clear();
					selected = -1;
					whiteTurn = !whiteTurn;
					
				}
			}
			
			/*if (!whiteTurn) {
				int[] a = artificialOpponent(3, playingBoard, new ArrayList<ChessPiece>(), whiteKingX, whiteKingY, blackKingX, blackKingY, false);
				//System.out.println(pieces.get(a[0]).number + " : " + pieces.get(a[0]).x + " : " + pieces.get(a[0]).y + " ][ " + a[1] + " : " + a[2] + " : " + a[3] + " : " + blackKingX + " : " + blackKingY + " : " + whiteKingX + " : " + whiteKingY);
				ArrayList<Integer> holdMoves = pieces.get(a[0]).getMoves(whiteTurn? whiteKingX: blackKingX, whiteTurn? whiteKingY: blackKingY, playingBoard, pieces);
				//System.out.println(holdMoves);
				//System.out.println(playingBoard[a[2]][a[1]]);
				if (movePiece(a[0], a[1], a[2], holdMoves, whiteTurn, playingBoard, false) != -1) {
					whiteTurn = !whiteTurn;
				} else {
					System.out.println("ERROR! AI'S MOVE NOT FOUND");
					System.out.println(a[1] + " : " + a[2]);
				}
			}*/
			
			for (int i = 0; i < pieces.size(); i++) {
				if (pieces.get(i).x == selectedX && pieces.get(i).y == selectedY && pieces.get(i).white == whiteTurn) {
					selected = i;
					moves = pieces.get(i).getMoves(pieces.get(i).white? whiteKingX: blackKingX, pieces.get(i).white? whiteKingY: blackKingY, playingBoard, pieces);
					return;
				}
			}
			selected = -1;
			moves.clear();
		}
		
	}
	
	public void runAnimate() {
		if (!whiteTurn && !animating && !gameOver) {
			int[] a = artificialOpponent(3, playingBoard, new ArrayList<ChessPiece>(), whiteKingX, whiteKingY, blackKingX, blackKingY, false);
			//System.out.println("a: " + a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
			//System.out.println(pieces.get(a[0]).number + " : " + pieces.get(a[0]).x + " : " + pieces.get(a[0]).y + " ][ " + a[1] + " : " + a[2] + " : " + a[3] + " : " + blackKingX + " : " + blackKingY + " : " + whiteKingX + " : " + whiteKingY);
			ArrayList<Integer> holdMoves = pieces.get(a[0]).getMoves(whiteTurn? whiteKingX: blackKingX, whiteTurn? whiteKingY: blackKingY, playingBoard, pieces);
			//System.out.println(holdMoves);
			//System.out.println(a[3]);
			if (movePiece(a[0], a[1], a[2], holdMoves, whiteTurn, playingBoard, true) != -1) {
				whiteTurn = !whiteTurn;
				animating = true;
				//System.out.println("AI'S MOVE FOUND");
				//System.out.println(a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
			} else {
				//System.out.println("ERROR! AI'S MOVE NOT FOUND");
				//System.out.println(a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
				System.out.println("White Wins");
				gameOver = true;
			}
		}
		
		if (whiteTurn && !animating && !humanPlayer && !gameOver) {
			int[] a = artificialOpponent(3, playingBoard, new ArrayList<ChessPiece>(), whiteKingX, whiteKingY, blackKingX, blackKingY, true);
			//System.out.println("a: " + a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
			//System.out.println(pieces.get(a[0]).number + " : " + pieces.get(a[0]).x + " : " + pieces.get(a[0]).y + " ][ " + a[1] + " : " + a[2] + " : " + a[3] + " : " + blackKingX + " : " + blackKingY + " : " + whiteKingX + " : " + whiteKingY);
			ArrayList<Integer> holdMoves = pieces.get(a[0]).getMoves(whiteTurn? whiteKingX: blackKingX, whiteTurn? whiteKingY: blackKingY, playingBoard, pieces);
			//System.out.println(holdMoves);
			//System.out.println(a[3]);
			if (movePiece(a[0], a[1], a[2], holdMoves, whiteTurn, playingBoard, true) != -1) {
				whiteTurn = !whiteTurn;
				animating = true;
				//System.out.println("AI'S MOVE FOUND");
				//System.out.println(a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
			} else {
				//System.out.println("ERROR! AI'S MOVE NOT FOUND");
				//System.out.println(a[0] + " : " + pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
				System.out.println("Black Wins");
				gameOver = true;
			}
		}
		
		//boolean stoppedAnimating = false;
		if (animating) {
			for (ChessPiece i: pieces) {
				if (!i.animateMove()) {
					//stoppedAnimating = true;
					animating = false;
				}
			}
		}
		
		//if (stoppedAnimating) {
			/*if (!whiteTurn) {
				int[] a = artificialOpponent(3, playingBoard, new ArrayList<ChessPiece>(), whiteKingX, whiteKingY, blackKingX, blackKingY, false);
				//System.out.println(pieces.get(a[0]).number + " : " + pieces.get(a[0]).x + " : " + pieces.get(a[0]).y + " ][ " + a[1] + " : " + a[2] + " : " + a[3] + " : " + blackKingX + " : " + blackKingY + " : " + whiteKingX + " : " + whiteKingY);
				ArrayList<Integer> holdMoves = pieces.get(a[0]).getMoves(whiteTurn? whiteKingX: blackKingX, whiteTurn? whiteKingY: blackKingY, playingBoard, pieces);
				//System.out.println(holdMoves);
				//System.out.println(playingBoard[a[2]][a[1]]);
				if (movePiece(a[0], a[1], a[2], holdMoves, whiteTurn, playingBoard, false) != -1) {
					whiteTurn = !whiteTurn;
					animating = true;
				} else {
					System.out.println("ERROR! AI'S MOVE NOT FOUND");
					System.out.println(pieces.get(a[0]).number + " : " + a[1] + " : " + a[2]);
				}
			}*/
		//}
	}
	
	private void sortPieces() {
		//if (start < end) {
		int end = pieces.size() - 1;
		int start = 0;
		int[] positions = new int[end - start + 2];
		int index = -1;
		positions[++index] = start;
		positions[++index] = end;
		
		while (index >= 0) {
			end = positions[index--];
			start = positions[index--];
			
			int pivot = partition(start, end);
			
			if (pivot - 1 > start) {
				positions[++index] = start;
				positions[++index] = pivot - 1;
			}
			if (pivot + 1 < end) {
				positions[++index] = pivot + 1;
				positions[++index] = end;
			}
			
		}
	}
	
	private int partition(int start, int end) {
		int i = start - 1;
		ChessPiece pivot = pieces.get(end);
		for (int j = start; j < end; j++) {
			if (pieces.get(j).drawZ >= pivot.drawZ) {
				i++;
				ChessPiece holder = pieces.get(j);
				pieces.set(j,  pieces.get(i));
				pieces.set(i, holder);
				if (selected == i)
					selected = j;
				else if (selected == j)
					selected = i;
			}
		}
		
		ChessPiece holder = pieces.get(end);
		pieces.set(end,  pieces.get(i + 1));
		pieces.set(i + 1, holder);
		
		if (selected == i + 1)
			selected = end;
		else if (selected == end)
			selected = i + 1;
		
		return i + 1;
	}
}
