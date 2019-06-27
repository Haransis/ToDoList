/*
package com.example.myhello.data;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.UiThread;

import com.example.myhello.data.API.ApiInterface;
import com.example.myhello.data.API.ListeToDoServiceFactory;
import com.example.myhello.data.database.ListeToDoDAO;
import com.example.myhello.data.database.RoomListeToDoDb;
import com.example.myhello.data.models.ListeToDo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import retrofit2.Response;

public class DataProvider {
    private List<Future> futures = new ArrayList<>();
    private final ListeToDoDAO listeDao;
    private final ApiInterface service = ListeToDoServiceFactory.createService(ApiInterface.class);

    private final Handler uiHandler;

    public DataProvider(Context context) {
        uiHandler = new Handler(context.getMainLooper());
        listeDao = RoomListeToDoDb.getDatabase(context).getListes();
    }

    //EXEMPLE AVEC UN EXECUTOR
    public void syncPosts(final ListesListener listener) {
        Future future = Utils.BACKGROUND.submit(new Runnable() {
            @Override public void run() {
                try {
                    Response<PostResponseList> response = service.getLists().execute();
                    if( response.isSuccessful()) {
                        final List<ListeToDo> post = converter.from(response.body().posts);
                        listeDao.save(post);
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                listener.onSuccess(post);
                            }
                        });

                    }else {
                        uiHandler.post(new Runnable() {
                            @Override public void run() {
                                listener.onError();
                            }
                        });

                    }

                } catch (IOException e) {
                    uiHandler.post(new Runnable() {
                        @Override public void run() {
                            listener.onError();
                        }
                    });
                }
            }
        });
        futures.add(future);
    }

    public void stop() {
        for (Future future : futures) {
            if(!future.isDone()){
                future.cancel(true);
            }
        }
        futures.clear();
    }

    public List<ListeToDo> loadPost() {
        return listeDao.getListes();
    }

    public void save(List<ListeToDo> postList) {
        listeDao.save(postList);
    }

    public interface ListesListener {
        @UiThread
        public void onSuccess(List<ListeToDo> posts);
        @UiThread
        public void onError();
    }
}
*/
