package io.l0neman.example;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.l0neman.app.ServiceManager;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ServiceManager.addService(this, "foo", new IFoo.Stub() {
      @Override public int add(int x, int y) {
        return x + y;
      }
    });

    ServiceManager.addService(this, "bar", new IBar.Stub() {
      @Override public int sub(int x, int y) {
        return x - y;
      }
    });
  }

  public void test(View view) {
    IFoo iFoo = IFoo.Stub.asInterface(ServiceManager.getService(this, "foo"));
    if (iFoo != null) {
      try {
        final int add = iFoo.add(1, 2);
        Log.d(TAG, "add: " + add);
      } catch (RemoteException ignore) {}
    }

    IBar iBar = IBar.Stub.asInterface(ServiceManager.getService(this, "bar"));
    if (iBar != null) {
      try {
        final int sub = iBar.sub(3, 1);
        Log.d(TAG, "sub: " + sub);
      } catch (RemoteException ignore) {}
    }
  }
}
