package com.zero.support.work;

import com.zero.support.util.Observer;

import java.util.ArrayList;
import java.util.List;

public class MergeTask extends SnapShotTask<List<MergeTask.MergeParam>, Void> {
    @Override
    protected Void process(List<MergeParam> input) throws Exception {
        if (input == null) {
            return null;
        }
        Progress progress = new Progress();
        for (int i = 0; i < input.size(); i++) {
            final MergeParam param = input.get(i);
            progress.init(param.type, 0, 0, 0, i, input.size());
            param.task.newInstance().observerOn(AppExecutor.current())
                    .input(param)
                    .run(AppExecutor.current()).progress().observe(new Observer<Progress>() {
                @Override
                public void onChanged(Progress progress) {
                    progress.init(progress.handled, progress.total);
                    publishProgressChanged(progress);
                }
            });
        }
        return null;
    }

    public static class Builder {
        List<MergeParam> params = new ArrayList<>();

        public Builder add(int type, Class<? extends Task> task, Object param) {
            params.add(new MergeParam(type, task, param));
            return this;
        }

        public void request(Class<? extends MergeTask> task) {

        }

        public List<MergeParam> params() {
            return params;
        }
    }

    static class MergeParam {
        int type;
        Class<? extends Task> task;
        Object param;

        public MergeParam(int type, Class<? extends Task> task, Object param) {
            this.type = type;
            this.task = task;
            this.param = param;
        }
    }


}
