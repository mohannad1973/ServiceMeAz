package com.gropse.serviceme.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFcmMessageServices extends FirebaseMessagingService {

    private static final String TAG = MyFcmMessageServices.class.getSimpleName();
    public static final String MESSAGE = "message";
    public static final String CHAT_ID = "chat_id";
    public static final String ITEM_ID = "item_id";
    public static final String MESSAGE_FROM = "message_from";
    public static final String MESSAGE_TO = "message_to";
    public static final String IS_NEGOTIABLE = "is_negotiable";
    private static final String MSG = "msg";
    private static final String TYPE = "type";


    public MyFcmMessageServices() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
//            PushMsg pushMsg = new PushMsg();
//            pushMsg.message = remoteMessage.getData().get(MSG);
//            Log.e(TAG, pushMsg.message);
//            JSONObject object = new JSONObject(pushMsg.message);
//            pushMsg.type = object.optString(TYPE);
//            pushMsg.msg = object.optString(MESSAGE);
//            pushMsg.userId = object.optString(MESSAGE_FROM);
//            pushMsg.chatId = object.optInt(CHAT_ID, 0);
//            pushMsg.itemId = object.optString(ITEM_ID);
//            if (Prefs.instanceOf(getApplicationContext()).getLogin()) {
//                switchPush(pushMsg);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void switchPush(PushMsg pushMsg) {
//        switch (pushMsg.type) {
//            case "search_alert":
//                SearchNotification searchNotification = new Gson().fromJson(pushMsg.message, SearchNotification.class);
//                searchNotification.itemsBean.setSellerPrice(searchNotification.itemsBean.getPrice());
//                searchNotification.itemsBean.setBuyerPrice(searchNotification.itemsBean.getPrice());
//                sendNotification(searchNotification);
//                break;
//            case "message":
//                ChatPush chatPush = new Gson().fromJson(pushMsg.message, ChatPush.class);
//                ItemsBean itemsBean = new ItemsBean();
//                itemsBean.id = chatPush.itemId;
//                itemsBean.itemTitle = chatPush.itemName;
//                itemsBean.setSellerPrice(chatPush.sellerPrice);
//                itemsBean.setPrice(chatPush.sellerPrice);
//                itemsBean.setBuyerPrice(chatPush.buyerPrice);
//                itemsBean.setSellerName(Prefs.instanceOf(getApplicationContext()).getUserId().equals(chatPush.sellerId) ? chatPush.userName : chatPush.sellerName);
//                itemsBean.setSellerImage(Prefs.instanceOf(getApplicationContext()).getUserId().equals(chatPush.sellerId) ? chatPush.userImage : chatPush.sellerImage);
//                itemsBean.setSellerImage(chatPush.userImage);
//                itemsBean.setUserId(chatPush.sellerId);
//                itemsBean.setIsNegotiable(chatPush.isNegotiable);
//                itemsBean.setImage1(chatPush.image1);
////                    itemsBean.setImage3(chatPush.getImage3());
////                    itemsBean.setImage4(chatPush.getImage4());
//                Intent intent = new Intent();
//                intent.putExtra(MESSAGE, pushMsg.msg);
//                intent.putExtra(MESSAGE_FROM, pushMsg.userId);
//                intent.putExtra(ITEM_ID, pushMsg.itemId);
//                intent.putExtra("itemsBean", itemsBean);
//                intent.setAction(PUSH);
//                sendOrderedBroadcast(intent, null);
//                break;
//            case "read_status":
//                Intent intentA = new Intent();
//                intentA.putExtra(MESSAGE, pushMsg.msg);
//                intentA.putExtra(CHAT_ID, pushMsg.chatId);
//                intentA.putExtra(MESSAGE_FROM, pushMsg.userId);
//                intentA.putExtra(ITEM_ID, pushMsg.itemId);
//                intentA.setAction(READ);
//                sendBroadcast(intentA, null);
//                break;
//        }
//
//    }
//
//
//    //This method is generating a notification and displaying the notification
//    private void sendNotification(SearchNotification searchNotification) {
//        try {
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            Intent resultIntent = new Intent(this, ChatActivity.class);
//            resultIntent.putExtra(MESSAGE_FROM, searchNotification.userId);
//            resultIntent.putExtra("itemsBean", searchNotification.itemsBean);
//            resultIntent.putExtra(KEY_USER_TYPE, VALUE_SELLER);
//            stackBuilder.addParentStack(ChatActivity.class);
//
//            // Add a SecondActivity intent to the task stack.
//            stackBuilder.addNextIntent(resultIntent);
//
//            // Obtain a PendingIntent for launching the task constructed by this builder.
//            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
//
//            NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
//                    .setContentText(searchNotification.message)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent);
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                noBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//                noBuilder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            } else {
//                noBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
//            }
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = noBuilder.build();
//            notification.defaults = Notification.DEFAULT_ALL;
//            notificationManager.notify(AppUtility.createID(), notification); //0 = ID of notification
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

   /* public static OnChatMessageReceived listener;

    public static void setListener(OnChatMessageReceived listener){
        MyFcmMessageServices.listener = listener;
    }

    public interface OnChatMessageReceived{
        void onMessageReceived();
    }*/
}
//    private void sendNotification(String typeId, String type, String id, String message) {
//
//        Intent resultIntent;
//
//        switch (type) {
//            case TYPE_EDUCATION:
//                resultIntent = new Intent(this, EducationEmploymentDetailsActivity.class);
//                resultIntent.putExtra(AppConstants.EXTRA_ACTIVITY_TYPE, AppConstants.TYPE_EDUCATION);
//                resultIntent.putExtra(AppConstants.EXTRA_RESULT_DATA, new EmployementEducationBean().setId(typeId));
//                break;
//            case TYPE_BUSINESS:
//                resultIntent = new Intent(this, EducationEmploymentDetailsActivity.class);
//                resultIntent.putExtra(AppConstants.EXTRA_ACTIVITY_TYPE, AppConstants.TYPE_BUSINESS);
//                resultIntent.putExtra(AppConstants.EXTRA_RESULT_DATA, new EmployementEducationBean().setId(typeId));
//                break;
//            default:
//                resultIntent = new Intent(this, HomeActivity.class);
//                break;
//        }
//
//        Intent backIntent = new Intent(this, HomeActivity.class);
//        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        final PendingIntent resultPendingIntent = PendingIntent.getActivities(
//                this, 0,
//                new Intent[]{backIntent, resultIntent}, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.refhope_not)
//                .setContentText(message)
//                .setContentTitle(getString(R.string.app_name))
//                .setAutoCancel(true)
//                .setContentIntent(resultPendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = noBuilder.build();
//        notification.defaults = Notification.DEFAULT_ALL;
//        notificationManager.notify(AppUtility.createID(), notification); //0 = ID of notification
//    }

