package com.roomiercg.madlab.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.service.autofill.Dataset;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomiercg.madlab.Adapter.UserAdapter;
import com.roomiercg.madlab.Model.User;
import com.roomiercg.madlab.R;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {

    private RecyclerView  recyclerView;
    private UserAdapter  userAdapter;
    private List<User> mUser;

    /*
        Aqui Useramos la clase UserAdapter creada anteriormente, al crear la vista al recycler view
        le pasaremos parametros e inicializamos un futura lista de usuarios y ReadUser()
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser = new ArrayList<>();
        readUser();

        return view;
    }

    /*
        Con este metodo recorremos la base de datos en busca de todos los usuarios que esten dados
        de alta menos a nosotros mismo, esto lo hace de forma iterable para pasarselo al recyclerView
        Y este los vaya creando.
     */

    private void readUser() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())){
                        mUser.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), mUser);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
