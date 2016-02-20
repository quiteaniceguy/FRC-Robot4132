
package org.usfirst.frc.team4132.robot;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Ultrasonic;
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
	private final int BUTTONCOUNT=4;
	private final int A=1,B=2,X=3,Y=4, LTRIGGER=5,RTRIGGER=6;
	private final int CAMERAPORT=1;
	private final int[]	PICKUPSOLENOIDPORT={2,3};
	private final int[] ROBOTDRIVEPORT={2,0,3,1};
	private final int CAMERASENSORPORT[]={6,7};
	private final int SHOOTERPORTS[]={1,2};
	public static OI oi;
	private int buttonInitPressed=-1;
	private int numberOfInitButtonsPressed=0;
	

    Command autonomousCommand;
    SendableChooser chooser;
    
    RobotDrive myRobot;
    Joystick controller;
    
    CameraServer server;
    
    public Button[] Buttons=new Button[BUTTONCOUNT];
    public boolean[] Edges=new boolean[BUTTONCOUNT];
    
    DoubleSolenoid pickUpSolenoid;
    enum PistonStates_t{
		IDLE,PISTONOUT,WAIT,PISTONIN
	}
    PistonStates_t pickUpPistonState=PistonStates_t.IDLE;
    
    CANJaguar[] shooter={new CANJaguar(SHOOTERPORTS[0]), new CANJaguar(SHOOTERPORTS[1])};
    enum ShooterStates_t{
    	REVERSE, FORWARD, IDLE
    }
    private int shooterTimer;
    private ShooterStates_t shooterState;
    
    enum AutoDrawbridge{
    	TOBRIDGE, LIFT,TO
    }
    
    
    public static int autonomousLoopCounter=0;
    Servo cameraServo;
    Ultrasonic cameraSensor; 
    
    Compressor c=new Compressor(0);

   
    private double leftXAxis;
    private double leftYAxis;
    private double rightXAxis;
    private double rightYAxis;
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	c.setClosedLoopControl(true);
    	
    	for(int i=0;i<Buttons.length;i++){
    		Buttons[i]=new Button(i, controller);
    		Edges[i]=false;
    	}
    	
		
//        chooser.addObject("My Auto", new MyAutoCommand());
        SmartDashboard.putData("Auto mode", chooser);
        
        myRobot=new RobotDrive(ROBOTDRIVEPORT[0],ROBOTDRIVEPORT[1],ROBOTDRIVEPORT[2],ROBOTDRIVEPORT[3]);
        server=CameraServer.getInstance();
        server.setQuality(50);
        server.startAutomaticCapture("cam0");
        
        pickUpSolenoid=new DoubleSolenoid(PICKUPSOLENOIDPORT[0],PICKUPSOLENOIDPORT[1]);
        
		pickUpSolenoid.set(DoubleSolenoid.Value.kForward);
		pickUpPistonState=PistonStates_t.PISTONOUT;
		
		cameraServo=new Servo(CAMERAPORT);
		cameraSensor=new Ultrasonic(CAMERASENSORPORT[0],CAMERASENSORPORT[1]);
		
        
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
    			buttonInitPressed=i;
    			numberOfInitButtonsPressed++;
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
		
        if(numberOfInitButtonsPressed==1){
        	switch(buttonInitPressed){
        	
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
    	shooterTimer=0;
    	shooterState=ShooterStates_t.IDLE;
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
        ///gets values from joystick on the controller
        leftXAxis=controller.getRawAxis(0);
    	leftYAxis=controller.getRawAxis(1)*-1;
    	rightXAxis=controller.getRawAxis(4);
    	rightYAxis=controller.getRawAxis(5)*-1;
        ///updates buttons values by asking for if is edge
        for(int i=0;i<Buttons.length;i++){
        	Edges[i]=Buttons[i].isEdge();
        }
        ///drives the robot
        myRobot.setMaxOutput(.35);
        myRobot.arcadeDrive(leftYAxis,leftXAxis*-1,true);
        
      
      
        /////does the shooter "motion" for robot
        switch(shooterState){
        case IDLE:
        	if(Buttons[B].isPressed()){
        		shooterState=ShooterStates_t.REVERSE;
        		shooterTimer=0;
        	}
        case REVERSE:
        	//piston out
        	shooter[0].set(-.35);
        	shooter[1].set(-.35);
        	if(shooterTimer++>25){
        		shooterState=ShooterStates_t.FORWARD;
        		shooterTimer=0;
        	}
        	break;
        case FORWARD:
        	shooter[0].set(.35);
        	shooter[1].set(.35);
        	if(shooterTimer++>25){
        		shooterState=ShooterStates_t.IDLE;
        		shooterTimer=0;
        	}
        	break;
        }	
    
        ///contrrols the solenoids that lift the robot off the ground CHNAGE THE GETRAWBUTTON TO BUTTON CLASS
       
        if(Buttons[X].isPressed()){
        	pickUpSolenoid.set(DoubleSolenoid.Value.kForward);
        }
        if(Buttons[Y].isPressed()){
        	pickUpSolenoid.set(DoubleSolenoid.Value.kReverse);
        }
        ////moves camerservo up and down
    	double cameraMovement=0;
    	double cameraPosition=cameraServo.getPosition();

        if(Buttons[LTRIGGER].isPressed() && !Buttons[RTRIGGER].isPressed()){
        	cameraMovement+=.05;
        	cameraPosition+=cameraMovement;
        	cameraServo.setPosition(cameraPosition);
        }
        if(!Buttons[LTRIGGER].isPressed() && Buttons[RTRIGGER].isPressed()){
        	cameraMovement-=.05;
        	cameraPosition+=cameraMovement;
        	cameraServo.setPosition(cameraPosition);
        }
        
       
    }
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    public void openGate(){
    	
    }
}
