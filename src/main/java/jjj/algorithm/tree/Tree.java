package jjj.algorithm.tree;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  22:46 15/06/2018.
 */
public interface Tree<T extends Comparable> {
    Node<T> buildTree(T[] array);
    
    int height(Node<T> node);
    
    int size(Node<T> node);
    
    // A utility function to print preorder traversal of the tree.
    // The function also prints height of every node
    void preOrder(Node<T> node);
    
    void inOrder(Node<T> node);
    
    void postOrder(Node<T> node);
    
    void levelOrder(Node<T> node);
    
    Node<T> insert(Node<T> node, T value);
    
    void remove(T value);
}
