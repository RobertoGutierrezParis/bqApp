package es.bq.bqapp.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import es.bq.bqapp.Libros;
import es.bq.bqapp.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LibrosAdapter extends ArrayAdapter<Libros>{

	private final String TAG = this.getClass().toString();
	private Context context;
	private ArrayList<Libros> libros;

	private boolean titleDescent = false;
	private boolean dateDescent = false;

	public LibrosAdapter(Context context, ArrayList<Libros> libros) {
		super(context, R.layout.activity_gridview, libros);
		// Guardamos los parámetros en variables de clase.
		this.context = context;
		this.libros = libros;		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// View item = inflater.inflate(R.layout.activity_listview, null);

		LibrosHolder holder;
		View item = convertView;
		if (item == null) {
			item = LayoutInflater.from(context).inflate(
					R.layout.activity_gridview, null);

			holder = new LibrosHolder();
			holder.imgLibro = (ImageView) item.findViewById(R.id.imgLibro);
			holder.tvTitle = (TextView) item.findViewById(R.id.tvTitleField);
			holder.tvDate = (TextView) item.findViewById(R.id.tvDateField);
			item.setTag(holder);
		}

		holder = (LibrosHolder) item.getTag();

		// A partir del holder, asignamos los valores que queramos a los
		// controles.
		// Le asignamos una foto al ImegeView.
		holder.imgLibro.setImageBitmap(libros.get(position)
				.getDrawableImage());

		// Asignamos los textos a los TextView.
		holder.tvTitle.setText(libros.get(position).getTitulo());
		holder.tvDate.setText(libros.get(position).getFecha());

		// Devolvemos la vista para que se muestre en el ListView.
		return item;
	}

	public void sortByTitle() {
		Toast.makeText(context, "Sort by Title", Toast.LENGTH_SHORT).show();
		Collections.sort(libros, new Comparator<Libros>() {
			public int compare(Libros obj1, Libros obj2) {
				if (titleDescent)
					return obj2.getTitulo().compareToIgnoreCase(
							obj1.getTitulo());
				else
					return obj1.getTitulo().compareToIgnoreCase(
							obj2.getTitulo());
			}
		});
		titleDescent = !titleDescent;
	}

	public void sortByDate() {
		Toast.makeText(context, "Sort By Date", Toast.LENGTH_SHORT).show();
		Collections.sort(libros, new Comparator<Libros>() {
			public int compare(Libros obj1, Libros obj2) {
				if (dateDescent)
					return obj2.getFecha().compareToIgnoreCase(obj1.getFecha());
				else
					return obj1.getFecha().compareToIgnoreCase(obj2.getFecha());
			}
		});
		dateDescent = !dateDescent;
	}

	public boolean isEmpty(){
		return libros.isEmpty();
	}
	
	@Override
	public void clear(){
		super.clear();
		if(libros!=null){
			for (Libros libro: libros) {
				libro.getDrawableImage().recycle();				
			}
			libros.clear();
			
		}

			
	}
	
	public Libros getPosition(int position){
		if(position <= libros.size())
			return libros.get(position);
		else{
			Log.e(TAG,"El libro en la posicion: "+position+" no existe");
			return null;
		}
	}
	

}
