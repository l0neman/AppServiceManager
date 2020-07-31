package io.l0neman.app;

interface IServiceManager {

    void addService(in String name, in IBinder binder);

    IBinder getService(in String name);

    void removeService(in String name);
}