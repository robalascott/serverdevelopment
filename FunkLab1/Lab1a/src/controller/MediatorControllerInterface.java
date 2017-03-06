package controller;

import pojo.FormattingObject;
import pojo.ShapeType;


public interface MediatorControllerInterface {
	  void registerController(MenuController menuController);
	  void registerController(FormatController formatController);
	  void registerController(ShapeMenuController shapeMenuController);
	  void registerController(CanvasController canvasController);
	  void controller3OperateOn(int value);
	  FormattingObject getFormattingClass();
	  ShapeType ObjectNameClass(); 
	  void undo();
}
