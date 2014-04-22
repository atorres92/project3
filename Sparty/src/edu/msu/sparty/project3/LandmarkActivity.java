package edu.msu.sparty.project3;

import android.os.Bundle;
import android.app.Activity;

public class LandmarkActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String landmarkRes = getIntent().getStringExtra(MainActivity.LANDMARK);
		
		if(landmarkRes.equalsIgnoreCase("breslin")) {
			setContentView(R.layout.activity_landmark_breslin);
			setTitle("Breslin Center");
		} else if (landmarkRes.equalsIgnoreCase("sparty")) {
			setContentView(R.layout.activity_landmark_statue);
			setTitle("Sparty Statue");
		} else if (landmarkRes.equalsIgnoreCase("beaumont")) {
			setContentView(R.layout.activity_landmark_beaumont);
			setTitle("Beaumont Tower");
		}
	}

}
