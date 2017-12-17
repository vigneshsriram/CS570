package newtictacto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class newtictacto {
	static void game() throws Gameexpt{
		Tictactoe tictactoe;
		PrintMat ibd=new Print();
		try{
			String loadgameFn=ibd.LoadGame();
			if(loadgameFn.compareTo("")==0){		//@Deprecated - should use Status Code
				// Create a new game
				int np=ibd.Players();
				int nr=ibd.Rows();
				int nw=ibd.Winseq();
				tictactoe=new Tictactoe(np,nr,nw);
			}else{
				// Restart a saved game
				tictactoe=new Tictactoe(loadgameFn);
			}
			while(true){
				ibd.printMatrix(tictactoe);
				String nextStep=ibd.NextStep();
				if(nextStep==null){	//@Deprecated - should use Status Code
					while(true){
						try{
							String sg=ibd.SaveGame();
							if(sg.compareTo("")!=0) tictactoe.saveGame(sg);
							break;
						}
						catch(Gameexpt e){
							ibd.ExceptionSavegame();
						}
					}
					break;
				}
				//save
				else if(nextStep.length() > 4) {
					//save it is a file
					System.out.print("Enter the file name");
					Scanner t = new Scanner(System.in);
					String filename = t.next();
					t.close();
					if(filename == "") {
						filename = "SavedGame";
					}
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				              new FileOutputStream(filename), "utf-8"))) {
				   writer.write(nextStep);
				}
					System.exit(0);
				}
				
				//normal
				else{
					int a = Character.getNumericValue(nextStep.charAt(0));
					int b = Character.getNumericValue(nextStep.charAt(1));
					int ck=tictactoe.play(a, b);
					if(ck!=0){	//Someone wins(2) or tie(1)
						ibd.printMatrix(tictactoe);
						ibd.printWinTie(ck,tictactoe.win);
						break;
					}
				}
			}
		}catch(Exception e){
			ibd.Exception(e);
		}
	}
	public static void main(String[] args) {
		System.out.println("Let the game Begin");
		game();
	}
}


 class Tictactoe {
	int win;// stores the winner
	int [][]matrix;//blank matrix initialise
	int n;// size of matrix
	int m;// number of players
	int k;// win seq
	int playercnt;// counter for current player
	Tictactoe(int player, int rows, int wseq) throws Gameexpt{
		this.n=rows;// board size
		this.m=player;//no of players
		this.k=wseq;// winning sequence
		this.playercnt=0;
		this.matrix=new int[rows][rows];
		for(int i=0;i<rows;i++) for(int j=0;j<rows;j++){
			this.matrix[i][j]=-1;// means empty board
		}
		if(wseq>rows) throw new Gameexpt("No winner");
		if(rows*rows<player*(wseq-1)+1) throw new Gameexpt("No winner");
	}
	Tictactoe(String filename) throws Gameexpt{
        try{
        	File file = new File(filename);
        	Scanner sc = new Scanner(file);												
        	this.m=sc.nextInt(); // Change this to m
        	this.n=sc.nextInt(); // this to n
        	this.k=sc.nextInt();
        	this.playercnt=sc.nextInt();//
        	String s = sc.nextLine();
        	s = s.trim();
        	String[] s1 = s.split(" ");
        	for(int i = 0 ;i< s1.length-1; i++) { //0 to 
        		System.out.println(s1[i]);
        	}
        	
        	this.matrix=new int[this.n][this.n];
        	//This will create a blank matrix
	        for(int i=0;i<this.n;i++){
	        	for(int j=0;j<this.n;j++){
	        		this.matrix[i][j]= -1 ;// storing position for player move
	        		
	        	}
	        }
	        int counter1 = 0;
	        for(int k = 0 ;k< s1.length-1 ; k = k+2) {
        			this.matrix[Integer.parseInt(s1[k])][Integer.parseInt(s1[k+1])]= counter1 ;// storing position for player move
        			if (counter1 == m-1) {
        				counter1 = 0;
        			}
        			else {
        				counter1 = counter1 + 1;
        			}
    	        }
	        
	        sc.close();
        }catch(NoSuchElementException e){
        	throw new Gameexpt("IO Exception: File is corruped");
        } catch (FileNotFoundException e) {
			throw new Gameexpt("IO Exception: File Not Found");
		}
    }
	void saveGame(String filename) throws Gameexpt{
		File file = new File(filename); 
		try{
			file.createNewFile();// create file
	        BufferedWriter out = new BufferedWriter(new FileWriter(file));  
	        out.write(""+this.n+" "+this.m+" "+this.k+" "+this.playercnt+"\n");
	        for(int i=0;i<this.n;i++){
	        	for(int j=0;j<this.n;j++){
	        		out.write(""+this.matrix[i][j]+" ");
	        	}
	        	out.write("\n");
	        }
	        out.flush();  
	        out.close();//close file
		}catch(IOException e){
			throw new Gameexpt("IO Exception: Write file failed. ");
		}
	}
	int play(int x,int y) throws Gameexpt{
		if(this.matrix[x][y]!=-1) throw new Gameexpt("This place is already used! ");
		this.matrix[x][y]=this.playercnt;
		
		int n=this.n;
		int dx[]={0,1,1,-1};
		int dy[]={1,0,1,1};
		
		// Check if game is WIN 
		// check this row
		int sx[]={x,0,x-y,0};
		int sy[]={0,y,0,x+y};
		for(int d=0;d<4;d++){
			int longest=0;
			for(int i=0;i<n;i++){
				int cx=sx[d]+dx[d]*i;
				int cy=sy[d]+dy[d]*i;
				if(cx<0 || cx>=n || cy<0 || cy>=n) continue;
				if(this.matrix[cx][cy]==playercnt) if(++longest>=this.k){
					win=playercnt;
					return 2;
				}else ;
				else longest=0;
			}
		}
		
		// Check if game is draw
		// check rows and cols
		int fsx[]=new int[n*3];
		int fsy[]=new int[n*3];
		for(int c=0;c<n;c++){
			fsx[c]=0;
			fsy[c]=n-c-1;
		}
		for(int c=n;c<n*3;c++){
			fsx[c]=c-n;
			fsy[c]=0;
		}
		for(int c=0;c<n*3;c++){
			for(int d=0;d<4;d++){
				int lostplr=0;
				int lostemp=0;
				int lstplr=-1;
				for(int i=0;i<n;i++){
					int cx=fsx[c]+dx[d]*i;
					int cy=fsy[c]+dy[d]*i;
					if(cx<0 || cx>=n || cy<0 || cy>=n) continue;
					if(this.matrix[cx][cy]==-1){
						lostemp+=1;
					}else if(this.matrix[cx][cy]==lstplr){
						lostplr=lostplr+lostemp+1;
						lostemp=0;
					}else{
						lostplr=lostemp+1;
						lostemp=0;
						lstplr=this.matrix[cx][cy];
					}
					if(lostplr+lostemp>=this.k){
						playercnt=(playercnt+1)%this.m;
						return 0;
					}
				}	
			}
		}
		
		return 1;
	}
}

 
 interface PrintMat{
		void printMatrix(Tictactoe tictactoe);
		void printWinTie(int wt, int st);
		int Players();
		int Rows();
		int Winseq();
		String NextStep();
		String LoadGame();
		String SaveGame();
		void ExceptionSavegame();
		void Exception(Exception e);
	}
 
 
 class Print implements PrintMat {
		String savedGame = "";
		String Players = "";
		String rows = "";
		String winSeq = "";
		String counter = "";
		Scanner sc;
		Print(){
			sc = new Scanner(System.in);
		}
		int nextInt(){
			int option = 0;
			try {
			    option = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
			    e.printStackTrace();
			}
			return option;
		}
		String nextLine(){
			String str=sc.nextLine();
			return str;
		}
		@Override
		public void printMatrix(Tictactoe tictactoe) {
			String rep = " XOABCDEFGHIJKLMNPQRSTUVWYZ";
			int n = tictactoe.n;
			System.out.print("   ");
			if (n <= 9) {
				for (int i = 1; i <= n; i++) {
					System.out.print(" " + i + "  ");
				}
	}
			if (10 <= n && n <= 99) {
				for (int i = 1; i <= 9; i++) {
					System.out.print(" " + i + "  ");
				}
				for (int i = 10; i <= n; i++) {
					System.out.print(i + "  ");
				}
			}
			if (100 <= n && n <= 999) {
				for (int i = 1; i <= 9; i++) {
					System.out.print(" " + i + "  ");
				}
				for (int i = 10; i <= 99; i++) {
					System.out.print(i + "  ");
				}
				for (int i = 100; i <= n; i++) {
					System.out.print(i + " ");
				}
			}
			 for(int i=1;i<=n;i++)
			 {
			    System.out.println("");
			    if(i<=9){
			    	System.out.print(i +"  ");
			    }
			    if(10<=i && i<=99){
			    	System.out.print(i+" ");
			    }
			    if(100<=i && i<=999){
			    	System.out.print(i);
			    }
		    	for (int j = 1; j <= n; j++) {
					System.out.print(" " + rep.charAt(tictactoe.matrix[i-1][j-1]+1)+ " ");
					if(j!=n) System.out.print("|");
		    	}
			    System.out.println("");
			    System.out.print("   ");
			    for (int j = 1; i!=n && j <= n; j++) {
					System.out.print("---");
					if(j!=n) System.out.print("+");
				}
			 }
			
			System.out.println("It's "+rep.charAt(tictactoe.playercnt+1)+"'s turn: ");
			counter = Integer.toString(tictactoe.playercnt);
			System.out.println("This is counter r " + counter);	
		}
		static public void main1(String args[]){
			Tictactoe tictactoe=new Tictactoe(15,105,3);
			PrintMat ib=new Print();
			tictactoe.play(1, 5);
			tictactoe.play(1, 9);
			ib.printMatrix(tictactoe);
		}

		@Override
		public int Players() {
			System.out.println("Enter number of players: ");
			int players = nextInt();
			Players = Integer.toString(players);
			return players;
		}

		@Override
		public int Rows() {
			System.out.println("Enter number of Rows and Coloumns: ");
			int Rows = nextInt();
			rows = Integer.toString(Rows);
			return Rows;
		}

		@Override
		public int Winseq() {
			System.out.println("Enter Winning Sequence: ");
			int askNumWinseq = nextInt();
			winSeq = Integer.toString(askNumWinseq);
			return askNumWinseq;
		}
		
		
		@Override
		public String NextStep() {
			String str = nextLine();
			savedGame = savedGame + " " +  str ;
			System.out.println(savedGame);
			String[] parts = str.split(" ");
			if (parts[0].equals("Q"))
				return null;
			else if(parts[0].equals("S")) {
				
				String s = Players + " " + rows + " " + winSeq + " "+ counter;
				
				savedGame = s + " " + savedGame;
				
				return savedGame;
					}
			else {
				String partsString = parts[0] + parts[1];
				int ans[] = new int[2];
				ans[0] = Integer.parseInt(parts[0])-1;
				ans[1] = Integer.parseInt(parts[1])-1;
				return partsString;
			}
		}
		
		@Override
		public void printWinTie(int wt, int st) { // wt: win(2) or tie(1), st:
													// winner(win) or useless(tie)
			System.out.println("Congratulations!!!");
			if (wt == 2) {
				System.out.print("\n The winner is: ");
				String rep = " XOABCDEFGHIJKLMNPQRSTUVWYZ";
				System.out.println(rep.charAt(st+1)+" ");
			} else
				System.out.println("\n The game is a DRAW ");
		}

		@Override
		
		public String LoadGame() {
			System.out.println("Press enter to begin new game or enter filename to load saved game");
			String str = nextLine();
			return str;
		}

		@Override
		public String SaveGame() {
			System.out.println("Save Game?:");
			String str = nextLine();
			return str;
		}

		@Override
		public void ExceptionSavegame() {
			System.out.println("Error while saving the game. ");
			System.out.println("Please try again. ");
		}
		
		@Override
		public void Exception(Exception e){
			System.out.println("ERROR!");
			System.out.println(e.toString());
		}
	}

 
 class Gameexpt extends RuntimeException {
		private static final long serialVersionUID = 8780124501704029360L;
		String msg;
		public Gameexpt(){
			super();
		}
		public Gameexpt(String msg) {  
	        super(msg);  
	        this.msg = msg;  
	    }
		public String toString(){
			return this.msg;
		}
	}