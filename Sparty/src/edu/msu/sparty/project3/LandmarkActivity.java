package edu.msu.sparty.project3;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LandmarkActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String landmarkRes = getIntent().getStringExtra(MainActivity.LANDMARK);
		
		if(landmarkRes.equalsIgnoreCase("breslin")) {
			setContentView(R.layout.activity_landmark_breslin);
		} else if (landmarkRes.equalsIgnoreCase("sparty")) {
			setContentView(R.layout.activity_landmark_statue);
		} else if (landmarkRes.equalsIgnoreCase("beaumont")) {
			setContentView(R.layout.activity_landmark_breslin);
		}
	}

}
