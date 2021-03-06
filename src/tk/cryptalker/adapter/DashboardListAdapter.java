package tk.cryptalker.adapter;


import android.content.Intent;
import android.util.Log;
import android.widget.*;
import com.android.volley.VolleyError;
import tk.cryptalker.R;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tk.cryptalker.activity.ChatActivity;
import tk.cryptalker.activity.DashboardActivity;
import tk.cryptalker.manager.RequestManager;
import tk.cryptalker.model.Response;
import tk.cryptalker.model.Room;

public class DashboardListAdapter extends BaseAdapter {

    private static final String TAG = "DashboardListAdapter";

    private Activity activity;
    private LayoutInflater inflater;
    private List<Room> rooms;

    public DashboardListAdapter(Activity activity, List<Room> rooms) {
        this.activity = activity;
        this.rooms = rooms;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int location) {
        return rooms.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // getting room data for the row
        final Room room = rooms.get(position);

        if (room.isInvite()) {
            convertView = inflater.inflate(R.layout.row_dashboard_invite, null);

            TextView name = (TextView) convertView.findViewById(R.id.name);

            // On click go to room's chat
            final RelativeLayout container = (RelativeLayout)convertView.findViewById(R.id.room_row);

            container.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    if (view == container) {

                        Context context = activity.getApplicationContext();

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });

            name.setText(room.getName());

            Button request_accept = (Button)convertView.findViewById(R.id.request_accept);
            Button request_deny = (Button)convertView.findViewById(R.id.request_deny);

            request_accept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int user_id = room.getInviteId();

                    acceptFriendRequest(user_id, position);
                }
            });

            request_deny.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int user_id = room.getInviteId();

                    denyFriendRequest(user_id, position);
                }
            });

        } else {
            convertView = inflater.inflate(R.layout.row_dashboard_room, null);
            TextView name = (TextView) convertView.findViewById(R.id.name);

            TextView lastMessage = (TextView) convertView.findViewById(R.id.last_message);

            name.setText(room.getName());
            lastMessage.setText(room.getName());
        }

        return convertView;
    }

    private void acceptFriendRequest(int user_id, final int position)
    {
        RequestManager.getInstance(DashboardActivity.getContext()).acceptFriendRequest(user_id, new com.android.volley.Response.Listener<Response>() {

            @Override
            public void onResponse(Response response) {

                if (response.isSuccess()) {

                    Toast.makeText(DashboardActivity.getContext(), R.string.dashboard_tab_friend_request_accepted, Toast.LENGTH_SHORT).show();

                    rooms.remove(position);
                    DashboardActivity.roomList = rooms;
                    DashboardActivity.adapter.notifyDataSetChanged();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error during the request => " + error.toString());
            }
        });
    }

    private void denyFriendRequest(int user_id, final int position)
    {
        RequestManager.getInstance(DashboardActivity.getContext()).denyFriendRequest(user_id, new com.android.volley.Response.Listener<Response>() {

            @Override
            public void onResponse(Response response) {

                if (response.isSuccess()) {

                    Toast.makeText(DashboardActivity.getContext(), R.string.dashboard_tab_friend_request_deny, Toast.LENGTH_SHORT).show();

                    rooms.remove(position);
                    DashboardActivity.roomList = rooms;
                    DashboardActivity.adapter.notifyDataSetChanged();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error during the request => " + error.toString());
            }
        });
    }
}
