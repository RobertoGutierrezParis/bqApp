package es.bq.bqapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Libros implements Serializable {

	private String TAG=this.getClass().getSimpleName();
	
	
	private static final long serialVersionUID = 7296824833886996820L;
	private String titulo;
	private String fileName;
	private String fecha;
	private Bitmap drawableImage = null;
	private String dateFormat = "yyyy-MM-dd HH:mm";
	private String dateFormatIn = "EEE, dd MMM yyyy HH:mm:ss Z";
	private String rev="";
	private boolean isRead=false;

	public Libros(String titulo, String fileName, String fecha,String rev, Bitmap image,boolean read) {
		this.titulo = titulo;
		this.fileName = fileName;
		this.fecha = formatDate(fecha);
		this.setDrawableImage(image);
		this.isRead=read;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = formatDate(fecha);
	}

	private String formatDate(String dateIn) {
		SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat);
		try {
			SimpleDateFormat formatterIn = new SimpleDateFormat(
					this.dateFormatIn);
			// Log.v(this.getClass().toString(),"Ejemplo de parseo: "+formatterIn.format(Calendar.getInstance().getTime()));
			Date parse = formatterIn.parse(dateIn);
			return formatter.format(parse);
		} catch (Exception e) {
			Log.e(this.getClass().toString(),
					"Error formateando fecha de entrada: " + dateIn
							+ ".Error: " + e);
			Log.v(this.getClass().toString(),
					"Error formateando fecha de entrada: " + dateIn
							+ ".Error: " + e);
			return dateIn;
		}
	}

	private String formatDate(Date dateIn) {
		SimpleDateFormat formatter = new SimpleDateFormat(this.dateFormat);
		return formatter.format(dateIn);
	}

	public Bitmap getDrawableImage() {
		return drawableImage;
	}

	public void setDrawableImage(Bitmap drawableImage) {
		this.drawableImage = drawableImage;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
}
