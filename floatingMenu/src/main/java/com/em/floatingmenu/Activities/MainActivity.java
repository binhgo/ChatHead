package com.em.floatingmenu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.em.floatingmenu.Object.ChatHead;
import com.em.floatingmenu.R;
import com.em.floatingmenu.Service.ServiceFloating;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<ChatHead> listChatHeads = new ArrayList<ChatHead>();


        ChatHead chatHead = new ChatHead();
        chatHead.id = 1;
        chatHead.imageResId = R.drawable.floating5;
        chatHead.hashCode = "abc asd qwe";
        listChatHeads.add(chatHead);

        chatHead = new ChatHead();
        chatHead.id = 2;
        chatHead.imageResId = R.drawable.floating2;
        chatHead.hashCode = "abc asd qwe";
        listChatHeads.add(chatHead);

        chatHead = new ChatHead();
        chatHead.id = 3;
        chatHead.imageResId = R.drawable.floating3;
        chatHead.hashCode = "abc asd qwe";
        listChatHeads.add(chatHead);



        Intent intent = new Intent(MainActivity.this, ServiceFloating.class);
        intent.putExtra("CHATHEAD", listChatHeads);
        startService(intent);

    }

    @Override
    protected void onResume()
    {/*
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString("LAUNCH").equals("YES"))
        {
            startService(new Intent(MainActivity.this, ServiceFloating.class));
        }*/
        super.onResume();
    }
}
