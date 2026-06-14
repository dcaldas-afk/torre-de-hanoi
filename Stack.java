public class Stack {
    Node top;

    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    ///////////////////////////////////////// Operações essenciais ///////////////////////////////////
    public void push(int data) {
        Node newNode = new Node(data);
        newNode.next = top;
        top = newNode;
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("A pilha está vazia");
        }
        int data = top.data;
        top = top.next;
        return data;
    }

    public int peek() {
        if (isEmpty()) {
            throw new RuntimeException("A pilha está vazia");
        }
        return top.data;
    }

    ///////////////////////////////////////// Checagem de erros ///////////////////////////////////
    public boolean isEmpty() {
        return top == null;
    }

    ///////////////////////////////////////// Métodos de print ///////////////////////////////////
    public void printStack() {
        Node node = top;
        while (node != null) {
            System.out.println(node.data);
            node = node.next;
        }
    }

    public void printCat() {
        Node node = top;
        while(node != null) {
            System.out.print(node.data);
            node = node.next;
        }
        System.out.println("");
    }


}
