package unal.datos_abiertos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private List<ItemData> mData;
    private LayoutInflater mInflater;

    ItemRecyclerViewAdapter(Context context, List<ItemData> data) {
        this.mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombregrupoTextView;
        TextView codigogrupoTextView;
        TextView institucionTextView;

        public ViewHolder(View view) {
            super(view);
            nombregrupoTextView = view.findViewById(R.id.nombregrupoTextView);
            codigogrupoTextView = view.findViewById(R.id.codigogrupoTextView);
            institucionTextView = view.findViewById(R.id.institucionTextView);

        }

        public TextView getnombregrupoTextView() { return nombregrupoTextView; }
        public TextView getcodigogrupoTextView() { return codigogrupoTextView; }
        public TextView getinstitucionTextView() { return institucionTextView; }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getnombregrupoTextView().setText(mData.get(position).nombregrupoTextView);
        viewHolder.getcodigogrupoTextView().setText(mData.get(position).codigogrupoTextView);
        viewHolder.getinstitucionTextView().setText(mData.get(position).institucionTextView);

    }

    @Override
    public int getItemCount() {  return mData.size(); }
}