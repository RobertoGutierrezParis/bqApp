package es.bq.bqapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class DisplayEBook extends Activity implements OnClickListener{

	private final String TAG= this.getClass().getName();	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Basic Android widgets
		setContentView(R.layout.activity_cover);
		ImageView cover = (ImageView) findViewById(R.id.coverImage);
		cover.setOnClickListener(this);
		Libros libro=(Libros)EnumHolderData.getData();		
		cover.setImageBitmap(libro.getDrawableImage());
	}


	@Override
	public void onClick(View v) {
		super.finish();
		
	}

}
