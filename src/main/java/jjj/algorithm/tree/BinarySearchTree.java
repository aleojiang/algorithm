package jjj.algorithm.tree;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

/**
 * A binary search tree (BST) is a binary tree where each node has a Comparable key (and an associated value)
 * and satisfies the restriction that the key in any node is larger than the keys in all nodes in that node’s left
 * subtree and smaller than the keys in all nodes in that node’s right subtree.
 *
 *
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  23:32 15/06/2018.
 */
public class BinarySearchTree<T extends Comparable<T>> implements Tree<T> {
    private Node<T> root;
    
    @Override
    public Node<T> buildTree(T[] array) {
        List<Node<T>> list = Arrays.stream(array).map(Node::new).collect(Collectors.toList());
        
        IntStream.range(0, array.length / 2 - 1).forEach(index -> {
            list.get(index).setLeft(list.get(index * 2 + 1));
            list.get(index).setRight(list.get(index * 2 + 2));
        });
        // handle the last
        int index = array.length / 2 - 1;
        list.get(index).setLeft(list.get(index + 1));
        if (array.length % 2 == 1) {
            list.get(index).setRight(list.get(index + 2));
        }
        return root;
    }
    
    @Override
    public int height(Node<T> node) {
        return isNull(node) ? 0 : Math.max(height(node.getLeft()) + 1, height(node.getRight()) + 1);
    }
    
    @Override
    public int size(Node<T> node) {
        return isNull(node) ? 0 : 1 + size(node.getLeft()) + size(node.getRight());
    }
    
    @Override
    public void preOrder(Node<T> node) {
        if (!isNull(node)) {
            System.out.print(node.getValue() + ",");
            preOrder(node.getLeft());
            preOrder(node.getRight());
        }
    }
    
    @Override
    public void inOrder(Node<T> node) {
        if (!isNull(node)) {
            preOrder(node.getLeft());
            System.out.print(node.getValue() + ",");
            preOrder(node.getRight());
        }
    }
    
    @Override
    public void postOrder(Node<T> node) {
        if (!isNull(node)) {
            preOrder(node.getLeft());
            preOrder(node.getRight());
            System.out.print(node.getValue() + ",");
        }
    }
    
    @Override
    public void levelOrder(Node<T> node) {
        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            Node<T> current = queue.poll();
            assert current != null;
            System.out.print(current.getValue() + ",");
            if (!isNull(current.getLeft())) {
                queue.add(current.getLeft());
            }
            if (!isNull(current.getRight())) {
                queue.add(current.getRight());
            }
        }
    }
    
    @Override
    public Node<T> insert(Node<T> node, T value) {
        if (node == null)
            return new Node<>(value);
        
        if (value.compareTo(node.value) > 0) {
            node.right = insert(node.right, value);
        } else if (value.compareTo(node.value) < 0) {
            node.left = insert(node.left, value);
        }
        return node;
    }
    
    @Override
    public void remove(T value) {
    
    }
    
    public static void main(String args[]) {
        //将一个数组转化为一颗完全二叉树
        //        Integer[] array = { 1, 2, 3, 4, 5, 6, 7, 8 };
        //        BinaryTree bt = new BinaryTree();
        //        TreeNode root = bt.buildTree(array);
        //        System.out.print("树的高度：");
        //        System.out.println(bt.height(root));
        //        System.out.print("节点的个数：");
        //        System.out.println(bt.size(root));
        //        System.out.println("先序遍历：");
        //        bt.preOrder(root);
        //        System.out.println("\n"+"非递归先序遍历：");
        //        bt.nonRecPreOrder(root);
        //        System.out.println();
        //
        //
        //        System.out.println("中序遍历：");
        //        bt.inOrder(root);
        //        System.out.println("\n"+"非递归中序遍历：");
        //        bt.nonRecInOrder(root);
        //        System.out.println();
        //
        //        System.out.println("后序遍历：");
        //        bt.postOrder(root);
        //        System.out.println("\n"+"非递归后序遍历：");
        //        bt.nonRecPostOrder(root);
        //        System.out.println();
        //
        //        System.out.println("层次遍历：");
        //        bt.levelOrder(root);
        //
        //        //手工构建一颗二叉树
        //        TreeNode nodeA = new TreeNode("A");
        //        TreeNode nodeB = new TreeNode("B");
        //        TreeNode nodeC = new TreeNode("C");
        //        TreeNode nodeD = new TreeNode("D");
        //        TreeNode nodeE = new TreeNode("B");
        //        TreeNode nodeF = new TreeNode("F");
        //        TreeNode nodeG = new TreeNode("G");
        //        TreeNode nodeH = new TreeNode("H");
        //        TreeNode nodeI = new TreeNode("I");
        //        nodeA.setLchild(nodeB);
        //        nodeA.setRchild(nodeD);
        //        nodeB.setRchild(nodeC);
        //        nodeD.setLchild(nodeE);
        //        nodeD.setRchild(nodeF);
        //        nodeF.setLchild(nodeG);
        //        nodeF.setRchild(nodeI);
        //        nodeG.setRchild(nodeH);
        //
        //
        //        System.out.println("\n\n"+"*****************");
        //        System.out.print("树的高度：");
        //        System.out.println(bt.height(nodeA));
        //        System.out.print("节点的个数：");
        //        System.out.println(bt.size(nodeA));
        //        System.out.println("先序遍历：");
        //        bt.preOrder(nodeA);
        //        System.out.println();
        //
        //        System.out.println("中序遍历：");
        //        bt.inOrder(nodeA);
        //        System.out.println();
        //
        //        System.out.println("后序遍历：");
        //        bt.postOrder(nodeA);
        //        System.out.println();
        //
        //        System.out.println("层次遍历：");
        //        bt.levelOrder(nodeA);
        
        final BinarySearchTree<Integer> bt1 = new BinarySearchTree();
        
        Arrays.asList(4, 3, 2, 1, 8, 7, 6, 5).forEach(v -> {
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
