package com.kk.solution.dev.androidprochapapp;

import android.app.ProgressDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ChatDialogsActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    ListView lstChatDialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_dialogs);
        
        createSessionForChat();

        loadChatDialogs();
    }

    private void loadChatDialogs() {

        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);

        QBRestChatService.getChatDialogs(null,requestBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> qbChatDialogs, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void createSessionForChat() {
        final ProgressDialog mDialog = new ProgressDialog(ChatDialogsActivity.this);
        mDialog.setMessage("Please Wait....");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        String user,password;
        user = getIntent().getStringExtra("user");
        password = getIntent().getStringExtra("password");

        final QBUser qbUser = new QBUser(user,password);
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                qbUser.setId(qbSession.getUserId());
                try {
                    qbUser.setPassword(BaseService.getBaseService().getToken());
                } catch (BaseServiceException e) {
                    e.printStackTrace();
                }
                QBChatService.getInstance().login(qbUser, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialog.dismiss();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("ERROR",""+e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}
