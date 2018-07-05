package jjj.algorithm.tree;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  22:49 15/06/2018.
 */
public class Node<T extends Comparable> {
    T value;
    int height;
    Node left;
    Node right;
    
    public Node() {
    }
    
    public Node(T value) {
        this.value = value;
        this.height = 1;
    }
    
    public Node(T value, Node left, Node right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }
    
    public Node(T value, int height, Node left, Node right) {
        this.value = value;
        this.height = height;
        this.left = left;
        this.right = right;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public Node getLeft() {
        return left;
    }
    
    public void setLeft(Node left) {
        this.left = left;
    }
    
    public Node getRight() {
        return right;
    }
    
    public void setRight(Node right) {
        this.right = right;
    }
}
