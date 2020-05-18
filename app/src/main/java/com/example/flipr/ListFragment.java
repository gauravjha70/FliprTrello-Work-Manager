package com.example.flipr;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class ListFragment extends Fragment {

    ListClass listClass;
    ArrayList<CardsClass> cards;

    TextView header;
    ListView cardsList;
    ImageButton deleteButton, addButton;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    String boardId, listId, type;
    int newKey = 0;

    public ListFragment(ListClass listClass, String boardId, String listId, String type)
    {
        this.listClass = listClass;
        this.boardId = boardId;
        this.listId = listId;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_pager_adapter,container,false);

        cardsList = rootView.findViewById(R.id.cards_list);
        header = rootView.findViewById(R.id.header);
        addButton = rootView.findViewById(R.id.add_button);

        //Firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        header.setText(listClass.getListName());
        addCardsToList();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inflateAddCardDialog();
            }
        });

        cardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CardDetailsBottomSheetFragment bottomSheetFragment = new CardDetailsBottomSheetFragment(cards.get(i),type,boardId,i,listId);
                bottomSheetFragment.show(getChildFragmentManager(),bottomSheetFragment.getTag());
            }
        });

        return rootView;
    }


    //Adding cards to the lists
    private void addCardsToList()
    {
        if(type.equals("Private"))
        {
            addPrivateCards();
        }
        else
        {
            addTeamCards();
        }

    }

    //Team board cards
    private void addTeamCards()
    {
        DatabaseReference ref = firebaseDatabase.getReference().child("Boards").child(boardId).child("Lists").child(listId).child("Cards");

        cards = new ArrayList<CardsClass>();
        CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
        cardsList.setAdapter(adapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                cards.add(dataSnapshot.getValue(CardsClass.class));
                newKey++;
                CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
                cardsList.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int id = Integer.parseInt(dataSnapshot.getKey());
                ArrayList<CardsClass> objects = new ArrayList<CardsClass>();
                objects.addAll(cards);
                cards = new ArrayList<CardsClass>();
                objects.remove(id);
                cards.addAll(objects);
                CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
                cardsList.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int id = Integer.parseInt(dataSnapshot.getKey());
                cards.remove(id);
                CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
                cardsList.setAdapter(adapter);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addPrivateCards()
    {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards").child("Private").child(boardId).child("Lists").child(listId).child("Cards");

        cards = new ArrayList<CardsClass>();
        CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
        cardsList.setAdapter(adapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                cards.add(dataSnapshot.getValue(CardsClass.class));
                newKey++;
                CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
                cardsList.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int id = Integer.parseInt(dataSnapshot.getKey());
                cards.remove(id);
                CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.cards_item_adapter,cards);
                cardsList.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inflateAddCardDialog()
    {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View addListDialogView = factory.inflate(R.layout.add_item_dialog, null);
        final AlertDialog addDialog = new AlertDialog.Builder(getActivity()).create();
        addDialog.setView(addListDialogView);
        addDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText listName = addListDialogView.findViewById(R.id.list_name);
        Button addListButton = addListDialogView.findViewById(R.id.add_list_button);
        final ProgressBar progressBar = addListDialogView.findViewById(R.id.progress_bar);

        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listName.getText().toString().trim().equals(""))
                {
                    progressBar.setVisibility(View.VISIBLE);

                    CardsClass cardsClass = new CardsClass();
                    cardsClass.setCardName(listName.getText().toString().trim());
                    cardsClass.setCreationDate(new Date());
                    cardsClass.setDueDate(new Date());
                    cardsClass.setDueDateSet(false);

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference ref;
                    if(type.equals("Private"))
                    {
                        ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards")
                                .child("Private").child(boardId).child("Lists").child(listId).child("Cards");
                    }
                    else
                    {
                        ref = firebaseDatabase.getReference().child("Boards").child(boardId).child("Lists").child(listId).child("Cards");
                    }

                    ref.child(newKey+"").setValue(cardsClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(),"Card added!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addDialog.cancel();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Card cannot be created!",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            addDialog.cancel();
                        }
                    });
                }
            }
        });

        addDialog.show();

    }


}
