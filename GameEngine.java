public class GameEngine {
    private Stack[] pegs;
    private History history;
    private int numDisks;

    public GameEngine(int numDisks) {
        this.numDisks = numDisks;
        this.history = new History();
        this.pegs = new Stack[3];
        
        for (int i = 0; i < 3; i++) {
            pegs[i] = new Stack();
        }
        
        // Coloca os discos no pino 0 (A)
        for (int i = numDisks; i >= 1; i--) {
            pegs[0].push(i);
        }
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
        return true;
    }

    public void printState() {
        System.out.println("-----------------------");
        System.out.print("Pino A (0): "); pegs[0].printCat();
        System.out.print("Pino B (1): "); pegs[1].printCat();
        System.out.print("Pino C (2): "); pegs[2].printCat();
        System.out.println("-----------------------");
    }
}
