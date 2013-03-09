package com.heinrisch.minsida;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.heinrisch.minsida.fragments.NewComponentFragment;
import com.heinrisch.minsida.fragments.RealTimeFragment;

import java.util.Iterator;
import java.util.Map;

public class MainActivity extends Activity {
  private static final String TAG_NEW_COMPONENT = "new-component";
  public static final String SETTINGS = "settings";
  public static final String SETTINGS_COMPONENTS = "settings";
  public static final int RESULT_NEWCOMPONENT = 1337;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    refresh();
  }

  private void addSite(String header, int sideId, String tag) {
    Fragment fragment = RealTimeFragment.newInstance(header, sideId, 10, tag);
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.fragmentHolder, fragment, tag);
    transaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_add_item:
        startActivityForResult(new Intent(this, NewComponentFragment.class), RESULT_NEWCOMPONENT);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == RESULT_NEWCOMPONENT){
      refresh();
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void refresh() {
    SharedPreferences settings = getSharedPreferences(MainActivity.SETTINGS, 0);
    Map<String, ?> allSettings = settings.getAll();
    Iterator it = allSettings.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      System.out.println(pairs.getKey() + " = " + pairs.getValue());
      String[] data = ((String) pairs.getValue()).split(",");
      String key = (String) pairs.getKey();
      RealTimeFragment fragment = (RealTimeFragment) getFragmentManager().findFragmentByTag(key);
      if(fragment == null){
        addSite(data[0], Integer.parseInt(data[1]), key);
      }
    }

  }
}
