package edu.ntu.ssp4_rzdns_outhink.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.ntu.ssp4_rzdns_outhink.R;
import edu.ntu.ssp4_rzdns_outhink.modals.Agenda;

public class AgendaRecyclerViewAdapter extends RecyclerView.Adapter<AgendaRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "AgendaRecyclerViewAdapter";

    private ArrayList<String> lName = new ArrayList<>();
    private ArrayList<String> lDescription = new ArrayList<>();
    private ArrayList<String> lImage = new ArrayList<>();
    private Context mContext;

    public AgendaRecyclerViewAdapter(ArrayList<String> lImage, ArrayList<String> lName, ArrayList<String> lDescription, Context context){
        this.lImage = lImage;
        this.lName = lName;
        this.lDescription = lDescription;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agenda_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder Called");
        Glide.with(mContext).asBitmap().load(lImage.get(position)).into(holder.agendaImage);
        holder.locationName.setText(lName.get(position));
        holder.locationDescription.setText(lDescription.get(position));
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query attrQuery = ref.child("attractions").orderByChild("att_name").equalTo(lName.get(holder.getBindingAdapterPosition()));
                attrQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String attrid = null;
                        for(DataSnapshot dSnapshot: snapshot.getChildren()){
                            attrid = dSnapshot.getKey();
                        }
                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user",MODE_PRIVATE);
                        String email = sharedPreferences.getString("email","");
                        Query userAgenda = ref.child("agenda").orderByChild("useremail").equalTo(email);
                        String finalAttrid = attrid;
                        userAgenda.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                ArrayList<String> chosenattr = new ArrayList<>();
                                String agendaId="";
                                for (DataSnapshot useragenda: datasnapshot.getChildren()){
                                    Agenda att = useragenda.getValue(Agenda.class);
                                    chosenattr = att.getChosenattrs();
                                    agendaId = useragenda.getKey();
                                }
                                System.out.println("agendaId is: " + agendaId);
                                for(int i = 0; i<chosenattr.size();i++){
                                    if (chosenattr.get(i).equals(finalAttrid)){
                                        chosenattr.remove(i);
                                        break;
                                    }
                                }
                                Agenda agenda = new Agenda(chosenattr,email);
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("agenda").child(agendaId);
                                dbRef.setValue(agenda);
                                lName.clear();
                                lDescription.clear();
                                lImage.clear();
                                notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return lName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView agendaImage;
        TextView locationName;
        TextView locationDescription;
        RelativeLayout parentLayout;
        Button removeButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            agendaImage = itemView.findViewById(R.id.agendaImage);
            locationName = itemView.findViewById(R.id.agendaLocation);
            locationDescription = itemView.findViewById(R.id.agendaDescription);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }

}
