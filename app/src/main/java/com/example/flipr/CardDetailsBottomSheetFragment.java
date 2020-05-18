package com.example.flipr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CardDetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private CardsClass cardsClass;
    private ImageButton deleteButton, dueDateButton;
    private TextView name, creationDateText, dueDateText;
    private LinearLayout dueDateLayout;
    private Button okButton, saveButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private String type, boardId, listId;
    private String newDueDate;
    private int position;

    public CardDetailsBottomSheetFragment(CardsClass cardsClass, String type, String boardId, int position, String listId)
    {
        this.cardsClass = cardsClass;
        this.type = type;
        this.boardId = boardId;
        this.position = position;
        this.listId = listId;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((View) getView().getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_details_bottom_fragment,container,false);

        deleteButton = rootView.findViewById(R.id.delete_button);
        dueDateButton = rootView.findViewById(R.id.edit_due_date_button);
        name = rootView.findViewById(R.id.card_name);
        creationDateText = rootView.findViewById(R.id.creation_date);
        dueDateText = rootView.findViewById(R.id.due_date);
        dueDateLayout = rootView.findViewById(R.id.due_date_layout);
        okButton = rootView.findViewById(R.id.ok_button);
        saveButton = rootView.findViewById(R.id.save_button);

        name.setText(cardsClass.getCardName());
        Date creationDate = cardsClass.getCreationDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm aa");
        creationDateText.setText(simpleDateFormat.format(creationDate));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Boolean dueDateStatus = cardsClass.getDueDateSet();
        if(dueDateStatus)
        {
            Date dueDate = cardsClass.getDueDate();
            dueDateText.setText(simpleDateFormat.format(dueDate));
        }
        else
        {
            dueDateText.setText("Not set");
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type.equals("Private"))
                {
                    deleteFromPersonal();
                }
                else
                {
                    deleteFromTeam();
                }
            }
        });

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {

                                int hour = c.get(Calendar.HOUR_OF_DAY);
                                int minute = c.get(Calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        newDueDate = dayOfMonth+"/"+monthOfYear+"/"+year+", "+selectedHour+":"+selectedMinute;
                                        dueDateText.setText(dayOfMonth+"/"+monthOfYear+"/"+year+", "+selectedHour+":"+selectedMinute+" hrs");
                                        cardsClass.setDueDateSet(true);
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePicker.setTitle("Select Time");
                                mTimePicker.show();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setTitle("Select date");
                datePickerDialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cardsClass.getDueDateSet())
                    return;

                if(type.equals("Private"))
                {
                    editFromPersonal();
                }
                else
                {
                    editFromTeam();
                }
            }
        });

        return rootView;
    }

    private void editFromPersonal()
    {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        Date date = new Date();
        try {
            date = format.parse(newDueDate);
            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards")
                .child("Private").child(boardId).child("Lists").child(listId).child("Cards").child(position+"").child("dueDate");
        ref.setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Due date set.",Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error setting due date",Toast.LENGTH_LONG).show();
            }
        });

        DatabaseReference booleanRef = firebaseDatabase.getReference().child("Users").child(userId).child("Boards")
                .child("Private").child(boardId).child("Lists").child(listId).child("Cards").child(position+"").child("dueDateSet");
        booleanRef.setValue(true);
    }

    private void  editFromTeam()
    {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        Date date = new Date();
        try {
            date = format.parse(newDueDate);
            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference ref = firebaseDatabase.getReference().child("Boards").child(boardId)
                .child("Lists").child(listId).child("Cards").child(position+"").child("dueDate");
        ref.setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Due date set.",Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error setting due date",Toast.LENGTH_LONG).show();
            }
        });

        DatabaseReference booleanRef = firebaseDatabase.getReference().child("Boards")
                .child(boardId).child("Lists").child(listId).child("Cards").child(position+"").child("dueDateSet");
        booleanRef.setValue(true);
    }


    private void deleteFromPersonal()
    {
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference().child("Users").child(userId).child("Boards")
                .child("Private").child(boardId).child("Lists").child(listId).child("Cards").child(position+"");
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Card deleted!",Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error deleting the card",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteFromTeam()
    {
        DatabaseReference ref = firebaseDatabase.getReference().child(boardId).child("Lists").child(listId).child("Cards").child(position+"");
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(),"Card deleted!",Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),"Error deleting the card",Toast.LENGTH_LONG).show();
            }
        });
    }

}
