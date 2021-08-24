package com.zero.support.app;//package com.zero.support.app;
//
//import android.content.ContentProvider;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.pm.ProviderInfo;
//import android.database.Cursor;
//import android.net.Uri;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class SupportProvider extends ContentProvider {
//    //name-authority
//    private Map<Class<? extends ContentProvider>,String> components = new HashMap<>();
//    private Map<Class<? extends ContentProvider>, ContentProvider> providers = new HashMap<>();
//
//    public SupportProvider() {
//        for (Class<? extends ContentProvider> cls : components.keySet()) {
//            try {
//                ContentProvider provider = cls.newInstance();
//                providers.put(cls, provider);
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void attachInfo(Context context, ProviderInfo info) {
//        super.attachInfo(context, info);
//        for (ContentProvider provider : providers.values()) {
//            ProviderInfo providerInfo  = new ProviderInfo(info);
//            providerInfo.authority =components.get( provider.getClass());
//            providerInfo.name = provider.getClass().getName();
//            provider.attachInfo(context, info);
//        }
//    }
//
//    @Override
//    public boolean onCreate() {
//        return true;
//    }
//
//
//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public String getType(@NonNull Uri uri) {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
//        return null;
//    }
//
//    @Override
//    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
//        return 0;
//    }
//
//    @Override
//    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
//        return 0;
//    }
//}
