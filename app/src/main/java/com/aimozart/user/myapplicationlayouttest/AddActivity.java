package com.aimozart.user.myapplicationlayouttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private EditText etName;
    private MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        findViews();
        if(helper == null){
            helper = new MySQLiteOpenHelper(this);
        }
    }

    private void findViews(){
        etName = (EditText)findViewById(R.id.etName);

    }
    public void confirmBtn(View view){
        if(etName.getText().toString().length() == 0){
            Toast.makeText(AddActivity.this, "清單名稱不可為空", Toast.LENGTH_SHORT).show();
        }
        else {
            helper.insert(new Member(etName.getText().toString().trim()));
            Toast.makeText(AddActivity.this, "新增成功", Toast.LENGTH_LONG).show();
            etName.setText("");
            finish();
        }
    }
    public void cancelBtn(View view){
        finish();
    }
    public void clearBtn(View view){
        etName.setText("");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
    }
}
