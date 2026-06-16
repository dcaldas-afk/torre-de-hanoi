public class Stack {
    Node top;
    int size;

    private class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    Stack(){
        this.top = null;
        this.size = 0;
    }

    ///////////////////////////////////////// Operações essenciais ///////////////////////////////////
    public void push(int data) {
        Node newNode = new Node(data);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("A pilha está vazia");
        }
        int data = top.data;
        top = top.next;
        size--;
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

    public void clear(){
        top = null;
        size = 0;
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

    public int getSize(){
        return size;
    }

    public boolean isSorted(){
        if(!isEmpty()){
            Node aux = top;
            while(aux.next != null){
                if(aux.data > aux.next.data){
                    return false;
                }
                aux = aux.next;
            }
            return true;
        }
        return false;
    }

}
