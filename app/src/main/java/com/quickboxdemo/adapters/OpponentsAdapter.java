package com.quickboxdemo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;
import com.quickblox.sample.core.ui.adapter.BaseSelectableListAdapter;
import com.quickblox.sample.core.utils.ResourceUtils;
import com.quickblox.sample.core.utils.SharedPrefsHelper;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.core.utils.UiUtils;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickboxdemo.R;
import com.quickboxdemo.activities.CallActivity;
import com.quickboxdemo.activities.OpponentsActivity;
import com.quickboxdemo.services.CallService;
import com.quickboxdemo.utils.CollectionsUtils;
import com.quickboxdemo.utils.Consts;
import com.quickboxdemo.utils.PushNotificationSender;
import com.quickboxdemo.utils.WebRtcSessionManager;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.quickblox.sample.core.utils.ResourceUtils.getString;
import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * QuickBlox team
 */
public class OpponentsAdapter extends BaseSelectableListAdapter<QBUser> {

    private QBUser currentUser;

    List<QBUser> userss=new ArrayList<>();

    private SelectedItemsCountsChangedListener selectedItemsCountChangedListener;

    public OpponentsAdapter(Context context, List<QBUser> users,List<QBUser> serverusers) {
        super(context, users);
        this.userss=serverusers;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_opponents_list_new, null);
            holder = new ViewHolder();
            currentUser = SharedPrefsHelper.getInstance().getQbUser();
            holder.opponentIcon = (ImageView) convertView.findViewById(R.id.image_opponent_icon);
            holder.opponentName = (TextView) convertView.findViewById(R.id.opponentsName);

            holder.opponentsQlftn = (TextView) convertView.findViewById(R.id.opponentsQlftn);
            holder.speciality = (TextView) convertView.findViewById(R.id.speciality);
            holder.city = (TextView) convertView.findViewById(R.id.city);
            holder.docId = (TextView) convertView.findViewById(R.id.docId);


            holder.image_video_call = (ImageView) convertView.findViewById(R.id.image_video_call);
            holder.image_audio_call = (ImageView) convertView.findViewById(R.id.image_audio_call);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBUser user = getItem(position);

        if (user != null) {
            holder.opponentName.setText(user.getFullName());
            if (null!=userss.get(position).getCustomData()){
                String ss=userss.get(position).getCustomData();
                holder.opponentsQlftn.setText("("+ss.split("!")[1]+")");
                holder.speciality.setText(ss.split("!")[2]);
                holder.city.setText(ss.split("!")[3]);
                holder.docId.setText(ss.split("!")[0]);
            }else{
                holder.opponentsQlftn.setText("");
                holder.speciality.setText("");
                holder.city.setText("");
                holder.docId.setText("");
            }
           /* holder.opponentsQlftn.setText(user.getCustomData().split("!")[1]);
            holder.speciality.setText(user.getCustomData().split("!")[2]);
            holder.city.setText(user.getCustomData().split("!")[3]);
            holder.docId.setText(user.getCustomData().split("!")[0]);*/
           /* if (selectedItems.contains(user)){
                convertView.setBackgroundResource(R.color.background_color_selected_user_item);
                holder.opponentIcon.setBackgroundDrawable(
                        UiUtils.getColoredCircleDrawable(ResourceUtils.getColor(R.color.icon_background_color_selected_user)));
                holder.opponentIcon.setImageResource(R.drawable.ic_checkmark);
            } else {
                convertView.setBackgroundResource(R.color.background_color_normal_user_item);
                holder.opponentIcon.setBackgroundDrawable(UiUtils.getColorCircleDrawable(user.getId()));
                holder.opponentIcon.setImageResource(R.drawable.ic_person);
            }*/
        }

        holder.image_video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // toggleSelection(position);
                ArrayList<Integer> opponentsList = new ArrayList<Integer>();
                opponentsList.add(user.getId());
                if (isLoggedInChat()) {
                    startCall(true, opponentsList);
                }
            }
        });

        holder.image_audio_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Integer> opponentsList = new ArrayList<Integer>();
                opponentsList.add(user.getId());
                if (isLoggedInChat()) {
                    startCall(false, opponentsList);
                }

            }
        });



       /* convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(position);
                selectedItemsCountChangedListener.onCountSelectedItemsChanged(selectedItems.size());
            }
        });
*/
        return convertView;
    }

    private boolean isLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            Toaster.shortToast(R.string.dlg_signal_error);
            tryReLoginToChat();
            return false;
        }
        return true;
    }

    private void tryReLoginToChat() {
        if (SharedPrefsHelper.getInstance().hasQbUser()) {
            QBUser qbUser = SharedPrefsHelper.getInstance().getQbUser();
            CallService.start(context, qbUser);
        }
    }

    private void startCall(boolean isVideoCall, ArrayList<Integer> opponentsList) {
        if (getSelectedItems().size() > Consts.MAX_OPPONENTS_COUNT) {
            Toaster.longToast(String.format(getString(R.string.error_max_opponents_count),
                    Consts.MAX_OPPONENTS_COUNT));
            return;
        }

        Log.d(TAG, "startCall()");
        // ArrayList<Integer> opponentsList = CollectionsUtils.getIdsSelectedOpponents(getSelectedItems());
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(context);

        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);

        WebRtcSessionManager.getInstance(context).setCurrentSession(newQbRtcSession);

        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());

        CallActivity.start(context, false);
        Log.d(TAG, "conferenceType = " + conferenceType);
    }

    public static class ViewHolder {
        ImageView opponentIcon;
        TextView opponentName;
        TextView opponentsQlftn;
        TextView speciality;
        TextView city;
        TextView docId;
        ImageView image_video_call;
        ImageView image_audio_call;
    }

    public void setSelectedItemsCountsChangedListener(SelectedItemsCountsChangedListener selectedItemsCountsChanged) {
        if (selectedItemsCountsChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountsChanged;
        }
    }

    public interface SelectedItemsCountsChangedListener {
        void onCountSelectedItemsChanged(int count);
    }
}
