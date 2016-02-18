package org.usfirst.frc.team4132.robot;

import org.usfirst.frc.team4132.robot.Button.ButtonEdge_t;

import edu.wpi.first.wpilibj.Joystick;

public class Button {
	private final int buttonNumber;
	private final Joystick controller;
	public enum ButtonEdge_t{
		IDLE, EDGE, WAITFORRELEASE
	}
	private ButtonEdge_t buttonState;
	
	public Button(int buttonNumber, Joystick controller){
		this.buttonNumber=buttonNumber;
		this.controller=controller;
	}
	
	public boolean isEdge(){
		switch(buttonState){
	     	case IDLE:
	     		if(controller.getRawButton(buttonNumber)==true){
	     			buttonState=ButtonEdge_t.EDGE;
	     			return true;
	     		}
	     		break;
	     	case EDGE:
	     		buttonState=ButtonEdge_t.WAITFORRELEASE;
	     	case WAITFORRELEASE:
	     		if(controller.getRawButton(buttonNumber)==false){
	     			buttonState=ButtonEdge_t.IDLE;
	     		}
		}
		return false;
     
	}
	public boolean isPressed(){
		if(controller.getRawButton(buttonNumber)==true){
			return true;
		}
		return false;
	}

}
