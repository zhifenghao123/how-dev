package com.howdev.common.datamodel.tree.parentnotation;

/**
 * ParentNode class
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
