package com.howdev.common.datamodel.tree.parent;

/**
 * ParentNode class
 *
 * 双亲表示法:
 * (1)每个结点都记录自己的双亲，除根节点外，每个结点都有且仅有一个双亲结点。
 * (2)使用一维数组来存储树的各个结点（一般按层序存储），数组元素包括结点的数据信息、该结点的双亲在数组中的下标。
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
 *   下标      data     parent
 *   0          A       -1
 *   1          B       0
 *   2          C       0
 *   3          D       1
 *   4          E       1
 *   5          F       1
 *   6          G       2
 *   7          H       2
 *   8          I       4
 *
 *
 * @author haozhifeng
 * @date 2023/03/13
 */
public class ParentNode<T> {
    /**
     * 定义结点泛型数据域
     */
    private T data;
    /**
     * 定义结点双亲下标
     */
    private int parent;

    public ParentNode(T data, int parent) {
        this.data = data;
        this.parent = parent;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
