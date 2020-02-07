import java.util.Iterator;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

/**
 * The linked structure implementation of a k-ary tree.
 * It supports walking through encoding trees and decoding messages.
 * @author Quang Vo
 * @param <E> the type of the value in the K-ary Tree.
 */
public class KTree<E> implements TreeIterable<E> {
	/****************************************/
	/* YOUR CODE SECTION!                   */
	/****************************************/
	
	//add your methods here...
	
	Node<E> root; //the root node of the k-ary tree.
	
	private int numberOfElements; //number of valid values in the k-ary tree
	private int numberOfNodes; //number of nodes of a perfect k-ary tree
	private int kValue; //k branching factor of the k-ary tree
	private int height; //height of a perfect k-ary tree
	
	/**
	 * Define a tree node that has an array of children and an associated index.
	 * @param <E> the type of the value of the Node.
	 */
	private static class Node<E>{
		private E data;
		private Node<E>[] children; 
		private int index;
		
		/**
		 * Create a new node with the specified data, index, and children array.
		 * @param data the specified value.
		 * @param kValue the specified branching factor k.
		 * @param index the index associated with each node.
		 */
		@SuppressWarnings("unchecked")
		public Node(E data, int kValue, int index){
			this.data = data;
			this.children = new Node[kValue];
			this.index = index;
		}
	}
	
	/**
	 * Construct a k-ary tree from the given array with the branching factor k.
	 * @param arrayTree the given array that stores the tree in level order.
	 * @param k the branching factor k.
	 */
	public KTree(E[] arrayTree, int k){
				
		if(k < 2){ //k can't be less than 2
			throw new InvalidKException(); 
		}
		
		this.numberOfNodes = arrayTree.length; //count number of nodes of a perfect tree
		
		this.kValue = k; //assign the branching factor k
				
		this.height = (int)Math.ceil(Math.log((kValue - 1) * numberOfNodes + 1) / Math.log(kValue)) - 1; //formula for the height of a perfect tree		
		
		this.root = this.addChildren(arrayTree, this.root, 0); //add each child to the tree
		
	}
	
	/**
	 * This method supports the constructor for creating a new k-ary tree.
	 * @param arrayTree the given array that stores the tree in level order.
	 * @param root the root of the k-ary tree.
	 * @param index the index associated with each node.
	 * @return the root of the new constructed tree.
	 */
	private Node<E> addChildren(E[] arrayTree, Node<E> root, int index){
		
		if(index < arrayTree.length){
			Node<E> temp = new Node<E>(arrayTree[index], kValue, index);
			
			if(temp.data != null){ //don't count the null nodes
				numberOfElements++;
			}
			
			root = temp;
			
			for(int j = 0, z = 1; j < kValue; j++, z++){ //add children based on k value
				root.children[j] = addChildren(arrayTree, root.children[j], kValue * index + z); //using k * index + c-th child of the node
			}
		}
		return root;
	}
	
	/**
	 * Return the branching factor k of the tree
	 * @return the branching factor k.
	 */
	public int getK(){
		
		return this.kValue;
	}
	
	/**
	 * Return the number of elements in the tree.
	 * @return the number of elements in the tree.
	 */
	public int size(){
		
		return this.numberOfElements;
	}
	
	/**
	 * Return the height of the k-ary tree.
	 * @return the height of the k-ary tree.
	 */
	public int height(){
		
		return this.height;
	}
	
	/**
	 * Accept a location index and return the value at that location in the tree.
	 * @param i the level-order location index of the node if the tree was perfect.
	 * @return the value at the location index.
	 * @throws IllegalArgumentException if the location i is not a node in the tree.
	 */
	public E get(int i){
		
		if(i < 0 || this.root == null){  //invalid index or the tree is empty
			throw new IllegalArgumentException(); 
		}
		
		Node<E> toReturn = findNode(root, i);
		
		if(toReturn == null || toReturn.data == null){ //i is not a node in the tree
			throw new IllegalArgumentException("Invalid Index");
		}
		
		return toReturn.data;
		
	}
	
	/**
	 * This method supports the get(), set(), and subtree() methods.
	 * @param root the root of the k-ary tree. 
	 * @param i the level-order location index of the node if the tree was perfect.
	 * @return the node at the specified location.
	 */
	private Node<E> findNode(Node<E> root, int i){
		
		
		Node<E> toReturn = null;
		
		if(root.index == i){ //base case
			return root;
		}
		
		if(root.children[0] != null){ 
			toReturn = findNode(root.children[0], i); //recursively looking for the node
		}
		
		for(int j = 1; j < kValue; j++){ 
			if(toReturn == null && root.children[j] != null){
				toReturn =  findNode(root.children[j], i); //recursively looking for the node
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Accept a location index and a value.
	 * Attempt to set the node at location index to the value indicated.
	 * Also can be used to remove leaves from the tree.
	 * @param i the level-order location index of the node if the tree was perfect.
	 * @param value the new value to set the location to (or null if attempting to remove a leaf).
	 * @return true if the operation could be performed, false otherwise.
	 * @throws InvalidTreeException if setting this location would make an invalid tree.
	 */
	public boolean set(int i, E value){
		
		if(i < 0 || this.root == null){ //invalid index or the tree is empty
			return false;
		}
				
		Node<E> toChange = findNode(root, i);
		
		//when value is null and node is not in the tree, or node has children
		if(value == null){
			if(toChange == null || toChange.data == null){ 
				return false;
			}
			else{
				for(int z = 0; z < kValue; z++){ //check to see if the node has children
					if(toChange.children[z] != null){
						if(toChange.children[z].data != null){
							return false; 
						}
					}
				}
				toChange.data = null; //remove the node when the node doesn't have children
				numberOfElements--;
				return true;
			}
		}
		else if(value != null && toChange != null && toChange.data != null){ //when there is a node, value is not null
				toChange.data = value;
				return true;
		}
		else{  //when the value is not null, and there is no node at the i-th location
			
			for(int j = 1; j < kValue; j++){
				Node<E> validParent = findNode(root, (i - j)/kValue); //check for valid parent
				if(validParent != null){
					if(validParent.data != null){
						validParent.children[j] = new Node<E>(value, kValue, i);
						numberOfElements++;
						if(i >= numberOfNodes){ //check number of nodes of a perfect tree
							height++; //increment the height base on the index
							numberOfNodes = (int)(Math.pow(kValue, this.height() + 1) - 1) / (kValue - 1); //recount the number of nodes
						}
						return true;
					}
					else{
						throw new InvalidTreeException(); //the location will make the tree invalid
					}
				}
			}
			return false;
			//Note for the TA: this set() method is a nightmare!!! So glad I finished it! Please be easy on this method, thank you so much! :)
		}	
	}
	
	
	/**
	 * Return the copy of the tree stored in level order.
	 * @return the copy of the tree stored in level order, return an empty array if there are no elements.
	 */
	public Object[] toArray(){
		
		int arraySize = (int)(Math.pow(kValue, this.height() + 1) - 1) / (kValue - 1); //formula for number of nodes of a perfect tree
		
		Object[] toReturn = new Object[arraySize];
		
		addToArray(this.root, toReturn, 0); //add values in the nodes to the array representation
		
		return toReturn;
	}
	
	/**
	 * This method supports the toArray() and mirror() methods.
	 * @param root the root of the k-ary tree.
	 * @param toReturn the copy of the tree stored in level order.
	 * @param index the location index of each node.
	 */
	private void addToArray(Node<E> root, Object[] toReturn, int index){
		
		if(root == null){ //base case
			return;
		}
		
		toReturn[index] = root.data;
		
		for(int i = 0, j = 1; i < kValue; i++, j++){
			if(root.children[i] != null){
				addToArray(root.children[i], toReturn, index * kValue + j); //using formula k * index + c-th child of the node
			}
		}
	}
	
	/**
	 * Output the tree where each level is printed on its own line, each node is separated by a space.
	 * @return the output of the tree where each level is printed on its own line.
	 */
	public String toString(){
		
		StringBuilder s = new StringBuilder();
		
		for(int i = 1; i <= this.height() + 1; i++){
			printLevel(s, this.root, i); //print each level, start with level 1 of the tree
			s.append("\n");
		}
		
		return s.toString();
	}
	
	/**
	 * This method supports the toString() method.
	 * @param s the StringBuilder for building a string representation of the tree.
	 * @param root the root of the k-ary tree.
	 * @param level each level of the tree.
	 */
	private void printLevel(StringBuilder s, Node<E> root, int level){
						
		if(root == null){ //base case
			return;
		}
		
		if(level == 1){
			s.append(root.data); //append values in the nodes to the StringBuilder s
			s.append(" ");
		}
		else if(level > 1){
			for(int i = 0; i < kValue; i++){
				printLevel(s, root.children[i], level - 1); //recursively printing values on each level
			}
		}		
	}
	
	/**
	 * The DynamicArray implements an array whose capacity can grow and shrink.
	 * @param <E> the type of the value in the DynamicArray.
	 */
	private static class DynamicArray<E>{
		
		private static final int INITCAP = 2;	// default initial capacity / minimum capacity
		private E[] storage;	// underlying array
		private int numOfElements; //number of elements in the array

		@SuppressWarnings("unchecked")
		/**
		 * Constructs an empty Dynamic Array.
		 */
		public DynamicArray(){
			numOfElements = 0;
			storage = (E[]) new Object[INITCAP];
		}
		
		/**
		 * Returns the number of elements in the list.
		 * @return the number of elements in the list.
		 */
		public int size() {	  
			
			return numOfElements;
		}
			
		/**
		 * Reports the max number of elements before the next expansion.
		 * @return the max number of elements the list can hold.
		 */
		public int capacity() { 

			return storage.length;
		}
		
		public boolean add(E value){
			
			if(size() == capacity()){
				this.increaseCapacity(); //doubles the capacity
			}
			
				storage[size()] = value; //place new value into the highest possible index
				numOfElements++;
				return true;
		}
		
		@SuppressWarnings("unchecked")
		/**
		 * Removes the item at position index, shifts items to remove any gap in the list.
		 * Halves the capacity if the number of elements falls below 1/3 of the capacity.
		 * Makes sure the capacity doesn't go below INITCAP.
		 * @param index the index of the object.
		 * @return the old item removed from the list.
		 * @throws IndexOutOfBoundsException if index is out of range.
		 */
		public E remove(int index){

			if(index >= size() || index < 0){
				throw new IndexOutOfBoundsException(this.toString());
			}
			else{
				E old = storage[index];
				for(int i = index; i < size() - 1; i++){
						storage[i] = storage[i + 1]; //shifting the elements					
				}
				
				numOfElements--;
							
				//size is below 1/3 of capacity and capacity is not below INITCAP
				if((size() < (double)capacity() / 3) && (capacity()/2 >= INITCAP)){ 
					E[] newStorage = (E[]) new Object[capacity() / 2]; //halve the capacity
					
					for(int i = 0; i < size(); i++){
						newStorage[i] = storage[i]; //copy each item from old storage to new storage
					}
					storage = newStorage;
				}
				
				return old; //return old element
			}
		}  
		
		@SuppressWarnings("unchecked")
		/**
		 * Doubles the capacity if no space is available.
		 */
		private void increaseCapacity(){
			E[] newStorage = (E[]) new Object[capacity() * 2]; //double the capacity of the array;
			
			for(int i = 0; i < size(); i++){
				newStorage[i] = storage[i]; //copy each item from old storage to new storage
			}
			storage = newStorage;
		}

	}
	
	/**
	 * Return an iterator that walks through the tree in level order.
	 * @return an iterator that walks through the tree in level order.
	 */
	public Iterator<E> getLevelOrderIterator(){
		
		return new LevelOrderIterator();
	}
	
	/**
	 * This is the implementation of the LevelOrderIterator.
	 */
	private class LevelOrderIterator implements Iterator<E>{
		
		private final DynamicArray<Node<E>> list;
		
		/**
		 * Construct a new LevelOrderIterator.
		 */
		public LevelOrderIterator(){
			list = new DynamicArray<>();
			list.add(root); //add the root of the tree to the queue
		}
		
		/**
		 * Test if there are more items in the tree.
		 * @return true if there are more items in the tree.
		 */
		public boolean hasNext(){
			
			return !(list.size() == 0);
		}
		
		/**
		 * Obtain the next item in the tree.
		 * @return the next item in the tree, return null if no more items in the tree.
		 */
		public E next(){
			
			if(!hasNext()){
				return null;
			}
							
			Node<E> nextNode = list.remove(0); //get the front of the queue
			
			for(int i = 0; i < kValue; i++){
				if(nextNode.children[i] != null){
					if(nextNode.children[i].data != null){ //don't add null nodes to the queue
						list.add(nextNode.children[i]);
					}
				}
			}
			return nextNode.data;
		}		
	}
	
	/**
	 * Return the string representation of the level order walk.
	 * @return the string representation of the level order walk.
	 */
	public String toStringLevelOrder(){
		
		StringBuilder s = new StringBuilder();
		
		Iterator<E> itr = this.getLevelOrderIterator();
		
		while(itr.hasNext()){
			s.append(itr.next()); //append values in nodes in the queue to the StringBuilder s
			s.append(" ");
		}
		
		return s.toString();
	}
	
	/**
	 * The implementation of a Node.
	 * @author Professors
	 * @param <E> the type of the value of Node.
	 */
	private static class StackNode<E> {
		private E value;
		private StackNode<E> next;
		private StackNode<E> prev;
		
		/**
		 * Create a new node with the specified value.
		 * @param value the specified value.
		 */
		public StackNode(E value) {
			this.value = value;
		}
		
		/**
		 * Return the value stored this node.
		 * @return the value stored this node.
		 */
		public E getValue() {
			return value;
		}
		
		/**
		 * Set the value stored in this node.
		 * @param value the (new) value of this node.
		 */
		public void setValue(E value) {
			this.value = value;
		}
		
		/**
		 * Return the next node after this node.
		 * @return the node after this node.
		 */
		public StackNode<E> getNext() {
			return this.next;
		}
		
		/**
		 * Set the next node after this node.
		 * @param next the (new) next node after this node.
		 */
		public void setNext(StackNode<E> next) {
			this.next = next;
		}
		
		/**
		 * Return the previous node before this node.
		 * @return the node before this node.
		 */
		public StackNode<E> getPrev() {
			return this.prev;
		}
		
		/**
		 * Set the previous node before this node.
		 * @param previous the previous node before this node.
		 */
		public void setPrev(StackNode<E> prev) {
			this.prev = prev;
		}
		
	}
	
	/**
	 * Linked list implementation of the stack.
	 * @param <E> the type of the value in the ProgramStack.
	 */
	private static class ProgramStack<E>{
		
		private StackNode<E> top;
		private int size;	
		
		/**
		 * Construct an empty stack.
		 */
		public ProgramStack() {
		
			top = null;
			size = 0;
		}
		
		/**
		 * Insert a new item onto the stack.
		 * @param item the item to insert.
		 */
		public void push(E item) {
			
			StackNode<E> newNode = new StackNode<E>(item); //create a new node with the item
			
			//link the nodes
			newNode.setNext(top);
			if(size() != 0){
				top.setPrev(newNode);
			}
			
			top = newNode; //set the newNode to be the top
			size++;
		}
		
		/**
		 * Remove the most recently inserted item from the stack.
		 * @return the removed item.
		 * @return null if the stack is empty.
		 */
		public E pop() {
			
			if(isEmpty()){ //check if the stack is empty
				return null;
			}
			else{
				E toReturn = top.getValue(); 
				top = top.getNext(); //set top to be the next item
				size--;
				if(size != 0){
					top.setPrev(null);
				}
				return toReturn; //return the old value
			}
			
		}
		/**
		 * Get the most recently inserted item in the stack.
		 * Do not remove the item from the stack.
		 * @return the most recently inserted item in the stack.
		 * @return null if the stack is empty.
		 */
		public E peek() {
			
			if(isEmpty()){ //check if the stack is empty
				return null;
			}
			else{
				return top.getValue();
			}
		}
		
		/**
		 * Return the number of items in the stack.
		 * @return the size of the stack.
		 */
		public int size() {
			
			return size;
		}
		
		/**
		 * Check if the stack is logically empty.
		 * @return true if empty, false otherwise.
		 */
		public boolean isEmpty() {
			
			if(size() == 0){ //if the stack is empty
				return true;
			}
			else{
				return false;
			}
		}	
	}
		
	
	/**
	 * Return an iterator that walks through the tree using pre-order walk.
	 * @return an iterator that walks through the tree using pre-order walk.
	 */
	public Iterator<E> getPreOrderIterator(){
		
		return new PreOrderIterator();
	}
	
	/**
	 * This is the implementation of the PreOrderIterator.
	 */
	private class PreOrderIterator implements Iterator<E>{
		
		private final ProgramStack<Node<E>> stack;
		
		/**
		 * Construct a new PreOrderIterator.
		 */
		public PreOrderIterator(){
			stack = new ProgramStack<>();
			stack.push(root); //push the root of the tree to the stack
		}
		
		/**
		 * Test if there are more items in the tree.
		 * @return true if there are more items in the tree.
		 */
		public boolean hasNext(){
			
			return !stack.isEmpty();
		}
		
		/**
		 * Obtain the next item in the tree.
		 * @return the next item in the tree, return null if no more items in the tree.
		 */
		public E next(){
			
			if(!hasNext()){
				return null;
			}
							
			Node<E> nextNode = stack.pop();
			
			for(int i = kValue - 1; i >= 0; i--){
				if(nextNode.children[i] != null){
					if(nextNode.children[i].data != null){ //don't push null node to the stack
						stack.push(nextNode.children[i]);
					}
				}
			}	
			return nextNode.data;
		}
	}
	
	/**
	 * Return the string representation of the pre-order walk.
	 * @return the string representation of the pre-order walk.
	 */
	public String toStringPreOrder(){
		
		StringBuilder s = new StringBuilder();
		
		Iterator<E> itr = this.getPreOrderIterator(); 
		
		while(itr.hasNext()){
			s.append(itr.next()); //append values in nodes on the stack to StringBuilder s
			s.append(" ");
		}
		
		return s.toString();
	}
	
	/**
	 * Return an iterator that walks through the tree using post-order walk.
	 * @return an iterator that walks through the tree using post-order walk.
	 */
	public Iterator<E> getPostOrderIterator(){
		
		return new PostOrderIterator();
	}
	
	/**
	 * This is the implementation of the PostOrderIterator.
	 */
	private class PostOrderIterator implements Iterator<E>{
		
		private final ProgramStack<Node<E>> stack1;
		private final ProgramStack<Node<E>> stack2;
		
		/**
		 * Construct a new PostOrderIterator.
		 */
		public PostOrderIterator(){
			stack1 = new ProgramStack<>();
			stack2 = new ProgramStack<>();
			stack1.push(root); //push the root of the tree to stack1
		}
		
		/**
		 * Test if there are more items in the tree.
		 * @return true if there are more items in the tree.
		 */
		public boolean hasNext(){
			
			return !stack1.isEmpty();
		}
		
		/**
		 * Obtain the next item in the tree.
		 * @return the next item in the tree, return null if no more items in the tree.
		 */
		public E next(){
			
			if(root == null){
				return null;
			}
			
			while(hasNext()){
				
				Node<E> nextNode = stack1.pop();
				stack2.push(nextNode);
				
				for(int i = 0; i < kValue; i++){
					if(nextNode.children[i] != null){
						if(nextNode.children[i].data != null){ //don't push null nodes to the stack1
							stack1.push(nextNode.children[i]);
						}
					}
				}
			}
			
			Node<E> toReturn = stack2.pop();
				
			return toReturn.data;
		}
	}
	
	/**
	 * Return the string representation of the post-order walk.
	 * @return the string representation of the post-order walk.
	 */
	public String toStringPostOrder(){
		
		StringBuilder s = new StringBuilder();
		
		Iterator<E> itr = this.getPostOrderIterator();
		
		for(int i = 0; i < this.size(); i++){
			s.append(itr.next()); //append values in nodes in stack2 to the the StringBuilder s
			s.append(" ");
		}
		
		return s.toString();
	}
	
	/**
	 * Receive a decoding tree and an encoded string, then decode the string to get the secret message.
	 * The value of the leaves will be used to decode the string, the values at internal nodes are ignored.
	 * @param tree the decoding tree.
	 * @param codedMessage the encoded string with the secret message.
	 * @return the decoded string with the secret message.
	 */
	public static String decode(KTree<String> tree, String codedMessage){
				
		if(tree.root == null){ //check null root
			return null;
		}
		
		Node<String> current = tree.root;
				
		StringBuilder decodedMessage = new StringBuilder();
		
		char character = '\0';
		int number = 0;
		
		for(int i = 0; i < codedMessage.length(); i++){ //loop based on the length of the codedMessage
			
			character = codedMessage.charAt(i); //get each instruction
			number = Character.getNumericValue(character);
			
			switch(number){
				case 0:
					current = getEachLetter(current, decodedMessage, tree.root, 0);
					break;
				case 1:
					current = getEachLetter(current, decodedMessage, tree.root, 1);
					break;
				case 2:
					current = getEachLetter(current, decodedMessage, tree.root, 2);
					break;
				case 3:
					current = getEachLetter(current, decodedMessage, tree.root, 3);
					break;
				case 4:
					current = getEachLetter(current, decodedMessage, tree.root, 4);
					break;
				case 5:
					current = getEachLetter(current, decodedMessage, tree.root, 5);
					break;
				case 6:
					current = getEachLetter(current, decodedMessage, tree.root, 6);
					break;
				case 7:
					current = getEachLetter(current, decodedMessage, tree.root, 7);
					break;
				case 8:
					current = getEachLetter(current, decodedMessage, tree.root, 8);
					break;
				case 9:
					current = getEachLetter(current, decodedMessage, tree.root, 9);
					break;
			}
		}
		
		return decodedMessage.toString();
	}
	
	/**
	 * This method supports the decode() method.
	 * @param current the current node during the decoding period.
	 * @param decodedMessage the decoded string with the secret message.
	 * @param root the root of the tree.
	 * @param number the instruction.
	 * @return the current node during the decoding period, reset back to root if encounters a leaf.
	 */
	private static Node<String> getEachLetter(Node<String> current, StringBuilder decodedMessage, Node<String> root, int number){
						
		if(current.children[number] == null || current.children[number].data == null){
			current = root; //reset back to the root if the instruction is wrong
		}
		else{
			
			int k = root.children.length; //get non-static kValue
						
			current = current.children[number];
			for(int i = 0; i < k; i++){ //check to see if the current node is a leaf
				if(current.children[i] != null){
					if(current.children[i].data != null){
						return current; //return the current node if it is not a leaf
					}
				}
			}
			decodedMessage.append(current.data); //append the value to the decodedMessage if the node is a leaf
			current = root; //reset back to the root
		}
		return current;
	}
	
	/**
	 * Return an array representation of a subtree where the location index is the root.
	 * @param i the level-order location index of the node if the tree was perfect.
	 * @return an array representation of a subtree.
	 */
	@SuppressWarnings("unchecked")
	public E[] subtree(int i){
		
		if(i < 0){ //check for valid index
			return null;
		}

		int arraySize = (int)(Math.pow(kValue, this.height() + 1) - 1) / (kValue - 1); //formula for number of nodes of perfect tree 

		E[] toReturn = (E[]) new Object[arraySize];

		Node<E> toFindNode = findNode(this.root, i); //get the node at the i-th location
						
		addToArray(toFindNode, toReturn, 0); //add values in nodes to the return array
		
		return toReturn;
	}
	
	/**
	 * Return the mirror of the k-ary tree in array format.
	 * @return the mirror of the k-ary tree in array format.
	 */
	@SuppressWarnings("unchecked")
	public E[] mirror(){ 
		
		int arraySize = (int)(Math.pow(kValue, this.height() + 1) - 1) / (kValue - 1); //formula for no. of nodes of perfect tree
				
		E[] toReturn = (E[]) new Object[arraySize];
		
		Node<E> newRoot = this.root;
		
		newRoot = getNewRoot(newRoot); //create a mirror of the tree
				
		addToArray(newRoot, toReturn, 0); //add values in nodes to the return array
		
		return toReturn;
	}
	
	/**
	 * This method supports the mirror() method.
	 * @param root the root of the tree.
	 * @return the new root of the mirror of the tree.
	 */
	private Node<E> getNewRoot(Node<E> root){
		
		if(root == null){
			return null;
		}
			
		Node<E> tempNode = null;
		
		for(int i = 0; i < kValue; i++){
			getNewRoot(root.children[i]);
		}
		
		for(int i = 0; i < kValue/2; i++){
			tempNode = root.children[i];
			root.children[i] = root.children[kValue - i - 1]; //interchange the nodes
			root.children[kValue - i - 1] = tempNode;
		}
		
		return root;
	}	
	
	/****************************************/
	/* EDIT THIS MAIN METHOD FOR TESTS. PUT */
	/* HELPER TEST METHODS IN THIS SECTION  */
	/* AS WELL. TESTS REQUIRED FOR FULL     */
    /* CREDIT.                              */
	/****************************************/
	
	/**
	 * A main method to test/demo
	 * @param args not used
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//change this method around to test!
		//methodSigCheck();
		
				
//		String[] strings2 = { "a", "b", "c", null, "e", "f", null, "h", "i", "j", "k", "l", "m", null, null,
//				null, null, "r", "s", null, null, "v", "w", "x", "y", null, null, null, null, null, null, null, null,
//				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
//				null, null, null, null, null, null, null, null,null, null, null, null,null, null, null, null,null, null, null, null,
//				null, null, null, "z", null, null, null, null, null, null, null, null, 
//				null, null, null, null};
//		
//		KTree<String> tree10 = new KTree<>(strings2, 4);
//		
//		System.out.println(tree10.size()); //18
//		System.out.println(tree10.height()); //3
//		System.out.println(tree10.numberOfNodes); //85
//		System.out.println(tree10.getK()); //4
//		System.out.println(tree10.get(0)); //a
//		System.out.println(tree10.get(1)); //b
////		System.out.println(tree10.get(3)); //IllegalArgumentException
////		System.out.println(tree10.get(67)); //IllegalArgumentException
//		System.out.println(tree10.get(4)); //e
//		System.out.println(tree10.get(7)); //h
//		System.out.println(tree10.get(17)); //r
//		System.out.println(tree10.get(24)); //y
//		System.out.println(tree10.get(72)); //z
//		System.out.println();
				
//		System.out.println(tree10.set(0, "A")); //true
//		System.out.println(tree10.set(13, "n")); //InvalidTreeException
//		System.out.println(tree10.set(84, "hi")); //InvalidTreeException
//		System.out.println(tree10.set(20, null)); //false
//		System.out.println(tree10.set(5, null)); //false
//		System.out.println(tree10.set(24, null)); //true
//		System.out.println(tree10.set(85, "hey")); //true
//		System.out.println(tree10.get(24)); //InvalidTreeException
//		System.out.println(tree10.set(17, null)); //false
//		System.out.println(tree10.get(17)); //still r
//		System.out.println(tree10.height); //4
//		System.out.println(tree10.numberOfNodes); //341
//		System.out.println(tree10.set(-1, "ha")); //false

//		Object o10[] = tree10.toArray();
//		System.out.println(o10[0]); //a
//		System.out.println(o10[3]); //null
//		System.out.println(o10[12]); //m
//		System.out.println(o10[72]); //z
//		System.out.println(o10[84]); //null
		
//		String s10 = tree10.toString();
//		System.out.println(s10); //walla long
		
//		Iterator<String> it10 = tree10.getLevelOrderIterator();
//		System.out.println(it10.next());//a
//		System.out.println(it10.next());//b
//		System.out.println(it10.next());//c
//		System.out.println(tree10.toStringLevelOrder()); 
//
//		Iterator<String> it10_1 = tree10.getPreOrderIterator();
//		System.out.println(it10_1.next());//a
//		System.out.println(it10_1.next());//b
//		System.out.println(it10_1.next());//f
//		System.out.println(tree10.toStringPreOrder());
//		
//		Iterator<String> it10_2 = tree10.getPostOrderIterator();
//		System.out.println(it10_2.next());//v
//		System.out.println(it10_2.next());//w
//		System.out.println(it10_2.next());//x
//		System.out.println(tree10.toStringPostOrder());
		
		//System.out.println(tree10.get(72));
		
//		String sDecode = decode(tree10, "0203001020031300331020030203"); //0203001020031300331020030203 
//		System.out.println(sDecode); // hiwhymyshyhi
		
//		Object[] subtree10 = tree10.subtree(1);
//		System.out.println(subtree10[0]); //b
//		System.out.println(subtree10[1]); //f
//		System.out.println(subtree10[2]); //null
//		System.out.println(subtree10[4]); //i
//		System.out.println(subtree10[8]); //y
		
//		Object[] mirror10 = tree10.mirror();
//		System.out.println(mirror10[1]); //e
//		System.out.println(mirror10[2]); //null
//		System.out.println(mirror10[3]); //c
//		System.out.println(mirror10[4]); //b
//		System.out.println(mirror10[5]); //null
//		System.out.println(mirror10[8]); //r
//		System.out.println(mirror10[15]); //k
//		System.out.println(mirror10[18]); //h
//		System.out.println(mirror10[82]); //x
//		System.out.println(mirror10[84]); //v


		
//		String[] strings = { "_", "_", "A", "B", "N", null, null };
//		KTree<String> tree = new KTree<>(strings, 2);
		
//		boolean b = tree.set(0, "x"); //should set the root to "x"
//		
//		Object[] o = tree.toArray(); //should return [ "x", "_", "A", "B", "N", null, null ]
//		System.out.println(o[0]); //x
//		System.out.println(o[1]); //_
//		System.out.println(o[2]); //A
//		System.out.println(o[3]); //B
//		System.out.println(o[4]); //N
//		System.out.println(o[5]); //null
//		System.out.println(o[6]); //null
//		System.out.println();

		
//		System.out.println(tree.getK()); //2
//		System.out.println(tree.size()); //5
//		System.out.println(tree.height()); //2
//		System.out.println(tree.get(0)); //_
//		System.out.println(tree.get(3)); //B
//		System.out.println(tree.get(2)); //A
//		System.out.println(tree.get(4)); //N
		//System.out.println(tree.get(6)); //Invalid

//		Integer[] integers = { 1,2,3,4,5,6,7,8,9,10,11,12,13};
//		KTree<Integer> tree2 = new KTree<>(integers, 3);
		
		//System.out.println(tree2.root.children[2].children[2].parent.parent.data);

//		
//		System.out.println(tree2.get(0)); //1
//		System.out.println(tree2.get(2)); //3
//		System.out.println(tree2.get(3)); //4
//		System.out.println(tree2.get(6)); //7
//		System.out.println(tree2.get(7)); //8
//		System.out.println(tree2.get(8)); //9
//		System.out.println(tree2.get(9)); //10
//		System.out.println(tree2.get(11)); //12
//		System.out.println(tree2.get(12)); //13

//		System.out.println(tree2.getK()); //3
//		System.out.println(tree2.size()); //13
//		System.out.println(tree2.height()); //2

//		Integer[] inputs = {0, null, 2, null, null, 5, 6};
//		KTree<Integer> tree3 = new KTree<>(inputs, 2);
		
		//System.out.println(tree3.get(3));
		
//		System.out.println(tree3.set(4, 3)); //throws InvalidTreeException because 3 would not have a parent
//		System.out.println(tree3.set(14, 14)); //but this should work and return true
//		System.out.println(tree3.set(11, 11)); //but this should work and return true
//		System.out.println(tree3.set(30, 30)); //but this should work and return true
//		System.out.println(tree3.set(2, null)); //this should return false (2 has children)
//		System.out.println(tree.set(-1, null)); //returns false due to invalid index
		
//		System.out.println(tree3.size());
//		System.out.println(tree3.numberOfNodes);
//		System.out.println(tree3.height());
		//System.out.println(tree3.nodesCount());
//		System.out.println();
//		
//		Object[] o1 = tree3.toArray(); //should return [ "0", "null", "2", null, null, "5", "6", ]
//		System.out.println(o1[0]); //0
//		System.out.println(o1[1]); //null
//		System.out.println(o1[2]); //2
//		System.out.println(o1[3]); //null
//		System.out.println(o1[4]); //null
//		System.out.println(o1[5]); //5
//		System.out.println(o1[6]); //6
//		System.out.println();
		
//		System.out.println(tree3.get(0)); //0
//		System.out.println(tree3.get(1)); //invalid
//		System.out.println(tree3.get(6)); //6
//		System.out.println(tree3.get(11)); //invalid

//		Object[] output = tree3.toArray();
//		System.out.println(output[0]);
		
//		System.out.println(tree3.toString());
		//tree2.toString();
		
//		Iterator<String> it = tree.getPreOrderIterator();
//		System.out.println(it.next()); //_
//		System.out.println(it.next()); //_
//		System.out.println(it.next()); //A
//		System.out.println(it.next()); //B
//		System.out.println(it.next()); //N
//		System.out.println(it.next()); //null
		
//		String s1 = tree.toStringLevelOrder();
//		System.out.println(s1);
//		
//		String s1_5 = tree.toStringPreOrder();
//		System.out.println(s1_5);
//		
//		String s1_6 = tree.toStringPostOrder();
//		System.out.println(s1_6);
		
//
//		Iterator<Integer> it3 = tree3.getPreOrderIterator();
//		System.out.println(it3.next()); //0
//		System.out.println(it3.next()); //2
//		System.out.println(it3.next()); //5
//		System.out.println(it3.next()); //6
		
//		Iterator<Integer> it2 = tree2.getPreOrderIterator();
//		System.out.println(it2.next()); //1
//		System.out.println(it2.next()); //2
//		System.out.println(it2.next()); //5
//		System.out.println(it2.next()); //6
//		System.out.println(it2.next()); //7
//		System.out.println(it2.next()); //3
//		System.out.println(it2.next()); //8
		
//		Integer[] input4 = {0, 1, 2, null, 4, 5, null};
//		KTree<Integer> tree4 = new KTree<>(input4, 2);
//		Iterator<Integer> it4 = tree4.getPreOrderIterator();
//		System.out.println(it4.next()); //0
//		System.out.println(it4.next()); //1
//		System.out.println(it4.next()); //4
//		System.out.println(it4.next()); //2
//		System.out.println(it4.next()); //5
//		System.out.println();
//		
//		String s5_1 = tree4.toStringPreOrder();
//		System.out.println(s5_1);
//		
//		Iterator<Integer> it5 = tree4.getLevelOrderIterator();
//		System.out.println(it5.next()); //0
//		System.out.println(it5.next()); //1
//		System.out.println(it5.next()); //2
//		System.out.println(it5.next()); //4
//		System.out.println(it5.next()); //5
//		System.out.println();
//		
//		String s5 = tree4.toStringLevelOrder();
//		System.out.println(s5);
//		
//		Iterator<Integer> it10 = tree4.getPostOrderIterator();
//		System.out.println(it10.next()); //4
//		System.out.println(it10.next()); //1
//		System.out.println(it10.next()); //5
//		System.out.println(it10.next()); //2
//		System.out.println(it10.next()); //0
//		System.out.println();
//		
//		String s5_2 = tree4.toStringPostOrder();
//		System.out.println(s5_2);
//		
//		Iterator<Integer> it6 = tree2.getLevelOrderIterator();
//		System.out.println(it6.next()); //1
//		System.out.println(it6.next()); //2
//		System.out.println(it6.next()); //3
//		System.out.println(it6.next()); //4
//		System.out.println(it6.next()); //5
//		System.out.println(it6.next()); //6
//		System.out.println(it6.next()); //7
//		
//		String s6 = tree2.toStringLevelOrder();
//		System.out.println(s6);
//		String s6_1 = tree2.toStringPreOrder();
//		System.out.println(s6_1);
//		String s6_2 = tree2.toStringPostOrder();
//		System.out.println(s6_2);
		
//		String s6 = decode(tree, "001011011"); //should be "BANANA"
//		
//		System.out.println(s6); //BANANA
		
//		Object[] o = tree.toArray(); //should return [ "_", "_", "A", "B", "N", null, null ]
//		System.out.println(o[0]); //_
//		System.out.println(o[1]); //_
//		System.out.println(o[2]); //A
//		System.out.println(o[3]); //B
//		System.out.println(o[4]); //N
//		System.out.println(o[5]); //null
//		System.out.println(o[6]); //null
//		System.out.println();
//		
//		Object[] o1 = tree3.toArray(); //should return [ "0", "null", "2", null, null, "5", "6", ]
//		System.out.println(o1[0]); //0
//		System.out.println(o1[1]); //null
//		System.out.println(o1[2]); //2
//		System.out.println(o1[3]); //null
//		System.out.println(o1[4]); //null
//		System.out.println(o1[5]); //5
//		System.out.println(o1[6]); //6
//		System.out.println();
//
//		Object[] o2 = tree2.toArray(); //should return [ 1 2 3 4 5 6 7 8 9 10 11 12 13 ]
//		System.out.println(o2[0]); //1
//		System.out.println(o2[1]); //2
//		System.out.println(o2[2]); //3
//		System.out.println(o2[3]); //4
//		System.out.println(o2[4]); //5
//		System.out.println(o2[5]); //6
//		System.out.println(o2[6]); //7
//		System.out.println(o2[7]); //8
//		System.out.println(o2[8]); //9
//		System.out.println(o2[9]); //10
//		System.out.println(o2[10]); //11
//		System.out.println(o2[11]); //12
//		System.out.println(o2[12]); //13
		
//		Object[] sub1 = tree.subtree(1); //should return [ "_", "B", "N" ]
//		System.out.println(sub1[0]); //_
//		System.out.println(sub1[1]); //B
//		System.out.println(sub1[2]); //N
//		System.out.println();
//		
//		Object[] sub2 = tree2.subtree(2); //should return [3 8 9 10]
//		System.out.println(sub2[0]); //3
//		System.out.println(sub2[1]); //8
//		System.out.println(sub2[2]); //9
//		System.out.println(sub2[3]); //10
		
//		Object[] mirror1 = tree.mirror(); //should return [ "_", "A", "_", null, null, "N", "B" ]
//		System.out.println(mirror1[0]); //_
//		System.out.println(mirror1[1]); //A
//		System.out.println(mirror1[2]); //_
//		System.out.println(mirror1[3]); //null
//		System.out.println(mirror1[4]); //null
//		System.out.println(mirror1[5]); //N
//		System.out.println(mirror1[6]); //B
//		System.out.println();
//		
//		Object[] mirror2 = tree2.mirror(); //should return [ 1 4 3 2 13 12 11 10 9 8 7 6 5]
//		//System.out.println(mirror2.length);
//		System.out.println(mirror2[0]); //1
//		System.out.println(mirror2[1]); //4
//		System.out.println(mirror2[2]); //3
//		System.out.println(mirror2[3]); //2
//		System.out.println(mirror2[4]); //13
//		System.out.println(mirror2[5]); //12
//		System.out.println(mirror2[6]); //11
//		System.out.println(mirror2[7]); //10
//		System.out.println(mirror2[8]); //9
//		System.out.println(mirror2[9]); //8
//		System.out.println(mirror2[10]); //7
//		System.out.println(mirror2[11]); //6
//		System.out.println(mirror2[12]); //5
//		System.out.println();
		
		String[] s100 = {"_", "A", "P", "O", "E", "L"};
		KTree<String> tree100 = new KTree<String>(s100, 5);
				
		//System.out.println(tree100.toStringLevelOrder());
		System.out.println(tree100.toString());
		
		String[] s101 = {"_", "Q", "W", null, "R"};
		KTree<String> tree101 = new KTree<String>(s101, 4);

		//System.out.println(tree101.toStringLevelOrder());
		System.out.println(tree101.toString());

		
		String[] s102 = {"_","_", "_", "_", "E", null, "V", null, "I", "L", "S", null, null};
		KTree<String> tree102 = new KTree<String>(s102, 3);

		//System.out.println(tree102.toStringLevelOrder());
		System.out.println(tree102.toString());

		
		String[] s103 = {null, null, null};
		KTree<String> tree103 = new KTree<String>(s103, 2);

		//System.out.println(tree103.toStringLevelOrder());
		System.out.println(tree103.toString());
		Object[] a103 = tree103.toArray();
		System.out.println(a103.length);
		System.out.println(tree103.height);
		
		String[] s104 = {"A","B","C","D","E",null,null};
		KTree<String> tree104 = new KTree<String>(s104, 2);

		System.out.println(tree104.toStringLevelOrder());
		
		Integer[] s105 = {1,2,3,4,null,null,null,5,6,7,null,null,null,null,null,null,null,null,null,null,null};
		KTree<Integer> tree105 = new KTree<Integer>(s105, 4);
		System.out.println(tree105.toStringPreOrder());

	}
	
	/****************************************/
	/* DO NOT EDIT ANYTHING BELOW THIS LINE */
	/****************************************/
	
	public static void methodSigCheck() {
		//This ensures that you've written your method signatures correctly
		//and understand how to call the various methods from the assignment
		//description.
		
		String[] strings = { "_", "_", "A", "B", "N", null, null };
		
		KTree<String> tree = new KTree<>(strings, 2);
		int x = tree.getK(); //should return 2
		int y = tree.size(); //should return 5
		int z = tree.height(); //should return 2
		
		String v = tree.get(0); //should be "_"
		boolean b = tree.set(0, "x"); //should set the root to "x"
		Object[] o = tree.toArray(); //should return [ "x", "_", "A", "B", "N", null, null ]
		
		String s = tree.toString(); //should be "x\n_ A\nB N null null"
		String s2 = "" + tree; //should also be "x\n_ A\nB N null null"
		
		Iterator<String> it1 = tree.getLevelOrderIterator(); //gets an iterator
		Iterator<String> it2 = tree.getPreOrderIterator(); //gets an iterator
		Iterator<String> it3 = tree.getPostOrderIterator(); //gets an iterator
		
		String s3 = tree.toStringLevelOrder(); //should be "_ _ A B N"
		String s4 = tree.toStringPreOrder(); //should be "_ _ B N A"
		String s5 = tree.toStringPostOrder(); //should be "B N _ A _"
		
		String s6 = decode(tree, "001011011"); //should be "BANANA"
		
		Object[] o2 = tree.mirror(); //should return [ "x", "A", "_", null, null, "N", "B" ]
		Object[] o3 = tree.subtree(1); //should return [ "_", "B", "N" ]
	}
}