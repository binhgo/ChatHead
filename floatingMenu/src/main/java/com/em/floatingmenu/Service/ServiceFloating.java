package com.em.floatingmenu.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.em.floatingmenu.Object.ChatHead;
import com.em.floatingmenu.R;

import java.util.ArrayList;


public class ServiceFloating extends Service
{

    public static int ID_NOTIFICATION = 2018;

    private WindowManager windowManager;
    private ImageView chatHead;
    private PopupWindow pwindo;

    private ArrayList<ChatHead> listChatHeads = new ArrayList<ChatHead>();
    private int removeChatHeadsCount = 0;
    //private ArrayList<ImageView> listChatHeadsRemoved = new ArrayList<ImageView>();

    boolean mHasDoubleClicked = false;
    long lastPressTime;
    private Boolean _enable = true;

    private ImageView removeCircle;

    private boolean isCollision = false;
    private int collisionChatHeadId = -1;

    //ArrayList<String> myArray;
    //ArrayList<PInfo> apps;
    //List listCity;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // ArrayList<ChatHead> ch = (ArrayList<ChatHead>) intent.getSerializableExtra("CHATHEAD");
        //Toast.makeText(ServiceFloating.this, ch.get(0).id + "-" + ch.get(0).imageResId + "-" + ch.get(0).hashCode, Toast.LENGTH_SHORT).show();
        //Toast.makeText(ServiceFloating.this, ch.get(1).id + "-" + ch.get(1).imageResId + "-" + ch.get(1).hashCode, Toast.LENGTH_SHORT).show();
        //Toast.makeText(ServiceFloating.this, ch.get(2).id + "-" + ch.get(2).imageResId + "-" + ch.get(2).hashCode, Toast.LENGTH_SHORT).show();


        ChatHead chat = new ChatHead();
        chat.id = 1;
        chat.imageResId = R.drawable.floating5;
        chat.hashCode = "abc asd qwe";
        listChatHeads.add(chat);

        chat = new ChatHead();
        chat.id = 2;
        chat.imageResId = R.drawable.floating2;
        chat.hashCode = "abc asd qwe";
        listChatHeads.add(chat);

        chat = new ChatHead();
        chat.id = 3;
        chat.imageResId = R.drawable.floating3;
        chat.hashCode = "abc asd qwe";
        listChatHeads.add(chat);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        //
        removeCircle = new ImageView(this);
        removeCircle.setImageResource(R.drawable.floatingcircle);
        removeCircle.setVisibility(View.GONE);

        final WindowManager.LayoutParams paramCircle = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        paramCircle.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        //paramCircle.x = 150;
        paramCircle.y = 80;

        windowManager.addView(removeCircle, paramCircle);
        //


        //
        for (int i = 0; i < listChatHeads.size(); i++)
        {
            chatHead = new ImageView(this);
            //chatHead.setElevation(10);

            chatHead.setImageResource(listChatHeads.get(i).imageResId);

            listChatHeads.get(i).imagView = chatHead;
        }


        for (int i = 0; i < listChatHeads.size(); i++)
        {

            final int currentI = i;
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = currentI * 20 + 10;
            params.y = currentI * 20 + 10;

            windowManager.addView(listChatHeads.get(i).imagView, params);

            try
            {
                listChatHeads.get(i).imagView.setOnTouchListener(new View.OnTouchListener()
                {
                    private WindowManager.LayoutParams paramsF = params;
                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        switch (event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:

                                // Get current time in nano seconds.
                                long pressTime = System.currentTimeMillis();

                                // If double click...
                                if (pressTime - lastPressTime <= 300)
                                {
                                    createNotification();
                                    ServiceFloating.this.stopSelf();
                                    mHasDoubleClicked = true;
                                } else
                                {     // If not double click....
                                    mHasDoubleClicked = false;
                                }
                                lastPressTime = pressTime;
                                initialX = paramsF.x;
                                initialY = paramsF.y;
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_UP:
                                removeCircle.setVisibility(View.GONE);
                                if (isCollision)
                                {
                                    windowManager.removeView(listChatHeads.get(collisionChatHeadId).imagView);
                                    //listChatHeads.remove(collisionChatHeadId);
                                    removeChatHeadsCount++;
                                    if (listChatHeads.size() == removeChatHeadsCount)
                                    {

                                        createNotification();
                                        ServiceFloating.this.stopSelf();
                                    }

                                    isCollision = false;

                                } else
                                {

                                }
                                break;
                            case MotionEvent.ACTION_MOVE:
                                removeCircle.setVisibility(View.VISIBLE);
                                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(listChatHeads.get(currentI).imagView, paramsF);

                                if (checkCollision(listChatHeads.get(currentI).imagView, removeCircle))
                                {
                                    //Toast.makeText(getApplicationContext(), "Collision", Toast.LENGTH_SHORT).show();
                                    Log.e("Collision", "Collision");
                                    //windowManager.removeView(listChatHeads.get(currentI));
                                    isCollision = true;
                                    collisionChatHeadId = currentI;
                                } else
                                {
                                    isCollision = false;
                                    collisionChatHeadId = -1;
                                }

                                break;
                        }
                        return false;
                    }
                });
            } catch (Exception e)
            {
                // TODO: handle exception
            }

            listChatHeads.get(i).imagView.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View arg0)
                {
                    initPopup(listChatHeads.get(currentI).imagView);
                    _enable = false;
                    //				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    //				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    //				getApplicationContext().startActivity(intent);
                }
            });
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Toast.makeText(ServiceFloating.this, "onCreate", Toast.LENGTH_SHORT).show();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }


    public boolean checkCollision(View chatHead, View removeCircle)
    {

        int[] chatHeadLocation = new int[2];
        chatHead.getLocationOnScreen(chatHeadLocation);
        Rect R1 = new Rect(chatHeadLocation[0], chatHeadLocation[1], chatHeadLocation[0] + chatHead.getRight(), chatHeadLocation[1] + chatHead.getBottom());
        Log.e("ChatHead", chatHeadLocation[0] + "-" + chatHeadLocation[1] + "-" + chatHead.getRight() + "-" + chatHead.getBottom());


        int[] removeCircleLocation = new int[2];
        removeCircle.getLocationOnScreen(removeCircleLocation);
        Rect R2 = new Rect(removeCircleLocation[0], removeCircleLocation[1], removeCircleLocation[0] + removeCircle.getRight(), removeCircleLocation[1] + removeCircle.getBottom());
        Log.e("Circle", removeCircleLocation[0] + "-" + removeCircleLocation[1] + "-" + removeCircle.getRight() + "-" + removeCircle.getBottom());


        return R2.contains(R1);
    }

    private void initPopup(View anchor)
    {
        try
        {
            if (pwindo != null)
            {
                if (pwindo.isShowing())
                {
                    pwindo.dismiss();
                } else
                {

                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.popup, null);
                    pwindo = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    pwindo.showAsDropDown(anchor);
                }

            } else
            {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup, null);
                pwindo = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                pwindo.showAsDropDown(anchor);

            }

        } catch (Exception ex)
        {
            Log.e("", "");
        }
    }


    private void initiatePopupWindow(View anchor)
    {
        try
        {
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            ListPopupWindow popup = new ListPopupWindow(this);
            popup.setAnchorView(anchor);
            popup.setWidth((int) (display.getWidth() / (1.5)));

            popup.setOnItemClickListener(new OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long id3)
                {

                }
            });
            popup.show();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void createNotification()
    {
        Intent notificationIntent = new Intent(getApplicationContext(), ServiceFloating.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, notificationIntent, 0);

        Notification notification = new Notification(R.drawable.floating2, "Click to start launcher", System.currentTimeMillis());
        notification.setLatestEventInfo(getApplicationContext(), "Start launcher", "Click to start launcher", pendingIntent);
        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID_NOTIFICATION, notification);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (listChatHeads != null)
        {
            for (int i = 0; i < listChatHeads.size(); i++)
            {
                try
                {
                    windowManager.removeView(listChatHeads.get(i).imagView);
                } catch (Exception ex)
                {

                }
            }
        }

    }

}