package io.l0neman.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.BundleCompat;

/**
 * Created by l0neman on 2019/08/08.
 */
public class ServiceManager {

  private static final String TAG = ServiceManager.class.getSimpleName();

  private ServiceManager() {
    throw new AssertionError("no instance.");
  }

  private static IServiceManager sIServiceManager;

  private static void ensureIServiceManager(final Context context) {
    Bundle bundle = context.getContentResolver().call(
        Uri.parse(ServiceManagerProvider.URI),
        ServiceManagerProvider.METHOD_SM, null, new Bundle());

    if (bundle == null) {
      throw new RuntimeException("Service Manager is null [Bundle].");
    }

    IBinder serviceManager = BundleCompat.getBinder(bundle, ServiceManagerProvider.KEY_SM);

    if (serviceManager == null) {
      throw new RuntimeException("Service Manager is null [getBinder].");
    }

    try {
      serviceManager.linkToDeath(new IBinder.DeathRecipient() {
        @Override public void binderDied() {
          ensureIServiceManager(context);
        }
      }, 0);
    } catch (RemoteException e) {
      Log.w(TAG, "ensureIServiceManager: linkToDeath", e);
    }

    sIServiceManager = IServiceManager.Stub.asInterface(serviceManager);
  }

  private static IServiceManager getIServiceManager(Context context) {
    ensureIServiceManager(context);
    return sIServiceManager;
  }

  public static void addService(Context context, String name, IBinder binder) {
    try {
      getIServiceManager(context).addService(name, binder);
    } catch (RemoteException e) {
      Log.w(TAG, "addService: remote", e);
    }
  }

  public static IBinder getService(Context context, String name) {
    try {
      return getIServiceManager(context).getService(name);
    } catch (RemoteException e) {
      Log.w(TAG, "#getService", e);
      return null;
    }
  }

  public static void removeService(Context context, String name) {
    try {
      getIServiceManager(context).removeService(name);
    } catch (RemoteException e) {
      Log.w(TAG, "#removeService", e);
    }
  }
}