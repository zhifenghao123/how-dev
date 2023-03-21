package com.howdev.common.datamodel.tree.childnotation;

/**
 * TreeNode class
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
