package com.zero.support.sample;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zero.support.compat.AppGlobal;
import com.zero.support.compat.app.ButtonActivity;
import com.zero.support.compat.app.DialogClickEvent;
import com.zero.support.compat.quick.SimpleDialogModel;
import com.zero.support.compat.quick.SimplePermissionModel;
import com.zero.support.work.Observable;
import com.zero.support.work.Observer;

public class MainActivity extends ButtonActivity {
    private Observable<String> observable = new Observable<>();
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addButton("show tip", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().postLoading(null);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestViewModel().postDismiss();
                    }
                }, 10 * 1000);
            }
        });


        addButton("show dialog", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder().content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });
        addButton("show dialog with title", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });
        addButton("show dialog with title positive", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .positive("确定")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });

        addButton("show dialog with title negative", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .negative("取消")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });

        addButton("show dialog with title negative positive", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .negative("取消")
                        .positive("确定")
                        .content("hahahaha").build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });

        addButton("show dialog all", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestDialog(new SimpleDialogModel.Builder()
                        .title("xxx")
                        .content("sadfhaskdjhflasdhflshfla;sdhfkljshfs")
                        .negative("取消")
                        .positive("确定")
                        .build()).click().observe(new Observer<DialogClickEvent>() {
                    @Override
                    public void onChanged(DialogClickEvent dialogClickEvent) {
                        AppGlobal.sendMessage("click:" + dialogClickEvent.which());
                        dialogClickEvent.dismiss();
                    }
                });
            }
        });

        addButton("permission", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestViewModel().requestPermission(new SimplePermissionModel.Builder()
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
                        b.setText("test: "+count);
                        if (!running){

                        }else {
                            count++;
                            b.postDelayed(this,1000);
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
                observable.setValue("send "+count);
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
               Button button =  addButton("test",null);
               observable.asLive().observe(MainActivity.this, new androidx.lifecycle.Observer<String>() {
                   @Override
                   public void onChanged(String s) {
                       button.setText("test ："+s);
                   }
               });
            }
        });


        addButton("增加监听", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button =  addButton("test",null);
                observable.observe(new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        button.setText("test ："+s);
                    }
                });
            }
        });


    }
}