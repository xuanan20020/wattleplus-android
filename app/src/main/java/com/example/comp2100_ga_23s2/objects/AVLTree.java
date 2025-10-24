package com.example.comp2100_ga_23s2.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * An implementation of AVLTree which ensures the tree is balanced.
 * AVL Trees are a type of self-balancing binary search tree.
 * @author Xuan An
 * @param <T> type of values. Must be Comparable.
 */
public class AVLTree<T extends Comparable<T>> {
    public T value;
    public AVLTree<T> leftNode;
    public AVLTree<T> rightNode;
    public AVLTree(){
        value = null;
    }
    /**
     * Initializes AVLTree with given value.
     *
     * @param value node's value.
     */
    public AVLTree(T value) {
        // Ensure input is not null.
        if (value == null)
            throw new IllegalArgumentException("Input cannot be null");

        this.value = value;
        this.leftNode = new EmptyAVL<>();
        this.rightNode = new EmptyAVL<>();
    }
    /**
     * Initializes AVLTree with given value and children.
     *
     * @param value     node's value.
     * @param leftNode  left child.
     * @param rightNode right child.
     */
    public AVLTree(T value, AVLTree<T> leftNode, AVLTree<T> rightNode) {
        // Ensure inputs are not null.
        if (value == null || leftNode == null || rightNode == null)
            throw new IllegalArgumentException("Inputs cannot be null");

        this.value = value;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }
    /**
     * Computes the balance factor for AVL tree node.
     *
     * @return difference between heights of left and right subtrees.
     */
    public int getBalanceFactor() {
        return leftNode.getHeight()-rightNode.getHeight();
    }

    /**
     * Searches for a specific element in the tree.
     *
     * @param element the element to be found.
     * @return the tree node containing the element, or null if not found.
     */

    public AVLTree<T> find(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        assert value != null;
        int compareResult = element.compareTo(value);

        if (compareResult == 0) {
            return this;
        } else if (compareResult < 0 && leftNode != null) {
            return leftNode.find(element);
        } else if (compareResult > 0 && rightNode != null) {
            return rightNode.find(element);
        } else {
            return null;
        }
    }

    public AVLTree<T> insert(T element) {
        AVLTree<T> nt = this.insertFirst(element);
        int bal = nt.getBalanceFactor();
        if (bal > 1 && element.compareTo(nt.leftNode.value)<0) {
            return nt.rightRotate();
        }
        if (bal < -1 && element.compareTo(nt.rightNode.value)>0) {
            return nt.leftRotate();
        }
        if (bal > 1 && element.compareTo(nt.leftNode.value)>0){
            AVLTree<T> lt = nt.leftNode;
            nt.leftNode = lt.leftRotate();
            return nt.rightRotate();
        }
        if (bal < -1 && element.compareTo(nt.rightNode.value)<0){
            AVLTree<T> rt = nt.rightNode;
            nt.rightNode = rt.rightRotate();
            return nt.leftRotate();
        }
        return nt;
    }
    public AVLTree<T> insertFirst(T element){
        if (element == null)
            throw new IllegalArgumentException("Input cannot be null");

        if (value == null){
            return new AVLTree<>(element);
        }
        if (element.compareTo(value) > 0) {
            return new AVLTree<>(value,leftNode,rightNode.insertFirst(element));
        } else if (element.compareTo(value) < 0) {
            return new AVLTree<>(value,leftNode.insertFirst(element),rightNode);
        } else {
            return this;
        }
    }

    /**
     * Conducts a left rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> leftRotate() {
        AVLTree<T> newParent = this.rightNode;
        AVLTree<T> newRightOfCurrent = newParent.leftNode;
        AVLTree<T> newLeft = new AVLTree<>(value,leftNode,newRightOfCurrent);
        return new AVLTree<>(newParent.value,newLeft,newParent.rightNode);
    }
    /**
     * Conducts a right rotation on the current node.
     *
     * @return the new 'current' or 'top' node after rotation.
     */
    public AVLTree<T> rightRotate() {
        AVLTree<T> newParent = this.leftNode;
        AVLTree<T> newLeftOfCurrent = newParent.rightNode;
        AVLTree<T> newRight = new AVLTree<>(value,newLeftOfCurrent,rightNode);
        return new AVLTree<>(newParent.value,newParent.leftNode,newRight);
    }

    /**
     * Computes the height for AVL tree node.
     *
     * @return height
     */
    public int getHeight() {
        return 1 + Math.max(leftNode.getHeight(), rightNode.getHeight());
    }

    /**
     * Computes inOrder List representation of Tree
     *
     * @return inOrderList
     */
    public List<T> inOrder() {
        return this.treeToListInOrder(this);
    }

    /**
     * Converts tree to list in-order. Helper method of inOrder.
     * @param tree to convert to list.
     * @return in-order list of tree values.
     */
    private List<T> treeToListInOrder(AVLTree<T> tree) {
        List<T> list = new LinkedList<>();
        // Recurse through left subtree.
        if (tree.leftNode != null) {
            if (tree.leftNode.value != null) {
                list.addAll(treeToListInOrder(tree.leftNode));
            }
        }

        // Add current node's value
        if (tree.value != null) {list.add(tree.value);}

        // Recurse through left subtree.
        if (tree.rightNode != null) {
            if (tree.rightNode.value != null) {
                list.addAll(treeToListInOrder(tree.rightNode));
            }
        }
        return list;
    }

    /**
     *
     * @param limit
     * @return inOrder List of size Limit
     */
    public List<T> inOrderLimit(int limit) {
        return this.treeToListInOrder(this, limit); // Limit to the first 100 elements
    }

    /**
     * Converts tree to list in-order. Helper method of inOrder.
     * @param tree to convert to list.
     * @param limit maximum number of elements to traverse.
     * @return in-order list of tree values.
     */
    private List<T> treeToListInOrder(AVLTree<T> tree, int limit) {
        List<T> list = new LinkedList<>();
        treeToListInOrder(tree, list, limit);
        return list;
    }

    private int treeToListInOrder(AVLTree<T> tree, List<T> list, int limit) {
        if (tree == null || list.size() >= limit) {
            return limit; // Return immediately if limit is reached
        }

        // Recurse through left subtree.
        if (tree.leftNode != null) {
            if (tree.leftNode.value != null) {
                limit = treeToListInOrder(tree.leftNode, list, limit);
            }
        }
        // Add current node's value
        if (tree.value != null && list.size() < limit) {
            list.add(tree.value);
        }
        // Recurse through left subtree.
        if (tree.rightNode != null) {
            if (tree.rightNode.value != null) {
                limit = treeToListInOrder(tree.rightNode, list, limit);
            }
        }
        return limit;
    }

    // Stacks to hold predecessor and successor nodes for the kClosestValues method.
    private final Stack<AVLTree<T>> predecessors = new Stack<>();
    private final Stack<AVLTree<T>> successors = new Stack<>();

    /**
     * Returns the k values in the AVL tree closest to the given target value.
     *
     * @param target The value to find closest nodes to.
     * @param k The number of closest nodes to find.
     * @return A list containing the k values closest to the target.
     */
    public List<T> kClosestValues(T target, int k) {
        AVLTree<T> p  = this;
        AVLTree<T> s = this;
        initPredecessors(p, target);
        initSuccessors(s, target);

        List<T> result = new LinkedList<>();
        while (k-- > 0) {
            if (predecessors.isEmpty()) {
                result.add(getNextSuccessor());
            } else if (successors.isEmpty()) {
                result.add(getNextPredecessor());
            } else if (Math.abs(target.compareTo(predecessors.peek().value))
                    < Math.abs(target.compareTo(successors.peek().value))) {
                result.add(getNextPredecessor());
            } else {
                result.add(getNextSuccessor());
            }
        }
        return result;
    }

    /**
     * Initializes the predecessors stack for the kClosestValues method.
     *
     * @param node The current node.
     * @param target The value to find closest nodes to.
     */
    private void initPredecessors(AVLTree<T> node, T target) {
        while (node != null && node.value != null) {
            if (target.compareTo(node.value) < 0) {
                node = node.leftNode;
            } else {
                predecessors.push(node);
                node = node.rightNode;
            }
        }
    }

    /**
     * Initializes the successors stack for the kClosestValues method.
     *
     * @param node The current node.
     * @param target The value to find closest nodes to.
     */
    private void initSuccessors(AVLTree<T> node, T target) {
        while (node != null && node.value != null) {
            if (target.compareTo(node.value) >= 0) {
                node = node.rightNode;
            } else {
                successors.push(node);
                node = node.leftNode;
            }
        }
    }

    /**
     * Retrieves the next predecessor value from the predecessors stack.
     *
     * @return The next predecessor value.
     */
    private T getNextPredecessor() {
        AVLTree<T> popped = predecessors.pop();
        AVLTree<T> p = popped.leftNode;
        while (p != null && p.value != null) {
            predecessors.push(p);
            p = p.rightNode;
        }
        return popped.value;
    }

    /**
     * Retrieves the next successor value from the successors stack.
     *
     * @return The next successor value.
     */
    private T getNextSuccessor() {
        AVLTree<T> popped = successors.pop();
        AVLTree<T> p = popped.rightNode;
        while (p != null && p.value != null) {
            successors.push(p);
            p = p.leftNode;
        }
        return popped.value;
    }

    /**
     * A helper class to represent an empty AVL tree node.
     * Helps in simplifying operations and handling null values.
     */
    public static class EmptyAVL<T extends Comparable<T>> extends AVLTree<T> {
        public EmptyAVL() {}

        @Override
        public AVLTree<T> insertFirst(T element) {return new AVLTree<>(element);}
        @Override
        public int getHeight() {return -1;}
    }
}
