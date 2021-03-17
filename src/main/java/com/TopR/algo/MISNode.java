package com.TopR.algo;

import java.util.ArrayList;
import java.util.List;

/**
 * @see AlgoCFPGrowth
 * @see MISTree
 */
public class MISNode {
	int itemID = -1;  // item represented by this node
	int counter = 1;  // frequency counter
	
	MISNode parent = null;
	
	List<MISNode> childs = new ArrayList<MISNode>();
	
	MISNode nodeLink = null;
	
	MISNode(){
		
	}

	MISNode getChildWithID(int id) {
		// for each child
		for(MISNode child : childs){
			// if the id is found, return the node
			if(child.itemID == id){
				return child;
			}
		}
		return null; // if not found, return null
	}
	
	/**
	 * Return the index of the immmediate child of this node having a given ID.
	 * If there is no such child, return -1;
	 */
	int getChildIndexWithID(int id) {
		int i=0;
		// for each child
		for(MISNode child : childs){
			// if the id is found, return the index
			if(child.itemID == id){
				return i;
			}
			i++;
		}
		return -1; // if not found, return -1
	}
}
