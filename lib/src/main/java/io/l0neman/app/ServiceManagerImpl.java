package io.l0neman.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.collection.ArrayMap;

import java.util.Map;


public class ServiceManagerImpl extends IServiceManager.Stub {

  private static final String TAG = ServiceManagerImpl.class.getSimpleName();

  private Map<String, ServiceEntry> mAliveServices = new ArrayMap<>();
  private Map<String, ServiceEntry> mDiedServices = new ArrayMap<>();

  private static final class ServiceEntry {
    final String name;
    final int callingPid;
    final int callingUid;
    final IBinder binder;

    IBinder.DeathRecipient recipient;

    ServiceEntry(String name, int callingPid, int callingUid, IBinder binder,
                 IBinder.DeathRecipient recipient) {
      this.name = name;
      this.callingPid = callingPid;
      this.callingUid = callingUid;
      this.binder = binder;
      this.recipient = recipient;
    }

    void linkToDeath() throws RemoteException {
      if (binder != null && recipient != null) {
        binder.linkToDeath(recipient, 0);
      }
    }

    void unlinkToDeath() {
      if (binder != null && recipient != null) {
        binder.unlinkToDeath(recipient, 0);
      }
    }
  }

  @Override public void addService(final String name, IBinder binder) throws RemoteException {
    if (mAliveServices.containsKey(name)) {
      ServiceEntry entry = mAliveServices.get(name);
      // noinspection ConstantConditions: entry 不会为 null
      throw new RuntimeException(String.format("Service %s has registed by pid:%s, uid:%s", name,
          entry.callingPid, entry.callingUid));
    }

    IBinder.DeathRecipient recipient = new IBinder.DeathRecipient() {
      @Override public void binderDied() {
        ServiceEntry entry = mAliveServices.remove(name);
        if (entry != null) {
          mDiedServices.put(name, entry);
        }
      }
    };
    ServiceEntry entry = new ServiceEntry(name, Binder.getCallingPid(), Binder.getCallingUid(), binder, recipient);
    mAliveServices.put(name, entry);
    mDiedServices.remove(name);
    entry.linkToDeath();

    Log.d(TAG, "#addService: " + name);
  }

  @Override public IBinder getService(String name) {
    ServiceEntry entry = mAliveServices.get(name);
    if (entry == null) {
      Log.e(TAG, "#getService died: " + name);
      entry = mDiedServices.get(name);
    }

    if (entry != null) {
      Log.e(TAG, "#getService alive: " + name);
      return entry.binder;
    }

    Log.e(TAG, "#getService: " + name);
    return null;
  }

  @Override public void removeService(String name) {
    ServiceEntry entry = mAliveServices.get(name);
    if (entry != null) {
      entry.unlinkToDeath();
    }

    mDiedServices.remove(name);
  }
}