package mt.edu.mcast.liamscerri.tracksidedad;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.List;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Private Fields
    private Context context;
    private int recyclerTemplate;
    private List<T> listItems;

    public RecyclerAdapter(){
        //Hacky method
    }

    public RecyclerAdapter(Context context, String recyclerLayoutName, List<T> listItems) {
        this.context = context;
        this.listItems = listItems;

        int layoutId = context.getResources().getIdentifier(recyclerLayoutName, "layout", context.getPackageName());
        if (layoutId == 0) {
            throw new InvalidParameterException("Layout: " + recyclerLayoutName + " not found");
            // this layout doesn't exist
        } else {
            this.recyclerTemplate = layoutId;
        }
    }

    private final void _paintRecyclerItem(RecyclerView.ViewHolder holder, final int position, T dataObject) {
        paintRecyclerItem(holder, position, dataObject);
    }

    public void paintRecyclerItem(RecyclerView.ViewHolder holder, final int position, T dataObject) {
        // Override this method to reflect the data you want to render in your Recycler View items
    }

    private final void _onRecyclerItemClick(View v, final RecyclerView.ViewHolder holder, final int position, T dataObject) {
        onRecyclerItemClick(v, holder, position, dataObject);
    }

    public void onRecyclerItemClick(View v, final RecyclerView.ViewHolder holder, final int position, T dataObject) {
        // Override this method to handle click action on a Recycler View items
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View row = inflater.inflate(this.recyclerTemplate, parent, false);

        Item item = new Item<T>(row);

        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final T dataObject = listItems.get(position);
        ((Item) holder).dataSource = dataObject;

        _paintRecyclerItem(holder, position, dataObject);

        ((Item) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onRecyclerItemClick(v, holder, position, dataObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void configRecyclerItemModel(Item itemContext, View itemView) {
        // Override this method to configure all properties that each recycler item model should hold within
    }

    //Extremely complex custom method to add item on the fly
    public void addItem(T item){
        listItems.add(item);
        this.notifyItemInserted(this.getItemCount() - 1);
    }

    public void updateItems(List<T> listItems){
        this.listItems = listItems;
    }

    public class Item<T> extends RecyclerView.ViewHolder {
        Item itemContext = this;
        TextView textView1;
        TextView textView2;
        LinearLayout linearLayout;
        T dataSource;

        public Item(@NonNull View itemView) {
            super(itemView);

            configRecyclerItemModel(itemContext, itemView);
        }
    }
}

