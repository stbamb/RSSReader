package com.stbam.rssnewsreader;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.stbam.rssnewsreader.parser.RSSFeed;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DetailActivity extends FragmentActivity {

	RSSFeed feed;
	int pos;
	private DescAdapter adapter;
	private ViewPager pager;

    // variables utilizadas para Facebook
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
    private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// obtiene las parametros pasados desde la actividad principal
		feed = (RSSFeed) getIntent().getExtras().get("feed");
		pos = getIntent().getExtras().getInt("pos");

		// para UI
		adapter = new DescAdapter(getSupportFragmentManager());
		pager = (ViewPager) findViewById(R.id.pager);


		// se pasan los parametros al pager
		pager.setAdapter(adapter);
		pager.setCurrentItem(pos);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(this).inflate(R.menu.activity_desc, menu);

		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			// app icon in action bar clicked; finish activity to go home
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    // un adaptador para los fragments
    // que corresponden a las descripciones de las noticias
    public class DescAdapter extends FragmentStatePagerAdapter {
		public DescAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return feed.getItemCount();
		}

		@Override
		public Fragment getItem(int position) {

			DetailFragment frag = new DetailFragment();

			Bundle bundle = new Bundle();
			bundle.putSerializable("feed", feed);
			bundle.putInt("pos", position);
			frag.setArguments(bundle);

			return frag;

		}

	}

    // metodo creado por Facebook y adaptado para suplir nuestras necesidades
    // es necesario tener FB SDK para su correcto funcionamiento
    // pide permisos al ususario para publicar
    public void publishStory(View view) {
        Session session = Session.getActiveSession();

        if (session != null) {

            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(PERMISSIONS, permissions)) {
                pendingPublishReauthorization = true;
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(this, PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();

            int position = new DetailFragment().fPos2 - 1;

            // prevencion de errores
            if (position < 0)
                position = 0;

            feed.getItem(position).setCompartido(true);

            postParams.putString("name", feed.getItem(position).getTitle());
            postParams.putString("caption", feed.getItem(position).get_source_page());
            postParams.putString("link", feed.getItem(position).getLink());

                Request.Callback callback = new Request.Callback() {
                    public void onCompleted(Response response) {
                        JSONObject graphResponse = response
                                .getGraphObject()
                                .getInnerJSONObject();
                        String postId = null;
                        try {
                            postId = graphResponse.getString("id");
                        } catch (JSONException e) {

                        }
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras intentábamos publicar la noticia.", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "La noticia ha sido publicada.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                };

                Request request = new Request(session, "me/feed", postParams,
                        HttpMethod.POST, callback);

                RequestAsyncTask task = new RequestAsyncTask(request);
                task.execute();
        }

    }

    // metodo utilizado por el publishStory
    // propio de facebook
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
        uiHelper.onSaveInstanceState(outState);
    }

    // metodo de Facebook
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

     @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

}
