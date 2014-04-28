package backend.motion;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

public class HandsUpLeftGesture {

	public static boolean isDetected(Controller controller) {
		controller.frame().gestures();
		
		HandList hands = controller.frame().hands();
		HandList prevHands = controller.frame(1).hands();
		
		if (!hands.isEmpty() && !prevHands.isEmpty()){
			Hand rightHand = hands.rightmost();
			Hand prevRight = prevHands.rightmost();
			
			float dif = rightHand.stabilizedPalmPosition().getY() - prevRight.stabilizedPalmPosition().getY();
			
			int numFingers = rightHand.fingers().count();

			if(dif >= 3 && numFingers >= 3) {
				if ( rightHand.stabilizedPalmPosition().getX() < -100.0 ){
					return true;
				}
			}
		}
		return false;
	}
	
}
