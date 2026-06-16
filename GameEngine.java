import java.util.ArrayList;
import java.util.Collections;

public class GameEngine {
    private Stack[] pegs;
    private History history;
    private int numDisks;
    private Stack initialStackState;
    private int moveCounter;

    public GameEngine(int numDisks) {
        this.numDisks = numDisks;
        this.moveCounter = 0;
        this.history = new History();
        this.pegs = new Stack[3];
        
        for (int i = 0; i < 3; i++) {
            pegs[i] = new Stack();
        }
        
        generateRandomGame();
    }

    private void generateRandomGame(){
        //seta tudo pra null porque eu quero utilizar tambem no new game
        //entao tem que apagar o que foi feito antes
        for (int i = 0; i < 3; i++) { 
            pegs[i].clear();
        }

        ArrayList<Integer> disks = new ArrayList<>();
        for (int i = 1; i <= numDisks; i++) {
            disks.add(i);
        }
        Collections.shuffle(disks);

        for (int i = 0; i < numDisks; i++) {
            pegs[0].push(disks.get(i));
        }
        pegs[0].size = 3;
        
        initialStackState = copyStack(pegs[0]); //salva a torre inicial para caso de reset
    }

    private Stack copyStack(Stack original) {
        Stack copy = new Stack();
        Stack temp = new Stack();
        
        // Inverte para manter a ordem correta
        while (!original.isEmpty()) {
            temp.push(original.pop());
        }
        
        while (!temp.isEmpty()) {
            int disk = temp.pop();
            copy.push(disk);
            original.push(disk);
        }
        
        return copy; // quem souber forma menos bruta de fazer isso, agradeceria
    }


    public boolean moveDisk(int from, int to) {
        if (from < 0 || from > 2 || to < 0 || to > 2 || from == to) return false;
        
        if (pegs[from].isEmpty()) {
            System.out.println("Erro: O pino de origem está vazio!");
            return false;
        }

        int diskToMove = pegs[from].peek();

        if (!pegs[to].isEmpty() && diskToMove > pegs[to].peek()) {
            System.out.println("Erro: Não é permitido colocar disco maior (" + diskToMove + ") sobre menor (" + pegs[to].peek() + ")!");
            return false;
        }

        pegs[from].pop();
        pegs[to].push(diskToMove);
        history.makeMove(from, to, diskToMove);
        moveCounter++;
        return true;
    }

    public boolean undoMove() {
        History.Move move = history.undo();
        if (move == null) {
            System.out.println("Nada para desfazer.");
            return false;
        }

        // Inverte o movimento
        pegs[move.toPeg].pop();
        pegs[move.fromPeg].push(move.diskSize);
        moveCounter++;
        return true;
    }

    public boolean redoMove() {
        History.Move move = history.redo();
        if (move == null) {
            System.out.println("Nada para refazer.");
            return false;
        }

        // Reaplica o movimento original
        pegs[move.fromPeg].pop();
        pegs[move.toPeg].push(move.diskSize);
        moveCounter++;
        return true;
    }

    public void reset(){
        history.reset();
        pegs[0] = initialStackState;
        pegs[1].clear();
        pegs[2].clear();
    }

    public void newGame(){
        history.reset();
        generateRandomGame();
        moveCounter = 0;
    }

    public boolean checkWin(){
        for(int i = 0; i < 3; i++){
            if(pegs[i].getSize() == numDisks && pegs[i].isSorted()){
                return true;
            }
        }
        return false;
    }

    public void printState() {
        System.out.println("----------------------- move counter: " + moveCounter);
        System.out.print("Pino A (0): "); pegs[0].printCat();
        System.out.print("Pino B (1): "); pegs[1].printCat();
        System.out.print("Pino C (2): "); pegs[2].printCat();
        System.out.println("-----------------------");
    }
}
