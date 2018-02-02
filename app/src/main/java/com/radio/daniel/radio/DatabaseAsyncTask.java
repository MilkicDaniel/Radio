package com.radio.daniel.radio;

import android.os.AsyncTask;

public class DatabaseAsyncTask extends AsyncTask<Void, Void, Void> {

    interface TaskListener{
        void inBackground();
        void onPost();
    }

    private TaskListener taskListener;

    public DatabaseAsyncTask(TaskListener taskListener){
        this.taskListener = taskListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        taskListener.inBackground();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        taskListener.onPost();
    }

}
