package a59070020.kmitl.ac.th.mobilefinal;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterFragment extends Fragment {

    private static final String TAG = "REGISTER";

    private SQLiteDatabase myDB;
    private Account account;
    private ProgressDialog Loading;

    private EditText Username;
    private EditText Password;
    private EditText Name;
    private EditText Age;
    private Button Signup;
    private int count = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myDB = getActivity().openOrCreateDatabase("my.db", Context.MODE_PRIVATE, null);
        account = Account.getAccountInstance();
        Loading = new ProgressDialog(getActivity());
        registerFragmentElements();
        initSignup();
    }

    private void registerFragmentElements() {
        Log.d(TAG, "Get Elements");
        Username = getView().findViewById(R.id.register_username);
        Password = getView().findViewById(R.id.register_password);
        Name = getView().findViewById(R.id.register_name);
        Age = getView().findViewById(R.id.register_age);
        Signup = getView().findViewById(R.id.register_signupbtn);
    }

    private void initSignup() {
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Signup : clicked");
                Loading.setMessage("กำลังสมัครสมาชิก...");
                Loading.setCanceledOnTouchOutside(false);
                Loading.setCancelable(false);
                Loading.show();

                String username = Username.getText().toString();
                String password = Password.getText().toString();
                String fullname = Name.getText().toString();
                String Age_str = RegisterFragment.this.Age.getText().toString();

                if (username.isEmpty() || password.isEmpty() || fullname.isEmpty() || Age_str.isEmpty()) {
                    Log.d(TAG, "field is empty");
                    Toast.makeText(getActivity(), "กรุณาใส่ข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                    Loading.dismiss();
                } else if (!(fullname.isEmpty())) {
                    for (int i=0; i < fullname.length()-1; i++) {
                        if (fullname.charAt(i) == ' ') {
                            count++;
                        }
                    }
                    if (count > 1) {
                        Log.d(TAG, "Your name is incorrect");
                        Toast.makeText(getActivity(), "ชื่อและนามสกุลต้องคั่นด้วย 1 Space", Toast.LENGTH_SHORT).show();
                        Loading.dismiss();
                    } else {
                        count = 0;
                    }
                } else if (username.length() < 6 || username.length() > 12) {
                    Log.d(TAG, "Length's username between 6 and 12");
                    Toast.makeText(getActivity(), "กรุณากรอก username ความยาวระหว่าง6-12ตัวอักษร", Toast.LENGTH_SHORT).show();
                    Loading.dismiss();
                } else if (password.length() < 6) {
                    Log.d(TAG, "password more than 6");
                    Toast.makeText(getActivity(), "รหัสผ่านต้องมากกว่า 6 ตัวอักษร", Toast.LENGTH_SHORT).show();
                    Loading.dismiss();
                } else if(Integer.parseInt(Age_str) < 10 || Integer.parseInt(Age_str) > 80) {
                    Log.d(TAG, "Your age is not complete");
                    Toast.makeText(getActivity(), "กรุณากรอกอายุให้อยู่ในช่วง10-80ปี", Toast.LENGTH_SHORT).show();
                    Loading.dismiss();
                } else {
                    Cursor cursor = myDB.rawQuery("select * from account where username = '" + username + "'", null);
                    if (cursor.getCount() != 1) {

                        ContentValues registerAccount = new ContentValues();
                        registerAccount.put("username", username);
                        registerAccount.put("password", password);

                        Log.d(TAG, "Insert new account");
                        myDB.insert("account", null, registerAccount);
                        account.setUsername(username);
                        account.setPassword(password);

                        Cursor cursorPrimaryid = myDB.rawQuery("select * from account where username = '" + username + "'", null);
                        while (cursorPrimaryid.moveToNext()) {
                            ContentValues registerMy = new ContentValues();
                            registerMy.put("fullname", fullname);
                            registerMy.put("Age_str", Age_str);
                            registerMy.put("account_id", cursorPrimaryid.getInt(0));
                            myDB.insert("my", null, registerMy);
                            account.setPrimaryid(cursorPrimaryid.getInt(0));
                            account.setFullname(fullname);
                            account.setAge(Age_str);
                        }
                        cursorPrimaryid.close();
                        cursor.close();
                        Toast.makeText(getActivity(), "สมัครสมาชิกเรียบร้อย", Toast.LENGTH_LONG).show();
                        Loading.dismiss();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
                    } else {
                        Toast.makeText(getActivity(), "username นี้มีผู้ใช้อื่นใช้แล้ว", Toast.LENGTH_LONG).show();
                        cursor.close();
                        Loading.dismiss();
                    }
                }
            }
        });
    }
}
