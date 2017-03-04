package manager;

/**http://torgen-engineering.blogspot.se/2016/05/how-to-bring-undoredo-features-to-your.html
 * Built on code from 
 */

import java.util.ArrayDeque;
import java.util.Deque;

public final class UndoManager {
	/*Creator*/
	public final static UndoManager INSTANCE = new UndoManager();
	
	private final Deque<UndoableInterface> undo;
	private final Deque<UndoableInterface> redo;
	private final int maxSize;
	
	private UndoManager(){
		super();
		undo = new ArrayDeque<>();
		redo = new ArrayDeque<>();
		maxSize = 30;
	}
	
	public void add(final UndoableInterface undoable){
		if(undoable!=null && maxSize>0){
			
		}
	}
}
