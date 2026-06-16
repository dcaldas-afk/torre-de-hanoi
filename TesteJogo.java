import java.util.Scanner;
public class TesteJogo {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO O JOGO (3 Discos) ===");
        GameEngine game = new GameEngine(3);
        game.printState(); // Deve mostrar o game

        Scanner scanner = new Scanner(System.in);
        int op;
        int move;

        //omovimento por enquanto é um numero de daus casas sendo a dezena a origem e a unidade o destino
        do{
            if(game.checkWin()) {System.out.println("PARABENS VOCE GANHOU");}
            System.out.println("Voce deseja");
            System.out.println("1 - Mover");
            System.out.println("2 - Undo Move");
            System.out.println("3 - Redo Mover");
            System.out.println("4 - Reset");
            System.out.println("5 - NewGame");
            System.out.println("0 - Sair do sistema");
            op = scanner.nextInt();
            if(op == 1){
                System.out.print("Mover: ");
                move = scanner.nextInt();
                game.moveDisk(move / 10, move % 10);
                game.printState(); 
            } else if(op == 2){
                game.undoMove();
                game.printState(); 
            }else if(op == 3){
                game.redoMove();
                game.printState(); 
            }else if(op == 4){
                game.reset();
                game.printState(); 
            } else if(op == 5){
                game.newGame();
                game.printState(); 
            } else break;
            

        }while(op > 0);
    }
}
