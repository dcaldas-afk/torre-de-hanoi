public class TesteJogo {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO O JOGO (3 Discos) ===");
        GameEngine game = new GameEngine(3);
        game.printState(); // Deve mostrar 321 no Pino A

        System.out.println("\n--- JOGADA 1: Movendo do Pino 0 para 2 [Válida] ---");
        game.moveDisk(0, 2);
        game.printState(); // Pino C deve ter o disco 1

        System.out.println("\n--- JOGADA 2: Movendo do Pino 0 para 2 [INVÁLIDA] ---");
        // Vai tentar jogar o disco 2 por cima do disco 1
        game.moveDisk(0, 2); 
        game.printState(); // O estado não deve ter mudado

        System.out.println("\n--- JOGADA 3: Movendo do Pino 0 para 1 [Válida] ---");
        game.moveDisk(0, 1);
        game.printState(); // Pino B deve ter o disco 2

        System.out.println("\n--- testando undo/desfazer (desfazendo a jogada 3) ---");
        game.undoMove();
        game.printState(); // O disco 2 deve voltar para o Pino A

        System.out.println("\n--- Testando redo/refazer (refazendo a jogada 3) ---");
        game.redoMove();
        game.printState(); // O disco 2 deve ir novamente para o Pino B

        System.out.println("\n--- Testando a bifurcação do histórico ---");
        System.out.println("Desfazendo novamente...");
        game.undoMove(); // Volta o disco 2 pro Pino A
        game.printState();
        
        System.out.println("Movendo do Pino 0 para 2 (novo futuro)...");
        // O pino 2 já tem o disco 1, tentar jogar o 2 lá vai dar erro, então jogamos para o 0 pra 2, mas tbm é inválido
        // Vamos jogar o disco 1 do Pino 2 para o Pino 1
        game.moveDisk(2, 1);
        game.printState();

        System.out.println("Tentando Refazer o futuro antigo...");
        game.redoMove(); // Deve falhar, pois ao mover 2->1 a linha do tempo foi reescrita
    }
}
