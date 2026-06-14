public class History {
    Node head;
    Node tail;
    int size;
    private Node currentNode = null;

    // Representa uma jogada realizada
    public static class Move {
        public int fromPeg;
        public int toPeg;
        public int diskSize;

        public Move(int from, int to, int diskSize) {
            this.fromPeg = from;
            this.toPeg = to;
            this.diskSize = diskSize;
        }
    }

    // Nó que contém a jogada e as conexões
    class Node {
        Move move;
        Node prev;
        Node next;

        public Node(Move move) {
            this.move = move;
        }
    }

    // Cria as conexões entre o último nó e o novo 
    public void makeMove(int from, int to, int diskSize) {
        Move newMove = new Move(from, to, diskSize);
        Node n = new Node(newMove);

        if (head == null) {
            head = tail = n;
            currentNode = n;
            size = 1;
        } else {
            if (currentNode == null) {
                head = n;
                tail = n;
                currentNode = n;
                size = 1;
            } else {
                currentNode.next = n;
                n.prev = currentNode;
                
                tail = n;
                currentNode = n;
                size++; 
            }
        }
    } 

    // Desfazer jogada
    public Move undo() {
        if (currentNode == null) {
            return null; 
        }
        
        Move previousMove = currentNode.move;
        currentNode = currentNode.prev;
        return previousMove;
    }

    // Refazer jogada 
    public Move redo() {
        if (currentNode == null) {
            if (head != null) {
                currentNode = head;
                return currentNode.move;
            }
            return null;
        }

        if (currentNode.next == null) {return null;}

        currentNode = currentNode.next;
        return currentNode.move;
    }

    // =============================Métodos Auxiliares para a Interface Gráfica (Não sei se vai ser útil)======================================== //

    public boolean canUndo() {
        return currentNode != null;
    }

    public boolean canRedo() {
        if (currentNode == null) {return head != null;}
        
        return currentNode.next != null;
    }
}