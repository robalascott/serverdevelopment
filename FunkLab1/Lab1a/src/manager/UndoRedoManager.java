package manager;

import java.util.ArrayDeque;
import java.util.Deque;


public final class UndoRedoManager{
	public static final UndoRedoManager INSTANCE = new UndoRedoManager();
	private final Deque<RedoUndo> undo;
	private int maxSize;
	
	private UndoRedoManager(){
		undo = new ArrayDeque<RedoUndo>();
		maxSize = 30;
	}

	public void add(final RedoUndo redoUndo) {
		if(redoUndo!=null && maxSize>0) {
			if(undo.size()==maxSize) {
				undo.removeLast();
			}

			undo.push(redoUndo);
			//redo.clear(); /* The redoable objects must be removed. */
		}
	}
	public void undo() {
		if(!undo.isEmpty()) {
			final RedoUndo redoUndo = undo.pop();
			redoUndo.undo();
			System.out.println(redoUndo.toString());
		}
	}
}
