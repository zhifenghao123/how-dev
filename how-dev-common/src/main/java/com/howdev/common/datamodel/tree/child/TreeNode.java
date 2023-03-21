package com.howdev.common.datamodel.tree.child;

/**
 * TreeNode class
 *
 * 孩子表示法:
 * (1)每个结点记述自己的所有孩子。
 * (2)是一种基于链表的存储方法，即把每个结点的孩子排列起来，看成一个线性表，且以单链表存储，称为该结点的孩子链表。
 * (3)n个结点共有n个孩子链表（叶子结点的孩子链表为空表）
 * (4)n个链表共有n个头引用，这n个头引用又构成了一个线性表，为了便于查找，可以采用顺序存储。
 * (5)最后，将存放n个头引用的数组和存放n个结点数据信息的数组结合起来，构成孩子链表的表头数组。
 * 例如，树形数据如下:
 *                               A
 *                              /  \
 *                             /    \
 *                            B      C
 *                           / | \   / \
 *                          /  |  \ /   \
 *                          D  E  F G    H
 *                             |
 *                             I
 *   则存储数据如下：
 *   下标      data firstChild
 *   0          A      ---->  1  --->  2 ^
 *   1          B      ---->  1  --->  4  ---> 5 ^
 *   2          C      ---->  1  --->  7 ^
 *   3          D      ^
 *   4          E      ---->  8 ^
 *   5          F      ^
 *   6          G      ^
 *   7          H      ^
 *   8          I      ^
 *
 * @author haozhifeng
 * @date 2023/03/13
 */
public class TreeNode<T> {
    /**
     * 定义结点泛型数据域
     */
    private T data;
    /**
     * 孩子链表头引用
     */
    private ChildNode firstChild;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ChildNode getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(ChildNode firstChild) {
        this.firstChild = firstChild;
    }
}
