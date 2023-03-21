package com.howdev.common.datamodel.tree;

import com.howdev.common.datamodel.tree.parent.ParentNode;

import java.util.ArrayList;
import java.util.List;

/**
 * TreeTest class
 *
 * @author haozhifeng
 * @date 2023/03/13
 */
public class TreeTest {
    public static void main(String[] args) {
        /**
         *                               A
         *                              /  \
         *                             /    \
         *                            B      C
         *                           / | \   / \
         *                          /  |  \ /   \
         *                          D  E  F G    H
         *                             |
         *                             I
         */
    }

    public void treeMethod1(){
        List<ParentNode<String>> treeNodes = new ArrayList<>();
        treeNodes.add(new ParentNode("A",-1));
        treeNodes.add(new ParentNode("B",0));
        treeNodes.add(new ParentNode("C",0));
        treeNodes.add(new ParentNode("D",1));
        treeNodes.add(new ParentNode("E",1));
        treeNodes.add(new ParentNode("F",1));
        treeNodes.add(new ParentNode("G",2));
        treeNodes.add(new ParentNode("H",2));
        treeNodes.add(new ParentNode("I",4));

    }

    public void treeMethod2(){

    }

    public void treeMethod3(){

    }
}
