package controller;

import pojo.FormattingObject;
import pojo.ShapeType;


public interface MediatorControllerInterface {
	  void registerController1(MenuController menuController);
	  void registerController2(FormatController formatController);
	  void registerController3(ShapeMenuController shapeMenuController);
	  void controller3OperateOn(int value);
	  FormattingObject getFormattingClass();
	  ShapeType ObjectNameClass(); 
}
