package jjj.algorithm.tree;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.max;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  23:28 15/06/2018.
 */
public class AVLTree<T extends Comparable> implements Tree<T> {
    private Node<T> root;
    
    @Override
    public Node<T> buildTree(T[] array) {
        return null;
    }
    
    // A utility function to get the height of the tree
    @Override
    public int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }
    
    @Override
    public int size(Node<T> node) {
        return node == null? 0: size(node.left)+size(node.right)+1;
    }
    
    @Override
    public void preOrder(Node<T> node) {
        if (node != null) {
            System.out.print(node.value + "(" + node.height + ") ");
            preOrder(node.left);
            preOrder(node.right);
        }
    }
    
    @Override
    public void inOrder(Node<T> node) {
        if (node != null) {
            preOrder(node.left);
            System.out.print(node.value + "(" + node.height + ") ");
            preOrder(node.right);
        }
    }
    
    @Override
    public void postOrder(Node<T> node) {
        if (node != null) {
            preOrder(node.left);
            preOrder(node.right);
            System.out.print(node.value + "(" + node.height + ") ");
        }
    }
    
    @Override
    public void levelOrder(Node<T> node) {
        if (node != null) {
            Queue<Node<T>> queue = new LinkedList<>();
            queue.offer(node);
            
            while (!queue.isEmpty()) {
                Node<T> tmp = queue.poll();
                assert tmp != null;
                System.out.print(tmp.value + "(" + tmp.height + ") ");
                if (tmp.left != null) {
                    queue.offer(tmp.left);
                }
                if (tmp.right != null) {
                    queue.offer(tmp.right);
                }
            }
        }
    }
    
    @Override
    public Node<T> insert(Node<T> node, T value) {
        /* 1.  Perform the normal BST insertion */
        if (node == null) {
            return new Node<>(value);
        }
        if (node.getValue().compareTo(value) > 0) {
            node.left = insert(node.left, value);
        } else if (node.getValue().compareTo(value) < 0) {
            node.right = insert(node.right, value);
        } else {
            return node;
        }
        /* 2. Update height of this ancestor node */
        node.height = 1 + max(height(node.left), height(node.right));
        
        /**
         * 3. Get the balance factor of this ancestor
         * node to check whether this node became
         * unbalanced
         **/
        int balance = getBalance(node);
        
        // If this node becomes unbalanced, then there are 4 cases as below
        
        // Left Left Case
        if (balance > 1 && value.compareTo(node.left.value) < 0)
            return rightRotate(node);
        
        // Right Right Case
        if (balance < -1 && value.compareTo(node.right.value) > 0)
            return leftRotate(node);
        
        // Left Right Case
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        
        // Right Left Case
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        
        /* return the (unchanged) node pointer */
        return node;
    }
    
    // A utility function to left rotate subtree rooted with y
    private Node<T> leftRotate(Node<T> node) {
        Node<T> nr = node.right;
        Node<T> left = nr.left;
        
        // perform rotate
        nr.left = node;
        node.right = left;
        
        // update height
        node.height = max(height(node.left), height(node.right)) + 1;
        nr.height = max(height(nr.left), height(nr.right)) + 1;
        
        // return new root
        return nr;
    }
    
    // A utility function to right rotate subtree rooted with y
    // See the diagram given above.
    private Node<T> rightRotate(Node<T> node) {
        Node<T> nl = node.left;
        Node<T> tmp = nl.right;
        
        // Perform rotation
        nl.right = node;
        node.left = tmp;
        
        // Update heights
        node.height = max(height(node.left), height(node.right)) + 1;
        nl.height = max(height(nl.left), height(nl.right)) + 1;
        
        // Return new root
        return nl;
    }
    
    private int getBalance(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }
    
    @Override
    public void remove(T value) {
    
    }
    
    public static void main(String[] args) {
        final AVLTree<Integer> bt1 = new AVLTree();
        
        Arrays.asList(10, 20, 30, 40, 50, 25).forEach(v -> {
            bt1.root = bt1.insert(bt1.root, v);
        });
        Node<Integer> nodeA = bt1.root;
        System.out.println("\n\n" + "*****************");
        System.out.print("树的高度：");
        System.out.println(bt1.height(nodeA));
        System.out.print("节点的个数：");
        System.out.println(bt1.size(nodeA));
        System.out.println("先序遍历：");
        bt1.preOrder(nodeA);
        System.out.println();
        
        System.out.println("中序遍历：");
        bt1.inOrder(nodeA);
        System.out.println();
        
        System.out.println("后序遍历：");
        bt1.postOrder(nodeA);
        System.out.println();
        
        System.out.println("层次遍历：");
        bt1.levelOrder(nodeA);
        
    }
}
