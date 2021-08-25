package com.zero.support.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zero.support.app.ButtonActivity;
import com.zero.support.core.app.DialogClickEvent;
import com.zero.support.app.SimpleDialogModel;
import com.zero.support.app.SimplePermissionModel;
import com.zero.support.core.app.Tip;
import com.zero.support.core.AppGlobal;
import com.zero.support.core.observable.ConnectivityObservable;
import com.zero.support.core.observable.Observable;
import com.zero.support.core.observable.Observer;

public class DialogActivity extends ButtonActivity {
    private Observable<String> observable = new Observable<>();
    private Button button;
    private ConnectivityObservable connectivityObservable = new ConnectivityObservable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addButton("show tip", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(Tip.loading());

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        injectViewModel().dismiss();
                    }
                }, 10 * 1000);
            }
        });
        connectivityObservable.observe(new Observer<Intent>() {
            @Override
            public void onChanged(Intent intent) {
                Log.e("xgf", "onChanged: " + intent);
            }
        });


        addButton("show dialog", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder().content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());

                    }
                });
            }
        });
        addButton("show dialog with title", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());

                    }
                });
            }
        });
        addButton("show dialog with title positive", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .positive("确定")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());

                    }
                });
            }
        });

        addButton("show dialog with title negative", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .negative("取消")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                    }
                });
            }
        });

        addButton("show dialog with title negative positive", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .negative("取消")
                        .positive("确定")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                    }
                });
            }
        });

        addButton("show dialog all", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestWindow(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .content("sadfhaskdjhflasdhflshfla;sdhfkljshfs")
                        .negative("取消")
                        .positive("确定")
                        .build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());

                    }
                });
            }
        });

        addButton("permission", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectViewModel().requestPermission(new SimplePermissionModel.Builder()
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .denied("绝了").ration("给我").build());
            }
        });
        button = addButton("test", new View.OnClickListener() {
            boolean running;

            @Override
            public void onClick(View v) {
                running = !running;
                runOnUiThread(new Runnable() {
                    int count;

                    @Override
                    public void run() {
                        Button b = (Button) v;
                        b.setText("test: " + count);
                        if (!running) {

                        } else {
                            count++;
                            b.postDelayed(this, 1000);
                        }

                    }
                });
            }
        });

        addButton("发送数据", new View.OnClickListener() {
            int count;

            @Override
            public void onClick(View v) {
                count++;
                observable.setValue("send " + count);
            }
        });

        addButton("reset", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observable.reset();
            }
        });
        addButton("增加Live监听", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = addButton("test", null);
                observable.asLive().observe(DialogActivity.this, new androidx.lifecycle.Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        button.setText("test ：" + s);
                    }
                });
            }
        });


        addButton("增加监听", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = addButton("test", null);
                observable.observe(new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        button.setText("test ：" + s);
                    }
                });
            }
        });

    }
}
