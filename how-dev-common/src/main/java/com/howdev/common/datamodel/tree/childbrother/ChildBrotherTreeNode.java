package com.howdev.common.datamodel.tree.childbrother;

import java.util.List;

/**
 * ChildBrotherTreeNode class
 *
 * 孩子兄弟表示法:
 * (1)每个结点记录自己的长子和右兄弟。又称为二叉链表表示法
 * (2)除了每个结点的数据域之外，设置两个引用域分别引用该结点的第一个孩子和右兄弟。
 *  <firstChild，data，rightSideBrother> ，firstChild存储第一个孩子引用，rightSideBrother存储右兄弟引用
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
 *       根引用
 *         |
 *         A  ^
 *      |
 *      B  ---------------------->  C  ^
 *    |                            |
 *  ^ D -->   E -->  ^ F ^         ^ G ----> ^ H ^
 *         |
 *         ^ I ^
 *
 * @author haozhifeng
 * @date 2023/03/13
 */
public class ChildBrotherTreeNode<T> {
    /**
     * 定义节点泛型数据域
     */
    private T data;
    /**
     * 引用长子
     */
    private ChildBrotherTreeNode firstChild;
    /**
     * 引用右兄弟
     */
    private ChildBrotherTreeNode rightSideBrother;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ChildBrotherTreeNode getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(ChildBrotherTreeNode firstChild) {
        this.firstChild = firstChild;
    }

    public ChildBrotherTreeNode getRightSideBrother() {
        return rightSideBrother;
    }

    public void setRightSideBrother(ChildBrotherTreeNode rightSideBrother) {
        this.rightSideBrother = rightSideBrother;
    }
}
