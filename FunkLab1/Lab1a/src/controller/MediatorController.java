package controller;

import java.util.Observable;
import java.util.Observer;
import pojo.FormattingObject;
import pojo.ShapeType;

public class MediatorController implements MediatorControllerInterface, Observer{

	private MenuController menuController;
	private FormatController formatController;
	private ShapeMenuController shapeMenuController;
	/*Observable Class*/
	private MediatorController() {
	}

	public static MediatorController getInstance() {
		return MediatorControllerHolder.INSTANCE;
	}

	private static class MediatorControllerHolder {
		private static final MediatorController INSTANCE = new MediatorController();
	}

	@Override
	public void registerController1(MenuController menuController) {
		this.menuController = menuController;

	}

	@Override
	public void registerController2(FormatController formatController) {
		this.formatController = formatController;
		
		if(this.formatController != null){
			formatController.getFormattingClass().addObserver(this);	
		}
		
	}

	@Override
	public void registerController3(ShapeMenuController shapeMenuController) {
		this.shapeMenuController =  shapeMenuController;
		if(this.shapeMenuController  != null){
			this.shapeMenuController.ObjectShapeClass().addObserver(this);
			System.out.println(shapeMenuController.ObjectShapeClass().toStringAll());	
		}
	}
	
	@Override
	public void controller3OperateOn(int value) {
	
		if(this.formatController != null){
			System.out.println(value);
			System.out.println(this.formatController.toString());
			this.formatController.SetValue(value);	
		}
		if(this.menuController != null){
			System.out.println(value);
			System.out.println(this.menuController.toString());
		}
	}

	@Override
	public void update(Observable obs, Object arg1) {
		if(obs instanceof FormattingObject ){
			System.out.println(this.formatController.getFormattingClass().toStringAll());
			//Send formating object to canvas here
		}
		if(obs instanceof ShapeType ){
			System.out.println(this.shapeMenuController.ObjectShapeClass().toStringAll());
			//Send formating object to canvas here
		}
	}

	@Override
	public FormattingObject getFormattingClass() {
		return null;
	}

	@Override
	public ShapeType ObjectNameClass() {
		return null;
	}



}
