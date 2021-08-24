package com.zero.support.core.app;


import com.zero.support.core.observable.Observable;


public class PermissionModel extends ActivityModel {
    private final Observable<PermissionEvent> observable = new Observable<>();
    private volatile String[] permissions;
    private volatile int[] grantResults;
    private boolean executed;

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isExecuted() {
        return executed;
    }

    public PermissionModel(String... permissions) {
        this.permissions = permissions;
    }

    public String[] permissions() {
        return permissions;
    }

    void dispatchResult(String[] permissions, int[] grantResults) {
        if (permissions.length == 0) {
            return;
        }
        this.executed = false;
        this.permissions = permissions;
        this.grantResults = grantResults;
        PermissionEvent event = new PermissionEvent(this, permissions, grantResults);
        onReceivePermissionEvent(event);
    }

//    protected final void dispatchPermission() {
//        int[] grantResults = new int[permissions.length];
//        for (int i = 0; i < permissions.length; i++) {
//            Activity activity = requireViewModel().requireActivity();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                grantResults[i] = activity.checkPermission(permissions[i],Process.myPid(),Process.myUid());
//            } else {
//                grantResults[i] = PackageManager.PERMISSION_GRANTED;
//            }
//        }
//        dispatchResult(permissions, grantResults);
//    }

    protected void onReceivePermissionEvent(PermissionEvent event) {
        dispatchPermissionEvent(event);
    }

    protected final void dispatchPermissionEvent(PermissionEvent event) {
        observable.setValue(event);
    }

    public Observable<PermissionEvent> result() {
        return observable;
    }
}
