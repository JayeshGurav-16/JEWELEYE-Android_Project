package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private UserActionListener listener;

    public UserAdapter(Context context, List<User> userList, UserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Set user name
        holder.tvUserName.setText(user.getName());

        // Set user email
        holder.tvUserEmail.setText(user.getEmail());

        // Set user role
        holder.tvUserRole.setText("Role: " + user.getRole());

        // Set user status
        holder.tvUserStatus.setText(user.isActive() ? "Active" : "Blocked");

        // Set status text color based on active state
        int statusColor = user.isActive()
                ? R.color.success_green  // Use your color resource
                : R.color.error_red;     // Use your color resource

        holder.tvUserStatus.setTextColor(ContextCompat.getColor(context, statusColor));

        // Set button text based on user status
        holder.btnBlockUnblock.setText(user.isActive() ? "Block" : "Unblock");

        // Set click listeners
        holder.btnBlockUnblock.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBlockUnblockClicked(user, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(user, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole, tvUserStatus;
        Button btnBlockUnblock, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnBlockUnblock = itemView.findViewById(R.id.btnBlockUnblock);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface UserActionListener {
        void onBlockUnblockClicked(User user, int position);
        void onDeleteClicked(User user, int position);
    }
}