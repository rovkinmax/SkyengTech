package ru.rovkinmax.skyengtech.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

import java.util.concurrent.TimeUnit;

public class SyncService extends GcmTaskService {
    private static final String TAG = SyncService.class.getSimpleName();

    private static final long PERIOD_SECS = TimeUnit.DAYS.toSeconds(1);

    public static void startSync(@NonNull Context context) {
        GcmNetworkManager manager = GcmNetworkManager.getInstance(context.getApplicationContext());
        PeriodicTask periodic = new PeriodicTask.Builder()
                .setService(SyncService.class)
                .setPeriod(PERIOD_SECS)
                .setTag(TAG)
                .setPersisted(true)
                .setUpdateCurrent(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .build();
        manager.schedule(periodic);
    }

    @Override
    public int onRunTask(TaskParams params) {
        //тут делаем синхронизацию
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
