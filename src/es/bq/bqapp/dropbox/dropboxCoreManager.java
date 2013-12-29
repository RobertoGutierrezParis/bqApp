package es.bq.bqapp.dropbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import es.bq.bqapp.Libros;
import es.bq.bqapp.R;
import es.bq.bqapp.adapters.LibrosAdapter;

/*
 * Clase principal para la aplicacion
 * Contiene los objetos necesarios para la conexion con el core Api de Drobbox
 */

public class dropboxCoreManager extends Activity {

	// TAG utilizado para los logs que se generar
	private static final String TAG = "dropboxCoreManager";

	//Key and secret for APP Drobbox
	final static private String APP_KEY = "rhbwrioxdyyecbt";
	final static private String APP_SECRET = "s5xu9qayxvczvj5";

	//Access Type to core's Dropbox 
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;


	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	// Dropbox Api, uses for dropbox access
	DropboxAPI<AndroidAuthSession> mApi;

	// Directory in dropbox 
	private final String DROPBOX_DIR = "/";
	//Extension for files searched
	private final String FILE_EXTENSION = ".epub";

	final static private int RESULT_OK = 0;

	
	// Var's for view adapter
	LibrosAdapter adapter = null;	
	private GridView gvLibros;
	
	// Android widgets
	private Button mSubmit;
	private LinearLayout mDisplay;
	private boolean mLoggedIn;

	


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		//Initialize Dropbox Api whith current android session
		mApi = new DropboxAPI<AndroidAuthSession>(session);

		// Basic Android widgets
		setContentView(R.layout.activity_main);

		
		//Check correct configuration needed for Core Dropbox Api
		checkAppKeySetup();

		mSubmit = (Button) findViewById(R.id.auth_button);
		gvLibros = (GridView) findViewById(R.id.gvItems);


		mSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				if (mLoggedIn) {
					if (adapter != null)
						adapter.clear();
					logOut();
				} else {
					// Start the remote authentication					
					mApi.getSession().startAuthentication(dropboxCoreManager.this);
				}
			}
		});
		mDisplay = (LinearLayout) findViewById(R.id.logged_in_display);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Display the proper UI state if logged in or not
		setLoggedIn(mApi.getSession().isLinked());

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				if(adapter==null || adapter.isEmpty()){ 
					// Inicializamos el adapter.
					adapter = new LibrosAdapter(this,new ArrayList<Libros>());					
					listFiles(DROPBOX_DIR, FILE_EXTENSION);
				}
				setLoggedIn(true);
			} catch (IllegalStateException e) {
				showToast("Couldn't authenticate with Dropbox:"
						+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
		gvLibros.setAdapter(adapter);
	}

	
	// This is what gets called on finishing a media piece to import
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_OK) {
			// return from file upload
			if (resultCode == Activity.RESULT_OK) {
				Log.v(TAG, "OnActivityResult: ......");
			} else {
				Log.w(TAG, "Unknown Activity Result from mediaImport: "
						+ resultCode);
			}
		}
	}

	private void logOut() {
		// Remove credentials from the session
		mApi.getSession().unlink();
		if(adapter != null)
			adapter.clear();
		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}

	//
	// Convenience function to change UI state based on being logged in
	//
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		if (loggedIn) {
			mSubmit.setText("Unlink from Dropbox");
			mDisplay.setVisibility(View.VISIBLE);
		} else {
			mSubmit.setText("Link with Dropbox");
			mDisplay.setVisibility(View.GONE);
		}
	}

	private void checkAppKeySetup() {
		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			showToast("URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
			finish();
		}
	}

	/*
	 * Show message on device
	 */
	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	/*
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).	
	 * @return Array of [access_key, access_secret], or null if none stored
	*/
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/*
	* Shows keeping the access keys returned from Trusted Authenticator in a local
	* store, rather than storing user name & password, and re-authenticating each
	* time (which is not to be done, ever).
	*/
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	
	/*
	 * Clear stored keys
	 */
	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	
	/*
	 * Create an AndroidAudhSession
	 */
	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		// String[] stored = null;
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}


	/*
	 * Get all files in dropbox directory
	 * 
	 * Only get files on root directory, not in recursive directories, and aggregate everyone to grid 
	 * @in dir: Path for dropbox directory
	 * @in extension: extension for search
	 * 	 
	 * 
	 */
	private void listFiles(String dir,String extension) {
		try {

			Entry files = mApi.metadata(dir, 10000, null, true, null);
			List<Entry> contents = files.contents;
			adapter.clear();
			for (Entry entry : contents) {
				Log.i(TAG, "Fichero: " + entry.path + "/" + entry.fileName());
				if (!entry.isDeleted) {
					if (entry.fileName().toLowerCase()
							.endsWith(extension.toLowerCase())) {
						try {
							DropboxInputStream fileStream = mApi.getFileStream(
									entry.path + "/" + entry.fileName(),
									entry.rev);
							DropboxFileInfo fileInfo = fileStream.getFileInfo();
							Log.i("DbExampleLog", "The file's rev is: "
									+ fileInfo.getMetadata().rev);
							//TODO: Open file and read Title and cover image
							adapter.add(new Libros(entry.fileName(), entry
									.fileName(), entry.modified, null));													
						} catch (Exception e) {
							Log.e(TAG,"Erro: "+e);

						}
					} else
						Log.d(TAG, "El fichero: " + entry.fileName()
								+ " no tiene la extension buscada: "
								+ extension);

				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error leyendo directorio: " + DROPBOX_DIR + ". Error: "
					+ e);
			Log.d(TAG,
					"Error leyendo directorio: " + DROPBOX_DIR + ". Error: ", e);
		}
	}
	
	
	
}
