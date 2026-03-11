package com.example.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OrderActionListener listener;

    public OrderAdapter(Context context, List<Order> orderList, OrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set order ID
        holder.tvOrderId.setText("Order #" + order.getOrderId());

        // Set customer name
        holder.tvCustomerName.setText("Customer: " + order.getCustomerName());

        // Set order date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date(order.getOrderDate()));
        holder.tvOrderDate.setText("Date: " + formattedDate);

        // Set order total
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvOrderTotal.setText("Total: " + currencyFormat.format(order.getTotal()));

        // Set order status
        holder.tvOrderStatus.setText(order.getStatus());

        // Set status badge color based on status
        int statusColor;
        switch (order.getStatus().toLowerCase()) {
            case "shipped":
                statusColor = R.color.success_green;
                break;
            case "delivered":
                statusColor = R.color.info_blue;
                break;
            case "cancelled":
                statusColor = R.color.error_red;
                break;
            default: // pending or processing
                statusColor = R.color.warning_orange;
                break;
        }
        holder.tvOrderStatus.getBackground().setTint(context.getResources().getColor(statusColor));

        // Set click listeners
        holder.btnUpdateStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatusClicked(order, holder.getAdapterPosition());
            }
        });

        holder.btnViewDetails.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetailsClicked(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateList(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvOrderDate, tvOrderTotal, tvOrderStatus;
        Button btnUpdateStatus, btnViewDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }

    public interface OrderActionListener {
        void onUpdateStatusClicked(Order order, int position);
        void onViewDetailsClicked(Order order);
    }
}