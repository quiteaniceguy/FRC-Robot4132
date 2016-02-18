
package org.usfirst.frc.team4132.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.usfirst.frc.team4132.robot.Robot.PistonStates_t;
import org.usfirst.frc.team4132.robot.commands.ExampleCommand;
import org.usfirst.frc.team4132.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	private final int BUTTONCOUNT=4;
	private final int A=0,B=1,X=2,Y=3;
	private final int CAMERAPORT=1;
	int buttonPressed=-1;
	int numberOfButtonsPressed=0;
	

    Command autonomousCommand;
    SendableChooser chooser;
    
    RobotDrive myRobot;
    Joystick controller;
    Jaguar jaguar;
    
    CameraServer server;
    
    DoubleSolenoid liftSolenoid;
    DoubleSolenoid pickUpSolenoid;
    public Button[] Buttons=new Button[BUTTONCOUNT];
    public boolean[] Edges=new boolean[BUTTONCOUNT];
    enum PistonStates_t{
		IDLE,PISTONOUT,WAIT,PISTONIN
	}
    PistonStates_t liftPistonState=PistonStates_t.IDLE;
    PistonStates_t pickUpPistonState=PistonStates_t.IDLE;
    int autonomousLoopCounter=0;
    Servo cameraServo;
   
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	for(int i=0;i<Buttons.length;i++){
    		Buttons[i]=new Button(i, controller);
    		Edges[i]=false;
    	}
		oi = new OI();
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", new ExampleCommand());
//        chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", chooser);
        
        myRobot=new RobotDrive(3,0);
        jaguar=new Jaguar(6);
        server=CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture("cam0");
        
        liftSolenoid=new DoubleSolenoid(0,1);
        pickUpSolenoid=new DoubleSolenoid(2,3);
        
		pickUpSolenoid.set(DoubleSolenoid.Value.kForward);
		pickUpPistonState=PistonStates_t.PISTONOUT;
		
		cameraServo=new Servo(CAMERAPORT);
		
		
        
        //myRobot.setInvertedMotor(MotorType.kFrontLeft, true);
        //myRobot.setInvertedMotor(MotorType.kFrontLeft, true);
        
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
    	boolean[] buttonValues = {SmartDashboard.getBoolean("DB/Button 0", false),
    			SmartDashboard.getBoolean("DB/Button 1", false),
    			SmartDashboard.getBoolean("DB/Button 2", false),
    			SmartDashboard.getBoolean("DB/Button 3", false)};
    	for(int i=0;i<buttonValues.length;i++){
    		if(buttonValues[i]==true){
    			buttonPressed=i;
    			numberOfButtonsPressed++;
    		}
    	}
        autonomousCommand = (Command) chooser.getSelected();  
		/* String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		switch(autoSelected) {
		case "My Auto":
			autonomousCommand = new MyAutoCommand();
			break;
		case "Default Auto":
		default:
			autonomousCommand = new ExampleCommand();
			break;
		} */
    	
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        autonomousLoopCounter++;
        if(autonomousLoopCounter<20){
    		pickUpSolenoid.set(DoubleSolenoid.Value.kForward);
        }
        ///runs programming according to button pressed in driver view
		
        if(numberOfButtonsPressed==1){
        	switch(buttonPressed){
        	
        	case 1:
        		////if button one is pressed
        		break;
        	case 2:
        		/////if buttton 2 is pressed etc.....
        		break;
        	case 3:
        		break;
        	case 4:
        		break;
        	default:
        		////runs the default code
        		System.out.println("no button or multiple buttons selected");
        		break;
        	}
        		
        	
        }
        
              
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        ///updates buttons values by asking for if is edge
        for(int i=0;i<Buttons.length;i++){
        	Edges[i]=Buttons[i].isEdge();
        }
        ///drives the robot
        myRobot.arcadeDrive(controller.getRawAxis(0),controller.getRawAxis(1)*-1,true);
        
        ///lift thing for howards lift system
        if(controller.getRawAxis(5)>0){
        	jaguar.set(controller.getRawAxis(5)*controller.getRawAxis(5)*.35);
        }
        else if(controller.getRawAxis(5)<0){
        	jaguar.set(controller.getRawAxis(5)*controller.getRawAxis(5)*.35*-1);

        }
        
        ///contrrols the solenoids that lift the robot off the ground CHNAGE THE GETRAWBUTTON TO BUTTON CLASS
        if(controller.getRawButton(1)){
    		liftSolenoid.set(DoubleSolenoid.Value.kForward);
        }
        if(controller.getRawButton(2)){
        	liftSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
        if(controller.getRawButton(3)){
        	pickUpSolenoid.set(DoubleSolenoid.Value.kForward);
        }
        if(controller.getRawButton(4)){
        	pickUpSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
        ////moves camerservo up and down
        double cameraMovement=0;
        double cameraPosition=cameraServo.getPosition();
        if(controller.getRawButton(5)){
        	cameraMovement+=.05;
        }
        if(controller.getRawButton(6)){
        	cameraMovement-=.05;
        }
        cameraPosition+=cameraMovement;
        cameraServo.setPosition(cameraPosition);
        
       
    }
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
