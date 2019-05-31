package com.example.arrozcomfeijao.Helper;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RefFirebase {
    private static FirebaseAuth referenceAuth;
    private static FirebaseFirestore referenceStore;
    private static StorageReference referenceStorage;

    public static FirebaseFirestore getFirebaseStore(){
        if (referenceStore == null){
            referenceStore = FirebaseFirestore.getInstance();
        }
        return referenceStore;
    }


    public static FirebaseAuth getFirebaseAuth(){
        if (referenceAuth == null){
            referenceAuth = FirebaseAuth.getInstance();
        }
        return referenceAuth;
    }


    public static StorageReference getFirebaseStorege(){
        if (referenceStorage == null){
            referenceStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenceStorage;
    }
}

