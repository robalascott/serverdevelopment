package manager;

public interface UndoableInterface {
	void undo();
	void redo();
	String getName();
}
