package io.l0neman.app;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.BundleCompat;


/**
 * Created by l0neman on 2019/08/08.
 */
@SuppressWarnings("NullableProblems")
public class ServiceManagerProvider extends ContentProvider {
  private static final String TAG = ServiceManagerProvider.class.getSimpleName();

  public static final String URI = "content://io.hexman.ipc.provider";
  public static final String KEY_SM = "binder";
  public static final String METHOD_SM = "$";

  private ServiceManagerImpl mServiceManager = new ServiceManagerImpl();

  @Override public boolean onCreate() {
    Log.i(TAG, "#onCreate");
    return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) { return null; }

  @Override public String getType(Uri uri) { return null; }

  @Override public Uri insert(Uri uri, ContentValues values) { return null; }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }

  @Override public Bundle call(String method, String arg, Bundle extras) {
    if (METHOD_SM.equals(method)) {
      Bundle service = new Bundle();
      BundleCompat.putBinder(service, KEY_SM, mServiceManager);
      return service;
    }

    return null;
  }
}