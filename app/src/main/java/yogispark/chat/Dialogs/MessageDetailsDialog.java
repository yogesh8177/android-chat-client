package yogispark.chat.Dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import yogispark.chat.Adapters.ContactsViewAdapter;
import yogispark.chat.Asynctasks.FetchMessageDetails;
import yogispark.chat.Models.Contact;
import yogispark.chat.R;
import yogispark.chat.UI.ChatView;
import yogispark.chat.Utility.Constants;

/**
 * Created by yogesh on 18/10/16.
 */
public class MessageDetailsDialog extends DialogFragment implements LoaderManager.LoaderCallbacks<ArrayList<Contact>>{

    public static final int TYPE_DELIVERED = 0;
    public static final int TYPE_READ = 1;
    public int Category;
    public String ReadDate, DeliveredDate, GroupId, MessageId;

    ArrayList<Contact> DeliveredList, ReadList;
    ContactsViewAdapter deliveredAdapter, readAdapted;

    TextView delivered_date, read_date, label_delivery, label_read;
    RecyclerView delivered_list, read_list;
    boolean isLoadingDelivered, isLoadingRead = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DeliveredList = new ArrayList<>();
        ReadList = new ArrayList<>();

        deliveredAdapter = new ContactsViewAdapter(getContext(), DeliveredList);
        readAdapted = new ContactsViewAdapter(getContext(), ReadList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_details_dialog,container,false);


        label_delivery = (TextView) view.findViewById(R.id.label_delivered);
        label_read = (TextView) view.findViewById(R.id.label_read);

        delivered_date = (TextView) view.findViewById(R.id.delivery_date);
        read_date = (TextView) view.findViewById(R.id.read_date);
        delivered_list = (RecyclerView) view.findViewById(R.id.delivered_list_recyclerview);
        read_list = (RecyclerView) view.findViewById(R.id.read_list_recyclerview);

        if(Category == Constants.CATEGORY_PRIVATE_MESSAGE){
            read_list.setVisibility(View.GONE); label_read.setVisibility(View.GONE);
            delivered_list.setVisibility(View.GONE); label_delivery.setVisibility(View.GONE);

            read_date.setText(ReadDate);
            delivered_date.setText("Delivered: "+DeliveredDate);
        }else{
            delivered_date.setVisibility(View.GONE);
            read_date.setVisibility(View.GONE);

            delivered_list.setLayoutManager(new LinearLayoutManager(getContext()));
            delivered_list.setAdapter(deliveredAdapter);
            read_list.setLayoutManager(new LinearLayoutManager(getContext()));
            read_list.setAdapter(readAdapted);

            prepareData();
        }


        return view;
    }

    private void prepareData() {
        if(DeliveredList.size() == 0 && !isLoadingDelivered){
            getActivity().getSupportLoaderManager().restartLoader(TYPE_DELIVERED,null,this).forceLoad();
        }else if(DeliveredList.size() ==0 && isLoadingDelivered){
            getActivity().getSupportLoaderManager().initLoader(TYPE_DELIVERED,null,this);
        }

        if(ReadList.size() == 0 && !isLoadingRead){
            getActivity().getSupportLoaderManager().restartLoader(TYPE_READ, null, this).forceLoad();
        }else if(ReadList.size() == 0 && isLoadingRead){
            getActivity().getSupportLoaderManager().initLoader(TYPE_READ, null, this);
        }
    }

    @Override
    public Loader<ArrayList<Contact>> onCreateLoader(int id, Bundle args) {
        if(id == TYPE_DELIVERED){
            isLoadingDelivered = true;
            return new FetchMessageDetails(getContext(), MessageId,TYPE_DELIVERED);
        }else if(id == TYPE_READ){
            isLoadingRead = true;
            return new FetchMessageDetails(getContext(), MessageId,TYPE_READ);
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Contact>> loader, ArrayList<Contact> data) {
        if(loader.getId() == TYPE_DELIVERED){
            isLoadingDelivered = false;

            DeliveredList.clear();
            DeliveredList.addAll(data);
            deliveredAdapter.notifyDataSetChanged();

        }else if(loader.getId() == TYPE_READ){
            isLoadingRead = false;

            ReadList.clear();
            ReadList.addAll(data);
            readAdapted.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Contact>> loader) {

    }
}
