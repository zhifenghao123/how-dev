package com.howdev.common.datamodel.tree.childall;

import com.howdev.common.datamodel.tree.child.ChildNode;

import java.util.List;

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
     * 孩子链表
     */
    private List<TreeNode> childs;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<TreeNode> getChilds() {
        return childs;
    }

    public void setChilds(List<TreeNode> childs) {
        this.childs = childs;
    }
}
